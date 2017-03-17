package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirst extends AuctionAgent {
    public NearestFirst(MTRP state, int id) {
        super(state, id);
    }

    @Override
    double getUtility(Task t) {
        return -getCost(t);
    }
}
