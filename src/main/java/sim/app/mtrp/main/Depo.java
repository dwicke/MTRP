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

    public Depo(MTRP state, int id, Neighborhood neighborhood) {
        this.state = state;
        this.id = id;

        this.neighborhood = neighborhood;
        // i might actually want to put it at the mean...??? i'll do that for now...
        double x = /*state.random.nextGaussian() * state.taskLocStdDev +*/ neighborhood.meanLocation.getX();
        double y = /*state.random.nextGaussian() * state.taskLocStdDev +*/ neighborhood.meanLocation.getY();

        location = new Double2D(x, y);
        state.getDepoPlane().setObjectLocation(this, location);
        /*
        resources = new Resource[state.numResourceTypes];
        if (state.numResourceTypes > Resource.Type.values().length) throw new AssertionError("The number of MTRP.numResourceTypes is more than the number of Resource.Type");
        int i = 0;
        for (Resource.Type type: Resource.Type.values()) {
            if (i < resources.length) { // only do up to the number of resources I specify.
                resources[i] = new Resource(type, state.random.nextInt(state.getDepoCapacity()),
                        state.random.nextDouble() * state.getMaxCostPerResource(), state.random.nextDouble() * state.getMaxCostPerResource());
                i++;
            }
        }
        */

    }

    public void step(SimState simState) {
        // replenish the supplies

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
}
