package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.agents.comparisonagents.AuctionAgent;
import sim.app.mtrp.main.agents.comparisonagents.NearestFirst;
import sim.app.mtrp.main.agents.comparisonagents.NearestFirstWithJump;
import sim.app.mtrp.main.agents.comparisonagents.RandomAgent;
import sim.app.mtrp.main.agents.learningagents.LearningAgentWithCommunication;
import sim.app.mtrp.main.agents.learningagents.LearningAgentWithJumpship;
import sim.app.mtrp.main.agents.resourceagents.AuctionWithResources;
import sim.app.mtrp.main.agents.resourceagents.SimpleLearningWithResources;

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
                return new NearestFirstWithJump(state, id);
            case 6:
                return new NearestFirst(state, id);
            case 7:
                return new RandomAgent(state, id);
        }
        return null;
    }

}
