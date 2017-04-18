package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Card;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Player {
    @Getter
    private List<Card> hand = new ArrayList<>();

    public void deal(Card c) {
        hand.add(c);
    }
}
