package sim.app.mtrp.main;

/**
 * Created by drew on 2/20/17.
 */
public class Job {
    private static final long serialVersionUID = 1;

    Task task;
    MTRP state;
    int id;

    int resourcesNeeded[]; // index maps to the resource type and the value is the number of that type of resource.
    double currentBounty;
    int jobLength; // how long it takes to complete the job in number of timesteps


    public Job(Task task, MTRP state, int id) {
        this.id = id;
        this.state = state;
        this.task = task;

        // now setup the job
        currentBounty = state.basebounty;
        jobLength = state.random.nextInt(state.getMaxJobLength());
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

    public void incrementBounty() {
        currentBounty++;
    }
}
