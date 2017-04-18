package com.jacoblucas.hanabi.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

import static java.util.Collections.shuffle;

public class Deck {
    @Getter(AccessLevel.PROTECTED)
    private Queue<Card> cards;

    public Deck() {
        LinkedList<Card> cards = newInOrderDeck();
        shuffle(cards);
        this.cards = cards;
    }

    protected static LinkedList<Card> newInOrderDeck() {
        LinkedList<Card> cards = new LinkedList<>();
        for (Suit suit : Suit.values()) {
            for (int i=1; i<=5; i++) {
                if (i == 1) {
                    cards.add(new Card(i, suit));
                    cards.add(new Card(i, suit));
                    cards.add(new Card(i, suit));
                } else if (i == 2 || i == 3 || i == 4) {
                    cards.add(new Card(i, suit));
                    cards.add(new Card(i, suit));
                } else {
                    cards.add(new Card(i, suit));
                }
            }
        }
        return cards;
    }

    public int size() {
        return cards.size();
    }
}
