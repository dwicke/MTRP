package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Created by drew on 2/20/17.
 */
public class Bondsman implements Steppable {
    private static final long serialVersionUID = 1;

    MTRP state;

    public Bondsman(MTRP state) {
        this.state = state;
    }

    public void step(SimState simState) {

    }
}
