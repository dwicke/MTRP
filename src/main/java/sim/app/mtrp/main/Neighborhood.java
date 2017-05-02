package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;

import java.util.ArrayList;

/**
 * Created by drew on 2/20/17.
 */
public class Neighborhood implements Steppable{
    private static final long serialVersionUID = 1;

    MTRP state;
    int id;

    Double2D meanLocation;
    ArrayList<Task> tasks;
    ArrayList<Task> newTask;

    int totalTime, count, totalBounty, totalNumTasksGenerated;




    public Neighborhood(MTRP state, int id) {
        this.state = state;
        this.id = id;

        // first set the mean location for the neighborhood this will always be within the bounds of the simulation size
        meanLocation = new Double2D(state.random.nextDouble(true,true)*state.simWidth, state.random.nextDouble(true,true)*state.simHeight);
        // then generate the initial tasks locations
        tasks = new ArrayList<Task>();
    }


    public void step(SimState simState) {
        // here we decide if we create a new task
        generateTasks();

    }

    public Double2D getMeanLocation() {
        return meanLocation;
    }

    public void setMeanLocation(Double2D meanLocation) {
        this.meanLocation = meanLocation;
    }

    @Override
    public String toString() {
        return "id = " + id + " mean (" + meanLocation.getX() + ", " + meanLocation.getY() + ")" + " numTasks = " + tasks.size();
    }

    public void generateTasks() {
        if (state.random.nextDouble() <  (1.0 / (double) state.timestepsTilNextTask)) {
        //if (state.schedule.getSteps() % state.timestepsTilNextTask == 0) {
            makeTask();
        }else {
            newTask = null;
        }

    }

    public Task makeTask() {
        // generate a new task
        // first generate its coordinates
        //double x = state.random.nextGaussian() * state.taskLocStdDev + meanLocation.getX();
        //double y = state.random.nextGaussian() * state.taskLocStdDev + meanLocation.getY();
        // generate the x and y coordinates within the bounding area of the neighborhood
        double x = meanLocation.getX() + (state.random.nextDouble(true, true) * state.taskLocLength) - state.taskLocLength / 2.0;
        double y = meanLocation.getY() + (state.random.nextDouble(true, true) * state.taskLocLength) - state.taskLocLength / 2.0;
        // generate them within the view
        //double x = (state.random.nextDouble(true, true) * state.getSimWidth());
        //double y = (state.random.nextDouble(true, true) * state.getSimHeight());


        if (newTask == null) {
            newTask = new ArrayList<Task>();
        }
        Task genTask = new Task(this, state, new Double2D(x, y));
        newTask.add(genTask);
        tasks.add(genTask);
        totalNumTasksGenerated++;
        return genTask;
    }

    public void finishedTask(Task task) {
        totalTime += task.timeNotFinished;
        totalBounty += task.getBounty();
        count++;
        tasks.remove(task);
    }

    public int getId() {
        return id;
    }

    public int getTotalNumTasksGenerated() {
        return totalNumTasksGenerated;
    }


    public Task[] getTasksWithNoCommittedAgents() {
        Bag availTasks = new Bag();
        for (int i = 0; i < tasks.size(); i++) {
            if (((Task)tasks.get(i)).getCommittedAgents().isEmpty()) {
                availTasks.add(tasks.get(i));
            }
        }

        return (Task[]) availTasks.toArray(new Task[availTasks.size()]);

    }
}
