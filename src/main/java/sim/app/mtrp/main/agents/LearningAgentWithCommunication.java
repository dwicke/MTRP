package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Double2D;

/**
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunication extends LearningAgentWithJumpship {

    QTable subNeighborhood[];

    public LearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);

        subNeighborhood = new QTable[state.getNumNeighborhoods()];
        for (int i = 0; i < subNeighborhood.length; i++) {
            subNeighborhood[i] = new QTable(state.getNumAgents(),1, pLearningRate, pDiscountBeta, 1.0);
        }


    }

    @Override
    double getUtility(Task t) {
        if (t.getJob().isSignaled(this) || t.getJob().noSignals()) {
            return super.getUtility(t);
        } else {
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
}
