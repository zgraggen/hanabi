package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTipPlayer extends Player {
    public RandomTipPlayer(String name) {
        super(name);
    }

    @Override
    public Action takeAction(Map<Suit, Stack<Card>> fireworks, Map<Player, List<Card>> playerHands) {
        Player p = playerHands.keySet().iterator().next();
        List<Card> hand = playerHands.get(p);
        int randomIndex = ThreadLocalRandom.current().nextInt(0, hand.size());
        Card card = hand.get(randomIndex);

        List<Integer> indices = new ArrayList<>();
        TipType tipType = ThreadLocalRandom.current().nextInt() % 2 == 0 ? TipType.NUMBER : TipType.SUIT;

        if (tipType == TipType.NUMBER) {
            // find all indices matching card number
            for (int i=0; i<hand.size(); i++) {
                Card c = hand.get(i);
                if (c.getNumber() == card.getNumber()) {
                    indices.add(i);
                }
            }

            return new TipAction(p, card.getNumber(), indices);
        } else {
            // find all indices matching card suit
            for (int i=0; i<hand.size(); i++) {
                Card c = hand.get(i);
                if (c.getSuit() == card.getSuit()) {
                    indices.add(i);
                }
            }

            return new TipAction(p, card.getSuit(), indices);
        }
    }
}