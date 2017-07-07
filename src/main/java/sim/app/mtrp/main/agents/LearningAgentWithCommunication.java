package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Bag;
import sim.util.Double2D;

/**
 * Created by drew on 5/4/17.
 */
public class LearningAgentWithCommunication extends LearningAgentWithJumpship {

    QTable agentSuccess;
    QTable meanXLocation;
    QTable meanYLocation;
    Task[] dummy;

    //what is the average distance I will jumpship
    // so basically if the distance to the task is less than this then I will signal?
    double totalJumpshipDist;
    int numJumpships = 0;

    public LearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);
        agentSuccess = new QTable(state.getNumAgents(), 1, .99, .1,state.random);
        meanXLocation = new QTable(state.getNumNeighborhoods(), 1, .75, .1, 1.0);
        meanYLocation = new QTable(state.getNumNeighborhoods(), 1, .75, .1, 1.0);
//        dummy = new Task[state.getNumNeighborhoods()];
//        for (int i =0; i < state.getNumNeighborhoods(); i++) {
//            // for each neighborhood we have a dummy task location
//            dummy[i] = new Task(state.getNeighborhoods()[i], state, state.getNeighborhoods()[i].getMeanLocation());
//            dummy[i].setDummy(true);
//        }
    }

//    @Override
//    public Task getAvailableTask(Bag tasks) {
//        Task t = super.getAvailableTask(tasks);
//        if (state.getBondsman().getAvailableTasks().length == 0) {
//            //state.printlnSynchronized(" No tasks!");
//
//        }
//        return t;
//    }
//
//    @Override
//    public Task getAvailableTask() {
//        Task [] tasks = state.getBondsman().getAvailableTasks();
//        Bag tasksWithDummy = new Bag(tasks);
//        for (int i =0; i < state.getNumNeighborhoods(); i++) {
//            // for each neighborhood we have a dummy task location
//            dummy[i].setLocation(new Double2D(meanXLocation.getQValue(i, 0),meanYLocation.getQValue(i, 0)));
//        }
//        tasksWithDummy.addAll(dummy);
//        return getAvailableTask(getTasksWithinRange(tasksWithDummy));
//        //return getAvailableTask(getTasksWithinRange(state.getBondsman().getNewTasks()));
//    }

    @Override
    public void learn(double reward) {

        super.learn(reward);
        if (curJob == null || curJob.getCurWorker() == null) {
            state.printlnSynchronized("CurJob = " + curJob + "reward = " + reward + " am working = " + amWorking);
            if (curJob != null) {
                state.printlnSynchronized("Cur worker is null");
            }
        }

        if (reward == 1.0) {
            // I have completed the task!
            // so learn the mean location
            meanXLocation.update(curJob.getTask().getNeighborhood().getId(), 0, curJob.getTask().getLocation().getX());
            meanYLocation.update(curJob.getTask().getNeighborhood().getId(), 0, curJob.getTask().getLocation().getY());
        }


        agentSuccess.update(curJob.getCurWorker().getId(), 0, reward);
        agentSuccess.oneUpdate(oneUpdateGamma);
    }

    @Override
    public double getUtility(Task t) {
        if (t.getJob().isSignaled(this) || t.getJob().noSignals() ) {
            return super.getUtility(t);
        } else {
            if (!t.getJob().noSignals()) {
                //state.printlnSynchronized("Time step" + state.schedule.getSteps() + "Job id " + t.getJob().getId() + " is signaled but not by me " + getId());
            }
            double confidence = 1.0;
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

            // bounty hunters do worse than auctions when the rate at which the tasks are being generated is such that
            // not all of the agents have something to do.  This would be a light load case.
            // this is because they think it is worth there while to go chasing after the task...
            if (state.numAgents == state.getNeighborhoods().length) {
                confidence = pTable.getQValue(t.getNeighborhood().getId(), 0);
            } else if (state.numAgents < state.getNeighborhoods().length) {
                double weight = Math.max(0, ((double)  state.getNeighborhoods().length - state.numAgents) / (double) state.getNeighborhoods().length);
                confidence = weight * confidence + (1 - weight) * pTable.getQValue(t.getNeighborhood().getId(), 0);
            } else if ( state.getNeighborhoods().length > 1) {
                double weight = Math.max(0, ((double) state.numAgents - state.getNeighborhoods().length) / (double) state.numAgents);
                confidence = weight * confidence + (1 - weight) * pTable.getQValue(t.getNeighborhood().getId(), 0);
            }



            // how close to the mean location of the tasks that I'm interested in persuing is it?
//            double taskToMeandist = t.getLocation().distance(meanXLocation.getQValue(t.getNeighborhood().getId(), 0), meanYLocation.getQValue(t.getNeighborhood().getId(), 0));
//            double taskToMeDist = t.getLocation().distance(curLocation);
//            double meToMeanDist = curLocation.distance(meanXLocation.getQValue(t.getNeighborhood().getId(), 0), meanYLocation.getQValue(t.getNeighborhood().getId(), 0));
//
//            confidence *= (Math.abs(taskToMeDist - meToMeanDist)) / taskToMeandist;



            //double util =  ( confidence *  (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

            double costRate = (1-confidence) * (getCost(t) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)));

            double util =  -costRate + ( confidence *  (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement())) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));


            //double util =  ( confidence *  (t.getBounty()+ getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) );
            //state.printlnSynchronized("Task is dummy = " + t.isDummy() + " confidence = " + confidence + " util = " + util);

            return util;
            //return 0; // need to change this.
        }
    }

    @Override
    public boolean travel() {
        boolean hasTraveled = super.travel();

        double signalDist = 0;//state.getThresholdToSignal();
        if (numJumpships > 0) {
            signalDist = totalJumpshipDist / numJumpships;
           // state.printlnSynchronized(" agent id = " + id + " signal dist = " + signalDist);
        }

        if (hasTraveled == true && amWorking == false && curJob != null && curJob.getTask().getLocation().distance(this.curLocation) <= signalDist) {
            curJob.signal(this);
        }
        return hasTraveled;
    }

    @Override
    public Task handleJumpship(Task bestT) {
       // if (curJob.isSignaled(this)) {

        curJob.unsignal(this);

        totalJumpshipDist += getNumTimeStepsFromLocation(curJob.getTask().getLocation(), curLocation);
        numJumpships++;
            //pTable.update(curJob.getTask().getNeighborhood().getId(), 0, 0.0);
             //pTable.oneUpdate(oneUpdateGamma);

        //}
        return super.handleJumpship(bestT);
    }

    @Override
    public String toString() {
        return super.toString() + " " + pTable.getQTableAsString();
    }
}
