package sim.app.mtrp.main;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;

import javax.swing.*;
import java.awt.*;

/**
 * Created by drew on 2/21/17.
 */
public class MTRPWithUI extends GUIState {
    private static final long serialVersionUID = 1;

    public Display2D display;
    public JFrame displayFrame;

    // the goals grid displays the locations where balls can be dropped off at
    // each type of ball has a particular goal location.  So, ball type 1 has goal location 1
    // ball type 2 has goal location 2.  There can be multiple balls of type 1 but they
    // may be of different tasks but they each must be brought to goal location 1.


    // the grid that displays the balls.
    SparseGridPortrayal2D ballGridPortrayal = new SparseGridPortrayal2D();
    SparseGridPortrayal2D robotPortrayal = new SparseGridPortrayal2D();

    public static void main(String[] args) {
        new MTRPWithUI().createController();
    }

    public MTRPWithUI() {
        //super(new Bounties("kelsey".hashCode()));
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
        return "Bounties";
    }

    public void setupPortrayals() {
        MTRP bounties = (MTRP) state;
        // tell the portrayals what to portray and how to portray them
//        ballGridPortrayal.setField(bounties.tasksGrid);
//
//
//        for(int i = 0; i < bounties.tasksGrid.allObjects.numObjs; i++) {
//            ballGridPortrayal.setPortrayalForObject(bounties.tasksGrid.allObjects.objs[i],
//                    new MovablePortrayal2D(new TaskPortrayal((Task)bounties.tasksGrid.allObjects.objs[i])));
//        }
//
//
//        robotPortrayal.setField(bounties.robotgrid);
//        for(int i = 0; i < bounties.robotgrid.allObjects.numObjs; i++) {
//            IAgent ir = (IAgent) bounties.robotgrid.allObjects.objs[i];
//            robotPortrayal.setPortrayalForObject(bounties.robotgrid.allObjects.objs[i],
//                    new MovablePortrayal2D(new LabelledPortrayal2D(new RobotPortrayal(ir), "id: " + ir.getId())));
//        }
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
        display = new Display2D(600, 400, this); // at 400x400, we've got 4x4 per array position
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        // attach the portrayals from bottom to top
        display.attach(ballGridPortrayal, "Tasks");
        display.attach(robotPortrayal, "Agents");

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
