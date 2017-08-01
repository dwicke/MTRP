package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Created by drew on 7/31/17.
 */
public class WithExclusivity extends LearningAgentWithCommunication {


    public WithExclusivity(MTRP state, int id) {
        super(state, id);
    }

    @Override
    public Task getAvailableTask() {
        return getAvailableTask(getNonCommittedTasks());
        //return getAvailableTask(getTasksWithinRange(state.getBondsman().getNewTasks()));
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
}
