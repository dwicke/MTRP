package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirstSmart extends NearestFirstWithJump {
    public NearestFirstSmart(MTRP state, int id) {
        super(state, id);
    }

    @Override
    double getUtility(Task t) {


        //state.printlnSynchronized("Task " + t.getId() + " cost = " + -getCost(t));
        if (t.getNeighborhood().getId() == getId() || isFailing(t.getNeighborhood().getId())) {
            return -getCost(t);
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    private boolean isFailing(int neighID) {
        return state.getNeighborhoods()[getId()].getTasksWithNoCommittedAgents().length < state.getNeighborhoods()[neighID].getTasksWithNoCommittedAgents().length;
    }

}
