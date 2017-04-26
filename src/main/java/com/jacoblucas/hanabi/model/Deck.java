package com.jacoblucas.hanabi.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

import static java.util.Collections.shuffle;

// Deck represents the deck of cards in the game of Hanabi, throughout the course of the game.
// As cards are dealt to players, the size of the deck decreases.
public class Deck {
    @Getter(AccessLevel.PROTECTED)
    private Queue<Card> cards;

    // Constructor for a new, shuffled deck. This will initialise to be the complete number of cards in the game,
    // prior to being dealt out to players.
    public Deck() {
        LinkedList<Card> cards = newInOrderDeck();
        shuffle(cards);
        this.cards = cards;
    }

    // Builds a new, in-order deck of cards for the game.
    // In each deck there are five 1's, two 2's, two 3's, two 4's, and one 5.
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

    // Returns the number of cards remaining in the deck.
    public int size() {
        return cards.size();
    }

    // Takes the top card off the deck, and returns it.
    public Card deal() {
        return cards.poll();
    }
}
