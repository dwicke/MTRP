package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.Job;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.comparisonagents.FirstComeFirstServe;
import sim.app.mtrp.main.agents.comparisonagents.NearestFirst;
import sim.util.Bag;

/**
 * Created by drew on 4/28/17.
 */
public class LearningAgentWithJumpship extends LearningAgent {
    public LearningAgentWithJumpship(MTRP state, int id) {
        super(state, id);
        nf = new NearestFirst(state);
        fcfs = new FirstComeFirstServe(state);
        nf.setId(id);
        fcfs.setId(id);
        nf.setStepsize(state.stepsize);
        fcfs.setStepsize(state.stepsize);
        count = 0;
        totalFairness = 0;
    }

    public LearningAgentWithJumpship() {}


    int fairCounter = 0;
    int oldestCounter = 0;
    NearestFirst nf;
    FirstComeFirstServe fcfs;
    double totalFairness = 0.0;
    double count = 0;

    public Task getAvailableTask(Bag tasks) {

        Job prevJob = null;
        if (!amWorking && curJob != null && !curJob.getIsAvailable()) {
            // then someone beat me to it so learn
            learn(0.0);
            curJob.getTask().decommit(this);// must decommit.
            prevJob = curJob;
            // and set curJob to null
            curJob = null;
        }

//        if (amWorking) {
//            // consider letting the agent jump ship here if they need to...
//            return curJob.getTask();
//        }

        // let the agent jumpship if they have not made it to the job yet.
        Task bestT = getBestTask(tasks);

        nf.setCurLocation(curLocation);
        fcfs.setCurLocation(curLocation);
        nf.setCurJob(curJob);
        fcfs.setCurJob(curJob);
        Task nearest = nf.getBestTask(tasks);
        Task first = fcfs.getBestTask(tasks);


        boolean shouldPrint = false;
        if (curJob != null && (bestT == null || bestT.getJob().getId() != curJob.getId())) {
            bestT = handleJumpship(bestT);
            shouldPrint = true;
        } else if (curJob == null) {
            shouldPrint = true;
        } else {
            handleMaintain(bestT);
        }

        /*

        I want to know *how* fair it was that i went after that task
        so, what does that mean.

        if it is the longest waiting task then fair = 1
        if i go after the latest task it is least fair unless it is the only task
        (wait time of t ) / (waiting time of longest task )
        do i need to do the subtraction of the nearest neighbor's weighting time from numerator and denominator?...
         */

        if (shouldPrint) {
            // print out distance and times... try and see what is going on...
            if (bestT == null) {

            }else {
                count++;
                double fairness = (double)bestT.getTimeNotFinished() / (double)first.getTimeNotFinished();
                totalFairness += fairness;
                state.printlnSynchronized("Average fairness = " + (totalFairness / count) + " job fairness = " + fairness);
            }
//
// else if (nearest.getId() == bestT.getId()) {
//                state.printlnSynchronized("going after closest task with distance to it: " + nearest.getLocation().distance(curLocation) + " has been waiting: " + nearest.getTimeNotFinished() + " fcfs wait: " + first.getTimeNotFinished() + " fcfs dist = " + first.getLocation().distance(curLocation));
//                if (nearest.getId() == first.getId()) {
//                    state.printlnSynchronized("and it is also the fairest");
//                }
//
//            } else if (bestT.getId() == first.getId()) {
//                //oldestCounter++;
//                state.printlnSynchronized("going after oldest task " + oldestCounter);
//            } else if (bestT.getId() < nearest.getId()) {
//                state.printlnSynchronized("going after older task" + bestT.getId() + " dist = " + bestT.getLocation().distance(curLocation) + " nearest id = " + nearest.getId() + " dist = " + nearest.getLocation().distance(curLocation));
//                state.printlnSynchronized(" time diff = " + (bestT.getTimeNotFinished() - nearest.getTimeNotFinished()) + " distance diff = " + (bestT.getLocation().distance(curLocation) - nearest.getLocation().distance(curLocation)) + " bounty rate diff = " + (getUtility(bestT) - getUtility(nearest)));
//            }
        }

//        if ((prevJob != null && bestT != null) || (jumped == true && bestT != null)) {
//            // then i've changed jobs somehow either naturally or by jumping ship
//            // so see if i've also changed neighborhoods
//            if (prevJob != null && prevJob.getTask().getNeighborhood().getId() != bestT.getNeighborhood().getId()) {
//                state.printlnSynchronized("I " + id + " have moved neighborhoods!! from " + prevJob.getTask().getNeighborhood().getId() + " to " + bestT.getNeighborhood().getId() + " ptable = " + pTable.getQTableAsString());
//            } else if (jumped == true && curJob.getTask().getNeighborhood().getId() != bestT.getNeighborhood().getId()){
//                state.printlnSynchronized("I " + id + " have moved neighborhoods!! " + curJob.getTask().getNeighborhood().getId() + " to " + bestT.getNeighborhood().getId() + " ptable = " + pTable.getQTableAsString());
//            }
//        }


        return bestT;

    }

    public void handleMaintain(Task bestT) {
        // does nothing...
    }

    public Task handleJumpship(Task bestT) {
        // then I'm jumping ship and need to decommit and maybe learn too...
        // can't decommit if working on the task!
        // now check if i should change tasks.  I don't want to be swapping tasks if the distance is essentially zero and i was working on it already

        // TODO: consider learning after jumping ship
        if (bestT != null && amWorking == true) { // maybe do this if we were working and not otherwise...???
            //state.printlnSynchronized("Time step = " + state.schedule.getSteps() + " Agent " + getId() + " jumpingship to task id = " + bestT.getJob().getId() + " with utility " + getUtility(bestT) + " from task id " + curJob.getId() + " with utility " + getUtility(curJob.getTask()));
            //jobSuccess[curJob.getTask().getNeighborhood().getId()].update(curJob.getJobType(), 0, 0.5);
        }

        if (amWorking == true && curJob.getCurWorker() == null) {
            state.printlnSynchronized("I am supposedly working but the current worker is null!!!");
        }

        if (amWorking == true && curJob.getCurWorker().getId() == id) {
            curJob.leaveWork(this);
            amWorking = false;
        }

        curJob.getTask().decommit(this);// must decommit.
        return bestT;
    }
}
