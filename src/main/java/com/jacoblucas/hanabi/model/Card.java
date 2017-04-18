package com.jacoblucas.hanabi.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
// Card represents a card in the game of Hanabi, where each card in the game has a number and a suit.
public class Card {
    private int number;
    private Suit suit;
}