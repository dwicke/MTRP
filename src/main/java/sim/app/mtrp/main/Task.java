package sim.app.mtrp.main;

import sim.util.Bag;
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
    Bag committedAgents;
    boolean finished = false;
    Bag blackList; // agents who are not allowed to go after this task
    boolean dummy = false;

    public Task(Neighborhood neighborhood, MTRP state, Double2D location) {
        this.id = taskIDGenerator;
        taskIDGenerator++;// increment every new task.
        this.neighborhood = neighborhood;
        this.state = state;

        this.location = location;
        // add it to the continuous2d
        state.getTaskPlane().setObjectLocation(this, this.location);

        //state.getTaskPlane().getNeighborsWithinDistance()

        //state.printlnSynchronized("Task id: " + id + " location " + location + " discretized: " + state.getTaskPlane().discretize(location,20));
        //state.printlnSynchronized("Task id: " + id + " location " + location + " right of y: " + ((location.getX() - neighborhood.meanLocation.getX()) > 0 ? true : false) + " below x: " + ((location.getY() - neighborhood.meanLocation.getY()) > 0 ? true : false));




        // now generate the job
        //job = state.jobPrototypes[0].buildJob(state, this, id);
        job = state.jobPrototypes[state.random.nextInt(state.numJobTypes)].buildJob(state, this, id);
//        if (state.hasRandomness) {
//            job = state.jobPrototypes[state.random.nextInt(state.numJobTypes - state.numEmergentJobTypes)].buildJob(state, this, id);
//        }else {
//
//        }
        committedAgents = new Bag();
        blackList = new Bag();
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public void setJob(Job job) {
        this.job = job;
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
        finished = true;
    }

    public boolean getFinished() {
        return finished;
    }

    public void incrementTimeNotFinished() {
            timeNotFinished++;
    }
    public int getTimeNotFinished() {
        return timeNotFinished;
    }

    public Bag getCommittedAgents() {
        return committedAgents;
    }
    public void amCommitted(Agent a) {
        if (!committedAgents.contains(a))
            committedAgents.add(a);
    }

    public void decommit(Agent a) {
        committedAgents.remove(a);
    }

    public Bag getBlackList() {
        return blackList;
    }

    public void blacklistAgent(Agent ag) {
        blackList.add(ag);
    }

    public Job getJob() {
        return job;
    }

    public void setBaseBounty(double baseBounty) {
        job.setCurrentBounty(baseBounty);
    }

    public void setBountyRate(double bountyrate) {
        job.setBountyRate(bountyrate);
    }
    public double getBountyRate() {
        return job.getBountyRate();
    }

    public double getBaseBounty() { return job.getCurrentBounty();
    }




    public void setLocation(Double2D newLoc) {
        this.location = newLoc;
    }

    public boolean isDummy() {
        return dummy;
    }

    public MTRP getState() {
        return state;
    }
}
