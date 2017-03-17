package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;

/**
 * Created by drew on 3/6/17.
 */
public class AgentFactory {


    public static Agent buildAgent(MTRP state, int id, int type) {

        if (type == 0) {
            return new RandomAgent(state, id);
        } else if (type == 1) {
            return new LearningAgent(state, id);
        } else if (type == 2) {
            return new BribingAgent(state, id);
        } else if (type == 3) {
            return new AuctionAgent(state, id);
        } else if (type == 4) {
            return new FirstComeFirstServe(state, id);
        } else if (type == 5) {
            return new NearestFirst(state, id);
        }
        return null;
    }
}
