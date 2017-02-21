package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by drew on 2/20/17.
 */
public class Agent implements Steppable {
    private static final long serialVersionUID = 1;

    MTRP state;
    int id;

    public Agent(MTRP state, int id) {
        this.state = state;
        this.id = id;
    }

    public void step(SimState simState) {




    }
}
