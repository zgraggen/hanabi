package com.jacoblucas.hanabi.action;

import java.util.List;

// Action represents the action a player took in the game of Hanabi.
public interface Action {
    /**
     * Gets the ActionType of this Action - either a DISCARD, PLAY, or TIP.
     * @return the ActionType of this Action.
     */
    ActionType getActionType();

    /**
     * Gets the indices of the cards in the player hand affected by this action.
     * @return the indices of the cards in the player hand affected by this action.
     */
    List<Integer> getImpactedCardIndices();
}
