package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 * The agents come here to get the available tasks.
 * Created by drew on 2/20/17.
 */
public class Bondsman implements Steppable {
    private static final long serialVersionUID = 1;

    MTRP state;

    public Bondsman(MTRP state) {
        this.state = state;
    }

    public void step(SimState simState) {
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){
            ((Task)task).incrementBounty();
            ((Task)task).incrementTimeNotFinished();
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


    public double getTotalAverageTime() {
        int totalTime = 0;
        int totalCount = 0;
        /*
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){
            totalTime += ((Task) task).getTimeNotFinished();
        }

        return (double) totalTime / (double) state.getTaskPlane().getAllObjects().toArray().length;
        */

        for (Neighborhood n : state.neighborhoods ){
            totalTime += n.totalTime;
            totalCount += n.count;
            // reset
//            n.totalTime = 0;
//            n.count = 0;
        }
        if (totalCount == 0)
            return 0;
        return (double) totalTime / (double) totalCount;

    }

    public double getTotalTime() {
        int totalTime = 0;

        for (Neighborhood n : state.neighborhoods ){
            totalTime += n.totalTime;

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

    public double getCount() {
        double count = 0.0;
        for (Neighborhood n : state.neighborhoods) {
            count += n.count;
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



}
