package com.jacoblucas.hanabi.player;

public class AlwaysDiscardPlayer extends Player {
    @Override
    public Action takeAction() {
        return new Action(ActionType.DISCARD);
    }
}