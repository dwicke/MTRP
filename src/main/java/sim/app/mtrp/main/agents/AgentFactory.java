package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;

/**
 * Created by drew on 3/6/17.
 */
public enum AgentFactory {


    RANDOM,LEARNING_NO_JUMP,BRIBING,AUCTION,FCFS, NEAREST_FIRST,NO_JUMP_RESOURCES, AUCTION_RESOURCES, LEARNING_JUMP, ACO;







    public static Agent buildAgent(MTRP state, int id, int type) {
        //state.printlnSynchronized("random ordinal" + RANDOM.ordinal());
        if (type == RANDOM.ordinal()) {
            return new RandomAgent(state, id);
        } else if (type == LEARNING_NO_JUMP.ordinal()) {
            return new LearningAgent(state, id);
        } else if (type == BRIBING.ordinal()) {
            return new BribingAgent(state, id);
        } else if (type == AUCTION.ordinal()) {
            return new AuctionAgent(state, id);
        } else if (type == FCFS.ordinal()) {
            return new FirstComeFirstServe(state, id);
        } else if (type == NEAREST_FIRST.ordinal()) {
            return new NearestFirst(state, id);
        } else if (type == NO_JUMP_RESOURCES.ordinal()) {
            return new SimpleLearningWithResources(state, id);
        } else if (type == AUCTION_RESOURCES.ordinal()) {
            return new AuctionWithResources(state, id);
        } else if (type == LEARNING_JUMP.ordinal()) {
            return new LeaningAgentWithJumpship(state, id);
        } else if (type == ACO.ordinal()) {
            return new ACOAgent(state, id);
        }
        return null;
    }
}
