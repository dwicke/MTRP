package sim.app.mtrp.main.agents.resourceagents;

import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.Valuators.ResourceLearner;
import sim.app.mtrp.main.agents.learningagents.WithExclusivity;

/**
 * Created by drew on 7/31/17.
 */
public class WithExclusivityResources extends WithExclusivity {


    ResourceLearner resourceLearner;
    public WithExclusivityResources(MTRP state, int id) {
        super(state, id);
    }


    @Override
    public double getCost(Task t) {
        Depo d = getClosestDepo();
        return resourceLearner.getCost(t, d, getNumTimeStepsFromLocation(t.getLocation()), getNumTimeStepsFromLocation(d.getLocation()), getNumTimeStepsFromLocation(d.getLocation(), t.getLocation()));
    }

    public boolean checkNeedResources(Depo nearestDepo) {
        boolean needFuel =  super.checkNeedResources(nearestDepo);
        boolean needOtherResources = resourceLearner.checkNeedResources(nearestDepo, curJob,getBestTask(getTasksWithinRange(state.getBondsman().getAvailableTasks())));
        //state.printlnSynchronized("Need fuel = " + needFuel + " need other resources = " + needOtherResources);
        return needFuel || needOtherResources;
    }

    @Override
    public boolean buySellTaskResources(Depo nearestDepo) {
        double preBounty = bounty;
        bounty =  resourceLearner.buySellTaskResources(nearestDepo, bounty, getPercentageJobTypes(getTasksWithinRangeAsArray(state.bondsman.getAvailableTasks())));
        return preBounty != bounty;// i did something if i have different amount of bounty now.
    }

    @Override
    public void claimWork() {
        amWorking = resourceLearner.claimWork(curJob);
        if (amWorking) {
            amWorking = curJob.claimWork(this);
            //state.printlnSynchronized("Am working? = " + amWorking);
        } else {
            //state.printlnSynchronized("Am working? = " + amWorking);
        }
    }



    public int[] getMyResources() {
        return resourceLearner.getMyResources();
    }


}
