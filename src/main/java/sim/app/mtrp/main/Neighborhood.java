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

    int totalTime, count, totalBounty, totalNumTasksGenerated;

    double timestepsTilNextTask, totalDist, totalBr, totalBaseBounty, timeLastFinished;

    Task latestTask = null;
    double taskCompletionValue = 3000.0;

    double neighborhoodBounty = 0;



    public Neighborhood(MTRP state, int id) {
        this.state = state;
        this.id = id;

        // first set the mean location for the neighborhood this will always be within the bounds of the simulation size
        meanLocation = new Double2D(20 + state.random.nextDouble(true,true)*(state.simWidth - 40), 20 + state.random.nextDouble(true,true)*(state.simHeight -40));
//        if (state.numNeighborhoods == 4) {
//            if (id == 0) {
//                meanLocation = new Double2D(state.simWidth - 20, state.simHeight - 20);
//            } else if (id == 1) {
//                meanLocation = new Double2D(0 + 20, 20);
//            } else if (id == 2) {
//                meanLocation = new Double2D(20, state.simHeight - 20);
//            } else if (id == 3) {
//                meanLocation = new Double2D(state.simWidth - 20, 20);
//            }
//        }

        // then generate the initial tasks locations
        tasks = new ArrayList<Task>();
        timestepsTilNextTask = state.timestepsTilNextTask;
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




    double lastTime = 0;
    double totalInterTime = 0;
    double numTask = 0;
    public void generateTasks() {

        if (count > 0) {
            neighborhoodBounty += ((double) totalTime / (double) count );
            //neighborhoodBounty++;
        } else {
            neighborhoodBounty++;
        }

        // bernolli process by sampling geomtric distribution
        // i in effect am producing a poisson process.
        if (state.random.nextDouble() < (1.0 / getTimestepsTilNextTask())) {
         //   if (state.schedule.getSteps() % state.timestepsTilNextTask == 0) {
//            double interTime = state.schedule.getTime() - lastTime;
            numTask++;
//            lastTime = state.schedule.getTime();
//            totalInterTime += interTime;
//            state.printlnSynchronized(" average time = " + totalInterTime / numTask);
            makeTask();

        } else {
            //latestTask = null;
        }



    }


    public Double2D generateLocationInNeighborhood() {
        // first generate its coordinates using a gausian
//        double x = state.random.nextGaussian() * state.taskLocStdDev + meanLocation.getX();
//        double y = state.random.nextGaussian() * state.taskLocStdDev + meanLocation.getY();

        double neighborhoodLength = state.taskLocLength;// * (1 + 12.0 * state.random.nextDouble(true, true));
        // generate the x and y coordinates within the bounding area of the neighborhood
        double x = meanLocation.getX() + (state.random.nextDouble(true, true) * neighborhoodLength) - neighborhoodLength / 2.0;
        double y = meanLocation.getY() + (state.random.nextDouble(true, true) * neighborhoodLength) - neighborhoodLength / 2.0;




        // generate them within the view
        //double x = (state.random.nextDouble(true, true) * state.getSimWidth());
        //double y = (state.random.nextDouble(true, true) * state.getSimHeight());
        return new Double2D(x, y);
    }

    public Task makeTask() {
        // generate a new task
        Task genTask = new Task(this, state, generateLocationInNeighborhood());


        //state.printlnSynchronized("base bounty in neighborhood " + id + " is = " + (totalTime / count));
        genTask.setBaseBounty(getBaseBounty());
        double br = getBountyRate(genTask.getLocation());
        totalBr += br;
        genTask.setBountyRate(br);


        totalBaseBounty += genTask.getBounty();
        tasks.add(genTask);
        latestTask = genTask;
        totalNumTasksGenerated++;
        return genTask;
    }

    public double getAverageBountyRate() {
        if (count == 0) {
            return 0.0;
        } else {
            return (double) totalBr / (double) count;
        }
    }

    public void finishedTask(Task task) {
        neighborhoodBounty = 0;
        totalTime += task.timeNotFinished;
        timeLastFinished = state.schedule.getSteps();
        totalBounty += task.getBounty();
        totalDist += task.getLocation().distance(meanLocation);
        count++;
        tasks.remove(task);
    }

    public double getNeighborhoodBounty() {
        return neighborhoodBounty;
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

    public void setTimestepsTilNextTask(double timestepsTilNextTask) {
        this.timestepsTilNextTask = timestepsTilNextTask;
    }

    public double getTimestepsTilNextTask() {
        return timestepsTilNextTask;
    }

    public Task getLatestTask() {
        return latestTask;
    }

    public double getTotalBounty() {
        double total = 0.0;
        for (Task t : tasks) {
            total += t.getBounty();
        }
        return total;
    }

    public Task[] getTasks() {
        if (tasks.size() == 0) {
            return new Task[0];
        }
        return tasks.toArray(new Task[tasks.size()]);
    }

    public double getBaseBounty() {

        Depo closestDepo = getClosestDepo(meanLocation);
//        double numTasksHere = tasks.size();
//        double maxOut = 1;
//        // now get the number of tasks in the other neighborhoods
//        for (int i = 0; i < state.numNeighborhoods; i++) {
//            if (i != id && maxOut < state.neighborhoods[i].tasks.size()) {
//                maxOut = state.neighborhoods[i].tasks.size();
//            }
//        }

        double weight = 1;// + (numTasksHere / maxOut);
        if(count == 0) {
            return  weight * (getTaskCompletionValue() + state.getMaxCostPerResource() * (double) state.maxMeanResourcesNeededForType * state.getNumResourceTypes());
        } else {
            return weight * (getTaskCompletionValue() + closestDepo.getFuelCost() * ((double) totalTime / (double) count )+ (double) state.getMaxCostPerResource() * (double) state.maxMeanResourcesNeededForType * state.getNumResourceTypes());
        }
    }

    public double getTaskCompletionValue() {
//        double sum = 0;
//        if(tasks.size() == 0) {
//            return 1.0;
//        }
//        for(Task t : tasks) {
//            sum += t.getTimeNotFinished();
//        }
//        if (sum > 0) {
//            return sum / (double) tasks.size();
//        }
//        return 1;
        return 3600;
    }

    public double getBountyRate(Double2D loc) {
        Depo closestDepo = getClosestDepo(loc);

        // if some tasks are really suddenly far out this will rise rather quickly, but if it is maintained then the denominator should also rise as well
        // and should stabilize
        if (count == 0) {
            return closestDepo.getFuelCost();
        }



        return ( (stepDistance(loc, closestDepo.getLocation()) * closestDepo.getFuelCost()) / ((double) totalTime / (double) count ));

    }

    public double stepDistance(Double2D d, Double2D loc) {
        return (int) Math.floor((d.distance(loc))/state.getStepsize());
    }

    public Depo getClosestDepo(Double2D loc) {
        Depo[] depos = state.getDepos();
        double curMinDist = Double.MAX_VALUE;
        Depo closeset = null;
        for (Depo d : depos) {
            double dist = (int) Math.floor((d.getLocation().distance(loc))/state.getStepsize());//getNumTimeStepsFromLocation(d.location, loc);

            if (dist < curMinDist) {
                curMinDist = dist;
                closeset = d;
            }
        }

        return closeset;

    }


    public double getTotalDist() {
        return totalDist;
    }

    public double getTotalBaseBounty() {
        return totalBaseBounty;
    }

    public double getTotalBountyRate() {
        return totalBr;
    }

}
