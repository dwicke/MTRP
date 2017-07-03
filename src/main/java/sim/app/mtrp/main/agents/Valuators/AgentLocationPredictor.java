package sim.app.mtrp.main.agents.Valuators;

import sim.app.mtrp.main.Job;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Neighborhood;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Double2D;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by drew on 6/15/17.
 */
public class AgentLocationPredictor {


    int numAgentsEst = 0;
    Map<Task, Integer> agentLocations = new HashMap<Task, Integer>(); // location and time at job

    MTRP state;

    public AgentLocationPredictor(MTRP state) {
        this.state = state;
    }


    public void updatePositionPrediction(Job curJob, QTable tTable) {
        Task[] allTasks = state.getBondsman().getAllTasks();

        Map<Task, Integer> newAgentLocations = new HashMap<Task, Integer>();
        Map<Task, Integer> oldAgentLocations = new HashMap<Task, Integer>();

        // find which tasks are unavailable
        for (Task t : allTasks) {
            if (!t.getIsAvailable() && (curJob == null || curJob.getTask().getId() != t.getId())) {
                if (agentLocations.containsKey(t)) {
                    oldAgentLocations.put(t, Math.max(0, agentLocations.get(t) - 1));
                } else {
                    newAgentLocations.put(t, (int) tTable.getQValue(t.getJob().getJobType(), 0));
                }
            }
        }

        if (newAgentLocations.size() + oldAgentLocations.size() > numAgentsEst) {
            numAgentsEst = newAgentLocations.size() + oldAgentLocations.size();
        }

        if (agentLocations.isEmpty() && !newAgentLocations.isEmpty()) {
            agentLocations.putAll(newAgentLocations);
        } else {



            agentLocations.clear();
            agentLocations.putAll(oldAgentLocations);


            agentLocations.putAll(newAgentLocations);



            //state.printlnSynchronized("num agents = " + numAgentsEst + " known loc ( " + agentLocations.size() +  ") + prev loc ( " + lastSeenLocation.size() + ") = " + (lastSeenLocation.size() + agentLocations.size()));
        }
    }


    public int getNumAgentsEst() {
        return numAgentsEst;
    }

    public Map<Task, Integer> getAgentLocations() {
        return agentLocations;
    }


    public int getNumAgentsInNeighborhood(Neighborhood n) {

        int count = 0;
        for (Map.Entry<Task, Integer> en : getAgentLocations().entrySet()) {
            if (en.getKey().getNeighborhood().getId() == n.getId()) {
                count++;
            }
        }
        return count;
    }

    public Map<Task, Integer> getAllAgentLocations() {
        Map<Task, Integer> allLocs = new HashMap<Task, Integer>();
        allLocs.putAll(agentLocations);
        return allLocs;
    }


    public double getDistanceToClosestAgent(Double2D curLoc) {
        double minDist = Double.MAX_VALUE;
        for (Map.Entry<Task, Integer> en : getAgentLocations().entrySet()) {
            double dist = en.getKey().getLocation().distance(curLoc);
            if (dist  < minDist) {
                minDist = dist;
            }
        }
        return minDist;
    }
}
