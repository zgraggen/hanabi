package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Suit;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class Player {
    @Getter
    protected String name;

    @Getter
    @Setter
    private boolean takenLastAction;

    /**
     * Method for all implementations of Player to override.
     * @param fireworks The current state of each firework being built in the game.
     * @param playerHands A map of player -> List<Card> representing the other player's hands.
     * @return An implementation of Action, representing what action the player decided to take.
     */
    public abstract Action takeAction(Map<Suit, Stack<Card>> fireworks, Map<Player, List<Card>> playerHands);
}