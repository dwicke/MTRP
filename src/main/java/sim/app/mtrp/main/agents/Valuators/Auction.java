package sim.app.mtrp.main.agents.Valuators;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;

/**
 * Created by drew on 6/21/17.
 */
public class Auction {

    MTRP state;
    public Auction(MTRP state) {
        this.state = state;
    }


    public int runAuction(int numTasks, double[][] valuations, int myId) {
        for (int i = 0; i < numTasks; i++) {
            AgentTaskPair max = getAssignment(valuations);
            if (max != null) {
                if (max.agentID == myId) {

                    return max.taskID;
                }
                valuations = getNewValuations(valuations, max);
            }
        }
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
        if (numTasks > 0) {
            return state.random.nextInt(numTasks);

        }
        return -1;
    }

    /**
     * find the agent that has the highest valuation for a task
     * if duplicates pick randomly.
     * @param valuations
     * @return
     */
    public AgentTaskPair getAssignment(double[][] valuations) {


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
    public double[][] getNewValuations(double[][] valuations, AgentTaskPair max) {

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

    public class AgentTaskPair {
        public int agentID, taskID;
        public double valuation;

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
