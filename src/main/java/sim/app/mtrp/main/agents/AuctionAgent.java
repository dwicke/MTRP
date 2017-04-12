package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Created by drew on 3/11/17.
 */
public class AuctionAgent extends LearningAgent {

    public AuctionAgent(MTRP state, int id) {
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
                valuations[i] = ((AuctionAgent)state.agents[i]).getEvaluations(availableTasks);// agent id corresponds to agent's index.
            }

            //Task[] availableTasks = state.bondsman.getAvailableTasks();

            for (int i = 0; i < availableTasks.length; i++) {
                AgentTaskPair max = getAssignment(valuations);
                if (max != null) {
                    if (max.agentID == this.getId()) {

                        return availableTasks[max.taskID];
                    }
                    valuations = getNewValuations(valuations, max);
                }
            }
            //System.err.println("ERRRR  no task found for agent " + getId());
            //printValuations(valuations);

            // There is a case in which if everyone values the tasks equally
            // that given 4 agents and 6 tasks that the agents won't get assigned
            // a task.  Basically say the fist three agents get assigned tasks
            // then 3 tasks have been assigned so only 3 available
            // then we try and assign the 4 agent a task
            // since all of the agents value all of the tasks equally
            // then the remaining 3 tasks and 4 agents who have provided
            // valuations will leave the possiblity for that last agent
            // to not assign itself a task!
            // so instead of returning null and since the paper states
            // that we must pick a task.  We pick randomly from the available tasks.

            // the other case this happens is if there are no tasks available in which
            // case return null since I can't actually physically commit to a task since
            // no tasks exist

            if (availableTasks.length > 0) {
                Task chosenTask = availableTasks[state.random.nextInt(availableTasks.length)];
                //state.printlnSynchronized("picked randomly task" + chosenTask.);
                return chosenTask;
            }

            return null;
        }

        return curJob.getTask();
    }


    public Bag getNonCommittedTasks() {
        Bag closestWithinRange = getTasksWithinRange();
        Task[] availTasks = (Task[]) closestWithinRange.toArray(new Task[closestWithinRange.size()]);//getAvailableTasksInRange();//state.bondsman.getAvailableTasks();
        Bag nonCommitedTasks = new Bag();
        for (Task t : availTasks) {
            if (t.getCommittedAgents().size() == 0) {
                nonCommitedTasks.add(t);
            }
        }
        return nonCommitedTasks;
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



    /**
     * find the agent that has the highest valuation for a task
     * if duplicates pick randomly.
     * @param valuations
     * @return
     */
    AgentTaskPair getAssignment(double[][] valuations) {


        AgentTaskPair[] duplicates = new AgentTaskPair[valuations.length];
        for (int i = 0; i < duplicates.length; i++) {
            duplicates[i] = new AgentTaskPair();
        }
        int curDup = 0;
        for (int i = 0; i < valuations.length; i++) { // loop over the agents
            for (int j = 0; j < valuations[i].length; j++) { // loop over the tasks
                if (valuations[i][j] > duplicates[0].valuation) {
                    // clear the duplicates
                    for (int k = 0; k < duplicates.length; k++) {
                        duplicates[k].reset();
                    }
                    curDup = 0;
                    duplicates[curDup].agentID = i;
                    duplicates[curDup].taskID = j;
                    duplicates[curDup].valuation = valuations[i][j];
                    curDup = 1;
                } else if (valuations[i][j] == duplicates[curDup].valuation && valuations[i][j] > 0) {
                    // then we have a duplicate!
                    duplicates[curDup].agentID = i;
                    duplicates[curDup].taskID = j;
                    duplicates[curDup].valuation = valuations[i][j];
                    curDup++;
                }
            }
        }

        // now randomly pick from the duplicate set
        if (curDup == 0) {
            return null;
        }
        return duplicates[state.random.nextInt(curDup)];
    }


    void printValuations(double[][] valuations) {
        for (int i = 0; i < valuations.length; i++) {
            System.err.println("Agent id = " + i);
            for (int j = 0; j < valuations[i].length; j++) {
                System.err.print(valuations[i][j] + ", ");
            }
            System.err.println("\n-----------");
        }

    }

    /**
     * given current valuations return new valuations that have the agent removed
     * and the task that was set to negative max
     * @param valuations
     * @param max
     * @return
     */
    double[][] getNewValuations(double[][] valuations, AgentTaskPair max) {

        for (int i = 0; i < valuations.length; i++) { // for each agent
            // only cancel out the task that has been selected
            valuations[i][max.taskID] = Double.NEGATIVE_INFINITY;
        }

        // then cancel out the agent that has won
        for (int j = 0; j < valuations[max.agentID].length; j++) {
            valuations[max.agentID][j] = Double.NEGATIVE_INFINITY;
        }
        return valuations;
    }

    class AgentTaskPair {
        int agentID, taskID;
        double valuation;

        public AgentTaskPair() {
            agentID = -1;
            taskID = -1;
            valuation = -1;
        }

        public AgentTaskPair(int agentID, int taskID, double valuation) {
            this.agentID = agentID;
            this.taskID = taskID;
            this.valuation = valuation;
        }

        public void reset() {
            agentID = -1;
            taskID = -1;
            valuation = -1;
        }
    }


}
