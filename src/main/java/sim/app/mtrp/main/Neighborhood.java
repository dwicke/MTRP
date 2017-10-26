package sim.app.mtrp.main;

import sim.app.mtrp.main.agents.Valuators.EquitablePartitions;
import sim.app.mtrp.main.agents.comparisonagents.EquitableAgent;
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
    long waitsquared;

    int totalTime[], count[];
    int totalBounty, totalNumTasksGenerated;

    double timestepsTilNextTask, totalDist, totalBr, totalBaseBounty, timeLastFinished;
    double totalTimeBetween;

    Task latestTask = null;
    double taskCompletionValue = 800;

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

//        if (state.numNeighborhoods == 4) {
//            if (id == 0) {
//                meanLocation = new Double2D(40, 40);
//            } else if (id == 1) {
//                meanLocation = new Double2D(60, 40);
//            } else if (id == 2) {
//                meanLocation = new Double2D(60, 60);
//            } else if (id == 3) {
//                meanLocation = new Double2D(40, 60);
//            }
//        }
        //meanLocation = new Double2D(80, 80);



        meanLocation = new Double2D(state.simHeight / 2, state.getSimWidth() / 2);
       // meanLocation = getCentral();


        // then generate the initial tasks locations
        tasks = new ArrayList<Task>();
        timestepsTilNextTask = state.timestepsTilNextTask;

        totalTime = new int[state.numJobTypes];
        count = new int[state.numJobTypes];
        for (int i =0; i < state.numJobTypes; i++) {
            count[i] = 0;
            totalTime[i] = 0;
        }
    }



    public Double2D getCentral() {

//        int x = id / 10;
//        int y = id % 10;
//
//        double dx = ((x - 5) * 4 + 2) + location.getX();
//        double dy = ((y - 5) * 4 + 2) + location.getY();

        int width = (int)Math.sqrt(state.numAgents);
        if (Math.sqrt(state.numAgents) / width == 1.0) {

            double halfWidth = width / 2.0;
            int x = id / width;
            int y = id % width;

            double length = state.simWidth / width;
            double halfLength = length / 2.0;

            double dx = ((x - halfWidth) * length + halfLength) + meanLocation.getX();
            double dy = ((y - halfWidth) * length + halfLength) + meanLocation.getY();
            //state.printlnSynchronized("Dx = " + dx + " dy = " + dy);
            return new Double2D(dx, dy);
        }else {
            return meanLocation;
        }
    }



    public void step(SimState simState) {
        // here we decide if we create a new task
        if (state.agents.length > 0 && state.agents[0] instanceof EquitableAgent) {
            boolean shouldGenTasks = true;
            for(int i = 0; i < state.numAgents; i++) {
                EquitableAgent a = (EquitableAgent)state.agents[i];
                if (!EquitablePartitions.nearlyEqual(a.getRateInMyPolygon(), 1.0 / state.numAgents, .05)) {
                    shouldGenTasks = false;
                }
            }
            if(shouldGenTasks) {
                generateTasks();
            }else if (state.schedule.getSteps() == 30000) {
                EquitableAgent a = (EquitableAgent)state.agents[0];
                a.setEp(null);
            }
        }else {
            generateTasks();
        }
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

        double totalAllJobTime =0.0;
        double countAll = 0;
        for (int i = 0; i < state.numJobTypes; i++) {
            totalAllJobTime += totalTime[i];
            countAll += count[i];
        }

        if (countAll > 0) {
            neighborhoodBounty += ((double) totalAllJobTime / countAll );
        } else {
            neighborhoodBounty++;
        }

        // bernolli process by sampling geomtric distribution
        // i in effect am producing a poisson process.
        if (state.random.nextDouble() < (1.0 / getTimestepsTilNextTask())) {

            //state.printlnSynchronized("rate = " + (1.0 / getTimestepsTilNextTask()));

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


    public boolean inEpsilon(double x, double y) {
        double epsilon = .5;
        if (y < (state.taskLocLength/2.0) + epsilon && y > (state.taskLocLength/2.0) - epsilon &&
                x < (state.taskLocLength/2.0) + epsilon && x > (state.taskLocLength/2.0) - epsilon) {
            return true;
        }
        return false;
    }


    public Double2D generateLocationInNeighborhood() {
        // first generate its coordinates using a gausian
        double x = state.random.nextGaussian() * state.taskLocStdDev + meanLocation.getX();
        double y = state.random.nextGaussian() * state.taskLocStdDev + meanLocation.getY();

        double neighborhoodLength = state.taskLocLength;// * (1 + 12.0 * state.random.nextDouble(true, true));
        // generate the x and y coordinates within the bounding area of the neighborhood
//        double x, y;

//        if (state.random.nextDouble() < state.delta) {
//            // use rejection sampling to get the distribution
//            // generate those outside the epsilon area
//            do {
//                x = meanLocation.getX() + (state.random.nextDouble(true, true) * neighborhoodLength) - neighborhoodLength / 2.0;
//                y = meanLocation.getY() + (state.random.nextDouble(true, true) * neighborhoodLength) - neighborhoodLength / 2.0;
//            }while(inEpsilon(x, y));
//        }else {
//            // generate those inside the epsilon area
//            do {
//                x = meanLocation.getX() + (state.random.nextDouble(true, true) * neighborhoodLength) - neighborhoodLength / 2.0;
//                y = meanLocation.getY() + (state.random.nextDouble(true, true) * neighborhoodLength) - neighborhoodLength / 2.0;
//            }while(!inEpsilon(x, y));
//        }




        // generate them within the view
//        double x = (state.random.nextDouble(true, true) * state.getSimWidth());
//        double y = (state.random.nextDouble(true, true) * state.getSimHeight());
        return new Double2D(x, y);
    }

    public Task makeTask() {
        // generate a new task
        Task genTask = new Task(this, state, generateLocationInNeighborhood());


        //state.printlnSynchronized("base bounty in neighborhood " + id + " is = " + (totalTime / count));
        genTask.setBaseBounty(getBaseBounty(genTask.getJob().jobType, genTask));
        double br = getBountyRate(genTask.getLocation(),genTask.getJob().jobType);
        totalBr += br;
        genTask.setBountyRate(br);


        totalBaseBounty += genTask.getBounty();
        tasks.add(genTask);
        latestTask = genTask;
        totalNumTasksGenerated++;
        return genTask;
    }

    public double getAverageBountyRate() {
        int thecount = 0;
        for (int i = 0; i < count.length; i++) {
            thecount += count[i];
        }
        if (thecount == 0) {
            return 0.0;
        } else {
            return (double) totalBr / (double) thecount;
        }
    }

    public void finishedTask(Task task) {
        neighborhoodBounty = 0;
        totalTime[task.getJob().jobType] += task.timeNotFinished;
        totalTimeBetween += state.schedule.getSteps() - timeLastFinished;
        timeLastFinished = state.schedule.getSteps();
        waitsquared += Math.pow(task.timeNotFinished, 2);
        totalBounty += task.getBounty();
        totalDist += task.getLocation().distance(meanLocation);
        count[task.getJob().jobType]++;
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

    public double getBaseBounty(int jobType, Task t) {

        return state.basebounty;

//        Depo closestDepo = getClosestDepo(meanLocation);
////        double numTasksHere = tasks.size();
////        double maxOut = 1;
////        // now get the number of tasks in the other neighborhoods
////        for (int i = 0; i < state.numNeighborhoods; i++) {
////            if (i != id && maxOut < state.neighborhoods[i].tasks.size()) {
////                maxOut = state.neighborhoods[i].tasks.size();
////            }
////        }
//        int thetotaltime = 0;
//        int thecount = 0;
//        for (int i = 0; i < state.numJobTypes; i++) {
//            thetotaltime += totalTime[i];
//            thecount += count[i];
//        }
//
//        double weight = 1.0;//state.numJobTypes - jobType;//(state.jobLength / (jobType + 1)) / (state.jobLength);// + (numTasksHere / maxOut);
//
//        if (state.hasBountyRate == false) {
//            return state.basebounty;
//        }else {
//            return closestDepo.getFuelCost()*closestDepo.location.distance(t.getLocation());
//        }

        // c / (d1 + s1) = k / (d2 + s2)
        // s1 > s2
        // k = c * (d2 + s2) / (d1 + s1)
        // since d1 and d2 are unknown
        // and are different for each agent
        // setting the price so that agents
        // will pursue the closest task is not possible
        //

//
//        if(thecount == 0) {
//            return  weight * (getTaskCompletionValue(jobType) + state.getMaxCostPerResource() * (double) state.maxMeanResourcesNeededForType * state.getNumResourceTypes());
//        } else {
//            //double expectedDist = state.taskLocLength / (Math.sqrt((1.0 / state.getTimestepsTilNextTask()) * ((double) thetotaltime / (double) thecount )));
//
//            //return weight * (getTaskCompletionValue(jobType) + closestDepo.getFuelCost() * expectedDist + (double) state.getMaxCostPerResource() * (double) state.maxMeanResourcesNeededForType * state.getNumResourceTypes());
//            return  (getTaskCompletionValue(jobType) + closestDepo.getFuelCost() * ((double) thetotaltime / (double) thecount ) + (double) state.getMaxCostPerResource() * (double) state.maxMeanResourcesNeededForType * state.getNumResourceTypes());
//        }
    }

    public double getTaskCompletionValue(int jobtype) {
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
            return taskCompletionValue;

    }

    public double getBountyRate(Double2D loc, int jobtype) {


        //Depo closestDepo = getClosestDepo(loc);

        return state.increment;


        // if some tasks are really suddenly far out this will rise rather quickly, but if it is maintained then the denominator should also rise as well
        // and should stabilize
//        if (count[jobtype] == 0) {
//            return closestDepo.getFuelCost();
//        }
//        int thetotaltime = 0;
//        int thecount = 0;
//        for (int i = 0; i < state.numJobTypes; i++) {
//            thetotaltime += totalTime[i];
//            thecount += count[i];
//        }
//
//
//        //double expectedDist = state.taskLocLength / (Math.sqrt((1.0 / state.getTimestepsTilNextTask()) * ((double) thetotaltime / (double) thecount )));
//
//        //return ( (( (state.jobLength / (jobtype + 1))) * closestDepo.getFuelCost()) / ( state.jobLength + expectedDist));
//        return ( ((stepDistance(loc, closestDepo.getLocation())) * closestDepo.getFuelCost()) / ( ((double) thetotaltime / (double) thecount )));

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

    public int getTotalCount() {
        int thecount = 0;
        for (int i = 0; i < state.numJobTypes; i++) {
            thecount += count[i];
        }
        return thecount;
    }

    public double getMeanTimeBetweenTaskCompletion() {
        double thecount = getTotalCount();
        if (thecount > 0) {
            return totalTimeBetween / thecount;
        }
        return 0;
    }
}
