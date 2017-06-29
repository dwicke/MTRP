package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by drew on 3/6/17.
 */
public class AgentFactory {


    public static Agent buildAgent(MTRP state, int id, int type) {
        switch (type) {
            case 0:
                return new AuctionAgent(state, id);
            case 1:
                return new LearningAgentWithJumpship(state, id);
            case 2:
                return new AuctionWithResources(state, id);
            case 3:
                return new SimpleLearningWithResources(state, id);
            case 4:
                return new LearningAgentWithCommunication(state, id);
            case 5:
                return new LearningAgentWithCommunicationGeneral(state, id);
        }
        return null;
    }

}
