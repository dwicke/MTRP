package sim.app.mtrp.main.agents.Valuators;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.util.QTable;

/**
 * The purpose of this class is to learn what resources
 * a particular agent needs in order to complete task before returning
 * to a depo.
 * Created by drew on 6/14/17.
 */
public class ResourceLearner {

    MTRP state;
    QTable resources[]; // for each element in the array corresponds to the job type and within the job type we must learn the number of resources that are expected of each type


    public ResourceLearner(MTRP state) {
        this.state = state;
        resources = new QTable[state.numJobTypes];
    }







}
