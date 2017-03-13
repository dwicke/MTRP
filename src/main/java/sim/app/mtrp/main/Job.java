package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Job implements java.io.Serializable  {
    private static final long serialVersionUID = 1;

    Task task;
    MTRP state;
    int id;

    Agent curWorker;
    int resourcesNeeded[]; // index maps to the resource type and the value is the number of that type of resource.
    double currentBounty;
    boolean isAvailable;




    public Job(Task task, MTRP state, int id) {
        this.id = id;
        this.state = state;
        this.task = task;
        // now setup the job
        reset();
    }

    public final void reset() {
        isAvailable = true;
        currentBounty = state.basebounty;

        resourcesNeeded = new int[state.getNumResourceTypes()];
        int numResource = state.random.nextInt(state.getMaxNumResourcesPerJob());
        while (numResource != 0) {
            resourcesNeeded[state.random.nextInt(resourcesNeeded.length)]++;
            numResource--;
        }
    }

    public Task getTask() {
        return task;
    }

    public int[] getResourcesNeeded() {
        return resourcesNeeded;
    }

    public void setResourcesNeeded(int[] resourcesNeeded) {
        this.resourcesNeeded = resourcesNeeded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCurrentBounty() {
        return currentBounty;
    }

    public void setCurrentBounty(double currentBounty) {
        this.currentBounty = currentBounty;
    }


    public Agent getCurWorker() {
        return curWorker;
    }

    public void incrementBounty() {
        currentBounty++;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void claimWork(Agent worker) {

        curWorker = worker;
        isAvailable = false;
    }

    public boolean doWork() {
        // geometric distribution.
        return state.random.nextDouble() <= (1.0 / state.jobLength);
    }

    public void finish() {
        task.setFinished();
    }
}
