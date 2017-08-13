package sim.app.mtrp.main.agents.comparisonagents;

import kn.uni.voronoitreemap.j2d.PolygonSimple;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.Valuators.EquitablePartitions;

/**
 * Created by drew on 8/12/17.
 */
public class EquitableAgent extends NearestFirstWithJump {


    static EquitablePartitions ep;
    PolygonSimple myRegion;
    public EquitableAgent(MTRP state, int id) {
        super(state, id);
    }

    @Override
    public Task getAvailableTask() {
        if (ep == null) {
            ep = new EquitablePartitions(state);
            ep.init();
            ep.computeDiagram();
        }
        state.printlnSynchronized(" going to update");
        ep.update(id);
        state.printlnSynchronized("finished update");
        ep.computeDiagram();
        return super.getAvailableTask();
    }

    @Override
    public double getUtility(Task t) {
        PolygonSimple myRegion = ep.getRegion(id);

        kn.uni.voronoitreemap.j2d.Point2D tpoint = new kn.uni.voronoitreemap.j2d.Point2D(t.getLocation().getX(), t.getLocation().getY());
        if (myRegion.contains(tpoint)) {
            return -getNumTimeStepsFromLocation(t.getLocation());
        }
        return Double.NEGATIVE_INFINITY;
    }
}
