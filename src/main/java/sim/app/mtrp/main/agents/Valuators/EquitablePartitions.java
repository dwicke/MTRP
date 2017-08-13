package sim.app.mtrp.main.agents.Valuators;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import sim.app.mtrp.main.MTRP;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by drew on 8/12/17.
 * <p>
 * <p>
 * Going to try and do the equitable partitions thing going to use:
 * https://github.com/ArlindNocaj/power-voronoi-diagram
 * to generate the diagrams and the regions of dominance for each of the agents
 */
public class EquitablePartitions {


    MTRP state;
    // normal list based on an array
    OpenList sites;
    PowerDiagram diagram;

    public EquitablePartitions(MTRP state) {

        this.state = state;
        diagram = new PowerDiagram();

        // normal list based on an array
        sites = new OpenList();
    }


    public void init() {



        // create a root polygon which limits the voronoi diagram.
        // here it is just a rectangle.

        PolygonSimple rootPolygon = new PolygonSimple();
        int width =  (int) (state.getSimWidth() + state.taskLocLength);
        int height = (int) (state.getSimHeight() + state.taskLocLength);
        rootPolygon.add(0, 0);
        rootPolygon.add(width, 0);
        rootPolygon.add(width, height);
        rootPolygon.add(0, height);

        // create 100 points (sites) and set random positions in the rectangle defined above.
        for (int i = 0; i < state.numAgents; i++) {
            Site site = new Site(state.random.nextInt(width), state.random.nextInt(height));
            //Site site = new Site(state.agents[i].curLocation.getX() + (state.taskLocLength / 2) , state.agents[i].curLocation.getY() + (state.taskLocLength / 2));
            // we could also set a different weighting to some sites
            // site.setWeight(30)
            sites.add(site);
        }



        // set the list of points (sites), necessary for the power diagram
        diagram.setSites(sites);
        // set the clipping polygon, which limits the power voronoi diagram
        diagram.setClipPoly(rootPolygon);

        // do the computation
        diagram.computeDiagram();

    }


    public void computeDiagram() {
        diagram.setSites(sites);
        diagram.computeDiagram();
    }


    /*
        each agent calls this with their id
     */
    public void update(int id) {

        PolygonSimple polygon = getRegion(id);
        double u = 0.0;
        for (int j = 0; j < sites.get(id).getNeighbours().size(); j++) {

            //state.printlnSynchronized(polygon.toString());

            Iterator<kn.uni.voronoitreemap.j2d.Point2D> pr = polygon.iterator();
//            while(pr.hasNext()) {
//                //state.printlnSynchronized(pr.next().toString());
//
//            }

            double gamma = 1.0 / (2.0 * sites.get(id).distance(sites.get(id).getNeighbours().get(j)));
            //state.printlnSynchronized("gamma = " + gamma);
            double denom = Math.pow(getRateInPolygonCliped(polygon), 2);
            double rateInMe = (1.0 / denom);
            //state.printlnSynchronized(" rate in me = " + rateInMe);
            double rateInNeighbor = (1.0 / Math.pow(getRateInPolygonCliped(sites.get(id).getNeighbours().get(j).getPolygon()), 2));
            //state.printlnSynchronized(" rate in neighbor = " + rateInNeighbor);

            if (rateInNeighbor != rateInMe && rateInMe != Double.POSITIVE_INFINITY && rateInNeighbor != Double.POSITIVE_INFINITY) {
              //  state.printlnSynchronized(" diff was not zero!" + (rateInNeighbor - rateInMe));
                PolygonSimple neighbor = sites.get(id).getNeighbours().get(j).getPolygon();
                double lineIntegral = getBoarderRate(neighbor, polygon);

                u += gamma * (rateInNeighbor - rateInMe) * lineIntegral;
            }
        }

        sites.get(id).setWeight(sites.get(id).getWeight() + u);
    }

    public static boolean nearlyEqual(double a, double b, double epsilon) {
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double diff = Math.abs(a - b);

        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Double.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (epsilon * Double.MIN_NORMAL);
        } else { // use relative error
            return diff / Math.min((absA + absB), Float.MAX_VALUE) < epsilon;
        }
    }

    /**
     * basically just find the boundry between the neighbor and the polygon
     * and calculate the slope of this line segment
     * then clip or find the intersection between the polygon and each of the neighborhoods
     * then find if any of the points on the cliped polygon lie on the boundry
     * if so then find the length of that line segment
     * and multiply that length by the lambda/(area of neighorhood)
     * this then is the line integral
     *
     * @param neighbor
     * @param polygon
     * @return
     */
    public double getBoarderRate(PolygonSimple neighbor, PolygonSimple polygon) {
        ArrayList<kn.uni.voronoitreemap.j2d.Point2D> points = new ArrayList<kn.uni.voronoitreemap.j2d.Point2D>();
        // find the boundry with the neighbor
        Iterator<kn.uni.voronoitreemap.j2d.Point2D> neighborIter = neighbor.iterator();

        while (neighborIter.hasNext()) {
            kn.uni.voronoitreemap.j2d.Point2D n = neighborIter.next();
            Iterator<kn.uni.voronoitreemap.j2d.Point2D> polyIter = polygon.iterator();
            while(polyIter.hasNext()) {
                kn.uni.voronoitreemap.j2d.Point2D p = polyIter.next();
                //state.printlnSynchronized("The neighbor point is: " + n.toString() + " my point is = " + p.toString());
                //if (n.x == p.x && n.y == p.y) {
                if (nearlyEqual(n.x, p.x, 0.00001) && nearlyEqual(n.y, p.y, 0.00001)) {
                    points.add(p);
                }
            }
        }


        // get the slope of the boundry
        double slope = (points.get(0).getY() - points.get(1).getY()) / (points.get(0).getX() - points.get(1).getX());

        double lineIntegral = 0.0;
        // now for each of the neighborhoods clip with my region
        for (int i = 0; i < state.neighborhoods.length; i++) {

            double centerX = state.neighborhoods[i].getMeanLocation().x + (state.taskLocLength / 2);
            double centerY = state.neighborhoods[i].getMeanLocation().y + (state.taskLocLength / 2);

            PolygonSimple neighborhood = new PolygonSimple(4);
            neighborhood.add(centerX - (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            neighborhood.add(centerX + (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            neighborhood.add(centerX + (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));
            neighborhood.add(centerX - (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));

            PolygonSimple cliped = polygon.convexClip(neighbor);
            ArrayList<kn.uni.voronoitreemap.j2d.Point2D> seg = new ArrayList<kn.uni.voronoitreemap.j2d.Point2D>();

            // then for each clipped polygon find if any of the line segments
            // lie on the boundry
            if (cliped != null) {
                Iterator<kn.uni.voronoitreemap.j2d.Point2D> clipIter = cliped.iterator();

                while (clipIter.hasNext()) {
                    kn.uni.voronoitreemap.j2d.Point2D point = clipIter.next();
                    double cSlope = (point.getY() - points.get(1).getY()) / (point.getX() - points.get(1).getX());
                    if (cSlope == slope) {
                        // then we've got a point on the boundry
                        seg.add(point);
                    }
                }
                // find the length of that segment and multiply by (rate / area of neighborhood)
                if (seg.size() == 2) {
                    state.printlnSynchronized("WOOOHOO we have a segment that is on the boundry with length " + seg.get(0).distance(seg.get(1)));
                    double areaOfNeighborhood = state.taskLocLength * state.taskLocLength;
                    lineIntegral += seg.get(0).distance(seg.get(1)) * ((1.0 / state.neighborhoods[i].getTimestepsTilNextTask()) / (areaOfNeighborhood));
                }
            }


        }
        return lineIntegral;
    }



    public double getRateInPolygonCliped(PolygonSimple s) {

        double totalRate = 0.0;
        for (int i = 0; i < state.neighborhoods.length; i++) {

            // for each neighborhood get the area that intersects with the polygon
            // and multiply the rate / area of the neighborhood by this area
            double centerX = state.neighborhoods[i].getMeanLocation().x + (state.taskLocLength / 2);
            double centerY = state.neighborhoods[i].getMeanLocation().y + (state.taskLocLength / 2);

            PolygonSimple neighborhood = new PolygonSimple(4);
            neighborhood.add(centerX - (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            neighborhood.add(centerX + (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            neighborhood.add(centerX + (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));
            neighborhood.add(centerX - (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));


            double areaOfNeighborhood = state.taskLocLength * state.taskLocLength;


            PolygonSimple cl = s.convexClip(neighborhood);
            if (cl != null) {
                double areaIntersect = cl.getArea();
                totalRate += areaIntersect / areaOfNeighborhood * (1.0 / state.neighborhoods[i].getTimestepsTilNextTask());
            }

        }

        return totalRate;
    }

    public double getRateInPolygon(PolygonSimple s) {

        double totalRate = 0.0;
        for (int i = 0; i < state.neighborhoods.length; i++) {

            // for each neighborhood get the area that intersects with the polygon
            // and multiply the rate / area of the neighborhood by this area
            double centerX = state.neighborhoods[i].getMeanLocation().x + (state.taskLocLength / 2);
            double centerY = state.neighborhoods[i].getMeanLocation().y + (state.taskLocLength / 2);

            Point2D[] neighborhood = new Point2D[4];
            // top left
            neighborhood[0] = new Point2D.Double(centerX - (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            // top right
            neighborhood[1] = new Point2D.Double(centerX + (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            // bottom right
            neighborhood[2] = new Point2D.Double(centerX + (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));
            // bottom left
            neighborhood[3] = new Point2D.Double(centerX - (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));

            double areaOfNeighborhood = state.taskLocLength * state.taskLocLength;

            double xs[] = s.getXPoints();
            double ys[] = s.getYPoints();
            Point2D[] regionOfDominance = new Point2D[xs.length];
            for (int ps = 0; ps < regionOfDominance.length; ps++) {
                regionOfDominance[ps] = new Point2D.Double(xs[ps], ys[ps]);
            }

            double areaIntersect = PolygonIntersect.intersectionArea(neighborhood, regionOfDominance);

            totalRate += areaIntersect / areaOfNeighborhood * (1.0 / state.neighborhoods[i].getTimestepsTilNextTask());
        }

        return totalRate;
    }



    public PolygonSimple getRegion(int id) {
        return sites.get(id).getPolygon();
    }


}
