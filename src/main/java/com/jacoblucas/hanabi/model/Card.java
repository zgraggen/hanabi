package com.jacoblucas.hanabi.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
// Card represents a card in the game of Hanabi, where each card in the game has a number and a suit.
public class Card {
    private int number;
    private Suit suit;

    @Override
    public String toString() {
        return suit.name() + " " + number;
    }
}