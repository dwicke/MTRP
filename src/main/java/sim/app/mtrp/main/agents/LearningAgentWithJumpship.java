package sim.app.mtrp.main.agents;

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

        if (!amWorking && curJob != null && !curJob.getIsAvailable()) {
            // then someone beat me to it so learn
            learn(0.0);
            curJob.getTask().decommit(this);// must decommit.
            // and set curJob to null
            curJob = null;
        }

//        if (amWorking) {
//            // consider letting the agent jump ship here if they need to...
//            return curJob.getTask();
//        }

        // let the agent jumpship if they have not made it to the job yet.
        Task bestT = null;
        if (curJob == null) {
            // then evaluate from all the tasks
            bestT = getBestTask(tasks);
        } else {
            // i'm going to consider jumping ship in that case just look at the newest tasks
            //bestT = getBestTask(getTasksWithinRange(state.bondsman.getNewTasks()));
            bestT = getBestTask(tasks);
        }


        if (curJob != null && ( bestT == null || bestT.getJob().getId() != curJob.getId())) {
            bestT = handleJumpship(bestT);
        }
        return bestT;

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
