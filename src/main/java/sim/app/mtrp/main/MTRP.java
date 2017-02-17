package sim.app.mtrp.main;

import sim.engine.*;

/**
 * Entry into the mason simulator.
 * Created by drew on 2/16/17.
 */
public class MTRP extends SimState {

    private static final long serialVersionUID = 1;
    private static String[] myArgs;

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
    }
}
