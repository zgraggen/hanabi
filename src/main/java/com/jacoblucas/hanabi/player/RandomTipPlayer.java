package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.action.Action;
import com.jacoblucas.hanabi.action.TipAction;
import com.jacoblucas.hanabi.action.TipType;
import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.PlayerWithHand;
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
    public Action takeAction(Map<Suit, Stack<Card>> fireworks, List<PlayerWithHand> playerHands, int remainingTips, int remainingFuses) {
        Player p = playerHands.get(0).getPlayer();
        List<Card> hand = playerHands.get(0).getCards();
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