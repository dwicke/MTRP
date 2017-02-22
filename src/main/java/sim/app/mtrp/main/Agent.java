package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Double2D;

/**
 * Created by drew on 2/20/17.
 */
public class Agent implements Steppable {
    private static final long serialVersionUID = 1;

    MTRP state;
    int id;
    double fuelCapacity;
    double curFuel;
    double bounty;
    int resourcesQuantities[];
    int curTotalNumResources;
    double stepsize = 0.7; // this is the max distance I can travel in one step
    Job curJob;
    Double2D curLocation;

    public Agent(MTRP state, int id) {
        this.state = state;
        this.id = id;
        resourcesQuantities = new int[state.getNumResourceTypes()];
        bounty = state.getStartFunds();
        curFuel = state.getFuelCapacity();
        fuelCapacity = state.getFuelCapacity();
        curTotalNumResources = 0;
        // pick a random depo and start there
        Depo startDepo = state.getDepos()[state.random.nextInt(state.getDepos().length)];
        curLocation = new Double2D(startDepo.location.getX(), startDepo.location.getY());
        state.getAgentPlane().setObjectLocation(this, curLocation);
    }

    public void step(SimState simState) {

        buySellResources();
        pickDestination();
        travel();

    }


    public void buySellResources() {

    }

    public void pickDestination() {

    }


    public void travel() {
        if (curJob != null) {
            // then lets go!
            //
        }
    }
}
