package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Job implements java.io.Serializable  {
    private static final long serialVersionUID = 1;

    Task task;
    MTRP state;
    int id;
    int jobType;

    Agent curWorker;
    int resourcesNeeded[]; // index maps to the resource type and the value is the number of that type of resource.
    double currentBounty;
    boolean isAvailable;
    int meanJobLength;

    private Job() {}

    public Job(MTRP state, int id) {
        // create the prototype
        this.id = id;
        this.meanJobLength = state.random.nextInt(state.jobLength);
        // now the mean resources:
        resourcesNeeded = new int[state.getNumResourceTypes()];
        for (int i = 0; i < state.numResourceTypes; i++) {
            resourcesNeeded[i] = state.random.nextInt(state.maxMeanResourcesNeededForType);
        }

    }


    /**
     * builds a job from this prototypical job
     * @param state
     * @param task
     * @param id
     * @return
     */
    public Job buildJob(MTRP state, Task task, int id) {
        Job job = new Job();
        job.jobType = this.id;
        job.id = id;
        job.task = task;
        job.resourcesNeeded = new int[this.resourcesNeeded.length];
        job.isAvailable = true;
        job.currentBounty = state.basebounty;
        job.state = state;
        for (int i = 0; i < this.resourcesNeeded.length; i++) {
            while(state.random.nextDouble() > (1.0 / (double) this.resourcesNeeded[i])) {
                job.resourcesNeeded[i]++;
            }
        }
        job.meanJobLength = this.meanJobLength;
        return job;
    }


    public int getJobType() {
        return jobType;
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
        currentBounty += state.getIncrement();
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

        return state.random.nextDouble() <= (1.0 / (double) this.meanJobLength);
    }

    public void finish() {
        task.setFinished();
    }
}
