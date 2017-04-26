package com.jacoblucas.hanabi.player;

import java.util.UUID;

public class AlwaysDiscardPlayer extends Player {
    public AlwaysDiscardPlayer() {
        this.name = UUID.randomUUID().toString();
    }

    @Override
    public Action takeAction() {
        return new Action(ActionType.DISCARD, getHand().get(0));
    }
}