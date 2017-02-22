package sim.app.mtrp.main;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

/**
 * Created by drew on 2/20/17.
 */
public class Task {
    private static final long serialVersionUID = 1;

    Neighborhood neighborhood;
    MTRP state;
    int id; // unique id
    MutableDouble2D location;
    Job job;

    public Task(Neighborhood neighborhood, MTRP state, int id) {
        this.id = id;
        this.neighborhood = neighborhood;
        this.state = state;
        double x = state.random.nextGaussian() * state.taskLocStdDev + neighborhood.meanLocation.getX();
        double y = state.random.nextGaussian() * state.taskLocStdDev + neighborhood.meanLocation.getY();
        location = new MutableDouble2D(x,y);

        // now generate the job
        job = new Job(this, state, id);// can easily make this an array then later...

    }


    public Double2D getLocation() {
        return new Double2D(location.getX(), location.getY());
    }


    public void incrementBounty() {
        job.incrementBounty();
    }
}