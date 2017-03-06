package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Job {
    private static final long serialVersionUID = 1;

    Task task;
    MTRP state;
    int id;

    Agent curWorker;
    int resourcesNeeded[]; // index maps to the resource type and the value is the number of that type of resource.
    double currentBounty;
    int jobLength; // how long it takes to complete the job in number of timesteps
    boolean isAvailable;
    int curWorkLeft;


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
        jobLength = state.random.nextInt(state.getMaxJobLength());
        curWorkLeft = jobLength;
        resourcesNeeded = new int[state.getNumResourceTypes()];
        int numResource = state.random.nextInt(state.getMaxNumResourcesPerJob());
        while (numResource != 0) {
            resourcesNeeded[state.random.nextInt(resourcesNeeded.length)]++;
            numResource--;
        }
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

    public int getJobLength() {
        return jobLength;
    }

    public void setJobLength(int jobLength) {
        this.jobLength = jobLength;
    }

    public int getCurWorkLeft() {
        return curWorkLeft;
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
        if (curWorkLeft == 0)
            return true;
        curWorkLeft--;
        return curWorkLeft == 0;
    }

    public void finish() {
        if (curWorkLeft == 0) {
            task.setFinished();
        }
    }
}
