package sim.app.mtrp.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import sim.app.mtrp.main.agents.AgentFactory;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;

import java.util.ArrayList;
import java.util.List;


/**
 * Entry into the mason simulator.
 * Created by drew on 2/16/17.
 */
public class MTRP extends SimState {

    private static final long serialVersionUID = 1;
    private static String[] myArgs;

    @Parameter
    private List<String> parameters = new ArrayList<String>();

    @Parameter(names={"--numAgents", "-na"})
    public int numAgents = 1;

    @Parameter(names={"--numNeighborhoods", "-n"})
    public int numNeighborhoods = 1;

    @Parameter(names={"--simWidth", "-sw"})
    public int simWidth = 100;
    @Parameter(names={"--simHeight", "-sh"})
    public int simHeight = 100;

    // stat publisher
    StatsPublisher statsPublisher;

    // agent params:

    @Parameter(names={"--agentType", "-a"})
    public int agentType = 3;

    public int maxCarrySizePerResource = 100; // number of each type of resource I can carry
    public double startFunds = 100;

    @Parameter(names={"--fuelCapacity", "-fc"})
    public double fuelCapacity = 3000;
    public double stepsize = 0.7; // this is the max distance I can travel in one step

    @Parameter(names={"--thresholdToSignal", "-t"})
    public double thresholdToSignal = 5;


    @Parameter(names={"--hasRandomness", "-hr"}, arity = 1)
    public boolean hasRandomness = true;



    // neighborhood params:

    @Parameter(names={"--numDepos", "-nd"})
    public int numDepos = 1;
    public int depoCapacity = 50; // how many resources of each type can the depo carry
    public int depoRefreshRate = 100; // every one hundred timesteps we refresh the resources in the depo... this also is very arbitrary and could be investigated

    public int numResourceTypes = 1; // fuel is not a resource included here.
    public double maxCostPerResource = 15.0; // for each type of resource we get the price and set it for all of the depos
    public int maxMeanResourcesNeededForType = 3; // the max mean number of resources needed for each type of resource (so max mean total number of resources would be 18)

    public double fuelCost = 1.0;

    @Parameter(names={"--timestepsTilNextTask", "-s"})
    public int timestepsTilNextTask = 10; // used to calculate the arrival rate of the tasks using a geometric distribution

    @Parameter(names={"--jobLength", "-jl"})
    public int jobLength = 1; // the max mean job length (the mean is picked randomly from zero to this max)
    public double taskLocStdDev = 5.0; // 5.0 is the same as what we used in the original paper.
    public double taskLocLength = 40.0; // this is the length of the sides of the square region of the neighborhood
    public double meanDistBetweenNeighborhoods = 20;//Math.sqrt(Math.pow(taskLocLength / 2, 2)*2); // this is the average distance between any two neighborhoods

    public int numJobTypes = 1; // a job type is the average job length and the average number of resources needed for each type of resource.


    // bondsman params:
    @Parameter(names={"--basebounty", "-b"})
    public double basebounty = 100;
    @Parameter(names={"--bountyIncrement", "-bi"})
    public double increment = 1.0;


    public Bondsman bondsman;
    public Agent agents[];
    public Depo depos[];
    public Neighborhood neighborhoods[];
    public Job jobPrototypes[];

    public Continuous2D agentPlane;
    public Continuous2D taskPlane;
    public Continuous2D depoPlane;
    public Continuous2D neighborhoodPlane;


    // Augmentor stuff
    public Augmentor augmentor;
    @Parameter(names={"--shouldDie", "-d"}, arity = 1)
    public boolean shouldDie = false;
    public int numstepsDead = 30000; // an agent is removed

    @Parameter(names={"--hasEmergentJob", "-e"}, arity = 1)
    public boolean hasEmergentJob = false; // job type with base bounty of 2000 appears every 20000 steps
    public int numEmergentJobTypes = 1;
    public int numstepsEmergentJob = 20000;
    public double emergentBounty = basebounty * 5;

    @Parameter(names={"--hasUnexpectedlyHardJobs", "-u"}, arity = 1)
    public boolean hasUnexpectedlyHardJobs = false; // the length of the job increases by some factor

    @Parameter(names={"--hasSuddenTaskIncrease", "-i"}, arity = 1)
    public boolean hasSuddenTaskIncrease = false;
    public double newRate = timestepsTilNextTask / 2;
    public int disasterLength = 2000;
    public int disasterStep = 50000;

    public boolean slower = false;
    public long numstepsSlow = 2000;

    public boolean hasUnexpectedlySlowJobs = false;

    public int deadline = 2000;

    @Parameter(names={"--directory", "-y"})
    public String directory = "/home/drew/tmp";

    @Parameter(names={"--groupLabel", "-gl"})
    public String groupLabel = "NA";

    public MTRP(long seed) {
        super(seed);
    }

    public static void main(String[] args) {
        myArgs = args;
        doLoop(MTRP.class, args);
        System.exit(0);
    }


    @Override
    public void start() {
        super.start();
        this.random.setSeed(this.seed()); // need to fix this in Mason proper...  need this here for running from terminal multiple times
        // here we go!

        if (this.myArgs != null) {
            // remove the first
            String[] newArgs = new String[myArgs.length - 8];
            int j =0 ;
            for (int i =8 ; i < myArgs.length; i++) {
                newArgs[j] = myArgs[i];
                j++;
            }
            JCommander.newBuilder()
                    .addObject(this)
                    .build()
                    .parse(newArgs);


        }


        printlnSynchronized("Has randomness " + basebounty);

        agentPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        taskPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        depoPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());

        jobPrototypes = new Job[numJobTypes + numEmergentJobTypes];
        // create the job prototypes
        for (int i = 0; i < numJobTypes; i++) {
            jobPrototypes[i] = new Job(this, i, basebounty);
        }

        for (int i = numJobTypes; i < (numJobTypes + numEmergentJobTypes); i++) {
            jobPrototypes[i] = new Job(this, i, emergentBounty);
        }




        int order = 0;
        neighborhoods = new Neighborhood[numNeighborhoods];
        neighborhoodPlane = new Continuous2D(1.0, getSimWidth(), getSimHeight());
//        Double2D[] locations = new Double2D[5];
//        locations[0] = new Double2D(50,50);
//        locations[1] = new Double2D(100,50);
//        locations[2] = new Double2D(50,100);
//        locations[3] = new Double2D(100, 100);
//        locations[4] = new Double2D(25, 25);


        // First create the neighborhoods.  Use the mean location as the location for the depos
        for (int i = 0; i < numNeighborhoods; i++) {
            // create neighborhoods some distance appart
            Neighborhood n = new Neighborhood(this, i);
            // add it to the plane


            while (neighborhoodPlane.getNeighborsWithinDistance(n.getMeanLocation(), this.meanDistBetweenNeighborhoods).size() != 0) {
                n = new Neighborhood(this, i);
            }


            neighborhoodPlane.setObjectLocation(n, n.meanLocation);
            //n.setMeanLocation(locations[i]);
            //neighborhoodPlane.setObjectLocation(n, locations[i]);

            neighborhoods[i] = n;//new Neighborhood(this, i);
            // schedule it
            schedule.scheduleRepeating(Schedule.EPOCH, order, neighborhoods[i]);
            order++;
        }

        //setIncrement(basebounty / (Math.sqrt(Math.pow(getSimHeight(), 2) + Math.pow(getSimWidth(), 2)) * jobLength));


        depos = new Depo[numDepos];
        Bag shuffledNeighborhoods = new Bag(neighborhoods);
        shuffledNeighborhoods.shuffle(random);
        // create the depos after the neighborhood as we place the depos in random neighborhoods
        for (int i =0; i < numDepos; i++) {
            // only a single neighborhood per depo
            depos[i] = new Depo(this, i, (Neighborhood) shuffledNeighborhoods.get(i));
            schedule.scheduleRepeating(Schedule.EPOCH, order, depos[i], depoRefreshRate);
            order++;
        }

        // create the bondsman and pass in this
        bondsman = new Bondsman(this);
        schedule.scheduleRepeating(Schedule.EPOCH, order, bondsman);
        order++;



        // create the augementor
        augmentor = new Augmentor(this);
        schedule.scheduleRepeating(Schedule.EPOCH, order, augmentor);
        order++;




        // create the agents
        agents = new Agent[numAgents];
        for (int i = 0; i < numAgents; i++) {
            agents[i] = AgentFactory.buildAgent(this, i, agentType);
            agents[i].setStepsize(getStepsize());
            schedule.scheduleRepeating(Schedule.EPOCH, order, agents[i]);
            order++;
        }

        // create the stat publisher
        statsPublisher = new StatsPublisher(this, 200000, directory);
        schedule.scheduleRepeating(Schedule.EPOCH, order, statsPublisher);
    }



    @Override
    public void finish() {
        super.finish();
        statsPublisher.finish();
    }

    public int getAgentType() {
        return agentType;
    }

    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }

    public Depo[] getDepos() {
        return depos;
    }

    public Agent[] getAgents() {
        return agents;
    }

    public Bondsman getBondsman() {
        return bondsman;
    }

    public Neighborhood[] getNeighborhoods() {
        return neighborhoods;
    }

    public Continuous2D getAgentPlane() {
        return agentPlane;
    }

    public Continuous2D getDepoPlane() {
        return depoPlane;
    }

    public Continuous2D getTaskPlane() {
        return taskPlane;
    }

    public double getStepsize() {
        return stepsize;
    }

    public int getNumAgents() {
        return numAgents;
    }

    public void setNumAgents(int numAgents) {
        this.numAgents = numAgents;
    }

    public int getNumNeighborhoods() {
        return numNeighborhoods;
    }

    public void setNumNeighborhoods(int numNeighborhoods) {
        this.numNeighborhoods = numNeighborhoods;
    }

    public int getSimWidth() {
        return simWidth;
    }

    public void setSimWidth(int simWidth) {
        this.simWidth = simWidth;
    }

    public int getSimHeight() {
        return simHeight;
    }

    public void setSimHeight(int simHeight) {
        this.simHeight = simHeight;
    }

    public int getMaxCarrySizePerResource() {
        return maxCarrySizePerResource;
    }

    public void setMaxCarrySizePerResource(int maxCarrySizePerResource) {
        this.maxCarrySizePerResource = maxCarrySizePerResource;
    }

    public double getStartFunds() {
        return startFunds;
    }

    public void setStartFunds(double startFunds) {
        this.startFunds = startFunds;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public int getNumDepos() {
        return numDepos;
    }

    public void setNumDepos(int numDepos) {
        this.numDepos = numDepos;
    }

    public int getDepoCapacity() {
        return depoCapacity;
    }

    public void setDepoCapacity(int depoCapacity) {
        this.depoCapacity = depoCapacity;
    }

    public int getDepoRefreshRate() {
        return depoRefreshRate;
    }

    public void setDepoRefreshRate(int depoRefreshRate) {
        this.depoRefreshRate = depoRefreshRate;
    }

    public int getNumResourceTypes() {
        return numResourceTypes;
    }

    public void setNumResourceTypes(int numResourceTypes) {
        this.numResourceTypes = numResourceTypes;
    }

    public double getMaxCostPerResource() {
        return maxCostPerResource;
    }

    public void setMaxCostPerResource(double maxCostPerResource) {
        this.maxCostPerResource = maxCostPerResource;
    }

    public double getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(double fuelCost) {
        this.fuelCost = fuelCost;
    }

    public void setTimestepsTilNextTask(int timestepsTilNextTask) {
        this.timestepsTilNextTask = timestepsTilNextTask;
    }

    public int getTimestepsTilNextTask() {
        return timestepsTilNextTask;
    }

    public int getJobLength() {
        return jobLength;
    }

    public void setJobLength(int jobLength) {
        this.jobLength = jobLength;
    }

    public double getBasebounty() {
        return basebounty;
    }

    public void setBasebounty(double basebounty) {
        this.basebounty = basebounty;
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }

    public double getTotalTime() {
        // so get the total time the tasks have been waiting
        if (bondsman == null) { return 0.0;}
        return bondsman.getTotalAverageTime();
    }


    public double getTotalOutstandingBounty() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getTotalOutstandingBounty();
    }

    public int getNumberOfAvailableTasks() {
        if (bondsman == null) { return 0;}
        return bondsman.getAvailableTasks().length;
    }

    public int getNumTasksOnBoard() {
        if (bondsman == null) { return 0;}
        return bondsman.getTotalTasksGenerated();
    }

    public double getNumTasksCompleted() {
        if (bondsman == null) { return 0;}
        return bondsman.getCount();
    }



    public double getAverageOutstandingBounty() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getTotalOutstandingBounty() / (double)bondsman.getAvailableTasks().length;
    }

    public void setTaskLocStdDev(double taskLocStdDev) {
        this.taskLocStdDev = taskLocStdDev;
    }

    public double getTaskLocStdDev() {
        return taskLocStdDev;
    }

    public double getTasksPerAgent() {
        if (bondsman == null) { return 0.0;}
        return (double)bondsman.getAvailableTasks().length / (double) agents.length;
    }

    public void setNumJobTypes(int numJobTypes) {
        this.numJobTypes = numJobTypes;
    }

    public int getNumJobTypes() {
        return numJobTypes;
    }

    public void setTaskLocLength(double taskLocLength) {
        this.taskLocLength = taskLocLength;
    }

    public double getTaskLocLength() {
        return taskLocLength;
    }

    public int getNumEmergentJobTypes() {
        return numEmergentJobTypes;
    }

    public int getNumstepsDead() {
        return numstepsDead;
    }

    public int getNumstepsEmergentJob() {
        return numstepsEmergentJob;
    }

    public boolean isHasEmergentJob() {
        return hasEmergentJob;
    }

    public boolean isHasUnexpectedlyHardJobs() {
        return hasUnexpectedlyHardJobs;
    }

    public boolean isShouldDie() {
        return shouldDie;
    }

    public void setNumstepsEmergentJob(int numstepsEmergentJob) {
        this.numstepsEmergentJob = numstepsEmergentJob;
    }

    public void setHasEmergentJob(boolean hasEmergentJob) {
        this.hasEmergentJob = hasEmergentJob;
    }

    public void setShouldDie(boolean shouldDie) {
        this.shouldDie = shouldDie;
    }

    public void setHasUnexpectedlyHardJobs(boolean hasUnexpectedlyHardJobs) {
        this.hasUnexpectedlyHardJobs = hasUnexpectedlyHardJobs;
    }

    public void setNumEmergentJobTypes(int numEmergentJobTypes) {
        this.numEmergentJobTypes = numEmergentJobTypes;
    }

    public void setNumstepsDead(int numstepsDead) {
        this.numstepsDead = numstepsDead;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public boolean isHasSuddenTaskIncrease() {
        return hasSuddenTaskIncrease;
    }

    public void setHasSuddenTaskIncrease(boolean hasSuddenTaskIncrease) {
        this.hasSuddenTaskIncrease = hasSuddenTaskIncrease;
    }

    public double getNewRate() {
        return newRate;
    }

    public void setNewRate(double newRate) {
        this.newRate = newRate;
    }

    public int getDisasterLength() {
        return disasterLength;
    }

    public void setDisasterLength(int disasterLength) {
        this.disasterLength = disasterLength;
    }

    public int getDisasterStep() {
        return disasterStep;
    }

    public void setDisasterStep(int disasterStep) {
        this.disasterStep = disasterStep;
    }

    public double getThresholdToSignal() {
        return thresholdToSignal;
    }

    public void setThresholdToSignal(double thresholdToSignal) {
        this.thresholdToSignal = thresholdToSignal;
    }

    public void setHasRandomness(boolean hasRandomness) {
        this.hasRandomness = hasRandomness;
    }

    public boolean isHasRandomness() {
        return hasRandomness;
    }

    public double getAverageWage() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getTotalBounty() / bondsman.getTotalTime();
    }


    public boolean isSlower() {
        return slower;
    }

    public void setSlower(boolean slower) {
        this.slower = slower;
    }

    public long getNumstepsSlow() {
        return numstepsSlow;
    }

    public void setNumstepsSlow(long numstepsSlow) {
        this.numstepsSlow = numstepsSlow;
    }

    public boolean isHasUnexpectedlySlowJobs() {
        return hasUnexpectedlySlowJobs;
    }

    public void setHasUnexpectedlySlowJobs(boolean hasUnexpectedlySlowJobs) {
        this.hasUnexpectedlySlowJobs = hasUnexpectedlySlowJobs;
    }

    public double getNumStale() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getNumStale();
    }

    public double getTotalFuelPurchased() {
        if(depos == null || depos.length == 0) {
            return 0;
        }
        double totalFuelPurchased = 0.0;
        for (Depo d : depos) {
            totalFuelPurchased += d.totalFuelPurchased;
        }
        return totalFuelPurchased;
    }

    public void setStepsize(double stepsize) {
        this.stepsize = stepsize;
    }
}
