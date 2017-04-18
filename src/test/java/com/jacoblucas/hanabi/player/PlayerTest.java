package com.jacoblucas.hanabi.player;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Suit;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class PlayerTest {
    @Test
    public void DealPutsCardInPlayersHand() {
        Player player = new Player();

        assertThat(player.getHand(), empty());

        Card c = new Card(1, Suit.RED);
        player.deal(c);

        assertThat(player.getHand(), contains(c));
    }
}
