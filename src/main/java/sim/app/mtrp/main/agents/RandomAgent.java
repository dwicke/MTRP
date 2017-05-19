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

        if (!amWorking && (curJob == null || !curJob.getIsAvailable())) {

            Bag closestWithinRange = getTasksWithinRange(state.getBondsman().getAvailableTasks());

            if (closestWithinRange.size() == 0) {
                return null; // need to go for resources.
            }
            return (Task) closestWithinRange.get(state.random.nextInt(closestWithinRange.numObjs));
        } else {
            return curJob.getTask();// don't change
        }

    }
}
