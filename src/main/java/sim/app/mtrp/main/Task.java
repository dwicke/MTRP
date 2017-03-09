package sim.app.mtrp.main;

import sim.util.Double2D;
import sim.util.MutableDouble2D;

/**
 * Created by drew on 2/20/17.
 */
public class Task {
    private static final long serialVersionUID = 1;

    protected static int taskIDGenerator = 0;

    Neighborhood neighborhood;
    MTRP state;
    int id; // unique id
    Double2D location;
    Job job;
    int timeNotFinished = 0;

    public Task(Neighborhood neighborhood, MTRP state) {
        this.id = taskIDGenerator;
        taskIDGenerator++;// increment every new task.
        this.neighborhood = neighborhood;
        this.state = state;
        double x = state.random.nextGaussian() * state.taskLocStdDev + neighborhood.meanLocation.getX();
        double y = state.random.nextGaussian() * state.taskLocStdDev + neighborhood.meanLocation.getY();
        location = new Double2D(x,y);
        // add it to the continuous2d
        state.getTaskPlane().setObjectLocation(this, location);
        // now generate the job
        job = new Job(this, state, id);// can easily make this an array then later...

    }



    public Double2D getLocation() {
        return location;
    }


    public void incrementBounty() {
        job.incrementBounty();
    }

    public double getBounty() {
        return job.getCurrentBounty();
    }

    public int getId() {
        return id;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public boolean getIsAvailable() {
        return job.getIsAvailable();
    }

    public void setFinished() {
        state.getTaskPlane().remove(this);
        neighborhood.finishedTask(this);
    }

    public void incrementTimeNotFinished() {
            timeNotFinished++;
    }
    public int getTimeNotFinished() {
        return timeNotFinished;
    }
}
