package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
// Action represents the action a player took in the game of Hanabi.
public class Action {
    @Getter private ActionType actionType;
    @Getter private Card card;
}
