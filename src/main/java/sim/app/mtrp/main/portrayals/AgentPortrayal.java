package sim.app.mtrp.main.portrayals;

import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;
import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.agents.Valuators.EquitablePartitions;
import sim.app.mtrp.main.agents.comparisonagents.EquitableAgent;
import sim.app.mtrp.main.agents.learningagents.LearningAgentWithCommunication;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

import java.awt.*;
import java.util.Iterator;

/**
 * Created by drew on 2/22/17.
 */
public class AgentPortrayal extends OvalPortrayal2D {
    private static final long serialVersionUID = 1;

    Agent model;

    public AgentPortrayal(Agent model) {
        this.model = model;
    }

    @Override
    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {




        if (model.isAmWorking()) {
            graphics.setColor(Color.yellow);
        } else {
            //graphics.setColor(Color.black);
            switch (model.getId()) {
                case 0:
                    graphics.setColor(Color.black);
                    break;
                case 1:
                    graphics.setColor(Color.orange);
                    break;
                case 2:
                    graphics.setColor(Color.RED);
                    break;
                case 3:
                    graphics.setColor(Color.GREEN);
                    break;
            }
        }
        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);

//        if (model instanceof LearningAgentWithCommunication) {
//            LearningAgentWithCommunication lawc = (LearningAgentWithCommunication) model;
//            int sigDist = (int)lawc.getSignallingDistance() * 10;
//            int centerx = x - sigDist;// / 2;
//            int centery = y - sigDist;// / 2;
//            graphics.drawOval(centerx, centery,  sigDist * 2, sigDist*2);
//        }

//        graphics.setColor(Color.black);
//        graphics.drawOval((int) (x - model.getRadius() * 10), (int)(y - model.getRadius() * 10),  (int)(model.getRadius() * 2 * 10), (int)(model.getRadius()*2 * 10));
        int scalefactor = 1000 / model.getState().getSimWidth();
        //graphics.fillRect((int)model.getAverageX() *scalefactor, (int) model.getAverageY()*scalefactor, 10,10);

        OpenList pd = model.getState().getVoronoi();
        if (pd != null) {
            for (Site s : pd) {
                PolygonSimple rd = s.getPolygon();
                rd.scale(scalefactor);
                graphics.draw(rd);
//                rd.getCentroid();
//                if (rd != null) {
//                    Iterator<kn.uni.voronoitreemap.j2d.Point2D> ird = rd.iterator();
//                    kn.uni.voronoitreemap.j2d.Point2D p1 = ird.next();
//                    kn.uni.voronoitreemap.j2d.Point2D p2 = ird.next();
//
//                    //int scalefactor = 1000 / model.getState().getSimWidth();
//                    graphics.drawLine((int) (p1.x + 0) * scalefactor, (int) p1.y * scalefactor, (int) (p2.x + 0) * scalefactor, (int) p2.y * scalefactor);
//                    while (ird.hasNext()) {
//                        kn.uni.voronoitreemap.j2d.Point2D pi1 = ird.next();
//                        graphics.drawLine((int) (p2.x + 0) * scalefactor, (int) p2.y * scalefactor, (int) (pi1.x + 0) * scalefactor, (int) pi1.y * scalefactor);
//                        p2 = pi1;
//                    }
//                    graphics.drawLine((int) (p1.x + 0) * scalefactor, (int) p1.y * scalefactor, (int) (p2.x + 0) * scalefactor, (int) p2.y * scalefactor);
//
//                }
            }

        }

        if(model instanceof EquitableAgent) {
            // then draw the power diagram
            switch (model.getId()) {
                case 0:
                    graphics.setColor(Color.black);
                    break;
                case 1:
                    graphics.setColor(Color.orange);
                    break;
                case 2:
                    graphics.setColor(Color.RED);
                    break;
                case 3:
                    graphics.setColor(Color.GREEN);
                    break;
            }


            EquitableAgent a = (EquitableAgent) model;
            PolygonSimple rd = a.getRegionOfDominance();
            if (rd != null) {
                Iterator<kn.uni.voronoitreemap.j2d.Point2D> ird = rd.iterator();
                kn.uni.voronoitreemap.j2d.Point2D p1 = ird.next();
                kn.uni.voronoitreemap.j2d.Point2D p2 = ird.next();
                graphics.drawLine((int) p1.x * 10, (int) p1.y * 10, (int) p2.x * 10, (int) p2.y * 10);
                while (ird.hasNext()) {
                    kn.uni.voronoitreemap.j2d.Point2D pi1 = ird.next();
                    graphics.drawLine((int) p2.x * 10, (int) p2.y * 10, (int) pi1.x * 10, (int) pi1.y * 10);
                    p2 = pi1;

//                kn.uni.voronoitreemap.j2d.Point2D pi2;
//                if(ird.hasNext()) {
//                    pi2 = ird.next();
//                } else {
//                    pi2 = p1;
//                }
//                graphics.drawLine((int) pi1.x *10, (int) pi1.y *10 , (int) pi2.x * 10 , (int) pi2.y *10);
//                if(ird.hasNext())
                }
                graphics.drawLine((int) p1.x * 10, (int) p1.y * 10, (int) p2.x * 10, (int) p2.y * 10);

            }
        }

    }
}
