package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    @Getter
    private List<Card> hand = new ArrayList<>();

    @Getter
    @Setter
    private boolean takenLastAction;

    public void deal(Card c) {
        hand.add(c);
    }

    // Method that all player sub-classes must implement
    public abstract Action takeAction();
}
