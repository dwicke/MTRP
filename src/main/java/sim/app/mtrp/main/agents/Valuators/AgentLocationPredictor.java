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
    Map<Task, Integer> lastSeenLocation = new HashMap<Task, Integer>(); // last location seen and time estimated gone

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

        if (agentLocations.isEmpty() && lastSeenLocation.isEmpty() && !newAgentLocations.isEmpty()) {
            agentLocations.putAll(newAgentLocations);
        } else {

            Map<Task, Integer> prevAgentLocations = new HashMap<Task, Integer>();
            for (Map.Entry<Task, Integer> en : agentLocations.entrySet()) {
                if (!oldAgentLocations.containsKey(en.getKey())) {
                    // we are going over each of the locations we thought we knew and if
                    // the task was completed it is now a prev agent location
                    prevAgentLocations.put(en.getKey(), 1);
                }
            }

            agentLocations.clear();
            agentLocations.putAll(oldAgentLocations);

            // update the time the agent has been gone
            for (Task key : lastSeenLocation.keySet()) {
                lastSeenLocation.put(key, lastSeenLocation.get(key) + 1);
            }

            // now to see about adding in the new agent locations...
            // they could be new agents or they could be old agents that are appearing again
            if (lastSeenLocation.size() > 0 && agentLocations.size() < numAgentsEst) {

                // then we have agents that are new that were previosly seen elsewhere that should be removed
                // so i'm just going to get the closest ones
                for (Map.Entry<Task, Integer> en : newAgentLocations.entrySet()) {

                    Double2D closest = null;

                    // this is wrong!!!  i know exactly how long the agent has been gone so I can know for pretty sure exactly which task he has gone
                    // to next!  WAIT can't do that because of jumpship!  so i have to do the heuristic method by distance.
                    for (Map.Entry<Task, Integer> ol : lastSeenLocation.entrySet()) {
//                        if (Math.abs(getNumTimeStepsFromLocation(en.getKey(), ol.getKey()) - ol.getValue()) <= 2) {
//                            //state.printlnSynchronized("num steps = " + getNumTimeStepsFromLocation(en.getKey(), ol.getKey()) + " num i counted = " + ol.getValue());
//                            closest = ol.getKey();
//                            break;
//                        }
                        if (closest == null || en.getKey().getLocation().distance(ol.getKey().getLocation()) < en.getKey().getLocation().distance(closest)) {
                            closest = ol.getKey().getLocation();
                        }
                    }
                    lastSeenLocation.remove(closest);
                }

            }

            // add the new previous locations to the last seen locations
            lastSeenLocation.putAll(prevAgentLocations);
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

    public Map<Task, Integer> getLastSeenLocation() {
        return lastSeenLocation;
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
        allLocs.putAll(lastSeenLocation);

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
