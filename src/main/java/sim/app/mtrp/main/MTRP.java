package sim.app.mtrp.main;

import sim.app.mtrp.main.agents.AgentFactory;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;


/**
 * Entry into the mason simulator.
 * Created by drew on 2/16/17.
 */
public class MTRP extends SimState {

    private static final long serialVersionUID = 1;
    private static String[] myArgs;


    public int numAgents = 4;
    public int numNeighborhoods = 4;
    public int simWidth = 100;
    public int simHeight = 100;

    // stat publisher
    StatsPublisher statsPublisher;

    // agent params:

    public int agentType = 0;
    public int maxCarrySize = 100;
    public double startFunds = 100;
    public double fuelCapacity = 1000;
    public double stepsize = 0.7; // this is the max distance I can travel in one step



    // neighborhood params:

    public int numDepos = 2;
    public int depoCapacity = 20; // how many resources of each type can the depo carry
    public int depoRefreshRate = 100; // every one hundred timesteps we refresh the resources in the depo... this also is very arbitrary and could be investigated

    public int numResourceTypes = 3; // fuel is not a resource included here.
    public double maxCostPerResource = 20.0; // for each type of resource we get the price and set it for all of the depos
    public int maxMeanResourcesNeededForType = numResourceTypes * 2; // the max mean number of resources needed for each type of resource (so max mean total number of resources would be 18)

    public double fuelCost = 1.0;
    public int timestepsTilNextTask = 30; // used to calculate the arrival rate of the tasks using a geometric distribution

    public int jobLength = 15; // the max mean job length (the mean is picked randomly from zero to this max)
    public double taskLocStdDev = 5.0; // 5.0 is the same as what we used in the original paper.
    public double taskLocLength = 40.0; // this is the length of the sides of the square region of the neighborhood
    public double meanDistBetweenNeighborhoods = 30.0; // this is the average distance between any two neighborhoods
    public int numJobTypes = 20; // a job type is the average job length and the average number of resources needed for each type of resource.


    // bondsman params:
    public double basebounty = 100;
    public double increment = 1.0;


    public Bondsman bondsman;
    public Agent agents[];
    public Depo depos[];
    public Neighborhood neighborhoods[];
    public Job jobPrototypes[];

    public Continuous2D agentPlane;
    public Continuous2D taskPlane;
    public Continuous2D depoPlane;




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
        // here we go!


        agentPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        taskPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());
        depoPlane = new Continuous2D(1.0, getSimWidth(),getSimHeight());

        jobPrototypes = new Job[numJobTypes];
        // create the job prototypes
        for (int i = 0; i < numJobTypes; i++) {
            jobPrototypes[i] = new Job(this, i);

        }


        int order = 0;
        neighborhoods = new Neighborhood[numNeighborhoods];
        Continuous2D neighborhoodPlane = new Continuous2D(1.0, getSimWidth(), getSimHeight());
        // First create the neighborhoods.  Use the mean location as the location for the depos
        for (int i = 0; i < numNeighborhoods; i++) {
            // create neighborhoods some distance appart
            Neighborhood n = new Neighborhood(this, i);
            // add it to the plane

            /*
            while (neighborhoodPlane.getNeighborsExactlyWithinDistance(n.getMeanLocation(), this.meanDistBetweenNeighborhoods).size() > 0) {
                n = new Neighborhood(this, i);
            }
            */
            neighborhoodPlane.setObjectLocation(n, n.meanLocation);
            neighborhoods[i] = new Neighborhood(this, i);
            // schedule it
            schedule.scheduleRepeating(Schedule.EPOCH, order, neighborhoods[i]);
            order++;
        }

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

        // create the agents
        agents = new Agent[numAgents];
        for (int i = 0; i < numAgents; i++) {
            agents[i] = AgentFactory.buildAgent(this, i, agentType);
            schedule.scheduleRepeating(Schedule.EPOCH, order, agents[i]);
            order++;
        }

        // create the stat publisher
        statsPublisher = new StatsPublisher(this, 200000, "/home/drew/tmp");
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

    public int getMaxCarrySize() {
        return maxCarrySize;
    }

    public void setMaxCarrySize(int maxCarrySize) {
        this.maxCarrySize = maxCarrySize;
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

    public double getJobLength() {
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
        return bondsman.getTotalTime();
    }


    public double getTotalOutstandingBounty() {
        if (bondsman == null) { return 0.0;}
        return bondsman.getTotalOutstandingBounty();
    }

    public int getNumberOfAvailableTasks() {
        if (bondsman == null) { return 0;}
        return bondsman.getAvailableTasks().length;
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



}
