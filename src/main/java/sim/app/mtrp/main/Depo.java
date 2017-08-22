package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

/**
 * Created by drew on 2/20/17.
 */
public class Depo implements Steppable{
    private static final long serialVersionUID = 1;

    private final int id;
    MTRP state;

    Double2D location;
    Neighborhood neighborhood;
    Resource resources[];
    double totalFuelPurchased = 0;

    public Depo(MTRP state, int id, Neighborhood neighborhood) {
        this.state = state;
        this.id = id;

        this.neighborhood = neighborhood;
//        // i might actually want to put it at the mean...??? i'll do that for now...
//        double x = /*state.random.nextGaussian() * state.taskLocStdDev +*/ neighborhood.meanLocation.getX();
//        double y = /*state.random.nextGaussian() * state.taskLocStdDev +*/ neighborhood.meanLocation.getY();
//
//        location = new Double2D(x, y);

       //location = this.neighborhood.generateLocationInNeighborhood();

        //location = this.neighborhood.meanLocation;

        // so i go from the index to the center of the grid cell
        location = getCentral();



        state.getDepoPlane().setObjectLocation(this, location);

        resources = new Resource[state.numResourceTypes];
        for (int i = 0; i < state.numResourceTypes; i++) {
                resources[i] = new Resource(i, state.getDepoCapacity(),
                        state.random.nextDouble() * state.getMaxCostPerResource(), state.random.nextDouble() * state.getMaxCostPerResource());
        }


    }


    public Double2D getCentral() {

//        int x = id / 10;
//        int y = id % 10;
//
//        double dx = ((x - 5) * 4 + 2) + location.getX();
//        double dy = ((y - 5) * 4 + 2) + location.getY();

        int width = (int)Math.sqrt(state.numAgents);
        if (Math.sqrt(state.numAgents) / width == 1.0) {

            double halfWidth = width / 2.0;
            int x = id / width;
            int y = id % width;

            double length = state.taskLocLength / width;
            double halfLength = length / 2.0;

            double dx = ((x - halfWidth) * length + halfLength) + location.getX();
            double dy = ((y - halfWidth) * length + halfLength) + location.getY();

            return new Double2D(dx, dy);
        }else {
            return location;
        }
    }

    public void step(SimState simState) {
        // replenish the supplies
        for (Resource r : resources)
            r.replenish();
    }

    public Resource[] getResources() {
        return resources;
    }

    public int getID() {
        return id;
    }

    public double getFuelCost() {
        //return neighborhood.getAverageBountyRate();
        return state.getFuelCost();  /// set by the user
    }

    public double getResourceCost(int resourceID) {
        return resources[resourceID].getBuyPrice();
    }

    public double getResourceBuyBackPrice(int resourceID) {
        return resources[resourceID].buybackPrice;
    }

    public double buyBack(int resourceType, int quantity) {
        return resources[resourceType].buyBack(quantity);
    }

    public double buy(int resourceType, int numShouldBuy) {
        return resources[resourceType].buy(numShouldBuy);
    }

    public Double2D getLocation() {
        return location;
    }

    public void buyFuel(double fuelBought) {
        totalFuelPurchased += fuelBought;
    }

    public double getTotalFuelPurchased() {
        return totalFuelPurchased;
    }
}
