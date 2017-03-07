package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Created by drew on 3/6/17.
 */
public class RandomAgent extends Agent {
    public RandomAgent(MTRP state, int id) {
        super(state, id);
    }

    @Override
    public Task getAvailableTask() {

        if (curJob == null || !curJob.getIsAvailable()) {

            Task[] tasks = state.getBondsman().getAvailableTasks();
            if (tasks.length == 0) {
                state.printlnSynchronized("NO TASKS!");
            }
            Bag closestWithinRange = new Bag();


            for (Task t : tasks) {
                double dist = getNumTimeStepsFromLocation(t.getLocation());
                if (dist < this.curFuel) {
                    closestWithinRange.add(t);
                }
            }
            if (closestWithinRange.size() == 0) {
                return null; // need to go for resources.
            }
            return (Task) closestWithinRange.get(state.random.nextInt(closestWithinRange.numObjs));
        } else {
            return curJob.getTask();// don't change
        }

    }
}
