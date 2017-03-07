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


    public double getTotalTime() {
        double totalTime = 0.0;
        for (Object task: state.getTaskPlane().getAllObjects().toArray() ){
            totalTime += ((Task)task).getTimeNotFinished();
        }
        return totalTime;
    }
}
