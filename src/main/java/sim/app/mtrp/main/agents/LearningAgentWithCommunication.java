package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Double2D;

/**
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunication extends LearningAgentWithJumpship {


    public LearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);


    }

    @Override
    double getUtility(Task t) {
        if (t.getJob().isSignaled(this) || t.getJob().noSignals()) {
            return super.getUtility(t);
        } else {
            if (!t.getJob().noSignals()) {
                //state.printlnSynchronized("Time step" + state.schedule.getSteps() + "Job id " + t.getJob().getId() + " is signaled but not by me " + getId());
            }
            return 0; // might want to change this...
        }
    }

    @Override
    public boolean travel() {
        boolean hasTraveled = super.travel();

        if (hasTraveled == true && amWorking == false && curJob != null && curJob.getTask().getLocation().distance(this.curLocation) <= state.getThresholdToSignal()) {
            curJob.signal(this);
        }
        return hasTraveled;
    }


    @Override
    public Task handleJumpship(Task bestT) {


        // TODO: consider learning after jumping ship
        if (bestT != null && curJob != null && !curJob.isSignaled(this) && !curJob.noSignals()) {
            //state.printlnSynchronized("Time step = " + state.schedule.getSteps() + " Agent " + getId() + " jumpingship to task id = " + bestT.getJob().getId() + " with utility " + getUtility(bestT) + " from task id " + curJob.getId() + " with utility " + getUtility(curJob.getTask()));
            //state.printlnSynchronized("Jumpingship because there is another person going after this task that is closer so learn not to do this neighborhood");
            if (super.getUtility(bestT) < super.getUtility(curJob.getTask())) {
                return curJob.getTask();
            } else {
                pTable.update(curJob.getTask().getNeighborhood().getId(), 0, 0.15);
            }

        }

        // this must come last as I'm checking if i've signaled the task
        return super.handleJumpship(bestT);
    }
}
