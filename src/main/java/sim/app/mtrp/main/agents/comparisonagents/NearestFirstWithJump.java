package sim.app.mtrp.main.agents.comparisonagents;

import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.learningagents.LearningAgent;
import sim.app.mtrp.main.agents.learningagents.LearningAgentWithJumpship;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirstWithJump extends LearningAgentWithJumpship {
    public NearestFirstWithJump(MTRP state, int id) {
        super(state, id);
    }


}
