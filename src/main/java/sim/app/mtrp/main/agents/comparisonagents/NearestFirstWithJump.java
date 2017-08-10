package sim.app.mtrp.main.agents.comparisonagents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.learningagents.LearningAgentWithJumpship;
import sim.field.continuous.Continuous2D;
import sim.field.grid.DenseGrid2D;
import sim.field.grid.IntGrid2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Int2D;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirstWithJump extends LearningAgentWithJumpship {
    public NearestFirstWithJump(MTRP state, int id) {
        super(state, id);
    }

    @Override
    public double getUtility(Task t) {


        //state.printlnSynchronized("Task " + t.getId() + " cost = " + -getCost(t));
        if (state.numNeighborhoods == 1) {
            //state.printlnSynchronized("Task id: " + id + " location " + location + " right of y: " + ((location.getX() - neighborhood.meanLocation.getX()) > 0 ? true : false) + " below x: " + ((location.getY() - neighborhood.meanLocation.getY()) > 0 ? true : false));
            int rightOfY = ((t.getLocation().getX() - t.getNeighborhood().getMeanLocation().getX()) > 0 ? 1 : 0);

            int belowX = ((t.getLocation().getY() - t.getNeighborhood().getMeanLocation().getY()) > 0 ? 1 : 0);

//            if (rightOfY + 2*belowX == getId()) {
//                return -getNumTimeStepsFromLocation(t.getLocation());
//            } else {
//                return Double.NEGATIVE_INFINITY;
//            }


            // base it off of the x,y distance from start depo location
            int cellHalf = (int) (state.getTaskLocLength() / Math.sqrt(state.numAgents)) / 2;
            if (Math.abs(startDepo.getLocation().getX() - t.getLocation().getX() ) < cellHalf && Math.abs(startDepo.getLocation().getY() - t.getLocation().getY() ) < cellHalf)
            {
                return -getNumTimeStepsFromLocation(t.getLocation());
            }
            return Double.NEGATIVE_INFINITY;
            /*
            // create a grid and see if te task is within the agent's cell, if so then return the utility
            Double2D relativeCoords = new Double2D(t.getLocation().getX() - t.getNeighborhood().getMeanLocation().getX(), t.getLocation().getY() - t.getNeighborhood().getMeanLocation().getY());
            //state.printlnSynchronized("realtive coords = " + relativeCoords.toCoordinates());

            double width =  (state.getTaskLocLength() / Math.sqrt(state.numAgents));
            double rowLength = Math.sqrt(state.numAgents);

            Int2D di = new Int2D((int) Math.ceil( relativeCoords.getX() / width), (int) Math.ceil( (relativeCoords.getY() / width)));
            //state.printlnSynchronized("my id = " + id + " the coord is " + di.toCoordinates() + " to index = " + ((int) (((di.getX() + width) * rowLength) + di.getY() + width)));
            if (id == (int) (((di.getX() + width) * rowLength) + di.getY() + width)) {
                return -getNumTimeStepsFromLocation(t.getLocation());
            }

            return Double.NEGATIVE_INFINITY;
            */
        }
        else {
            if (t.getNeighborhood().getId() == getId()) {
                return -getNumTimeStepsFromLocation(t.getLocation());
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }
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
        }
        for (Task t : tasks) {

            double value = getUtility(t);
            if (value > curMax) {
                chosenTask = t;
                curMax = value;
            }
        }
        return chosenTask;
    }
}
