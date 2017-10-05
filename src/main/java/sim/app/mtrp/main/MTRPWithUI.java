package sim.app.mtrp.main;

import sim.app.mtrp.main.portrayals.AgentPortrayal;
import sim.app.mtrp.main.portrayals.DepoPortrayal;
import sim.app.mtrp.main.portrayals.TaskPortrayal;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.util.gui.SimpleColorMap;

import javax.swing.*;
import java.awt.*;

/**
 * Created by drew on 2/21/17.
 */
public class MTRPWithUI extends GUIState {
    private static final long serialVersionUID = 1;

    public Display2D display;
    public JFrame displayFrame;


    ContinuousPortrayal2D agentsPortrayal = new ContinuousPortrayal2D();
    ContinuousPortrayal2D tasksPortrayal = new ContinuousPortrayal2D();
    ContinuousPortrayal2D deposPortrayal = new ContinuousPortrayal2D();

    // portrayals for where the agents have completed tasks
    FastValueGridPortrayal2D areaDominance[];


    public static void main(String[] args) {
        new MTRPWithUI().createController();
    }

    public MTRPWithUI() {
        super(new MTRP(System.currentTimeMillis()));
    }

    public MTRPWithUI(SimState state) {
        super(state);
    }

    // allow the user to inspect the model
    public Object getSimulationInspectedObject() {
        return state;
    }  // non-volatile

    public static String getName() {
        return "MTRP with bounty hunting";
    }

    public void setupPortrayals() {
        MTRP myState = (MTRP) state;


        agentsPortrayal.setField(myState.agentPlane);
        // now setup the portrayals for the objects in this plane
        for (Agent a: myState.getAgents()) {
            agentsPortrayal.setPortrayalForObject(a, new MovablePortrayal2D(new LabelledPortrayal2D(new AgentPortrayal(a), "id = " + a.id)));
        }



        tasksPortrayal.setField(myState.taskPlane);
        tasksPortrayal.setPortrayalForAll(new MovablePortrayal2D(new TaskPortrayal()));

        deposPortrayal.setField(myState.depoPlane);
        deposPortrayal.setPortrayalForAll(new MovablePortrayal2D(new DepoPortrayal()));
        Color colors[] = new Color[4];
        colors[0] = Color.RED;
        colors[1] = Color.BLUE;
        colors[2] = Color.GREEN;
        colors[3] = Color.BLACK;
        areaDominance = new FastValueGridPortrayal2D[myState.numAgents];
        for (int i = 0; i < myState.numAgents; i++) {
            myState.printlnSynchronized("Making new area");
            areaDominance[i] = new FastValueGridPortrayal2D("dominance" + i);
            areaDominance[i].setField(myState.valgrid[i]);
            areaDominance[i].setMap(new SimpleColorMap(0, MTRP.MAX_TASK, new Color(0,0,0,0), colors[i]));
        }
        display.detachAll();
        display.attach(agentsPortrayal, "Agents");
        display.attach(tasksPortrayal, "Tasks");
        display.attach(deposPortrayal, "Depos");
        for (int i =0; i < areaDominance.length; i++) {

            display.attach(areaDominance[i], "Dominance" + i);
            System.out.println("WOOOHOO");
        }
        // reschedule the displayer
        display.reset();

        // redraw the display
        display.repaint();
    }

    public void start() {
        super.start();  // set up everything but replacing the display
        // set up our portrayals
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        // we now have new grids.  Set up the portrayals to reflect that
        setupPortrayals();
    }



    public void init(Controller c) {
        super.init(c);

        // Make the Display2D.  We'll have it display stuff later.
        display = new Display2D(1000, 1000, this);
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        // attach the portrayals from bottom to top
        display.attach(agentsPortrayal, "Agents");
        display.attach(tasksPortrayal, "Tasks");
        display.attach(deposPortrayal, "Depos");


        // specify the backdrop color  -- what gets painted behind the displays
        display.setBackdrop(Color.white);
    }

    public void quit() {
        super.quit();

        // disposing the displayFrame automatically calls quit() on the display,
        // so we don't need to do so ourselves here.
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;  // let gc
        display = null;       // let gc
    }

}
