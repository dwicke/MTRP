package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by drew on 5/30/17.
 */
public class ComplexLearningAgent extends LearningAgentWithJumpship {

    int numAgentsEst = 0;
    Map<Double2D, Integer> agentLocations = new HashMap<Double2D, Integer>(); // location and time at job
    Map<Double2D, Integer> lastSeenLocation = new HashMap<Double2D, Integer>(); // last location seen and time estimated gone


    public ComplexLearningAgent(MTRP state, int id) {
        super(state, id);
    }

    public Task getAvailableTask() {
        Task[] allTasks = state.getBondsman().getAllTasks();

        Map<Double2D, Integer> newAgentLocations = new HashMap<Double2D, Integer>();
        Map<Double2D, Integer> oldAgentLocations = new HashMap<Double2D, Integer>();

        // find which tasks are unavailable
        for (Task t : allTasks) {
            if (!t.getIsAvailable() && (curJob == null || curJob.getTask().getId() != t.getId())) {
                if (agentLocations.containsKey(t.getLocation())) {
                    oldAgentLocations.put(t.getLocation(), agentLocations.get(t.getLocation()) + 1);
                } else {
                    newAgentLocations.put(t.getLocation(), 1);
                }
            }
        }

        if (newAgentLocations.size() + oldAgentLocations.size() > numAgentsEst) {
            numAgentsEst = newAgentLocations.size() + oldAgentLocations.size();
        }

        if (agentLocations.isEmpty() && lastSeenLocation.isEmpty() && !newAgentLocations.isEmpty()) {
            agentLocations.putAll(newAgentLocations);
        } else {

            Map<Double2D, Integer> prevAgentLocations = new HashMap<Double2D, Integer>();
            for (Map.Entry<Double2D, Integer> en : agentLocations.entrySet()) {
                if (!oldAgentLocations.containsKey(en.getKey())) {
                    // we are going over each of the locations we thought we knew and if
                    // the task was completed it is now a prev agent location
                    prevAgentLocations.put(en.getKey(), 0);
                }
            }

            agentLocations.clear();
            agentLocations.putAll(oldAgentLocations);

            // update the time the agent has been gone
            for (Double2D key : lastSeenLocation.keySet()) {
                lastSeenLocation.put(key, lastSeenLocation.get(key) + 1);
            }

            // now to see about adding in the new agent locations...
            // they could be new agents or they could be old agents that are appearing again
            if (lastSeenLocation.size() > 0 && agentLocations.size() < numAgentsEst) {

                // then we have agents that are new that were previosly seen elsewhere that should be removed
                // so i'm just going to get the closest ones
                for (Map.Entry<Double2D, Integer> en : newAgentLocations.entrySet()) {

                    Double2D closest = null;

                    // this is wrong!!!  i know exactly how long the agent has been gone so I can know for pretty sure exactly which task he has gone
                    // to next!  WAIT can't do that because of jumpship!  so i have to do the heuristic method by distance.
                    for (Map.Entry<Double2D, Integer> ol : lastSeenLocation.entrySet()) {
//                        if (Math.abs(getNumTimeStepsFromLocation(en.getKey(), ol.getKey()) - ol.getValue()) <= 2) {
//                            //state.printlnSynchronized("num steps = " + getNumTimeStepsFromLocation(en.getKey(), ol.getKey()) + " num i counted = " + ol.getValue());
//                            closest = ol.getKey();
//                            break;
//                        }
                        if (closest == null || en.getKey().distance(ol.getKey()) < en.getKey().distance(closest)) {
                            closest = ol.getKey();
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



        return super.getAvailableTask();
    }


    @Override
    double getUtility(Task t) {
        double confidence;
        double maxVal = 1.0;
        for (Map.Entry<Double2D, Integer> en: agentLocations.entrySet()) {
            // TODO: Need to learn the speed of each of the agents
            double val = (t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + tTable.getQValue(t.getJob().getJobType(), 0) + en.getValue()) * state.getIncrement()) /  (getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + tTable.getQValue(t.getJob().getJobType(), 0));
            if (val > maxVal) {
                maxVal = val;
            }
        }
        for (Map.Entry<Double2D, Integer> en: lastSeenLocation.entrySet()) {
            // TODO: I think that this should scale with how long it has been since they have been seen so as to deal with dieing agents
            double val = (t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + tTable.getQValue(t.getJob().getJobType(), 0) + en.getValue()) * state.getIncrement()) /  (getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + tTable.getQValue(t.getJob().getJobType(), 0));
            if (val > maxVal) {
                maxVal = val;
            }
        }


        confidence = 0.9 * (1.0 / maxVal) + 0.1 * pTable.getQValue(t.getNeighborhood().getId(), 0);
        //state.printlnSynchronized("Confidence = " + confidence);

        double util =  (confidence * (t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        return util;
    }
}
