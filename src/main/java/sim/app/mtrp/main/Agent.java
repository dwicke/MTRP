package sim.app.mtrp.main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;

import java.util.Map;
import java.util.TreeMap;


/**
 * Created by drew on 2/20/17.
 */
public abstract class Agent implements Steppable {
    private static final long serialVersionUID = 1;

    protected MTRP state;
    protected int id;
    protected double curFuel;
    protected double bounty;
    protected int resourcesQuantities[];
    protected int curTotalNumResources;
    protected Job curJob;
    protected boolean amWorking = false;
    Double2D curDestination;
    protected Double2D curLocation;
    boolean needResources;
    double fuelEpsilon = 2; // min amount of fuel
    Depo curDepo;
    int numTimeStepsWorking = 0;
    protected boolean died = false;
    double stepsize;
    double originalStepSize = -1;
    boolean slow = false;


    final Logger logger = (Logger) LoggerFactory.getLogger(Agent.class);

    public Agent() {}

    public Agent(MTRP state, int id) {
        this.state = state;
        this.id = id;
        resourcesQuantities = new int[state.getNumResourceTypes()];
        bounty = state.getStartFunds();
        curFuel = state.getFuelCapacity();
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

        if(slow && originalStepSize == -1) {
            if (id == 0) {
                state.printlnSynchronized("going slow");
            }
            originalStepSize = stepsize;
            stepsize = originalStepSize / 10.0;
        }else if (!slow && originalStepSize != -1) {
            stepsize = originalStepSize;
            originalStepSize = -1;

        }

        boolean didAction = buySellResources();
        // I need to know where I should go
        pickDestination();
        if (died) {
            return;
        }
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
            //state.printlnSynchronized("Agent id " + id + "is at a depo..." + dist + " agent location = " + curLocation + " depo location " + nearestDepo.location);
            didAction = buyResources(nearestDepo);
        }

        return didAction;
    }

    public boolean buyResources(Depo nearestDepo) {
        return buyFuel(nearestDepo) || buySellTaskResources(nearestDepo);

    }

    public boolean buyFuel(Depo nearestDepo) {
        // I am! so check if i need fuel or resources
        if (curFuel < state.getFuelCapacity() && bounty > 0) {
            // so buy fuel, note that fuel price is 1-to-1 cost
            // buy whatever I can
            double fuelBought = Math.min(bounty / nearestDepo.getFuelCost(), state.getFuelCapacity() - curFuel);
            nearestDepo.buyFuel(fuelBought);
            bounty -= fuelBought * nearestDepo.getFuelCost();
            curFuel += fuelBought;
            return true;
        }
        else {
            return false;
        }

    }

    public boolean buySellTaskResources(Depo nearestDepo) {
        return false;
    }


    public void pickDestination() {

        Depo nearestDepo = getClosestDepo();

        // if not working
        // first analyze my resources and decide if I need to go to a depo
        // only need resources if not at a task and predict that can't do any task with current resources
        needResources = checkNeedResources(nearestDepo);

        //state.printlnSynchronized("Agent = " + id + "cur fuel = " + curFuel +  " dist = " + dist + " needResources = " + needResources);
        if (!amWorking && needResources) {
            // resources take priority over traveling to a task
            curDestination = nearestDepo.location;
            decommitTask();

        } else {
            Task nextTask = getAvailableTask();

            if (nextTask == null) {
                // this means that within my range i can't actually get to any tasks that are available
                // so go to the depo
                curDestination = nearestDepo.location;
                decommitTask();

            } else {
                curDestination = nextTask.getLocation();
                decommitTask();
                curJob = nextTask.job;
                commitTask(nextTask);
            }
        }


    }

    public boolean checkNeedResources(Depo nearestDepo) {
        return checkNeedFuel(nearestDepo);
    }

    public boolean checkNeedFuel(Depo nearestDepo) {
        double dist = getNumTimeStepsFromLocation(nearestDepo.location);
        return ((curFuel - dist) <= fuelEpsilon); // this will ensure we do not go outside the fuel range due to error in floating point precision
    }

    public void commitTask(Task t) {}

    public void decommitTask() {
        curJob = null; // not going after a job so free it up
    }

    /**
     * @return
     */
    public abstract Task getAvailableTask();

    public Task[] getTasksWithinRangeAsArray(Task[] tasks) {
        Bag b = getTasksWithinRange(tasks);
        return (Task[]) b.toArray(new Task[b.size()]);
    }

    public Bag getTasksWithinRange(Task[] tasks) {
        //Task[] tasks = state.getBondsman().getAvailableTasks();
        if (tasks.length == 0) {
            //state.printlnSynchronized("NO TASKS!");
        }
        Bag closestWithinRange = new Bag();

        for (Task t : tasks) {
            double dist = getNumTimeStepsFromLocation(t.getLocation());
            Depo d = getClosestDepo(t.getLocation());
            if (d != null) {
                double distToDepo = getNumTimeStepsFromLocation(d.location, t.getLocation());
                if ((dist + distToDepo + fuelEpsilon + 1) < this.curFuel ) {
                    closestWithinRange.add(t);
                }
            }

        }
        return closestWithinRange;
    }

    public Bag getTasksWithinRange(Bag tasks) {
        //Task[] tasks = state.getBondsman().getAvailableTasks();

        Bag closestWithinRange = new Bag();

        for (int i =0; i < tasks.numObjs; i++) {
            Task t = (Task)tasks.get(i);
            double dist = getNumTimeStepsFromLocation(t.getLocation());
            Depo d = getClosestDepo(t.getLocation());
            if (d != null) {
                double distToDepo = getNumTimeStepsFromLocation(d.location, t.getLocation());
                if ((dist + distToDepo + fuelEpsilon + 1) < this.curFuel ) {
                    closestWithinRange.add(t);
                }
            }

        }
        return closestWithinRange;
    }



    public Depo getClosestDepo() {
        return getClosestDepo(curLocation);
    }

    public Depo getClosestDepo(Double2D loc) {
        Depo[] depos = state.getDepos();
        Depo closestWithinRange = null;
        double curMinDist = Double.MAX_VALUE;

        for (Depo d : depos) {
            double dist = getNumTimeStepsFromLocation(d.location, loc);

            if (dist <= this.curFuel && dist < curMinDist) {
                curMinDist = dist;
                closestWithinRange = d;
            }
        }
        /*if (closestWithinRange == null) {
            for (Depo d : depos) {
                double dist = getNumTimeStepsFromLocation(d.location, loc);
                state.printlnSynchronized("agent " + id + "curfuel = " + curFuel + " dist = " + dist);
            }
        }*/

        return closestWithinRange;

    }










    public boolean travel() {
        // example
        // 3-4-5 triangle
        // sqrt((3/5*.7)^2 + (4/5*.7)^2) = .7
        // therefore if I move (3/5*.7) in the x direction and (4/5*.7) in the y direction I will end up only going .7
        // so do that. essentially normalizing on the euclidean distance.
        if (curFuel > 0) {

            double SS = getStepSize();
            if (curJob != null && curJob.isSlow()) {
                SS /= 2;
            }
            double numTimeSteps = getNumTimeStepsFromLocation(curDestination);
            if (numTimeSteps == 0) // might need to account for some error here eventually...
                return false; // don't move already at destination.
            double dis = curLocation.distance(curDestination);
            double dx = curDestination.getX() - curLocation.getX();
            dx = (dx / dis) * SS;

            double dy = curDestination.getY() - curLocation.getY();
            dy = (dy / dis) * SS;

            //Double2D oldLoc = curLocation;
            curLocation = new Double2D(curLocation.getX() + dx, curLocation.getY() + dy);
            //state.printlnSynchronized("CurLocation for agent  " + id + " is = " + curLocation);
            //state.printlnSynchronized("Agent = " + id + " distance traveled = " + curLocation.distance(oldLoc));

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
            numTimeStepsWorking++;
            if (curJob.doWork()) {
                //state.printlnSynchronized("agent " + id + " Finished task id = " + curJob.getId());
                finishTask();
                numTimeStepsWorking = 0;
            }

        } else {
            // check if at task?
            if (curJob != null) {
                double dist = getNumTimeStepsFromLocation(curJob.task.getLocation());
                if (dist == 0) {
                    claimWork();
                    numTimeStepsWorking = 0;
                }
            }
        }

    }

    /**
     * can overide this to learn the resources needed for particular jobs
     */
    public void claimWork() {
        // then I'm at the task!
        amWorking = curJob.claimWork(this);
    }


    /**
     * Can overide this method to provide functionallity to learn
     */
    protected void finishTask() {
        amWorking = false;
        bounty += curJob.getCurrentBounty();
        curJob.finish();
        curJob = null;
    }


    public Task[] getNonCommittedTasksAsArray() {
        Bag b = getNonCommittedTasks();
        return (Task[]) b.toArray(new Task[b.size()]);
    }


    public Bag getNonCommittedTasks() {
        Bag closestWithinRange = getTasksWithinRange(state.getBondsman().getAvailableTasks());
        Task[] availTasks = (Task[]) closestWithinRange.toArray(new Task[closestWithinRange.size()]);//getAvailableTasksInRange();//state.bondsman.getAvailableTasks();
        Bag nonCommitedTasks = new Bag();
        for (Task t : availTasks) {
            if (t.getCommittedAgents().size() == 0) {
                nonCommitedTasks.add(t);
            }
        }
        return nonCommitedTasks;
    }

    public TreeMap<Double,Integer> getPercentageJobTypes(Task[] tasks) {

        TreeMap<Double, Integer> percentJobTypes = new TreeMap<Double, Integer>();
        TreeMap<Integer, Double> jobTypeToNumber = new TreeMap<Integer, Double>();
        for (Task t : tasks) {
            if (jobTypeToNumber.containsKey(t.getJob().getJobType())) {
                jobTypeToNumber.put(t.getJob().getJobType(), jobTypeToNumber.get(t.getJob().getJobType()) + 1);
            } else {
                jobTypeToNumber.put(t.getJob().getJobType(), 1.0);
            }
        }
        double epsilon = .0001;
        for (Map.Entry<Integer, Double> en: jobTypeToNumber.entrySet()) {
            double percent = en.getValue() / (double) tasks.length;
            if (percentJobTypes.containsKey(percent)) {
                double key = percent+(epsilon * state.random.nextDouble());
                while(percentJobTypes.containsKey(key)) {
                    key = percent+(epsilon * state.random.nextDouble());
                }
                percentJobTypes.put(key, en.getKey());
            } else {
                percentJobTypes.put(percent, en.getKey());
            }
        }

        return percentJobTypes;
    }

    public int getNumTimeStepsFromLocation(Double2D dest) {
        return (int) Math.floor((curLocation.distance(dest))/getStepSize());
    }

    public int getNumTimeStepsFromLocation(Double2D dest, Double2D src) {
        // don't overshoot so do the floor. if you are between 0 and state.stepsize you are there!
        return (int) Math.floor((src.distance(dest))/getStepSize());
    }

    public double getStepSize() {
        return this.stepsize;
    }

    public void setStepsize(double stepsize) {
        this.stepsize = stepsize;
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

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "my ID " + id + " curJob = " + curJob + " avg wage = " + (bounty / state.schedule.getSteps());
    }

    public double getBounty() {
        return bounty;
    }

    public int getNumTimeStepsWorking() {
        return numTimeStepsWorking;
    }

    public void setDied(boolean died) {
        this.died = died;
    }

    public double getBountyRate() {
        if (state.schedule.getSteps() == 0) {
            return 0;
        }
        return getBounty() / state.schedule.getSteps();
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }
}
