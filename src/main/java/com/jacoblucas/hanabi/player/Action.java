package com.jacoblucas.hanabi.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
// Action represents the action a player took in the game of Hanabi.
public class Action {
    @Getter private ActionType actionType;
    @Getter private int cardIndexInPlayerHand;
}
