package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.MTRP;

/**
 * Created by drew on 3/6/17.
 */
public enum AgentFactory {


    RANDOM,LEARNING_NO_JUMP,BRIBING,AUCTION,FCFS, NEAREST_FIRST,JUMP_RESOURCES, AUCTION_RESOURCES, LEARNING_JUMP, LEARNING_JUMP_SIG,REAUCTION,NEAREST_FIRST_SMART,NEAREST_FIRST_JUMP, LEARNING_NNJ, AUCTION_NNJ, NNJB, COMPLEX;







    public static Agent buildAgent(MTRP state, int id, int type) {
        //state.printlnSynchronized("random ordinal" + RANDOM.ordinal());
        if (type == RANDOM.ordinal()) {
            return new RandomAgent(state, id);
        } else if (type == LEARNING_NO_JUMP.ordinal()) { //1
            return new LearningAgent(state, id);
        } else if (type == BRIBING.ordinal()) { // 2
            return new BribingAgent(state, id);
        } else if (type == AUCTION.ordinal()) { // 3
            return new AuctionAgent(state, id);
        } else if (type == FCFS.ordinal()) { // 4
            return new FirstComeFirstServe(state, id);
        } else if (type == NEAREST_FIRST.ordinal()) { // 5
            return new NearestFirst(state, id);
        } else if (type == JUMP_RESOURCES.ordinal()) { // 6
            return new SimpleLearningWithResources(state, id);
        } else if (type == AUCTION_RESOURCES.ordinal()) { // 7
            return new AuctionWithResources(state, id);
        } else if (type == LEARNING_JUMP.ordinal()) { // 8
            return new LearningAgentWithJumpship(state, id);
        } else if (type == LEARNING_JUMP_SIG.ordinal()) { // 9
            return new LearningAgentWithCommunication(state, id);
        } else if (type == REAUCTION.ordinal()) {
            return new ReAuctionAgent(state, id);
        } else if (type == NEAREST_FIRST_SMART.ordinal()) {
            return new NearestFirstSmart(state, id);
        } else if (type == NEAREST_FIRST_JUMP.ordinal()) { // 12
            return new NearestFirstWithJump(state, id);
        } else if (type == LEARNING_NNJ.ordinal()) { //13
            return new LearningAgentNNJ(state, id);
        } else if (type == AUCTION_NNJ.ordinal()) { //14
            return new AuctionAgentNN(state, id);
        } else if (type == NNJB.ordinal()) { //15
            return new NearestFirstWithJumpBounty(state, id);
        } else if (type == COMPLEX.ordinal()) { //16
            return new ComplexLearningAgent(state, id);
        }
        return null;
    }
}
