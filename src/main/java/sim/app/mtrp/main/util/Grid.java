package sim.app.mtrp.main.util;

import isula.aco.*;
import isula.aco.algorithms.antsystem.OfflinePheromoneUpdate;
import isula.aco.algorithms.antsystem.PerformEvaporation;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.algorithms.antsystem.StartPheromoneMatrix;
import isula.aco.exception.InvalidInputException;
import isula.aco.tsp.TspEnvironment;
import sim.app.mtrp.main.Task;
import sim.util.Double2D;
import sim.util.Int2D;
import tsp.isula.sample.TspProblemConfiguration;

import javax.naming.ConfigurationException;
import java.util.*;

import static tsp.isula.sample.AcoTspWithIsula.getAntColony;

/**
 * Created by drew on 4/29/17.
 */
public class Grid {

    private final double discretization;
    private final int width;
    private final int height;
    private int tourLength;
    Map<Int2D, ArrayList<Task>> grid;
    ArrayList<List<Task>> tasks;

    public Grid(double discretization, int width, int height, int tourLength) {
        this.discretization = discretization;
        this.width = width;
        this.height = height;
        this.tourLength = tourLength;
        grid = new HashMap<Int2D, ArrayList<Task>>();
        tasks = new ArrayList<List<Task>>();
    }


    /**
     *
     * @param newTasks Only the newly created tasks
     */
    public void addNewTasks(Task[] newTasks) {
        for (Task t : newTasks) {
            addNewTask(t);
        }
    }

    public void addNewTask(Task t) {
        Int2D key = discretize(t.getLocation());
        if (!grid.containsKey(key)) {
            grid.put( discretize(t.getLocation()), new ArrayList<Task>());
        }
        grid.get(key).add(t);
    }

    public void updateQueue() {
        for (ArrayList<Task> cell : grid.values()) {
            while (cell.size() >= tourLength) {
                // then we have achieved the size we need so create a tour and add to the tasks
                List<Task> newTasks = cell.subList(0, tourLength);
                tasks.add(getBestTask(newTasks.toArray(new Task[newTasks.size()])));
                cell.removeAll(newTasks);
            }
        }
    }

    public List<Task> getTour() {
        if (tasks.size() > 0) {
            return tasks.remove(0);
        }
        return null;
    }


    public final Int2D discretize(Double2D location) {
        return new Int2D((int)(location.x / this.discretization), (int)(location.y / this.discretization));
    }




    public List<Task> getBestTask(Task bagOfTasks[]) {


        double[][] problemRepresentation = new double[bagOfTasks.length][2];

        for (int i = 0; i < bagOfTasks.length; i++) {
            problemRepresentation[i][0] = bagOfTasks[i].getLocation().getX();
            problemRepresentation[i][1] =  bagOfTasks[i].getLocation().getY();
        }

        TspProblemConfiguration configurationProvider = new TspProblemConfiguration(problemRepresentation);
        AntColony<Integer, TspEnvironment> colony = getAntColony(configurationProvider);
        TspEnvironment environment = null;
        try {
            environment = new TspEnvironment(problemRepresentation);
        } catch (InvalidInputException e) {
            e.printStackTrace();
            return null;
        }

        AcoProblemSolver<Integer, TspEnvironment> solver = new AcoProblemSolver<Integer, TspEnvironment>();
        solver.initialize(environment, colony, configurationProvider);
        solver.addDaemonActions(new StartPheromoneMatrix<Integer, TspEnvironment>(),
                new PerformEvaporation<Integer, TspEnvironment>());

        solver.addDaemonActions(getPheromoneUpdatePolicy());

        solver.getAntColony().addAntPolicies(new RandomNodeSelection<Integer, TspEnvironment>());
        try {
            solver.solveProblem();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<Task> bestSolution = new ArrayList<Task>();
        for (int i = 0; i < solver.getBestSolution().length; i++) {
            bestSolution.add(bagOfTasks[solver.getBestSolution()[i]]);
        }
        return bestSolution;
    }


    /**
     * On TSP, the pheromone value update procedure depends on the distance of the generated routes.
     *
     * @return A daemon action that implements this procedure.
     */
    private static DaemonAction<Integer, TspEnvironment> getPheromoneUpdatePolicy() {
        return new OfflinePheromoneUpdate<Integer, TspEnvironment>() {
            @Override
            protected double getNewPheromoneValue(Ant<Integer, TspEnvironment> ant,
                                                  Integer positionInSolution,
                                                  Integer solutionComponent,
                                                  TspEnvironment environment,
                                                  ConfigurationProvider configurationProvider) {
                Double contribution = 1 / ant.getSolutionCost(environment);
                return ant.getPheromoneTrailValue(solutionComponent, positionInSolution, environment) + contribution;
            }
        };
    }
}
