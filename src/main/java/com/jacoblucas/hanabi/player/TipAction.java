package com.jacoblucas.hanabi.player;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TipAction implements Action {
    private TipType type;
    private List<Integer> tippedCardIncides;

    @Override
    public ActionType getActionType() {
        return ActionType.TIP;
    }

    @Override
    public List<Integer> getImpactedCardIndices() {
        return tippedCardIncides;
    }
}