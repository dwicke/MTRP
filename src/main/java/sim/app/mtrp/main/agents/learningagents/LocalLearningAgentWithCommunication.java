package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;

/**
 * Created by drew on 5/4/17.
 */
public class LocalLearningAgentWithCommunication extends LearningAgentWithCommunication {

    double maxDist = 20;

    public LocalLearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);
        maxCommDist = 40;
    }

    @Override
    public Task getAvailableTask() {

        Continuous2D taskField = state.getTaskPlane();
        Bag tasksNearby = taskField.getNeighborsWithinDistance(curLocation, maxDist);
        // i don't think i need to add my task because i should be going after it so should be within the neighborhood...
        Bag inRangeTasks = getTasksWithinRangeAndAvailable(tasksNearby);


        return getAvailableTask(inRangeTasks);
    }


}
