package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.Job;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.app.mtrp.main.util.ReentrantContinuous2D;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;

/**
 * Created by drew on 5/4/17.
 */
public class LocalLearningAgentWithCommunication extends LearningAgentWithCommunication {

    double maxDist = 20; // distance the tasks can comm
    public LocalLearningAgentWithCommunication(MTRP state, int id) {
        super(state, id);
        maxCommDist = 40; // agents have a great range of comm than the tasks
    }

    @Override
    public Task getAvailableTask() {


            ReentrantContinuous2D taskField = state.getRwTaskPlane();

            Bag tasksNearby = taskField.getNeighborsWithinDistance(curLocation, maxDist);
            // i don't think i need to add my task because i should be going after it so should be within the neighborhood...
            Bag inRangeTasks = getTasksWithinRangeAndAvailable(tasksNearby);


            return getAvailableTask(inRangeTasks);

    }




}
