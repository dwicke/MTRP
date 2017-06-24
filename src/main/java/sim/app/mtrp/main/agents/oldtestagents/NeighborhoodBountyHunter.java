package sim.app.mtrp.main.agents.oldtestagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Neighborhood;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.Valuators.AgentLocationPredictor;
import sim.app.mtrp.main.agents.Valuators.Auction;
import sim.app.mtrp.main.util.QTable;
import sim.util.Bag;
import sim.util.Double2D;

import java.util.Map;

/**
 *
 * I'm going to do bounty hunting on the neighborhoods
 * but auction within the neighborhood if there are other
 * agents operating in the neighborhood.
 *
 * Created by drew on 6/15/17.
 */
public class NeighborhoodBountyHunter extends Agent {


    Neighborhood curNeighborhood = null;
    QTable proportionOfBounty, tTable;
    double oneUpdateGamma = .001; // .001
    double tLearningRate = .5; // set to .1 originally (should be at .95 though...) tried .75
    double tDiscountBeta = .1; // not used...
    double epsilonChooseRandomTask =  .002; // was .002


    // this will be bounty hunting where the auction is more like an agreement that you won't go after the task
    // unless you pay a price to the other agent if you beat him to the task....
    // that might be better than just a plain auction.
    // then we have the coordination mechansim of the auction.  but we also have the non-exclusivity of bounty hunting.


    // better yet improve on the bounty hunting idea where I learn the location of the other agent within
    // the neighborhood and instead of doing what I did earlier where I predicted his next step and not really
    // doing anything else.  Actually run a virtual auction within myself. given I know where the agent was.

    AgentLocationPredictor alp;
    Auction a;

    public NeighborhoodBountyHunter(MTRP state, int id) {
        super(state, id);
        a = new Auction(state);
        alp = new AgentLocationPredictor(state);
        tTable = new QTable(state.numJobTypes + state.numEmergentJobTypes, 1, tLearningRate, tDiscountBeta, state.random, state.getJobLength(), 0.0);
        proportionOfBounty = new QTable(state.getNumNeighborhoods(), 1, tLearningRate, tDiscountBeta, state.random);
    }

    public Task getAvailableTask() {
        // 1. pick neighborhood
        // 2. pick task
        // 3. return task
        alp.updatePositionPrediction(curJob, tTable);
        return pickTask(pickNeighborhood());
    }

    public Neighborhood pickNeighborhood() {
        Neighborhood bestNeighborhood = null;
        double maxUtilty = -1.0;

        for (Neighborhood n : state.getNeighborhoods()) {
            double util = getNeighborhoodUtility(n);
            if (util > maxUtilty) {
                bestNeighborhood = n;
                maxUtilty = util;
            }
        }
        return bestNeighborhood;
    }

    private double getNeighborhoodUtility(Neighborhood n) {

        // do 1 + since I will be in it and alp ignores me
        double totalBounty = n.getTotalBounty() / (1.0 + (double) alp.getNumAgentsInNeighborhood(n) + getNumTimeStepsFromLocation(n.getMeanLocation(), curLocation));
        if (id == 0)
            state.printlnSynchronized("Total bounty = " + totalBounty + " neighborhood = " + n.getId());
        return totalBounty;

    }


    private Task pickTask(Neighborhood neighborhood) {
        // now pick a task within the neighborhood
        // to do so I simulate running an auction based on the locations of the other agents
        // that I have modeled.

        Bag b = getTasksWithinRange(neighborhood.getTasks());
        Task[] availableTasks = (Task[]) b.toArray(new Task[b.numObjs]);
        // now get the valuations for all of the agents within this neighborhood for these tasks
        int numAgentsInNeighborhood = 1 + alp.getNumAgentsInNeighborhood(neighborhood);
        if (numAgentsInNeighborhood > 1) {
            int myId = this.getId() % numAgentsInNeighborhood;
            double[][] valuations = new double[numAgentsInNeighborhood][availableTasks.length];

            int index = 0;
            for (Map.Entry<Task, Integer> en : alp.getAgentLocations().entrySet()) {
                // for each agent i get the simulated valuation for all of the tasks in the neighborhood
                if (index == myId) {
                    valuations[index] = getEvaluations(availableTasks, curLocation);
                } else {
                    try {
                        valuations[index] = getEvaluations(availableTasks, en.getKey().getLocation());
                    }catch (ArrayIndexOutOfBoundsException e) {
                        state.printlnSynchronized("index out of bounds! index = " + index + " valuations length = " + valuations.length + " num valuations = " + getEvaluations(availableTasks, en.getKey().getLocation()).length);
                    }
                }
                index++;
            }

            return availableTasks[a.runAuction(availableTasks.length, valuations, myId)];
        }


        if (availableTasks.length == 0 && curJob == null) {
            return null; // need to go for resources.
        } else if (availableTasks.length == 0 && curJob != null) {
            return curJob.getTask();
        }

        // epsilon random pick task
        if (state.random.nextDouble() < epsilonChooseRandomTask && availableTasks.length > 0) {
            Task randTask = (Task) availableTasks[state.random.nextInt(availableTasks.length)];
            return randTask;
        }

        // otherwise just pick it using real method
        Task[] tasks = availableTasks;
        Task chosenTask = null;
        double curMax = 0.0;
        if (curJob != null) {
            chosenTask = curJob.getTask();
            curMax = getUtility(chosenTask, curLocation);
            if (Double.isInfinite(curMax)) {
                return chosenTask;
            }
        }
        for (Task t : tasks) {

            double value = getUtility(t, curLocation);
            if (value > curMax) {
                chosenTask = t;
                curMax = value;
            }
        }
        return chosenTask;
    }

    public double[] getEvaluations(Task[] availTasks, Double2D loc) {
        double[] valuations = new double[availTasks.length];
        int i = 0;
        for (Task t: availTasks) {
            valuations[i] = getUtility(t, loc);
            i++;
        }
        return valuations;
    }



    double getUtility(Task t, Double2D loc) {
        double util =  ((-getCost(t, loc) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation(), loc) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation(), loc) + tTable.getQValue(t.getJob().getJobType(), 0));
        return util;
    }

    double getCost(Task t, Double2D loc) {
        // closest depo will never be null because we only consider tasks that are within distance of a depo
        return getNumTimeStepsFromLocation(t.getLocation(), loc) * getClosestDepo(t.getLocation()).getFuelCost();
    }
}
