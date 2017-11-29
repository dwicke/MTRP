package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

import java.util.TreeMap;

/**
 * The agents come here to get the available tasks.
 * Created by drew on 2/20/17.
 */
public class Bondsman implements Steppable {
    private static final long serialVersionUID = 1;

    MTRP state;

    int numStale = 0;
    int lengthStale = 0;
    int lengthNotStale = 0;

    Bag currentBatch, nextBatch;


    public Bondsman(MTRP state) {

        this.state = state;
        currentBatch = new Bag();
        nextBatch = new Bag();
    }

    public void step(SimState simState) {
        numStale = 0;
        //Bag tasksToRemove = new Bag();
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){

            ((Task)task).incrementBounty();
            ((Task)task).incrementTimeNotFinished();
            if (((Task)task).getTimeNotFinished() >= state.getDeadline()) {
                numStale++;
                //tasksToRemove.add(task);
            }

        }
        updateCurrentBatch();


        /*
        for (Object task: tasksToRemove) {
            Task t = (Task) task;
            for (Object commited: t.getCommittedAgents()) {
                Agent a = (Agent) commited;
                a.curJob = null;
            }
            t.setFinished();
        }
        */
        if (numStale > 0) {
            lengthStale++;
            lengthNotStale = 0;
        } else {
            lengthStale = 0;
            lengthNotStale++;
        }

    }

    public Task[] getAvailableTasks() {
        Bag availTasks = new Bag();
        for (Object task: state.getTaskPlane().getAllObjects().toArray()){
            if (((Task)task).getIsAvailable()) {
                availTasks.add(task);
            }
        }
        return (Task[]) availTasks.toArray(new Task[availTasks.size()]);
    }

    public Task[] getAllTasks() {
        Bag availTasks = new Bag();
        for (Object task: state.getTaskPlane().getAllObjects().toArray()){
            availTasks.add(task);
        }
        return (Task[]) availTasks.toArray(new Task[availTasks.size()]);
    }

    public Bag getNewTasks() {
        Bag availTasks = new Bag();
        for (Neighborhood neighborhood: state.getNeighborhoods()){
            Task t = neighborhood.getLatestTask();
            if (t != null)
                availTasks.add(t);
        }
        return availTasks;
    }

    public double getVarianceTime() {


//        double inner = 0;
//        double avg = getTotalAverageTime();
//        double count = 0;
//        for(Neighborhood n : state.neighborhoods) {
//            for (Double d : n.getWaitingTimes()) {
//                inner += Math.pow((d - avg),2);
//                if (d > avg) {
//                    //state.printlnSynchronized("Value of d = " + d + " avg = " + avg);
//                }
//                count++;
//            }
//        }
//
//        double var0 = (1.0 / (count - 1)) * inner;

        double avg = getTotalAverageTime();
        double count = getCount();
        double waitSquaredTotal = 0.0;
        for (Neighborhood n : state.neighborhoods ){
            waitSquaredTotal += n.waitsquared;
        }

        //state.printlnSynchronized("waitsquared = " + waitSquaredTotal + " avg = " + avg);
        double var1 = 1.0 / (count - 1) * (waitSquaredTotal - 2.0 * avg * getTotalTime() + count * avg * avg);
        //double var2 = (count / (count - 1))*(waitSquaredTotal / count - ((avg*avg) / (count * count)));
        //state.printlnSynchronized("Var new way = " + var1 + " var long way = " + var0);
        return var1;
        //return (count / (count - 1))*(waitSquaredTotal / count - ((avg*avg) / (count * count)));
    }


    public double getTotalAverageTime() {
        double totalTime = 0;
        double totalCount = 0;
        /*
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){
            totalTime += ((Task) task).getTimeNotFinished();
        }

        return (double) totalTime / (double) state.getTaskPlane().getAllObjects().toArray().length;
        */

        for (Neighborhood n : state.neighborhoods ){
            for (int i = 0; i < state.numJobTypes; i++) {
                totalTime += n.totalTime[i];
                totalCount += n.count[i];
            }
            // reset
//            n.totalTime = 0;
//            n.count = 0;
        }
        if (totalCount == 0)
            return 0;
        return totalTime / totalCount;

    }

    public double getJainFairness() {
        double totalTime = 0;
        double totalCount = 0;
        double totalSquaredTime = 0;
        /*
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){
            totalTime += ((Task) task).getTimeNotFinished();
        }

        return (double) totalTime / (double) state.getTaskPlane().getAllObjects().toArray().length;
        */

        for (Neighborhood n : state.neighborhoods ){
            for (int i = 0; i < state.numJobTypes; i++) {
                totalSquaredTime += n.totalTime[i] * n.totalTime[i];
                totalTime += n.totalTime[i];
                totalCount += n.count[i];
            }
            // reset
//            n.totalTime = 0;
//            n.count = 0;
        }
        if (totalCount == 0)
            return 0;
        return Math.pow(totalTime, 2) / (totalSquaredTime * totalCount);

    }



    public double getTotalTime() {
        int totalTime = 0;

        for (Neighborhood n : state.neighborhoods ){
            for (int i = 0; i < state.numJobTypes; i++) {
                totalTime += n.totalTime[i];
            }

        }

        return (double) totalTime;
    }

    public double getTotalBounty() {
        double totalBounty = 0.0;
        for (Neighborhood n : state.neighborhoods) {
            totalBounty += n.totalBounty;
        }
        return totalBounty;
    }

    public double getAverageBaseBounty() {
        double totalBaseBounty = 0.0;
        double count = 0.0;
        for (Neighborhood n : state.neighborhoods) {
            totalBaseBounty += n.getTotalBaseBounty();
            count += n.getTotalNumTasksGenerated();
        }
        return totalBaseBounty / count;
    }

    public double getAverageBountyRate() {
        double totalBountyRate = 0.0;
        double count = 0.0;
        for (Neighborhood n : state.neighborhoods) {
            totalBountyRate += n.getTotalBountyRate();
            count += n.getTotalNumTasksGenerated();
        }
        return totalBountyRate / count;
    }


    public double getCount() {
        double count = 0.0;
        for (Neighborhood n : state.neighborhoods) {
            for (int i = 0; i < state.numJobTypes; i++) {
                count += n.count[i];
            }
        }
        return count;
    }
    public int getTotalTasksGenerated() {
        int count = 0;
        for (Neighborhood n : state.neighborhoods) {
            count += n.getTotalNumTasksGenerated();
        }
        return count;
    }

    public double getTotalOutstandingBounty() {
        double totalBounty = 0.0;
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){
            totalBounty += ((Task) task).getBounty();
        }
        return totalBounty;
    }

    public int getNumStale() {
        return numStale;
    }

    public int getLengthStale() {
        return lengthStale;
    }

    public int getLengthNotStale() {
        return lengthNotStale;
    }

    public void updateCurrentBatch() {
        if (currentBatch.size() == 0 && nextBatch.size() == 0) {
            // then add all new tasks to the current batch
            currentBatch = getNewTasks();
        }else if (currentBatch.size() == 0 && nextBatch.size() > 0) {
            currentBatch = nextBatch;
            nextBatch = new Bag();
        } else if (currentBatch.size() > 0) {
            nextBatch.addAll(getNewTasks());
        }

    }


    public Bag getCurrentBatch() {
        return currentBatch;
    }

    public void removeFromCurrentBatch(Task t) {
        currentBatch.remove(t);
    }
}
