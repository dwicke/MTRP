package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Job;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

import java.util.ArrayList;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirstWithJumpBounty extends LearningAgentWithJumpship {
    public NearestFirstWithJumpBounty(MTRP state, int id) {
        super(state, id);
    }

    @Override
    double getUtility(Task t) {


        //state.printlnSynchronized("Task " + t.getId() + " cost = " + -getCost(t));
        if (t.getNeighborhood().getId() == getId()) {
            return -getCost(t);
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    // need this here because of the fact that utility is negative so am using negative
    public Task getBestTask(Bag bagOfTasks) {
        if (bagOfTasks.size() == 0 && curJob == null) {
            return null; // need to go for resources.
        } else if (bagOfTasks.size() == 0 && curJob != null) {
            return curJob.getTask();
        }



        // otherwise just pick it using real method
        Task[] tasks = (Task[]) bagOfTasks.toArray(new Task[bagOfTasks.size()]);
        Task chosenTask = null;
        //ArrayList<Task> curMaxTasks = new ArrayList<Task>();
        double curMax = Double.NEGATIVE_INFINITY;
        if (curJob != null) {
            chosenTask = curJob.getTask();
            //curMaxTasks.add(chosenTask);
            curMax = getUtility(chosenTask);
            if (curMax == 0) {
                return chosenTask; // if i am at the task don't leave!
            }
        }
        for (Task t : tasks) {
            double value = getUtility(t);
            if (value > curMax) {
                chosenTask = t;
                curMax = value;
            } else if (value != Double.NEGATIVE_INFINITY && value == curMax  && t.getBounty() > chosenTask.getBounty()) {
                chosenTask = t;
                curMax = value;
            }
        }




        return chosenTask;
    }
}
