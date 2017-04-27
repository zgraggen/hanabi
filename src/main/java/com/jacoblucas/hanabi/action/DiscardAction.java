package com.jacoblucas.hanabi.action;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class DiscardAction implements Action {
    private int cardIndexToDiscard;

    @Override
    public ActionType getActionType() {
        return ActionType.DISCARD;
    }

    @Override
    public List<Integer> getImpactedCardIndices() {
        return Collections.singletonList(cardIndexToDiscard);
    }
}