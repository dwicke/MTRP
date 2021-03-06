package sim.app.mtrp.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import sim.app.mtrp.main.agents.AgentFactory;
import sim.app.mtrp.main.agents.Valuators.EquitablePartitions;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.field.grid.DoubleGrid2D;
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
    public int numAgents = 4;

    @Parameter(names={"--numNeighborhoods", "-n"})
    public int numNeighborhoods = 6;

    @Parameter(names={"--simWidth", "-sw"})
    public int simWidth = 100;
    @Parameter(names={"--simHeight", "-sh"})
    public int simHeight = 100;

    // stat publisher
    StatsPublisher statsPublisher;

    // agent params:

    @Parameter(names={"--agentType", "-a"})
    public int agentType = 2;

    public int maxCarrySizePerResource = 100; // number of each type of resource I can carry
    @Parameter(names={"--startFunds", "-sf"})
    public double startFunds = 1000;

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
    @Parameter(names={"--depoCapacity", "-dc"})
    public int depoCapacity = 1000; // how many resources of each type can the depo carry
    @Parameter(names={"--depoRefreshRate", "-dr"})
    public int depoRefreshRate = 10; // (should be more frequent...) every one hundred timesteps we refresh the resources in the depo is too slow...

    @Parameter(names={"--numResourceTypes", "-rt"})
    public int numResourceTypes = 0; // fuel is not a resource included here.
    @Parameter(names={"--maxCostPerResource", "-cr"})
    public double maxCostPerResource = 15.0; // for each type of resource we get the price and set it for all of the depos
    @Parameter(names={"--maxMeanResourcesNeededForType", "-rn"})
    public int maxMeanResourcesNeededForType = 10; // the max mean number of resources needed for each type of resource (so max mean total number of resources would be 18)

    @Parameter(names={"--fuelCost", "-fco"})
    public double fuelCost = 1.0;

    @Parameter(names={"--timestepsTilNextTask", "-s"})
    public double timestepsTilNextTask = 30; // used to calculate the arrival rate of the tasks using a geometric distribution

    @Parameter(names={"--jobLength", "-jl"})
    public int jobLength = 1; // the max mean job length (the mean is picked randomly from zero to this max)
    public double taskLocStdDev = 10.0;
    @Parameter(names={"--taskLocLength", "-tll"})
    public double taskLocLength = 40.0; // this is the length of the sides of the square region of the neighborhood
    public double meanDistBetweenNeighborhoods = 20;//Math.sqrt(Math.pow(taskLocLength / 2, 2)*2); // this is the average distance between any two neighborhoods

    @Parameter(names={"--numJobTypes", "-nj"})
    public int numJobTypes = 1; // a job type is the average job length and the average number of resources needed for each type of resource.


    // bondsman params:
    @Parameter(names={"--basebounty", "-b"})
    public double basebounty = 500;
    @Parameter(names={"--bountyIncrement", "-bi"})
    public double increment = 0.0;

    @Parameter(names={"--hasBountyRate", "-hbr"}, arity = 1)
    public boolean hasBountyRate = true;
    @Parameter(names={"--hasNeighborhoodBounty", "-hnb"}, arity = 1)
    public boolean hasNeighborhoodBounty = false;

    // 0 -- indicates 64 agent experiment
    // 1 -- indicates 4 agent experiment continuous space
    // 2 -- indicates 4 agents discontinuous space
    // 3 -- indicates 1 agent continuous space
    // 4 -- indicates 1 agent discontinuous space
    @Parameter(names={"--expSetup", "-es"})
    public int expSetup = 0;

    public Bondsman bondsman;
    public Agent agents[];
    public Depo depos[];
    public Neighborhood neighborhoods[];
    public Job jobPrototypes[];

    public Continuous2D agentPlane;
    public Continuous2D taskPlane;
    public Continuous2D destPlane;
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
    public int disasterLength = 30000;
    public int disasterStep = 50000;

    public boolean slower = false;
    public long numstepsSlow = 2000;

    public boolean hasUnexpectedlySlowJobs = false;

    public int deadline = 2000;

    @Parameter(names={"--directory", "-y"})
    public String directory = "/home/drew/tmp";

    @Parameter(names={"--groupLabel", "-gl"})
    public String groupLabel = "NA";

    @Parameter(names={"--delta", "-de"})
    public double delta = 0.05;

    @Parameter(names={"--logStep", "-ls"})
    public long logStep = 300000;


    public DoubleGrid2D valgrid[];
    public static final double MAX_TASK = 2;

    public EquitablePartitions ep;
    public boolean pickupDelivery = true;

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


        printlnSynchronized("Has randomness " + numNeighborhoods);

        agentPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        taskPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        destPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        depoPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());

        valgrid = new DoubleGrid2D[numAgents];
        for(int i = 0; i < numAgents; i++) {
            valgrid[i] = new DoubleGrid2D(getSimWidth(),getSimHeight(), 0);
        }

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

//        Double2D[] locations = new Double2D[4];
//        locations[0] = new Double2D(50,50);
//        locations[1] = new Double2D(0, 50);
//        locations[2] = new Double2D(50, 0);
//        locations[3] = new Double2D(0, 0);

        ////


        // First create the neighborhoods.  Use the mean location as the location for the depos
        for (int i = 0; i < numNeighborhoods; i++) {
            // create neighborhoods some distance appart
            Neighborhood n = new Neighborhood(this, i);
            // add it to the plane


//            while (neighborhoodPlane.getNeighborsWithinDistance(n.getMeanLocation(), this.meanDistBetweenNeighborhoods).size() != 0) {
//                n = new Neighborhood(this, i);
//            }


            neighborhoodPlane.setObjectLocation(n, n.meanLocation);
//            n.setMeanLocation(locations[i]);
//            neighborhoodPlane.setObjectLocation(n, locations[i]);

            neighborhoods[i] = n;//new Neighborhood(this, i);
            // schedule it
            schedule.scheduleRepeating(Schedule.EPOCH, order, neighborhoods[i]);
            order++;
        }

        //setIncrement(basebounty / (Math.sqrt(Math.pow(getSimHeight(), 2) + Math.pow(getSimWidth(), 2)) * jobLength));


        depos = new Depo[numDepos];
        //Bag shuffledNeighborhoods = new Bag(neighborhoods);
        //shuffledNeighborhoods.shuffle(random);
        // create the depos after the neighborhood as we place the depos in random neighborhoods
        for (int i =0; i < numDepos; i++) {
            // pick neighborhood to end up in randomly
            depos[i] = new Depo(this, i, neighborhoods[i % numNeighborhoods]);
            //depos[i] = new Depo(this, i, neighborhoods[random.nextInt(numNeighborhoods)]);
            //depos[i] = new Depo(this, i, (Neighborhood) shuffledNeighborhoods.get(i % numNeighborhoods));
            schedule.scheduleRepeating(Schedule.EPOCH, order, depos[i], depoRefreshRate);
            order++;
        }

        // create the bondsman and pass in this
        bondsman = new Bondsman(this);
        schedule.scheduleRepeating(Schedule.EPOCH, order, bondsman);
        order++;


        // create the agents
        agents = new Agent[numAgents];
        for (int i = 0; i < numAgents; i++) {
            agents[i] = AgentFactory.buildAgent(this, i, agentType);
            agents[i].setStepsize(getStepsize());
            schedule.scheduleRepeating(Schedule.EPOCH, order, agents[i]);
            order++;
        }


        // create the augementor
        augmentor = new Augmentor(this);
        schedule.scheduleRepeating(Schedule.EPOCH, order, augmentor);
        order++;

        // create the stat publisher
        statsPublisher = new StatsPublisher(this, logStep, directory);
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

    public void setTimestepsTilNextTask(double timestepsTilNextTask) {
        this.timestepsTilNextTask = timestepsTilNextTask;
    }

    public double getTimestepsTilNextTask() {
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

    public double getJainFairness() {
        // so get the total time the tasks have been waiting
        if (bondsman == null) { return 0.0;}
        return bondsman.getJainFairness();
    }

    public double getVarianceTime() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getVarianceTime();
    }


    public double getTotalOutstandingBounty() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getTotalOutstandingBounty();
    }

    public int getNumberOfAvailableTasks() {
        if (bondsman == null) { return 0;}
        return bondsman.getAvailableTasks().length;
    }

    public int getTotalTasksGenerated() {
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



    public double getAverageDistance() {
        if (bondsman == null) { return 0.0;}
        return neighborhoods[0].getTotalDist() / (double) neighborhoods[0].getTotalCount();
    }

    public double getAverageRevenue() {
        if (bondsman == null || (bondsman != null && bondsman.getTotalTime() == 0)) { return 0.0;}
        return (bondsman.getTotalBounty() - depos[0].getTotalFuelPurchased()) / bondsman.getTotalTime();
    }

    public double getAverageBaseBounty() {
        if (bondsman == null || (bondsman != null && bondsman.getTotalTime() == 0)) { return 0.0;}
        return bondsman.getAverageBaseBounty();
    }

    public double getAverageBountyRate() {
        if (bondsman == null || (bondsman != null && bondsman.getTotalTime() == 0)) { return 0.0;}
        return bondsman.getAverageBountyRate();
    }

    public double getAverageThroughput() {
        if (bondsman == null || (bondsman != null && bondsman.getCount() == 0)) { return 0.0;}
        return bondsman.getCount() / schedule.getTime();
    }




    public EquitablePartitions getEp() {
        return ep;
    }

    public void setEp(EquitablePartitions ep) {
        this.ep = ep;
    }

    public void setHasBountyRate(boolean hasBountyRate) {
        this.hasBountyRate = hasBountyRate;
    }

    public boolean isHasBountyRate() {
        return hasBountyRate;
    }

    public double getFairness() {
        if (agents != null) {
            double totalFairness = 0;
            double totalCount = 0;
            for (int i = 0; i < agents.length; i++) {
                totalFairness += agents[i].getTotalFairness();
                totalCount += agents[i].getCount();
            }
            return totalFairness / totalCount;
        }
        return 0;
    }


    public double getAreaStats() {
        if (agents != null) {
            double minArea = agents[0].getTotalArea();
            double maxArea = agents[0].getTotalArea();
            for (int i = 0; i < agents.length; i++) {
                if (agents[i].getTotalArea() > maxArea) {
                    maxArea = agents[i].getTotalArea();
                } else if (agents[i].getTotalArea() < minArea) {
                    minArea = agents[i].getTotalArea();
                }
            }
            return minArea / maxArea;
        }
        return 0;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }



    public boolean isHasNeighborhoodBounty() {
        return hasNeighborhoodBounty;
    }

    public void setHasNeighborhoodBounty(boolean hasNeighborhoodBounty) {
        this.hasNeighborhoodBounty = hasNeighborhoodBounty;
    }

    public double getAgentNNI() {
        if (bondsman == null || (bondsman != null && bondsman.getCount() < 30)) { return -1;}

        double totalDist = 0;
        if (numAgents > 30) {
            for (int i = 0; i < agents.length; i++) {
                Agent t = agents[i];
                Bag results = new Bag();
                agentPlane.getNearestNeighbors(t.curLocation, 2, false, false, true, results);
                double dist = Double.MAX_VALUE;
                // printlnSynchronized("Results size = " + results.size());
                for (int j = 0; j < results.size(); j++) {
                    Agent nearT = (Agent) results.get(j);
                    double curdist = nearT.curLocation.distance(t.curLocation);
                    if (curdist < dist && nearT.getId() != t.getId()) {
                        dist = curdist;
                        //printlnSynchronized("dist = " + dist);
                    }
                }

                totalDist += dist;
            }
            //printlnSynchronized("NumTasks = " + numTasks + "  Total Dist = " + totalDist + " NNI = " + (totalDist / numTasks) / (.5 * Math.sqrt(((double)simWidth *(double)simHeight) / numTasks)));

            double dNN = (totalDist / numAgents);
            double dran = (.5 * Math.sqrt(((double)simWidth *(double)simHeight) / numAgents));
            double sran =  0.26136 / Math.sqrt((numAgents * numAgents) / ((double)simWidth *(double)simHeight));
            double zscore = (dNN - dran) / sran;
            double val = (totalDist / numAgents) / (.5 * Math.sqrt(((double)simWidth *(double)simHeight) / numAgents));

            return zscore;
            //return (totalDist / numAgents) / (.5 * Math.sqrt(((double)simWidth *(double)simHeight) / numAgents));
        }
        return -1;
    }


    public double getNearestNeighborIndex() {
        if (bondsman == null || (bondsman != null && bondsman.getCount() < 30)) { return -1;}

        double totalDist = 0;
        double numTasks = bondsman.getAllTasks().length;
        if (numTasks > 30) {
            for (int i = 0; i < bondsman.getAllTasks().length; i++) {
                Task t = bondsman.getAllTasks()[i];
                Bag results = new Bag();
                taskPlane.getNearestNeighbors(t.location, 2, false, false, true, results);
                double dist = Double.MAX_VALUE;
               // printlnSynchronized("Results size = " + results.size());
                for (int j = 0; j < results.size(); j++) {
                    Task nearT = (Task) results.get(j);
                    double curdist = nearT.getLocation().distance(t.getLocation());
                    if (curdist < dist && nearT.getId() != t.getId()) {
                        dist = curdist;
                        //printlnSynchronized("dist = " + dist);
                    }
                }

                totalDist += dist;
            }
            //printlnSynchronized("NumTasks = " + numTasks + "  Total Dist = " + totalDist + " NNI = " + (totalDist / numTasks) / (.5 * Math.sqrt(((double)simWidth *(double)simHeight) / numTasks)));
            return (totalDist / numTasks) / (.5 * Math.sqrt(((double)simWidth *(double)simHeight) / numTasks));
        }
        return -1;

    }



    public void setExpSetup(int expSetup) {
        this.expSetup = expSetup;
    }

    public int getExpSetup() {
        return expSetup;
    }

    public boolean getPickupDelivery() {
        return pickupDelivery;
    }

    public void setPickupDelivery(boolean pickupDelivery) {
        this.pickupDelivery = pickupDelivery;
    }
}
