package sim.app.mtrp.main;

import isula.aco.*;
import isula.aco.algorithms.antsystem.OfflinePheromoneUpdate;
import isula.aco.algorithms.antsystem.PerformEvaporation;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.algorithms.antsystem.StartPheromoneMatrix;
import isula.aco.exception.InvalidInputException;
import isula.aco.tsp.TspEnvironment;
import sim.app.mtrp.main.agents.ACOAgent;
import sim.app.mtrp.main.util.Grid;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Int2D;
import tsp.isula.sample.TspProblemConfiguration;

import javax.naming.ConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static tsp.isula.sample.AcoTspWithIsula.getAntColony;

/**
 * Created by drew on 4/27/17.
 */
public class TaskMaster implements Steppable {

    MTRP state;
    int tourLength = 5;
    int discretization = 30;
    Grid grid;


    public TaskMaster(MTRP state) {
        this.state = state;
        grid = new Grid(discretization, state.getSimWidth(), state.getSimHeight(), tourLength);
    }




    public void step(SimState simState) {

        for (Neighborhood n : state.getNeighborhoods()) {
            if (n.newTask != null)
                grid.addNewTasks(n.newTask.toArray(new Task[n.newTask.size()]));
        }
        grid.updateQueue();

        for (int i = 0; i < state.getAgents().length; i++) {
            if (state.getAgents()[i] instanceof  ACOAgent) {
                if (((ACOAgent) state.getAgents()[i]).getIsDoneTasks()) {

                    List<Task> tasks = grid.getTour();
                    if (tasks != null)
                        ((ACOAgent) state.getAgents()[i]).assignTasks(tasks.toArray(new Task[tasks.size()]));
                }
            }
        }

    }

    public int getTourLength() {
        return tourLength;
    }
}
