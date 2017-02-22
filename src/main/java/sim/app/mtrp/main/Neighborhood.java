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

    MTRP mtrp;
    int id;

    Double2D meanLocation;
    Task tasks[];




    public Neighborhood(MTRP state, int id) {
        this.mtrp = state;
        this.id = id;

        // first set the mean location for the neighborhood this will always be within the bounds of the simulation size
        meanLocation = new Double2D(state.random.nextDouble(true,true)*state.simWidth, state.random.nextDouble(true,true)*state.simHeight);
        // then generate the initial tasks locations
        tasks = new Task[state.getMaxNumTasksPerNeighborhood()];
        int numTasks = state.random.nextInt(state.getMaxNumTasksPerNeighborhood()) + 1;
        for (int i = 0; i < numTasks; i++) {
            tasks[i] = new Task(this, state, state.getMaxNumTasksPerNeighborhood()*id + i);
            // add it to the continuous2d
            state.getTaskPlane().setObjectLocation(tasks[i], tasks[i].getLocation());

        }


    }


    public void step(SimState simState) {
        // here we decide if we create a new task and we cleanup the finished ones


    }

    public Double2D getMeanLocation() {
        return meanLocation;
    }


}
