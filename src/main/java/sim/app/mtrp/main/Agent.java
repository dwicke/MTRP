package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;


/**
 * Created by drew on 2/20/17.
 */
public abstract class Agent implements Steppable {
    private static final long serialVersionUID = 1;

    protected MTRP state;
    int id;
    protected double fuelCapacity;
    protected double curFuel;
    protected double bounty;
    protected int resourcesQuantities[];
    protected int curTotalNumResources;
    protected Job curJob;
    boolean amWorking = false;
    Double2D curDestination;
    protected Double2D curLocation;
    boolean needResources;
    double fuelEpsilon = 2; // min amount of fuel
    Depo curDepo;



    public Agent(MTRP state, int id) {
        this.state = state;
        this.id = id;
        resourcesQuantities = new int[state.getNumResourceTypes()];
        bounty = state.getStartFunds();
        curFuel = state.getFuelCapacity();
        fuelCapacity = state.getFuelCapacity();
        curTotalNumResources = 0;
        // pick a random depo and start there
        Depo startDepo = state.getDepos()[state.random.nextInt(state.getDepos().length)];
        curLocation = new Double2D(startDepo.location.getX(), startDepo.location.getY());
        curDestination = new Double2D(curLocation.getX(), curLocation.getY());
        curDepo = startDepo;
        state.getAgentPlane().setObjectLocation(this, curLocation);
    }


    public void step(SimState simState) {

        // basically we can do one of three things during our step
        // 1. buy/sell resources
        // 2. travel somewhere
        // 3. work on a job
        // and we always pick a destination

        boolean didAction = buySellResources();
        // I need to know where I should go
        pickDestination();
        if (!didAction)
            didAction = travel();
        if (!didAction)
            work();


    }

    public boolean buySellResources() {
        boolean didAction = false;
        // am I at a depo?
        Depo nearestDepo = getClosestDepo();
        double dist = getNumTimeStepsFromLocation(nearestDepo.location);


        if (dist == 0.0) {
            // I am! so check if i need fuel or resources
            if (curFuel < fuelCapacity) {
                // so buy fuel, note that fuel price is 1-to-1 cost
                // buy whatever I can
                double fuelBought = Math.min(bounty, fuelCapacity - curFuel);
                bounty -= fuelBought;
                curFuel += fuelBought;
                didAction = true;
            } else {
                // need to figure out how to buy resources correctly...
                didAction = false;
            }

        }

        return didAction;
    }


    public void pickDestination() {

        Depo nearestDepo = getClosestDepo();
        double dist = getNumTimeStepsFromLocation(nearestDepo.location);
        // if not working
        // first analyze my resources and decide if I need to go to a depo
        // only need resources if not at a task and predict that can't do any task with current resources
        needResources = ((curFuel - dist) < fuelEpsilon); // this will ensure we do not go outside the fuel range.

        if (!amWorking && needResources) {
            // resources take priority over traveling to a task
            curDestination = nearestDepo.location;
            curJob = null; // not going after a job so free it up

        } else if (!amWorking /* && (curJob == null || !curJob.getIsAvailable())*/) {
            Task nextTask = getAvailableTask();

            if (nextTask == null) {
                // this means that within my range i can't actually get to any tasks that are available
                // so go to the depo
                curDestination = nearestDepo.location;
                curJob = null;
            } else {
                curDestination = nextTask.getLocation();
                curJob = nextTask.job;
            }
        }


    }

    public abstract Task getAvailableTask();


    public Depo getClosestDepo() {
        Depo[] depos = state.getDepos();
        Depo closestWithinRange = null;
        double curMinDist = Double.MAX_VALUE;

        for (Depo d : depos) {
            double dist = getNumTimeStepsFromLocation(d.location);

            if (dist <= this.curFuel && dist < curMinDist) {
                curMinDist = dist;
                closestWithinRange = d;
            }
        }
        if (closestWithinRange == null) {
            for (Depo d : depos) {
                double dist = getNumTimeStepsFromLocation(d.location);
                state.printlnSynchronized("curfuel = " + curFuel + " dist = " + dist);
            }
        }

        return closestWithinRange;

    }


    public Task getClosetAvailableTask() {
        Task[] tasks = state.getBondsman().getAvailableTasks();
        // so now pick the nearest one and go for it!
        Task closestWithinRange = null;
        double curMinDist = Double.MAX_VALUE;


        for (Task t : tasks) {
            double dist = getNumTimeStepsFromLocation(t.location);
            if (dist < this.curFuel && dist < curMinDist) {
                curMinDist = dist;
                closestWithinRange = t;
            }
        }
        if (closestWithinRange != null) {
            curJob = closestWithinRange.job;
            curDestination = closestWithinRange.getLocation();
        }
        return closestWithinRange;
    }

    public Task[] getAvailableTasksInRange() {
        Task[] tasks = state.getBondsman().getAvailableTasks();
        if (tasks.length == 0) {
            state.printlnSynchronized("NO TASKS!");
        }
        // so now pick the nearest one and go for it!
        Bag closestWithinRange = new Bag();


        for (Task t : tasks) {
            double dist = getNumTimeStepsFromLocation(t.location);
            if (dist < this.curFuel) {
                closestWithinRange.add(t);
            }
        }

        return (Task[]) closestWithinRange.toArray(new Task[closestWithinRange.size()]);
    }







    public boolean travel() {


        // example
        // 3-4-5 triangle
        // sqrt((3/5*.7)^2 + (4/5*.7)^2) = .7
        // therefore if I move (3/5*.7) in the x direction and (4/5*.7) in the y direction I will end up only going .7
        // so do that. essentially normalizing on the euclidean distance.
        if (curFuel > 0) {
            double dis = curLocation.distance(curDestination);
            if (Math.abs(dis) < state.stepsize) // might need to account for some error here eventually...
                return false; // don't move already at destination.
            double dx = curDestination.getX() - curLocation.getX();
            dx = dx / dis * state.getStepsize();

            double dy = curDestination.getY() - curLocation.getY();
            dy = dy / dis * state.getStepsize();

            curLocation = new Double2D(curLocation.getX() + dx, curLocation.getY() + dy);
            // now travel there!
            state.getAgentPlane().setObjectLocation(this, curLocation);
            curFuel--;
            return true;
        }
        else{
            state.printlnSynchronized("Agent id = " + id + " out of fuel, so dead in water...");
            return false;
        }


    }


    public void work() {


        if (amWorking) {
            // then continue to work
            // see if done the task
            if (curJob.doWork()) {
                amWorking = false;
                bounty += curJob.getCurrentBounty();
                curJob.finish();
                curJob = null;
            }

        } else {
            // check if at task?
            if (curJob != null) {
                double dist = getNumTimeStepsFromLocation(curJob.task.getLocation());
                if (Math.abs(dist) < state.stepsize) {
                    // then I'm at the task!
                    curJob.claimWork(this);
                    amWorking = true;
                }
            }
        }

    }

    protected int getNumTimeStepsFromLocation(Double2D dest) {
        return (int) Math.floor((curLocation.distance(dest))/state.stepsize);
    }


    public double getCurFuel() {
        return curFuel;
    }

    public boolean isAmWorking() {
        return amWorking;
    }


    public Job getCurJob() {
        return curJob;
    }
}
