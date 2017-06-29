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
            // but say i have a bunch of neighborhoods and the jobs appear a lot more slowly
            // say 400 neighborhoods and 20 agents and the tasks appear at a rate of 1 per 1000 timesteps for each neighborhood
            // then there is much more interaction between the agents than if the tasks were being generated at a much faster rate
            // therefore, i think i need to make the decision not based on the number of agents compared to the number of neighborhood
            // but consider the
            // The ratio of agent to neighborhood is not sufficient as the rate at which tasks are generated in the neighborhood is important as well
            // numN

            // if the agent to task density in the neighborhood is high then we want to coordinate based on signalling?
            // then if it is low then we should

            if (state.numAgents == state.getNeighborhoods().length) {
                confidence = pTable.getQValue(t.getNeighborhood().getId(), 0);
            } else if (state.numAgents < state.getNeighborhoods().length) {
                double weight = Math.max(0, ((double)  state.getNeighborhoods().length - state.numAgents) / (double) state.getNeighborhoods().length);
                confidence = weight * confidence + (1 - weight) * pTable.getQValue(t.getNeighborhood().getId(), 0);
            } else if ( state.getNeighborhoods().length > 1) {
                double weight = Math.max(0, ((double) state.numAgents - state.getNeighborhoods().length) / (double) state.numAgents);
                confidence = weight * confidence + (1 - weight) * pTable.getQValue(t.getNeighborhood().getId(), 0);
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

        if (hasTraveled == true && amWorking == false && curJob != null && curJob.getTask().getLocation().distance(this.curLocation) <= state.getThresholdToSignal()) {
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
