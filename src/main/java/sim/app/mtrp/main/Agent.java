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
    Job curJob;
    Double2D curDestination;
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
        curDestination = new Double2D(curLocation.getX(), curLocation.getY());
        state.getAgentPlane().setObjectLocation(this, curLocation);
    }

    public void step(SimState simState) {
        pickDestination(); // where do I want to go??
        buySellResources(); // what do I need when I get there?
        travel(); // lets go!
    }

    public void pickDestination() {

    }

    public void buySellResources() {

    }


    public void travel() {


        // example
        // 3-4-5 triangle
        // sqrt((3/5*.7)^2 + (4/5*.7)^2) = .7
        // therefore if I move (3/5*.7) in the x direction and (4/5*.7) in the y direction I will end up only going .7
        // so do that. essentially normalizing on the euclidean distance.
        double dis = curLocation.distance(curDestination);
        if (dis == 0.0) // might need to account for some error here eventually...
            return; // don't move already at destination.
        double dx = curDestination.getX() - curLocation.getX();
        dx = dx/dis * state.getStepsize();

        double dy = curDestination.getY() - curLocation.getY();
        dy = dy/dis * state.getStepsize();

        curLocation = new Double2D(curLocation.getX() + dx, curLocation.getY() + dy);
        // now travel there!
        state.getAgentPlane().setObjectLocation(this, curLocation);
    }
}
