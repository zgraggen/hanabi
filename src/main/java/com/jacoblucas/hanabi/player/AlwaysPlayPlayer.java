package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.action.Action;
import com.jacoblucas.hanabi.action.PlayAction;
import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.PlayerWithHand;
import com.jacoblucas.hanabi.model.Suit;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AlwaysPlayPlayer extends Player {
    public AlwaysPlayPlayer(String name) {
        super(name);
    }

    @Override
    public Action takeAction(Map<Suit, Stack<Card>> fireworks, List<PlayerWithHand> playerHands, int remainingTips, int remainingFuses) {
        return new PlayAction(0);
    }
}