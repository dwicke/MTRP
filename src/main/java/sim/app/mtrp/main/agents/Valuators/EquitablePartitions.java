package sim.app.mtrp.main.agents.Valuators;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import sim.app.mtrp.main.MTRP;
import sim.util.Double2D;

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
    Site fixedSites[];
    double learningRate = .001;
    public EquitablePartitions(MTRP state) {

        this.state = state;
        diagram = new PowerDiagram();

        // normal list based on an array
        sites = new OpenList();
        fixedSites = new Site[state.numAgents];
    }


    public void init() {



        // create a root polygon which limits the voronoi diagram.
        // here it is just a rectangle.
        state.printlnSynchronized("Initing");
        diagram = new PowerDiagram();

        // normal list based on an array
        sites = new OpenList();
        fixedSites = new Site[state.numAgents];

        PolygonSimple rootPolygon = new PolygonSimple();
        int width =  (int) (state.getSimWidth()/* + state.taskLocLength*/);
        int height = (int) (state.getSimHeight() /*+ state.taskLocLength*/);
        rootPolygon.add(0, 0);
        rootPolygon.add(width, 0);
        rootPolygon.add(width, height);
        rootPolygon.add(0, height);

        // create 100 points (sites) and set random positions in the rectangle defined above.
        for (int i = 0; i < state.numAgents; i++) {
            // get a random neighborhood and then a random point within it

            boolean goodSite = true;
            Site site;
            do {
                goodSite = true;
                Double2D loc = state.neighborhoods[state.random.nextInt(state.numNeighborhoods)].generateLocationInNeighborhood();

                //Double2D loc = state.depos[i].getLocation();
                state.printlnSynchronized("Depo loc = " + loc);
                site = new Site(loc.getX(), loc.getY());
                for (int j = 0; j < fixedSites.length; j++) {
                    if (fixedSites[j] != null && site.distance(fixedSites[j]) < 1) {
                        // then we need to generate a new site
                        goodSite = false;
                        break;
                    }
                }
            }while(goodSite == false);
            //Site site = new Site(state.agents[i].curLocation.getX() + (state.taskLocLength / 2) , state.agents[i].curLocation.getY() + (state.taskLocLength / 2));
            // we could also set a different weighting to some sites
            // site.setWeight(30)
            sites.add(site);
            fixedSites[i] = site;
        }



        // set the list of points (sites), necessary for the power diagram
        diagram.setSites(sites);
        // set the clipping polygon, which limits the power voronoi diagram
        diagram.setClipPoly(rootPolygon);

        // do the computation
        diagram.computeDiagram();

    }


    public void computeDiagram() {
        //diagram.setSites(sites);
        diagram.computeDiagram();
    }


    /*
        each agent calls this with their id
     */
    public void update(int id) {

        PolygonSimple polygon = getRegion(id);
        if (polygon == null) {
            //state.printlnSynchronized("id " + id + " has a null polygon");
            init();// reset and fix
            return;
        }
        double u = 0.0;
        for (int j = 0; j < fixedSites[id].getNeighbours().size(); j++) {

            //state.printlnSynchronized(polygon.toString());

            Iterator<kn.uni.voronoitreemap.j2d.Point2D> pr = polygon.iterator();
//            while(pr.hasNext()) {
//                //state.printlnSynchronized(pr.next().toString());
//
//            }

            double gamma = 1.0 / (2.0 * fixedSites[id].distance(fixedSites[id].getNeighbours().get(j)));
            //state.printlnSynchronized("gamma = " + gamma);
            double denom = Math.pow(getRateInPolygonCliped(polygon), 2);
            double rateInMe = (1.0 / denom);
            //state.printlnSynchronized(" rate in me = " + rateInMe);
            double rateInNeighbor = (1.0 / Math.pow(getRateInPolygonCliped(fixedSites[id].getNeighbours().get(j).getPolygon()), 2));
            //state.printlnSynchronized(" rate in neighbor = " + rateInNeighbor);

            if (rateInNeighbor != rateInMe && rateInMe != Double.POSITIVE_INFINITY && rateInNeighbor != Double.POSITIVE_INFINITY) {
              //  state.printlnSynchronized(" diff was not zero!" + (rateInNeighbor - rateInMe));
                PolygonSimple neighbor = fixedSites[id].getNeighbours().get(j).getPolygon();
                double lineIntegral = getBoarderRate(neighbor, polygon);
                if (lineIntegral != 0) {
                    u += gamma * (rateInNeighbor - rateInMe) * lineIntegral;
                }else {
                    //u += gamma * (rateInNeighbor - rateInMe);
                }

                //state.printlnSynchronized("Updated u! gamma = " + gamma + " rateInNeighbor = " + rateInNeighbor + "rateInMe = " + rateInMe + " lineIntegral = " + lineIntegral);
            }
        }
        //state.printlnSynchronized("U value = " + (u /  sites.get(id).getNeighbours().size()));
        //sites.get(id).setWeight(sites.get(id).getWeight() - (u ));
        fixedSites[id].setWeight(fixedSites[id].getWeight() - learningRate * u);
        //state.printlnSynchronized("Weight for id " + id + " weight = " + sites.get(id).getWeight());
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

        //return 1.0;

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
                if (nearlyEqual(n.x, p.x, 0.001) && nearlyEqual(n.y, p.y, 0.001)) {
                    points.add(p);
                }
            }
        }


        //return 1.0;/// idk this isn't exactly right as it should be the length of the side times the average rate but i'm going to just use 1 for now...
        //state.printlnSynchronized("Num points in common = " + points.size());

        if (points.size() == 2) {

            double rateInPerX = getRateInPolygonCliped(polygon);// / polygon.getArea();
            //state.printlnSynchronized("The line integral is = " + rateInPerX + " and the length = " + points.get(0).distance(points.get(1)) );
            return rateInPerX * points.get(0).distance(points.get(1));

        }else {

            return 0.0;

        }


    }



    public double getRateInPolygonCliped(PolygonSimple s) {

        double totalRate = 0; //s.getArea();
        for (int i = 0; i < state.neighborhoods.length; i++) {

            // for each neighborhood get the area that intersects with the polygon
            // and multiply the rate / area of the neighborhood by this area
            double centerX = state.neighborhoods[i].getMeanLocation().x;// + (state.taskLocLength / 2);
            double centerY = state.neighborhoods[i].getMeanLocation().y;// + (state.taskLocLength / 2);

            PolygonSimple neighborhood = new PolygonSimple(4);
            neighborhood.add(centerX - (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            neighborhood.add(centerX + (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
            neighborhood.add(centerX + (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));
            neighborhood.add(centerX - (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));



            if (s == null) {
                totalRate += 0;
                continue;
            }
            PolygonSimple cl = s.convexClip(neighborhood);
            if (cl != null) {
                double areaIntersect = cl.getArea();
                //totalRate += areaIntersect / areaOfNeighborhood * (1.0 / state.neighborhoods[i].getTimestepsTilNextTask());
                totalRate += areaIntersect * (1.0 / state.neighborhoods[i].getTimestepsTilNextTask());
            }

        }
        double areaOfNeighborhood = state.taskLocLength * state.taskLocLength;
        return totalRate / ( (1.0 / state.getTimestepsTilNextTask()) * state.numNeighborhoods * areaOfNeighborhood);
        //return totalRate /(  (state.getSimWidth() + state.taskLocLength)* (state.getSimWidth() + state.taskLocLength));
    }





    public Site getSite(int id) {
        return fixedSites[id];
    }

    public PolygonSimple getRegion(int id) {
        return fixedSites[id].getPolygon();
    }


}
