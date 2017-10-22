package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.action.Action;
import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.PlayerWithHand;
import com.jacoblucas.hanabi.model.Suit;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public abstract class Player {
    @Getter protected String name;
    @Getter @Setter private boolean takenLastAction;
    @Getter private List<Integer> knownNumbers;
    @Getter private List<Suit> knownSuits;

    Player(String name) {
        this.name = name;
        knownNumbers = new Vector<>(Arrays.asList(null, null, null, null, null));
        knownSuits = new Vector<>(Arrays.asList(null, null, null, null, null));
    }

    public void receiveNumberTip(int number, List<Integer> indices) {
        for (Integer i : indices) {
            knownNumbers.remove(i.intValue());
            knownNumbers.add(i, number);
        }
    }

    public void receiveSuitTip(Suit suit, List<Integer> indices) {
        for (Integer i : indices) {
            knownSuits.remove(i.intValue());
            knownSuits.add(i, suit);
        }
    }
    
    public void cardHasBeenUsed(int indices) {
    	for(int i=indices; i < knownNumbers.size()-1; i++) {
    		knownNumbers.set(i, knownNumbers.get(i+1));
    		knownSuits.set(i, knownSuits.get(i+1));
    	}
		knownNumbers.set(4, null);
		knownSuits.set(4, null);
    }

    /**
     * Method for all implementations of Player to override.
     * @param fireworks The current state of each firework being built in the game.
     * @param playerHands A list of PlayerWithHand.
     * @param remainingTips The number of remaining tips in the game.
     * @param remainingFuses The number of remaining fuses in the game.
     * @return An implementation of Action, representing what action the player decided to take.
     */
    public abstract Action takeAction(
            Map<Suit, Stack<Card>> fireworks,
            List<PlayerWithHand> playerHands,
            int remainingTips,
            int remainingFuses);
}