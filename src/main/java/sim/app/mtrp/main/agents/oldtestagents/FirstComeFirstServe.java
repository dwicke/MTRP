package sim.app.mtrp.main.agents.oldtestagents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;

/**
 *
 * This is an extremely bad policy and will not work.  It is essentially saying that
 * the agent who is closest to the task has claim on the task and that the tasks
 * are serviced in the order they were created.
 * Created by drew on 3/17/17.
 */
public class FirstComeFirstServe extends BribingAgent{
    public FirstComeFirstServe(MTRP state, int id) {
        super(state, id);
    }


    @Override
    public double getUtility(Task t) {
        if (t.getNeighborhood().getId() == getId()) {
            return t.getBounty();
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

}
