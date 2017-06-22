package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Double2D;

/**
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunication extends LearningAgentWithJumpship {

    QTable agentSuccess;

    public LearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);
        agentSuccess = new QTable(state.getNumAgents(), 1, .99, .1, 1.0);

    }


    @Override
    public void learn(double reward) {
        super.learn(reward);
        if (curJob == null || curJob.getCurWorker() == null) {
            state.printlnSynchronized("CurJob = " + curJob + "reward = " + reward + " am working = " + amWorking);
            if (curJob != null) {
                state.printlnSynchronized("Cur worker is null");
            }
        }
        agentSuccess.update(curJob.getCurWorker().getId(), 0, reward);
        agentSuccess.oneUpdate(oneUpdateGamma);
    }

    @Override
    double getUtility(Task t) {
        if (t.getJob().isSignaled(this) || t.getJob().noSignals()) {
            return super.getUtility(t);
        } else {
            if (!t.getJob().noSignals()) {
                //state.printlnSynchronized("Time step" + state.schedule.getSteps() + "Job id " + t.getJob().getId() + " is signaled but not by me " + getId());
            }
            double confidence = 0.0;
            for (int i = 0; i < state.numAgents; i++) {
                if (t.getJob().isSignaled(state.getAgents()[i]))
                    confidence *= agentSuccess.getQValue(i, 0);
            }
            return confidence * super.getUtility(t);
            //return 0; // need to change this.
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
        curJob.unsignal(this);
        return super.handleJumpship(bestT);
    }
}
