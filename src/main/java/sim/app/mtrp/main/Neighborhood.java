package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;

/**
 * Created by drew on 2/20/17.
 */
public class Neighborhood implements Steppable{
    private static final long serialVersionUID = 1;

    MTRP mtrp;
    int id;

    Double2D meanLocation;
    Bag tasks;



    public Neighborhood(MTRP mtrp, int id) {
        this.mtrp = mtrp;
        this.id = id;

        // first set the mean location for the neighborhood
        //meanLocation = new Double2D(mtrp.random.nextD)
        // then


    }


    public void step(SimState simState) {
        // here we decide if we create a new task

    }
}
