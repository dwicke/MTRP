package sim.app.mtrp.main;

import sim.engine.*;


/**
 * Entry into the mason simulator.
 * Created by drew on 2/16/17.
 */
public class MTRP extends SimState {

    private static final long serialVersionUID = 1;
    private static String[] myArgs;


    public int numAgents = 4;
    public int maxCarrySize = 4;
    public double startFunds = 100;
    public double fuelCapacity = 1000;

    public int numNeighborhoods = 4;
    public int numDepos = 2;
    public int depoCapacity = 20; // how many resources of each type can the depo carry
    public int depoRefreshRate = 100; // every one hundred timesteps we refresh the resources in the depo... this also is very arbitrary and could be investigated

    public int numResourceTypes = 3; // fuel is not a resource included here.
    public double maxCostPerResource = 20.0; // for each type of resource we get the price and set it for all of the depos

    public double fuelCost = 1.0;
    public int maxNumTasksPerNeighborhood = 6;
    public int maxJobLength = 10; // change...
    public int maxNumResourcesPerJob = numResourceTypes*2;
    public double basebounty = 100;


    public int simWidth = 100;
    public int simHeight = 100;



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


        // First create the neighborhoods.  Use the mean location as the location for the depos




    }
}
