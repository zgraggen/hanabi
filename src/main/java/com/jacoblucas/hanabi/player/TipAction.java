package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Suit;
import lombok.Getter;

import java.util.List;

public class TipAction implements Action {
    private List<Integer> tippedCardIndices;

    @Getter private Player receivingPlayer;
    @Getter private TipType type;
    @Getter private Integer tipNumber;
    @Getter private Suit tipSuit;

    /**
     * Gives a tip to a player, telling them that all cards at the provided indices are a certain number.
     * @param receivingPlayer The player receiving the tip.
     * @param tipNumber The value of the number in the tip.
     * @param tippedCardIndices The (0 based) indices of the receiving player's hand that are the provided number.
     */
    TipAction(Player receivingPlayer, int tipNumber, List<Integer> tippedCardIndices) {
        this.receivingPlayer = receivingPlayer;
        this.tipNumber = tipNumber;
        this.tippedCardIndices = tippedCardIndices;
        this.tipSuit = null;
        this.type = TipType.NUMBER;
    }

    /**
     * Gives a tip to a player, telling them that all cards at the provided indices are a certain suit.
     * @param receivingPlayer The player receiving the tip.
     * @param tipSuit The value of the suit in the tip.
     * @param tippedCardIndices The (0 based) indices of the receiving player's hand that are the provided suit.
     */
    TipAction(Player receivingPlayer, Suit tipSuit, List<Integer> tippedCardIndices) {
        this.receivingPlayer = receivingPlayer;
        this.tipNumber = null;
        this.tippedCardIndices = tippedCardIndices;
        this.tipSuit = tipSuit;
        this.type = TipType.SUIT;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TIP;
    }

    @Override
    public List<Integer> getImpactedCardIndices() {
        return tippedCardIndices;
    }
}