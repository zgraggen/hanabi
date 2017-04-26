package com.jacoblucas.hanabi.player;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class PlayAction implements Action {
    private int cardIndexToPlay;

    @Override
    public ActionType getActionType() {
        return ActionType.PLAY;
    }

    @Override
    public List<Integer> getImpactedCardIndices() {
        return Collections.singletonList(cardIndexToPlay);
    }
}