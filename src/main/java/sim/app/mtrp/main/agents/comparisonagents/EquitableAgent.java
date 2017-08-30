package sim.app.mtrp.main.agents.comparisonagents;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.Valuators.EquitablePartitions;
import sim.util.Bag;

import java.util.Iterator;

/**
 * Created by drew on 8/12/17.
 */
public class EquitableAgent extends NearestFirst {


    static EquitablePartitions ep;
    public EquitableAgent(MTRP state, int id) {
        super(state, id);
        ep = null;
    }

    @Override
    public void commitTask(Task t) {
        t.amCommitted(this);
    }

    @Override
    public void decommitTask() {
        if (curJob != null)
            curJob.getTask().decommit(this);
        super.decommitTask();

    }

    public PolygonSimple getRegionOfDominance() {
        if (ep == null) {
            return null;
        }
        return ep.getRegion(id);
    }

    @Override
    public Task getAvailableTask() {
        if (ep == null) {
            ep = new EquitablePartitions(state);
            ep.init();
            ep.computeDiagram();
        }
        double rateinMyPolygon = ep.getRateInPolygonCliped(ep.getRegion(id));// - ep.getRegion(id).getArea();
        double overAllRate = (state.numNeighborhoods * (1.0 / state.getTimestepsTilNextTask())) / state.numAgents;
        //state.printlnSynchronized("id = " + id + "  Rate in my polygon " + rateinMyPolygon + " should be " + overAllRate + " difference = " + (overAllRate - rateinMyPolygon));
        //state.printlnSynchronized(" going to update");
        //if (rateinMyPolygon != overAllRate) {
            ep.update(id);
            //state.printlnSynchronized("finished update");
            ep.computeDiagram();
       // }

        return getAvailableTask(getNonCommittedTasks());
    }

    @Override
    public double getUtility(Task t) {
        PolygonSimple myRegion = ep.getRegion(id);


            //state.printlnSynchronized("my id = " + id + " virtual generator = " + ep.getSite(id).getPoint().toString());

            /*
            my id = 0 virtual generator = (32.0,6.0)
my id = 1 virtual generator = (2.0,22.0)
my id = 2 virtual generator = (32.0,21.0)
my id = 0 virtual generator = (10.0,32.0)
             */

        Iterator<Point2D> pr = myRegion.iterator();
//        state.printlnSynchronized("My region " + id + " is bounded by:");
//        while(pr.hasNext()) {
//            state.printlnSynchronized(pr.next().toString());
//        }


        kn.uni.voronoitreemap.j2d.Point2D tpoint = new kn.uni.voronoitreemap.j2d.Point2D(t.getLocation().getX() , t.getLocation().getY());
        //state.printlnSynchronized("Does it contain " + tpoint.toString());

        if (myRegion.contains(tpoint)) {
            //state.printlnSynchronized(" yes!");
            return -getNumTimeStepsFromLocation(t.getLocation());
        }
        //state.printlnSynchronized(" no");
        return Double.NEGATIVE_INFINITY;
    }

    public boolean inRegion(Task t) {
        PolygonSimple myRegion = ep.getRegion(id);



        kn.uni.voronoitreemap.j2d.Point2D tpoint = new kn.uni.voronoitreemap.j2d.Point2D(t.getLocation().getX() , t.getLocation().getY());
        //state.printlnSynchronized("Does it contain " + tpoint.toString());

        if (myRegion.contains(tpoint)) {
            //state.printlnSynchronized(" yes!");
            return true;
        }
        return false;
    }

    public Task getBestTask(Bag bagOfTasks) {
        if (bagOfTasks.size() == 0 && curJob == null) {
            return null; // need to go for resources.
        } else if (bagOfTasks.size() == 0 && curJob != null) {
            return curJob.getTask();
        }

        // epsilon random pick task
        /*if (state.random.nextDouble() < epsilonChooseRandomTask && bagOfTasks.size() > 0) {
            Task randTask = (Task) bagOfTasks.get(state.random.nextInt(bagOfTasks.size()));
            return randTask;
        }*/

        // otherwise just pick it using real method
        Task[] tasks = (Task[]) bagOfTasks.toArray(new Task[bagOfTasks.size()]);
        Task chosenTask = null;
        double curMax = Double.NEGATIVE_INFINITY;
        if (curJob != null) {
            chosenTask = curJob.getTask();
            curMax = getUtility(chosenTask);
            if (Double.isInfinite(curMax)) {
                return chosenTask;
            }
        }
        for (Task t : tasks) {

            double value = getUtility(t);
            if (value > curMax && inRegion(t)) {
                chosenTask = t;
                curMax = value;
            }
        }
        return chosenTask;
    }
}
