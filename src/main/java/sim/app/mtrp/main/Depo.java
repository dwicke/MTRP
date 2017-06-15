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
        // i might actually want to put it at the mean...??? i'll do that for now...
        double x = /*state.random.nextGaussian() * state.taskLocStdDev +*/ neighborhood.meanLocation.getX();
        double y = /*state.random.nextGaussian() * state.taskLocStdDev +*/ neighborhood.meanLocation.getY();

        location = new Double2D(x, y);
        state.getDepoPlane().setObjectLocation(this, location);

        resources = new Resource[state.numResourceTypes];
        for (int i = 0; i < state.numResourceTypes; i++) {
                resources[i] = new Resource(i, state.getDepoCapacity(),
                        state.random.nextDouble() * state.getMaxCostPerResource(), state.random.nextDouble() * state.getMaxCostPerResource());
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
        return state.getFuelCost();
    }

    public double getResourceCost(int resourceID) {
        return resources[resourceID].buyPrice;
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
}
