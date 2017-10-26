package sim.app.mtrp.main.agents.comparisonagents;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.learningagents.LearningAgent;
import sim.app.mtrp.main.agents.learningagents.LearningAgentWithJumpship;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirstWithJump extends LearningAgentWithJumpship {
    public NearestFirstWithJump(MTRP state, int id) {
        super(state, id);
    }



    public NearestFirstWithJump() {

    }

    @Override
    public double getUtility(Task t) {


        //state.printlnSynchronized("Task " + t.getId() + " cost = " + -getCost(t));


        if (state.numNeighborhoods == state.numAgents) {

//            double centerX = state.neighborhoods[getId()].getMeanLocation().x;// + (state.taskLocLength / 2);
//            double centerY = state.neighborhoods[getId()].getMeanLocation().y;// + (state.taskLocLength / 2);
//
//            PolygonSimple neighborhood = new PolygonSimple(4);
//            neighborhood.add(centerX - (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
//            neighborhood.add(centerX + (state.taskLocLength / 2), centerY - (state.taskLocLength / 2));
//            neighborhood.add(centerX + (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));
//            neighborhood.add(centerX - (state.taskLocLength / 2), centerY + (state.taskLocLength / 2));
//
//            if (neighborhood.contains(new Point2D(t.getLocation().x, t.getLocation().y))) {

//            double cellHalf = (state.getTaskLocLength() / Math.sqrt(state.numAgents)) / 2.0;
//            if (Math.abs(startDepo.getLocation().getX() - t.getLocation().getX()) <= cellHalf && Math.abs(startDepo.getLocation().getY() - t.getLocation().getY()) <= cellHalf) {
//                return -getNumTimeStepsFromLocation(t.getLocation());
//            }
//            return Double.NEGATIVE_INFINITY;


            if (t.getNeighborhood().getId() == getId()) {
                return -(getNumTimeStepsFromLocation(t.getLocation()) + t.getJob().getMeanJobLength());
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }else {
//            double cellHalf = (state.getTaskLocLength() / Math.sqrt(state.numAgents)) / 2.0;
//            if (Math.abs(startDepo.getLocation().getX() - t.getLocation().getX()) <= cellHalf && Math.abs(startDepo.getLocation().getY() - t.getLocation().getY()) <= cellHalf) {
//                return -getNumTimeStepsFromLocation(t.getLocation());
//            }
//            return Double.NEGATIVE_INFINITY;
            double centerX = state.neighborhoods[0].getMeanLocation().x;// + (state.taskLocLength / 2);
            double centerY = state.neighborhoods[0].getMeanLocation().y;
            // if it is in my quadrant then it is my task it will have the same sign as the depo
            if (Math.signum(startDepo.getLocation().getX() - centerX) ==  Math.signum(t.getLocation().getX() - centerX) && Math.signum(centerY - startDepo.getLocation().getY() ) ==  Math.signum(centerY - t.getLocation().getY() ))
            {
                return -getNumTimeStepsFromLocation(t.getLocation());
            }
            return Double.NEGATIVE_INFINITY;

        }


//        if (t.getNeighborhood().getMeanLocation().getX() - t.getLocation().getX() < 0 && id == 0) {
//            // then we are to the right of the y axis (if y-axis runs down the center of the gaussian)
//            return -(getNumTimeStepsFromLocation(t.getLocation()) + t.getJob().getMeanJobLength());
//        } else if (t.getNeighborhood().getMeanLocation().getX() - t.getLocation().getX() >= 0 && id == 1) {
//            return -(getNumTimeStepsFromLocation(t.getLocation()) + t.getJob().getMeanJobLength());
//        }
//
//        return Double.NEGATIVE_INFINITY;

        //return -(getNumTimeStepsFromLocation(t.getLocation()) + t.getJob().getMeanJobLength());

    }
    public double getCost(Task t) {
        // closest depo will never be null because we only consider tasks that are within distance of a depo
        return getNumTimeStepsFromLocation(t.getLocation());
    }


    @Override
    public Task getAvailableTask() {
        if (state.numNeighborhoods == state.numAgents) {
            Bag tasksNearby = new Bag(state.neighborhoods[id].getTasks());
            Bag inRangeTasks = getTasksWithinRangeAndAvailable(tasksNearby);
            return getAvailableTask(inRangeTasks);
        }
        return super.getAvailableTask();
    }


    // need this here because of the fact that utility is negative so am using negative
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
            if (value > curMax && value != Double.NEGATIVE_INFINITY) {
                chosenTask = t;
                curMax = value;
            }
        }
        return chosenTask;
    }

}
