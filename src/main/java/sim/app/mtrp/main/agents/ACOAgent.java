package sim.app.mtrp.main.agents;


import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

import java.util.ArrayList;

/**
 * Created by drew on 4/27/17.
 */
public class ACOAgent extends LearningAgent {


    ArrayList<Task> tasksToDo;
    boolean isDoneTasks;

    public ACOAgent(MTRP state, int id) {
        super(state, id);
        tasksToDo = new ArrayList<Task>();
    }

    @Override
    public Task getBestTask(Bag bagOfTasks) {
        while (!tasksToDo.isEmpty() && ((Task)tasksToDo.get(0)).getFinished()) {

            tasksToDo.remove(0);
        }
        if (!tasksToDo.isEmpty()) {
            return (Task) tasksToDo.get(0);
        } else {
            isDoneTasks = true;
            return null;
        }
    }

    public void assignTasks(Task[] tasks) {
        for (int i = 0; i < state.master.getTourLength(); i++) {
            tasksToDo.add(tasks[i]);
            tasks[i].amCommitted(this);
        }
        isDoneTasks = false;
    }

    public boolean getIsDoneTasks() {
        return isDoneTasks;
    }

    public Task[] getTasksToDo() {
        return tasksToDo.toArray(new Task[tasksToDo.size()]);
    }
}
