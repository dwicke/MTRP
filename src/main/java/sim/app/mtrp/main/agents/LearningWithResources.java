package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Resource;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;

/**
 * Created by drew on 3/18/17.
 *
 *
 * Ok so this agent will need to learn how to deal with the resource requirements for the jobs
 * as well as the length of time each job lasts.
 *
 * i'm starting to wonder if for this type of agent it would be a good idea to make it so that
 * as soon as the agent arrives to the task that is the bounty they will receive for completing it
 * not the bounty at the end... we will see.
 */
public class LearningWithResources extends SimpleLearningWithResources {




    public LearningWithResources(MTRP state, int id) {
        super(state, id);

    }



    /**
     * Buy and sell resources from the depo passed
     * @param nearestDepo
     */
    @Override
    public boolean buySellTaskResources(Depo nearestDepo) {
        boolean didAction = false;

        /*TODO: so how do we do the buying/selling well, we actually should have an idea of what jobs we are going to do...??? whoa this is a bit complicated*/


        return didAction;
    }

}
