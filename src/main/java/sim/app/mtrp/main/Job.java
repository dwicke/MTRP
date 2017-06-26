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
    int signals[];
    int totalSignals = 0;
    double currentBounty;
    boolean isAvailable;
    int meanJobLength;
    int countWork = 0;
    boolean slow = false;

    private Job() {}

    public Job(MTRP state, int id, double baseBounty) {
        // create the prototype
        this.id = id;
        this.meanJobLength = state.jobLength;
        /*if (state.hasRandomness) {
            this.meanJobLength = state.random.nextInt(state.jobLength); //state.jobLength;
        }else {
            this.meanJobLength = state.jobLength;
        }*/
        // now the mean resources:
        resourcesNeeded = new int[state.getNumResourceTypes()];
        for (int i = 0; i < state.numResourceTypes; i++) {
            resourcesNeeded[i] = state.maxMeanResourcesNeededForType;//state.random.nextInt(state.maxMeanResourcesNeededForType);
        }
        this.currentBounty = baseBounty;
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
        job.resourcesNeeded = this.resourcesNeeded.clone();//new int[this.resourcesNeeded.length];
        job.isAvailable = true;
        job.currentBounty = this.currentBounty;
        job.state = state;
        job.signals = new int[state.numAgents];

//        for (int i = 0; i < this.resourcesNeeded.length; i++) {
//            while(state.random.nextDouble() > (1.0 / (double) this.resourcesNeeded[i])) {
//                job.resourcesNeeded[i]++;
//                //job.currentBounty += state.maxCostPerResource*2;
//            }
//        }
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

    public boolean claimWork(Agent worker) {

//        // check if the agent has enough resources
//        for (int i = 0; i < resourcesNeeded.length; i++) {
//            if (worker.resourcesQuantities[i] < resourcesNeeded[i]) {
//                return false;
//            }
//        }
//
//        // now since the agent has enough resources remove them
//        for (int i = 0; i < resourcesNeeded.length; i++) {
//            worker.resourcesQuantities[i] -= resourcesNeeded[i];
//        }


        curWorker = worker;
        isAvailable = false;

        return true;
    }

    public void leaveWork(Agent agent) {
        curWorker = null;
        isAvailable = true;
        countWork = 0;
    }



    public boolean doWork() {
        // geometric distribution.

        if (state.hasRandomness) {
            return state.random.nextDouble() <= (1.0 / (double) this.meanJobLength);
        }

        else {
            countWork++;
            boolean a = countWork >= this.meanJobLength;
            if (a) {
                countWork = 0;
            }
            return a;
        }

    }

    public int getCountWork() {
        return countWork;
    }

    public void finish() {
        task.setFinished();
    }

    public int getMeanJobLength() {
        return meanJobLength;
    }

    public void setMeanJobLength(int meanJobLength) {
        this.meanJobLength = meanJobLength;
    }

    public void signal(Agent agent) {
        if (signals[agent.getId()] == 0) {
            totalSignals++;
            signals[agent.getId()] = 1;
        }
    }

    public void unsignal(Agent agent) {
        if (signals[agent.getId()] == 1) {
            totalSignals--;
            signals[agent.getId()] = 0;
        }
    }

    public boolean isSignaled(Agent agent) {

        return signals[agent.getId()] == 1;
    }
    public boolean noSignals() {
        return totalSignals == 0;
    }

    public boolean isSlow() {
        return slow;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }
}
