package sim.app.mtrp.main;

import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.*;

/**
 * Created by drew on 5/1/17.
 */
public class Augmentor implements Steppable {

    MTRP state;
    TreeSet<Integer> ids = new TreeSet<Integer>();

    Map<Agent, Integer> deadAgents = new HashMap<Agent, Integer>();
    ArrayList<Agent> aliveAgents = null;

    Map<Agent, Integer> slowAgents = new HashMap<Agent, Integer>();
    ArrayList<Agent> regularAgents = null;


    Neighborhood inDisaster = null;
    int disasterCount = -1;
    double previousRate = 0;

    public Augmentor(MTRP state) {
        this.state = state;
    }

    public void step(SimState simState) {

        if (state.isHasEmergentJob()) {
            if ((state.schedule.getSteps() % state.getNumstepsEmergentJob()) == 0) {
                //state.printlnSynchronized("Made an emergent task!");
                Task newTask = state.getNeighborhoods()[state.random.nextInt(state.numNeighborhoods)].makeTask();
                newTask.setJob(state.jobPrototypes[state.numJobTypes + state.random.nextInt(state.numEmergentJobTypes)].buildJob(state, newTask, newTask.getId()));
            }
        }
        if (state.isHasUnexpectedlyHardJobs()) {
            if (state.random.nextDouble() <  (1.0 / ((double) state.timestepsTilNextTask * 10.0))) {
                if (state.getTaskPlane().getAllObjects().size() > 0) {
                    Task randTask = ((Task) state.getTaskPlane().getAllObjects().get(state.random.nextInt(state.getTaskPlane().getAllObjects().size())));
                    if (!ids.contains(randTask.getId())) {
                        ids.add(randTask.getId());
                        int prevMeanJobLength = randTask.getJob().getMeanJobLength();
                        randTask.getJob().setMeanJobLength(randTask.getJob().getMeanJobLength() * 5);
                        //state.printlnSynchronized("Made task " + randTask.getId() + " job length = " + randTask.getJob().getMeanJobLength() + " previously was = " + prevMeanJobLength);

                    }
                }
            }
        }
        if (state.isHasUnexpectedlySlowJobs()) {
            if (state.random.nextDouble() <  (1.0 / ((double) state.timestepsTilNextTask * 5))) {
                if (state.getTaskPlane().getAllObjects().size() > 0) {
                    Task randTask = ((Task) state.getTaskPlane().getAllObjects().get(state.random.nextInt(state.getTaskPlane().getAllObjects().size())));
                    if (!ids.contains(randTask.getId())) {
                        ids.add(randTask.getId());
                        randTask.getJob().setSlow(true);
                        //state.printlnSynchronized("Made task " + randTask.getId() + " job length = " + randTask.getJob().getMeanJobLength() + " previously was = " + prevMeanJobLength);

                    }
                }
            }
        }
        if (state.isShouldDie()) {
            if (state.getAgents() != null && aliveAgents == null) {
                aliveAgents = new ArrayList<Agent>();
                for (int i = 0; i < state.getAgents().length; i++) {
                    aliveAgents.add(state.getAgents()[i]);
                }
            }
            if ((state.schedule.getSteps() % state.getNumstepsDead()) == 0) {
                Agent agentDied = aliveAgents.remove(state.random.nextInt(aliveAgents.size()));
                agentDied.setDied(true);
                // agent and the number of timesteps the agent has been dead for
                deadAgents.put(agentDied, 0);
            }

            Iterator<Map.Entry<Agent, Integer>> setIterator = deadAgents.entrySet().iterator();
            for (; setIterator.hasNext();) {
                Map.Entry<Agent, Integer> a = setIterator.next();
                deadAgents.put(a.getKey(), a.getValue() + 1);

                if (a.getValue() == 20000) {
                    setIterator.remove();
                    // and put back to life!
                    aliveAgents.add(a.getKey());
                    a.getKey().setDied(false);
                }
            }

        }

        // now what if a neighborhood started producing tasks at a much higher rate
        if (state.isHasSuddenTaskIncrease()) {
            if (state.schedule.getSteps() % state.getDisasterStep() == 0) {
                // then start the disaster!
                inDisaster = state.getNeighborhoods()[state.random.nextInt(state.getNeighborhoods().length)];
                previousRate = inDisaster.getTimestepsTilNextTask();
                inDisaster.setTimestepsTilNextTask(state.getNewRate());
                disasterCount = 0;
            }
            if (disasterCount >= 0) {
                disasterCount++;
                if (disasterCount % state.getDisasterLength() == 0) {
                    // disaster ended!
                    disasterCount = -1;
                    inDisaster.setTimestepsTilNextTask(previousRate);
                    inDisaster = null;
                }
            }

        }

        if (state.isSlower()) {
            if (state.getAgents() != null && regularAgents == null) {
                regularAgents = new ArrayList<Agent>();
                for (int i = 0; i < state.getAgents().length; i++) {
                    regularAgents.add(state.getAgents()[i]);
                }
            }
            if ((state.schedule.getSteps() % state.getNumstepsSlow()) == 0) {
                Agent agentSlow = regularAgents.remove(state.random.nextInt(regularAgents.size()));
                agentSlow.setSlow(true);
                // agent and the number of timesteps the agent has been dead for
                slowAgents.put(agentSlow, 0);
                //state.printlnSynchronized("Set agent " + agentSlow.getId() + " to slow");
            }

            Iterator<Map.Entry<Agent, Integer>> setIterator = slowAgents.entrySet().iterator();
            for (; setIterator.hasNext();) {
                Map.Entry<Agent, Integer> a = setIterator.next();
                slowAgents.put(a.getKey(), a.getValue() + 1);

                if (a.getValue() == state.getNumstepsSlow()) {
                    setIterator.remove();
                    // and put back to life!
                    regularAgents.add(a.getKey());
                    a.getKey().setSlow(false);
                    //state.printlnSynchronized("Set agent " + a.getKey().getId() + " to regular");

                }
            }

        }




    }



}
