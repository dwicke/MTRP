package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.Job;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Created by drew on 4/28/17.
 */
public class LearningAgentWithJumpship extends LearningAgent {
    public LearningAgentWithJumpship(MTRP state, int id) {
        super(state, id);
    }



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


        if (curJob != null && ( bestT == null || bestT.getJob().getId() != curJob.getId())) {
            bestT = handleJumpship(bestT);
        } else {
            handleMaintain(bestT);
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
            // THIS WILL HAPPEN!!! very very very rarely but it can happen
            //  for example there is this very high bounty task that before had
            // agent around or nearby that they have now left because say they
            // went after some other task that had a higher bounty that they could
            // see that you can't...  So even though you are right directly working
            // on this task it may be worth your time to go after that task
            // because of the higher bounty
            // it might even be that there aren't any other agents near you.
            // its just the fact that they didn't go after that task that it is now lucrative
            // enough for you to leave what you are working on to go get that.
            //  then since we are possibly executing things in threads that we run into a scenario
            // where i'm leaving work and simultaneously some other agent that has decided to go
            // after this job sees that you were working on it but now since you aren't and have set
            // the curworker to null this causes the problem with the learning agent
            curJob.leaveWork(this);
            amWorking = false;
        }

        curJob.getTask().decommit(this);// must decommit.
        return bestT;
    }
}
