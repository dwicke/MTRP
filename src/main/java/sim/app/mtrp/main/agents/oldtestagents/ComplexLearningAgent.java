package sim.app.mtrp.main.agents.oldtestagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.LearningAgentWithJumpship;
import sim.app.mtrp.main.agents.Valuators.AgentLocationPredictor;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by drew on 5/30/17.
 */
public class ComplexLearningAgent extends LearningAgentWithJumpship {

    AgentLocationPredictor alp;

    public ComplexLearningAgent(MTRP state, int id) {

        super(state, id);
        alp = new AgentLocationPredictor(state);
    }

    public Task getAvailableTask() {

        alp.updatePositionPrediction(curJob, tTable);
        return super.getAvailableTask();
    }

    /*
    @Override
    double getUtility(Task t) {
        double confidence;
        double maxVal = Double.MAX_VALUE;
        for (Map.Entry<Double2D, Integer> en: agentLocations.entrySet()) {

            // TODO: Need to learn the speed of each of the agents
            double val = getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + en.getValue();
            if (val < maxVal) {
                maxVal = val;
            }
        }

        for (Map.Entry<Double2D, Integer> en: lastSeenLocation.entrySet()) {
            // TODO: I think that this should scale with how long it has been since they have been seen so as to deal with dieing agents
            //state.printlnSynchronized("Ttable = " + tTable.getQValue(t.getJob().getJobType(), 0) + " agent id = " + id);
            double val = getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + en.getValue();// + en.getValue is not the best... would be better to learn this...

            if (val < maxVal) {
                maxVal = val;
            }
        }

        if (id == 0) {
            state.printlnSynchronized("MaxVal = " + maxVal + " my val = " + getNumTimeStepsFromLocation(t.getLocation()));
        }
        // TODO: I also should scale what i use from each of the settings in order to work better in settings where the environment can easily be split
        if (maxVal == Double.MAX_VALUE) {
            return 1.0 / getNumTimeStepsFromLocation(t.getLocation());
        }
        // so this value is 1 if we are colocated for all of the tasks
        // that are closest to me
        // then for all other tasks that are closer to other agents
        // that value is less than one
        // so, i will end up going after the same task as the agent
        // that i am colocated with.
        // therefore, this is poorly designed.
        //
        confidence = maxVal / getNumTimeStepsFromLocation(t.getLocation());
        //state.printlnSynchronized("Confidence = " + confidence);

        double util =  (confidence); //* (t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        return util;
    }
*/

/*
    @Override
    double getUtility(Task t) {
        double confidence;
        double maxVal = Double.MAX_VALUE;
        for (Map.Entry<Double2D, Integer> en: agentLocations.entrySet()) {

            // TODO: Need to learn the speed of each of the agents
            double val = getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + en.getValue();
            if (val < maxVal) {
                maxVal = val;
            }
        }

        for (Map.Entry<Double2D, Integer> en: lastSeenLocation.entrySet()) {
            // TODO: I think that this should scale with how long it has been since they have been seen so as to deal with dieing agents
            //state.printlnSynchronized("Ttable = " + tTable.getQValue(t.getJob().getJobType(), 0) + " agent id = " + id);
            double val = getNumTimeStepsFromLocation(t.getLocation(), en.getKey()) + en.getValue();// + en.getValue is not the best... would be better to learn this...

            if (val < maxVal) {
                maxVal = val;
            }
        }

        // TODO: I also should scale what i use from each of the settings in order to work better in settings where the environment can easily be split
        if (maxVal == Double.MAX_VALUE) {
            return 1.0 / getNumTimeStepsFromLocation(t.getLocation());
        }
        confidence =  0.75 * maxVal / getNumTimeStepsFromLocation(t.getLocation()) + 0.25 * pTable.getQValue(t.getNeighborhood().getId(), 0);
        //state.printlnSynchronized("Confidence = " + confidence);

        double util =  (confidence * (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        return util;
    }
*/

/*

    @Override
    double getUtility(Task t) {
        double confidence;
        double maxVal = 1.0;
        for (Map.Entry<Task, Integer> en: alp.getAgentLocations().entrySet()) {

            // TODO: Need to learn the speed of each of the agents
            double val = (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0) + en.getValue()) * state.getIncrement()) /  (getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0) + en.getValue());
            if (val > maxVal) {
                maxVal = val;
            }
        }

        for (Map.Entry<Task, Integer> en: alp.getLastSeenLocation().entrySet()) {
            // TODO: I think that this should scale with how long it has been since they have been seen so as to deal with dieing agents
            //state.printlnSynchronized("Ttable = " + tTable.getQValue(t.getJob().getJobType(), 0) + " agent id = " + id);
            double val =  (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement()) /  (getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));
            if (val > maxVal) {
                maxVal = val;
            }
        }

        // TODO: I also should scale what i use from each of the settings in order to work better in settings where the environment can easily be split
        //confidence = 0.9 * ( maxVal / getNumTimeStepsFromLocation(t.getLocation())); //+ 0.1 * pTable.getQValue(t.getNeighborhood().getId(), 0);
        confidence = 1 / maxVal;
        //state.printlnSynchronized("Confidence = " + confidence);

        double util =  (confidence * (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        return util;
    }
    */

    @Override
    public double getUtility(Task t) {

        int count = 0;
        for (Map.Entry<Task, Integer> en: alp.getAgentLocations().entrySet()) {
            if (id == 0)
                state.printlnSynchronized("agent location = " + en.getKey().getId() + " count = " + count + " distance = " + getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()));
            count++;
            if (getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()) < 10) {
                return 0.0;
            }
        }

        /*
        for (Map.Entry<Task, Integer> en: alp.getLastSeenLocation().entrySet()) {
            // TODO: I think that this should scale with how long it has been since they have been seen so as to deal with dieing agents
            //state.printlnSynchronized("Ttable = " + tTable.getQValue(t.getJob().getJobType(), 0) + " agent id = " + id);
            if (getNumTimeStepsFromLocation(t.getLocation(), en.getKey().getLocation()) - en.getValue() < 2) {
                return 0.0;
            }
        }*/

        // TODO: I also should scale what i use from each of the settings in order to work better in settings where the environment can easily be split

        double util =  (pTable.getQValue(t.getNeighborhood().getId(), 0) * (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        return util;
    }

}