package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.LearningAgentWithJumpship;
import sim.app.mtrp.main.agents.Valuators.AgentLocationPredictor;
import sim.app.mtrp.main.util.QTable;

/**
 *
 * This is slow, but it beats auctions i think at everything
 * even when the neighborhoods are overlapping
 * LearningAgentWithCommunication does not beat auctions when
 * neighborhoods overlap.  this is because the signaling is not adaptive
 * and the determination of the utility does not incorporate the distance between
 * neighborhoods.
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunicationGeneral extends LearningAgentWithJumpship {

    QTable agentSuccess;
    AgentLocationPredictor alp;

    public LearningAgentWithCommunicationGeneral(MTRP state, int id) {
        super(state, id);
        agentSuccess = new QTable(state.getNumAgents(), 1, .99, .1, 1.0);
        alp = new AgentLocationPredictor(state);
    }

    public Task getAvailableTask() {

        alp.updatePositionPrediction(curJob, tTable);
        return super.getAvailableTask();
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
    public double getUtility(Task t) {
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
            // this is ORing...
            // so if i have

            // if the neighborhoods are non-overlapping then can do this.
            // if they are overlapping then i have to do confidence.
            // also, this is a continuous action multiagent systems
            // problem.  the agents in the ideal world would be learning how to set the weight
            // of the ptable and the confidence value so as to minimize the average wait time
            // of the customers.  That is global objective...  maximizing the bounty per timestep
            // does not actually translate into minimizing the average wait time...

            // the distance check is for when the neighborhoods overlap.  this check must be combined with
            // an adaptive signalling distance.  This along with the above adaptive version of this below
            // could be its own paper i think...
            if (state.neighborhoodPlane.getNeighborsWithinDistance(t.getNeighborhood().getMeanLocation(),20).size() == 0)
            {
                if (state.numAgents <= state.getNeighborhoods().length) {
                    confidence = pTable.getQValue(t.getNeighborhood().getId(), 0);
                } else if (state.getNeighborhoods().length > 1) {
                    double weight = Math.max(0, ((double) state.numAgents - state.getNeighborhoods().length) / (double) state.numAgents);
                    confidence = weight * confidence + (1 - weight) * pTable.getQValue(t.getNeighborhood().getId(), 0);
                }
            }

            double util =  ( confidence *  (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));
            //double util =  ( confidence *  (t.getBounty()+ getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) );
            return util;
            //return 0; // need to change this.
        }
    }

    @Override
    public boolean travel() {
        boolean hasTraveled = super.travel();
        // so the closer the agents are together the larger the threshold to signal should be (meaning the further away
        // you are from the task you notify the other agents
        // the further you are from other agents the less you have to signal so the small the distance to the task you
        // have to do the signalling
        // so here I can use the prediction of the location of the agents in order to make this distance calculation

         //= state.getThresholdToSignal();
        double closestDist = alp.getDistanceToClosestAgent(curLocation);
        double thresholdToSignal = Math.exp(20.0 / closestDist);


        if (hasTraveled == true && amWorking == false && curJob != null && curJob.getTask().getLocation().distance(this.curLocation) <= thresholdToSignal) {
            curJob.signal(this);
        }
        return hasTraveled;
    }

    @Override
    public Task handleJumpship(Task bestT) {
       // if (curJob.isSignaled(this)) {

            curJob.unsignal(this);
            //pTable.update(curJob.getTask().getNeighborhood().getId(), 0, 0.0);
             //pTable.oneUpdate(oneUpdateGamma);

        //}
        return super.handleJumpship(bestT);
    }
}
