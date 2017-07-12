package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
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

        double confidence = 1.0;
        double numSignaled = 0;
        for (int i = 0; i < state.numAgents; i++) {
            Agent a = state.getAgents()[i];
            if (i != id && t.getJob().isSignaled(state.getAgents()[i])) {
                confidence *= agentSuccess.getQValue(i, 0);
                numSignaled++;
            } else if (i != id && a.getCurJob() != null && a.getCurJob().isSignaled(a) && a.getCurJob().getTask().getLocation().distance(t.getLocation()) < curLocation.distance(t.getLocation()))
            {
                confidence *= agentSuccess.getQValue(i, 0);
                numSignaled++;
            }
        }

        // i need to weight the information
        // i need to account for the single neighborhood case... this is not as good if there is a single neighborhood
        // as then we don't want to consider the pTable at all.
        double weight = numSignaled / (double) state.numAgents;

        confidence = weight * confidence + (1 - weight) * pTable.getQValue(t.getNeighborhood().getId(), 0);

        double totalTime = (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));

        return confidence * (t.getBounty() / totalTime) + confidence * t.getJob().getBountyRate() - (getCost(t) / totalTime);
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
