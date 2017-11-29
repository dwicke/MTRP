package sim.app.mtrp.main.agents.comparisonagents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * based on the Gated Nearest Neighbor algorithm from:
 * An Explicit Formulation of the Earth Movers Distance with Continuous Road Map Distances
 *
 * Quote from the paper:
 *
 * Our simulations are of a “gated”, multi-vehicle, nearest-neighbor policy (gated m-NN).
 * A gated policy is one that completes in order a sequence of demand “batches”, where each
 * batch consists of all the demands that arrived while the previous batch was being worked on.
 * Within a particular batch, a vehicle i’s kth demand is the one—among all demands not yet
 * assigned to any vehicle at the time when i’s (k −1)th demand was delivered—whose pickup
 * location was nearest to the location of i. Although a proof that such policy is stabilizing
 * for all λ < λ∗ is currently not available, it has been observed that nearest neighbor policies
 * have good performance for a variety of vehicle routing problems.
 */
public class GatedNN extends NearestFirst {


    public GatedNN(MTRP state, int id) {
        super(state, id);
    }

    @Override
    public double getUtility(Task t) {
        return -getNumTimeStepsFromLocation(t.getLocation());
    }

    @Override
    public void commitTask(Task t) {
        t.amCommitted(this);
        state.bondsman.removeFromCurrentBatch(t);
    }


    @Override
    public void decommitTask() {
        if (curJob != null)
            curJob.getTask().decommit(this);
        super.decommitTask();

    }


    @Override
    public Task getAvailableTask() {


        // the idea of a gated NN algorithm is that
        // the agents work on batches of tasks
        // a batch consists of all of the tasks that were generated
        // while completing all of the tasks
        return getAvailableTask(getNonCommittedTasks(state.bondsman.getCurrentBatch()));
    }

}
