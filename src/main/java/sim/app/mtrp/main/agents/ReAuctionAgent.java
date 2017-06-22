package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.Valuators.Auction;
import sim.util.Bag;

/**
 * Created by drew on 3/11/17.
 */
public class ReAuctionAgent extends LearningAgent {

    public ReAuctionAgent(MTRP state, int id) {
        super(state, id);
    }


    @Override
    public Task getAvailableTask() {

        if (curJob == null) {

            // so here do the auction!

            // basically
            // merge the valuations from all agents
            // find max
            // if index of max / numTasks == agentID then we have found the task we want
            // then its just a matter of index of max % numTasks

            // if it is not this agent's id then note the agent and the task id
            // remove that agent's valuations by setting all of its valuations to -Max
            // then set all of the agent valuations for that task to -MAX

            // DO i look at all of the task? or just those in the range of me?  i think just those in range.
            Bag nonCommitedTasks = getTasksWithinRange(state.getBondsman().getAvailableTasks());
            Task[] availableTasks = (Task[]) nonCommitedTasks.toArray(new Task[nonCommitedTasks.size()]);

            double[][] valuations = new double[state.agents.length][availableTasks.length];
            //System.err.println("Num avail tasks = " + availableTasks.length);
            // for each agent get their valuation
            for (int i = 0; i < state.agents.length; i++) {
                valuations[i] = ((ReAuctionAgent)state.agents[i]).getEvaluations(availableTasks);// agent id corresponds to agent's index.
            }

            //Task[] availableTasks = state.bondsman.getAvailableTasks();

            Auction a = new Auction(state);
            return a.runAuction(availableTasks, valuations, this.getId());
        }

        return curJob.getTask();
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


    double getUtility(Task t) {

        // this seems to work the best!!!!!!!!! for some reason... got to figure this out.
        double util =  ( (t.getBounty()+ getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) );
        return util;
    }

    double getCost(Task t) {
        Depo closestDepo = getClosestDepo(t.getLocation());
        if (closestDepo == null) {
            return Double.POSITIVE_INFINITY; // can't get to a depo from here! this is the case if i'm being asked what I'd bid for it.
        }
        return getNumTimeStepsFromLocation(t.getLocation()) * closestDepo.getFuelCost();
    }

    public double[] getEvaluations(Task[] availTasks) {
        double[] valuations = new double[availTasks.length];
        int i = 0;
        for (Task t: availTasks) {
            valuations[i] = getUtility(t);
            i++;
        }
        return valuations;
    }




}
