package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.action.Action;
import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.PlayerWithHand;
import com.jacoblucas.hanabi.model.Suit;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PlayerTest {
    private Player p = new Player("MyPlayer") {
        @Override
        public Action takeAction(Map<Suit, Stack<Card>> fireworks, List<PlayerWithHand> playerHands, int remainingTips, int remainingFuses) {
            return null;
        }
    };

    @Test
    public void NewPlayerHasKnownNumberListInitialisedCorrectly() {
        List<Integer> knownNumbers = p.getKnownNumbers();
        assertThat(knownNumbers.size(), is(5));
        for (Integer i : knownNumbers) {
            assertThat(i, is(nullValue()));
        }
    }

    @Test
    public void ReceiveNumberTipUpdatesCorrectly() {
        int tipNumber = 2;
        p.receiveNumberTip(tipNumber, Arrays.asList(1, 4));

        assertThat(p.getKnownNumbers().get(0), is(nullValue()));
        assertThat(p.getKnownNumbers().get(1), is(tipNumber));
        assertThat(p.getKnownNumbers().get(2), is(nullValue()));
        assertThat(p.getKnownNumbers().get(3), is(nullValue()));
        assertThat(p.getKnownNumbers().get(4), is(tipNumber));
    }

    @Test
    public void NewPlayerHasKnownSuitListInitialisedCorrectly() {
        List<Suit> knownSuits = p.getKnownSuits();
        assertThat(knownSuits.size(), is(5));
        for (Suit s : knownSuits) {
            assertThat(s, is(nullValue()));
        }
    }

    @Test
    public void ReceiveSuitTipUpdatesCorrectly() {
        Suit tipSuit = Suit.BLUE;
        p.receiveSuitTip(tipSuit, Arrays.asList(2, 3));

        assertThat(p.getKnownSuits().get(0), is(nullValue()));
        assertThat(p.getKnownSuits().get(1), is(nullValue()));
        assertThat(p.getKnownSuits().get(2), is(tipSuit));
        assertThat(p.getKnownSuits().get(3), is(tipSuit));
        assertThat(p.getKnownSuits().get(4), is(nullValue()));
    }
}
