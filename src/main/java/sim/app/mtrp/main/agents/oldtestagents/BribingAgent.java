package sim.app.mtrp.main.agents.oldtestagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.Job;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.LearningAgentWithJumpship;
import sim.util.Bag;

/**
 *
 * This agent is able to propose and accept bribes so as to not go after
 * particular tasks.
 * Created by drew on 3/11/17.
 */
public class BribingAgent extends LearningAgentWithJumpship {
    public BribingAgent(MTRP state, int id) {
        super(state, id);
    }




    @Override
    public Task getAvailableTask() {
        Task chosenTask = super.getAvailableTask();

        if (!amWorking && (curJob == null || !curJob.getIsAvailable())) {
            // so now I must decide to commit to the task if not null
            if (chosenTask != null) {


                // so now check to see if i need to bribe anyone!
                Agent[] ag = (Agent[]) chosenTask.getCommittedAgents().toArray(new Agent[chosenTask.getCommittedAgents().size()]);
                double totalBribeAmount = 0.0;
                for (Agent a : ag) {
                    if ((a instanceof BribingAgent) && a.getId() != this.getId() && a.getNumTimeStepsFromLocation(chosenTask.getLocation()) <= getNumTimeStepsFromLocation(chosenTask.getLocation())) {
                        BribingAgent ba = (BribingAgent) a;
                        //state.printlnSynchronized("My id " + getId() + " agent id = " + ba.getId() + " task commit to = " + ba.getCurJob().getId() + " interested id = " + chosenTask.getId());
                        totalBribeAmount += ba.getBribeAmount(chosenTask);
                    }
                }

                if (totalBribeAmount > 0 && !Double.isInfinite(totalBribeAmount)) {
                    //state.printlnSynchronized("Total Bribe amount = " + totalBribeAmount + " for task id = " + chosenTask.getId());


                    // check if i'm willing to pay the bribe!
                    double modifiedUtility = (getUtility(chosenTask) * getNumTimeStepsFromLocation(chosenTask.getLocation()) - totalBribeAmount) / getNumTimeStepsFromLocation(chosenTask.getLocation());


                    Bag tasksInRange = getTasksWithinRange();
                    tasksInRange.remove(chosenTask);
                    Task secondBest;
                    if (tasksInRange.size() > 1) {
                        secondBest = getAvailableTask(tasksInRange);
                    } else {
                        secondBest = getBestTask(tasksInRange);
                    }

                    double secondBestUtility = 0.0;
                    if (secondBest != null) {
                        secondBestUtility = getUtility(secondBest);
                    }
                    //state.printlnSynchronized("Total Bribe amount = " + totalBribeAmount + " for task id = " + chosenTask.getId() + " utility = " + getUtility(chosenTask) + " modified utility with bribe = " + modifiedUtility + " second best utility = " + secondBestUtility);


                    if (modifiedUtility > secondBestUtility) {
                        // then pay the bribes!
                        for (Agent a : ag) {
                            if ((a instanceof BribingAgent) && a.getId() != this.getId() && a.getNumTimeStepsFromLocation(chosenTask.getLocation()) <= getNumTimeStepsFromLocation(chosenTask.getLocation())) {
                                BribingAgent ba = (BribingAgent) a;
                                ba.acceptBribe(ba.getBribeAmount(chosenTask));
                            }
                        }
                    } else {
                        if (secondBest != null) {

                            chosenTask = secondBest;// no, this really should be recursive...

                        }
                    }

                }

            }
        }
        return chosenTask;

    }

    @Override
    public void commitTask(Task t) {
        t.amCommitted(this);
    }

    @Override
    public void decommitTask() {
        if (curJob != null)
            curJob.getTask().decommit(this);
        super.decommitTask();

    }

    /**
     *
     * @param t the task that the caller wants to know for how much the agent will leave it for
     * @return the minimum amount of bribe the agent is willing to accept to leave the task
     */
    public double getBribeAmount(Task t) {
        // need to clear out the current job so as to get the second best job
        Task secondBestTask = getSecondBestTask();

//        if (curJob == null) {
//            state.printlnSynchronized("!!!!!!!!!!!!!!!!!!!I am agent id = " + getId() + " supposedly commited to task id = " + t.getId() + " but curJob = " + curJob);
//            state.printlnSynchronized("Agent ids commited to task " + t.getId());
//            for (int i = 0; i < t.getCommittedAgents().size(); i++) {
//                state.printlnSynchronized("id = " + t.getCommittedAgents().get(i).toString());
//            }
//        }
        double curJobUtil = curJob.getTask().getBounty()+ getNumTimeStepsFromLocation(curJob.getTask().getLocation());

        if (secondBestTask == null) {
            // well if no other task is available to me then sure you can have it as long as I get as much for it as I would have gotten if i would have continued
            // to go after it.
            return curJobUtil;//getUtility(curJob.getTask()) * getNumTimeStepsFromLocation(curJob.getTask().getLocation());
        }

        // so now get the difference of the utility
        double secondBestUtil = secondBestTask.getBounty()+ getNumTimeStepsFromLocation(secondBestTask.getLocation());
        return curJobUtil - secondBestUtil;

    }
    public Task getSecondBestTask() {
        Job tempJob = curJob;
        curJob = null;
        Bag tasksInRange = getTasksWithinRange();
        tasksInRange.remove(tempJob.getTask());

        Task secondBestTask = getBestTask(tasksInRange);
        curJob = tempJob;// return the job back so that I don't not do it if I don't get the bribe!
        return secondBestTask;
    }


    public void acceptBribe(double bribe) {
        curJob.getTask().blacklistAgent(this);
        decommitTask();
        bounty += bribe;
    }


    public Bag getTasksWithinRange() {
        Bag inRange = super.getTasksWithinRange(state.getBondsman().getAvailableTasks());
        Task[] inRangeTasks = (Task[]) inRange.toArray(new Task[inRange.size()]);
        Bag nonBlacklistedTasks = new Bag();
        // now get rid of the ones i've been bribed to ignore
        for (Task t: inRangeTasks) {
            if (!t.getBlackList().contains(this)) {
                nonBlacklistedTasks.add(t);
            }
        }
        return nonBlacklistedTasks;
    }
}
