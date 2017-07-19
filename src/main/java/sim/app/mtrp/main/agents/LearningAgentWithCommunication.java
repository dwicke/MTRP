package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Bag;
import sim.util.Double2D;

/**
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunication extends LearningAgentWithJumpship {

    QTable agentSuccess;
    double agentSuccessLR = .99;//.99;
    QTable meanJumpshipDist;
    Task[] dummy;

    //what is the average distance I will jumpship
    // so basically if the distance to the task is less than this then I will signal?
    double totalJumpshipDist;
    int numJumpships = 0;

    public LearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);
        agentSuccess = new QTable(state.getNumAgents(), 1, agentSuccessLR, .1,state.random);
    }

    @Override
    public Task getAvailableTask() {
//        agentSuccess.oneUpdate(oneUpdateGamma);
//        pTable.oneUpdate(oneUpdateGamma);

        return super.getAvailableTask();
    }

    @Override
    public void learn(double reward) {
        super.learn(reward);
        agentSuccess.update(curJob.getCurWorker().getId(), 0, reward);
        agentSuccess.oneUpdate(oneUpdateGamma);
    }

    @Override
    public double getUtility(Task t) {

        double confidence = 1.0;
        double numSignaled = 0;
        for (int i = 0; i < state.numAgents; i++) {
            Agent a = state.getAgents()[i];
            if (i != id && t.getJob().isSignaled(a)) {
                confidence *= agentSuccess.getQValue(i, 0);
                numSignaled++;
            }
            //else if (i != id && a.getCurJob() != null && a.getCurJob().isSignaled(a) && a.getCurJob().getTask().getLocation().distance(t.getLocation()) < curLocation.distance(t.getLocation()))
            else if (i != id && a.getCurJob() != null && a.getCurJob().isSignaled(a) && a.getCurJob().getTask().getLocation().distance(t.getLocation()) < curLocation.distance(t.getLocation()))
            {
                confidence *= agentSuccess.getQValue(i, 0);
                numSignaled++;
            }
            if (i == id && t.getJob().isSignaled(a)) {
                if (curJob == null) {
                    //state.printlnSynchronized("Agent id = " + id + " task id = " + t.getId() + " has been signaled by me but i am not going after any tasks!!");
                } else {
                    //state.printlnSynchronized("Agent id = " + id + " task id =" + t.getId() + " has signalled this task and curtask id = " + curJob.getTask().getId());
                }
                confidence = 1.0;
                break;
            }
        }

        double weight = state.numNeighborhoods == 1 ? 1.0 : numSignaled / (double) state.numAgents;
        double signalConf = confidence;


        double neighborhoodp = getNorm(t);

        confidence = weight * confidence + (1 - weight) * neighborhoodp;

        double totalTime = (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        double util =  confidence * (t.getBounty() / totalTime) + confidence * t.getJob().getBountyRate() - (getCost(t) / totalTime);
        if (util < 0) {
           // state.printlnSynchronized("Agent id = " + id + " task id = " + t.getId() + " weight = " + weight + " signal confidence = " + signalConf + " num signaled " + numSignaled + " ptable = " + pTable.getQValue(t.getNeighborhood().getId(), 0) + " conf = " + confidence);
        }
        return util;
    }

    public double getNorm(Task t) {
        double ptableSum = 0.0;
        for (int i = 0; i < state.numNeighborhoods; i++) {
            ptableSum += pTable.getQValue(i, 0);
        }
        double neighborhoodp = pTable.getQValue(t.getNeighborhood().getId(), 0) / ptableSum;
        return neighborhoodp;
    }


    @Override
    public boolean travel() {
        boolean hasTraveled = super.travel();

        double signalDist = getSignallingDistance();

        if (hasTraveled == true && amWorking == false && curJob != null && curJob.getTask().getLocation().distance(this.curLocation) <= signalDist) {
            curJob.signal(this);
        }
        return hasTraveled;
    }


    public double getSignallingDistance() {
        double signalDist = 0;//state.getThresholdToSignal();
        if (numJumpships > 0) {
            signalDist = totalJumpshipDist / numJumpships;
        }
        return signalDist;
    }
    @Override
    public Task handleJumpship(Task bestT) {

        curJob.unsignal(this);

        totalJumpshipDist += getNumTimeStepsFromLocation(curJob.getTask().getLocation(), curLocation);
        numJumpships++;
        return super.handleJumpship(bestT);
    }

    @Override
    public String toString() {
        return super.toString() + " " + pTable.getQTableAsString();
    }
}
