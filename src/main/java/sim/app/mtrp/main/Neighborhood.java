package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;

/**
 * Created by drew on 2/20/17.
 */
public class Neighborhood implements Steppable{
    private static final long serialVersionUID = 1;

    MTRP state;
    int id;

    Double2D meanLocation;
    Bag tasks;

    int totalTime, count;




    public Neighborhood(MTRP state, int id) {
        this.state = state;
        this.id = id;

        // first set the mean location for the neighborhood this will always be within the bounds of the simulation size
        meanLocation = new Double2D(state.random.nextDouble(true,true)*state.simWidth, state.random.nextDouble(true,true)*state.simHeight);
        // then generate the initial tasks locations
        tasks = new Bag();
    }


    public void step(SimState simState) {
        // here we decide if we create a new task
        generateTasks();

    }

    public Double2D getMeanLocation() {
        return meanLocation;
    }

    @Override
    public String toString() {
        return "id = " + id + " mean (" + meanLocation.getX() + ", " + meanLocation.getY() + ")" + " numTasks = " + tasks.size();
    }

    public void generateTasks() {
        while (state.random.nextDouble() <  (1.0 / (double) state.timestepsTilNextTask)) {
            // generate a new task
            tasks.add(new Task(this, state));
        }

    }

    public void finishedTask(Task task) {
        totalTime += task.timeNotFinished;
        count++;
        tasks.remove(task);
    }

    public int getId() {
        return id;
    }
}
