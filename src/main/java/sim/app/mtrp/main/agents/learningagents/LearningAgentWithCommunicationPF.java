package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;

/**
 *
 * Proportionally Fair Bounty Hunter
 * https://en.wikipedia.org/wiki/Proportionally_fair
 *
 * P = T^alpha / R^beta
 *
 * T is the bounty per timestep for the task
 * R is the historical average bounty per timestep the agent obtains
 *
 * alpha - adjust numerator [0,1]
 * beta - adjusts denominator [0,1]
 *
 * when alpha = 0 and beta = 1 then we have round robin scheduling which is max-min fair
 * when alpha = 1 and beta = 0 then we have normal bounty hunters which go after the task with the highest bounty per timestep
 * when alpha = 1 and beta = 1 then we have proportionally fair bounty hunters which is similar to the mechanism that 3G uses according to wikipedia
 *
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunicationPF extends LearningAgentWithJumpship {

    QTable agentSuccess;
    double agentSuccessLR = .99;//.99;
    QTable meanJumpshipDist;

    double totalBountyObtained = 0.0;
    double timeLastCompleted = 1.0;
    double alpha = .5;
    double beta = .5;


    Task[] dummy;

    //what is the average distance I will jumpship
    // so basically if the distance to the task is less than this then I will signal?
    double totalJumpshipDist;
    int numJumpships = 0;
    double maxCommDist = 5000;

    public LearningAgentWithCommunicationPF(MTRP state, int id) {
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
        if (reward == 1.0) {
            totalBountyObtained += curJob.getCurrentBounty();
            timeLastCompleted = state.schedule.getSteps();
        }
    }

    @Override
    public double getUtility(Task t) {

        double confidence = 1.0;
        double numSignaled = 0;
        // this is a hidden markov model
        // we have two states:
        // succeed
        // fail
        // we observe the probability of being in the succeed state when we observe
        // an agent i going after a task
        // probability of being in the fail state is 1 - this value

        for (int i = 0; i < state.numAgents; i++) {
            Agent a = state.getAgents()[i];
//            if (i != id && t.getJob().isSignaled(a) && this.curLocation.distance(a.curLocation) < maxCommDist) {
//                confidence *= agentSuccess.getQValue(i, 0);
//                numSignaled++;
//            }
//            else if (i != id && a.getCurJob() != null && this.curLocation.distance(a.curLocation) < maxCommDist && a.getCurJob().isSignaled(a) && a.getCurJob().getTask().getLocation().distance(t.getLocation()) < curLocation.distance(t.getLocation()))
//            {
//                confidence *= agentSuccess.getQValue(i, 0);
//                numSignaled++;
//            }
            if (i != id && t.getJob().isSignaled(a) && this.curLocation.distance(a.curLocation) < maxCommDist) {
                confidence *= agentSuccess.getQValue(i, 0);
                numSignaled++;
            }
            else if (i != id && a.getCurJob() != null && this.curLocation.distance(a.curLocation) < maxCommDist && a.getCurJob().isSignaled(a) && a.getNumTimeStepsFromLocation(t.getLocation(), a.getCurJob().getTask().getLocation()) < getNumTimeStepsFromLocation(t.getLocation(), curLocation))
            {
                confidence *= agentSuccess.getQValue(i, 0);
                numSignaled++;
            }



        }



        // weight needs to be based on the mean location of the tasks
        // and what not this is very simplistic
        /// get the distance to the nearest neighborhood
        // 1 - (numSignaled / state.numAgents)*(

        // This weight is very very important.
        // basically it adjust whether you should rely on signalling or the neighborhood
        // we have two major cases where we have the neighborhoods that are seperated
        // and the other where they overlap
        // when they are seperate a weight of 0 is best but,
        // when they overlap a weight of 1 is better
        // however, this is not
        double weight = 1;//state.numNeighborhoods == 1 ? 1.0 : numSignaled / (double) state.numAgents;
        //weight = (numSignaled > 0 || numNeighborhoods == 1) ? 1.0 : 0.0;

        // basically the idea is to use the neighborhood stuff for when we have seperate

        // ratio of number of agents to number of tasks in neighborhood could be the weight?
        // normalized?
        // the thing is that how do we get an agent to leave an occupied neighborhood
        // actually i think this is the best we can do... especially if we want the agents
        // to go to neighborhoods where they are possibly doing worse off
        // how high must the bounty go in order for an agent to leave an isolated neighborhood and go after it?
        // but that is the question we do want the agent to go between the neighborhoods

        double signalConf = confidence;


        double neighborhoodp = getNorm(t);

        confidence = weight * confidence;// + (1 - weight) * neighborhoodp;
        //state.printlnSynchronized("actual job mean service time = " + t.getJob().getMeanJobLength() + " learned value = " + tTable.getQValue(t.getJob().getJobType(), 0));
        double totalTime = getNumTimeStepsFromLocation(t.getLocation()) +  tTable.getQValue(t.getJob().getJobType(), 0);
        //double totalTime = t.getLocation().distance(curLocation) + tTable.getQValue(t.getJob().getJobType(), 0);
        //state.printlnSynchronized("Time = " + tTable.getQValue(t.getJob().getJobType(), 0));

        //double util =  confidence * ((t.getBounty() / totalTime) + t.getJob().getBountyRate() - (getCost(t) / totalTime));


            double util = confidence * ((t.getBounty() / totalTime) + t.getJob().getBountyRate() - (getCost(t) / totalTime)  /*+ (expectedNeighborhoodReward.getQValue(t.getNeighborhood().getId(), 0) / totalTime)*/);
            double averageUtil = (totalBountyObtained / timeLastCompleted);

            double fullVal = Math.pow(util, alpha) / Math.pow(averageUtil, beta);// - (1 - confidence) * (2*getCost(t) / totalTime); // add this in to get better results for small rho
            //state.printlnSynchronized("full value = " + fullVal + " task = " + t.getId() + " for agent id = " + id);
            return fullVal;

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

        //if (hasTraveled == true && amWorking == false && curJob != null && curJob.getTask().getLocation().distance(this.curLocation) <= signalDist) {
        if (hasTraveled == true && amWorking == false && curJob != null && getNumTimeStepsFromLocation(curJob.getTask().getLocation(), curLocation) <= signalDist) {
            curJob.signal(this);
        }
        return hasTraveled;
    }


    public double getSignallingDistance() {
        double signalDist = 0;//state.getThresholdToSignal();
        if (numJumpships > 0) {
            signalDist = totalJumpshipDist / numJumpships;
        }
        //return 40;
        return signalDist;


        // not using this
        //return signalDist / 2;
    }
    @Override
    public Task handleJumpship(Task bestT) {

        curJob.unsignal(this);

        totalJumpshipDist += getNumTimeStepsFromLocation(curJob.getTask().getLocation(), curLocation);
        //totalJumpshipDist += curJob.getTask().getLocation().distance(curLocation);
        numJumpships++;
        return super.handleJumpship(bestT);
    }



    @Override
    public String toString() {
        return super.toString() + " " + agentSuccess.getQTableAsString();
    }
}
