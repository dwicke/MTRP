package sim.app.mtrp.main;

import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;

import java.util.Collections;


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


    // agent params:

    public int maxCarrySize = 16;
    public double startFunds = 100;
    public double fuelCapacity = 1000;
    public double stepsize = 0.7; // this is the max distance I can travel in one step



    // neighborhood params:

    public int numDepos = 2;
    public int depoCapacity = 20; // how many resources of each type can the depo carry
    public int depoRefreshRate = 100; // every one hundred timesteps we refresh the resources in the depo... this also is very arbitrary and could be investigated

    public int numResourceTypes = 3; // fuel is not a resource included here.
    public double maxCostPerResource = 20.0; // for each type of resource we get the price and set it for all of the depos

    public double fuelCost = 1.0;
    public int maxNumTasksPerNeighborhood = 6; // task == customer
    public int maxJobLength = 10; // change...
    public int maxNumResourcesPerJob = numResourceTypes * 2;


    // bondsman params:

    public double basebounty = 100;
    public double increment = 1.0;


    public Bondsman bondsman;
    public Agent agents[];
    public Depo depos[];
    public Neighborhood neighborhoods[];

    public Continuous2D agentPlane;
    public Continuous2D taskPlane;
    public Continuous2D depoPlane;

    public double taskLocStdDev = 5.0; // this is the same as what we used in the original paper.


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


        int order = 0;
        neighborhoods = new Neighborhood[numNeighborhoods];
        // First create the neighborhoods.  Use the mean location as the location for the depos
        for (int i = 0; i < numNeighborhoods; i++) {
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
            agents[i] = new Agent(this, i);
            schedule.scheduleRepeating(Schedule.EPOCH, order, agents[i]);
            order++;
        }
    }



    @Override
    public void finish() {
        super.finish();
        StatsPublisher p = new StatsPublisher(this, 200000, "/home/drew/tmp");
        p.step(this);
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

    public int getMaxNumTasksPerNeighborhood() {
        return maxNumTasksPerNeighborhood;
    }

    public void setMaxNumTasksPerNeighborhood(int maxNumTasksPerNeighborhood) {
        this.maxNumTasksPerNeighborhood = maxNumTasksPerNeighborhood;
    }

    public int getMaxJobLength() {
        return maxJobLength;
    }

    public void setMaxJobLength(int maxJobLength) {
        this.maxJobLength = maxJobLength;
    }

    public int getMaxNumResourcesPerJob() {
        return maxNumResourcesPerJob;
    }

    public void setMaxNumResourcesPerJob(int maxNumResourcesPerJob) {
        this.maxNumResourcesPerJob = maxNumResourcesPerJob;
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
}