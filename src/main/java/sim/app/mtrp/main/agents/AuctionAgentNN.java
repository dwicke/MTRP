package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.Valuators.Auction;
import sim.util.Bag;

/**
 * Created by drew on 3/11/17.
 */
public class AuctionAgentNN extends NearestFirstWithJump {

    public AuctionAgentNN(MTRP state, int id) {
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
            Bag nonCommitedTasks = getNonCommittedTasks();
            Task[] availableTasks = (Task[]) nonCommitedTasks.toArray(new Task[nonCommitedTasks.size()]);

            double[][] valuations = new double[state.agents.length][availableTasks.length];
            //System.err.println("Num avail tasks = " + availableTasks.length);
            // for each agent get their valuation
            for (int i = 0; i < state.agents.length; i++) {
                valuations[i] = ((AuctionAgentNN)state.agents[i]).getEvaluations(availableTasks);// agent id corresponds to agent's index.
            }

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




    public double[] getEvaluations(Task[] availTasks) {
        double[] valuations = new double[availTasks.length];
        int i = 0;
        for (Task t: availTasks) {
            valuations[i] = getUtility(t);
            System.out.println("task " + t.getId() + " utility " + valuations[i]);
            i++;
        }
        return valuations;
    }




}
