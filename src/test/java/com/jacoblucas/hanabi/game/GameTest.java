package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.AlwaysDiscardPlayer;
import com.jacoblucas.hanabi.player.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GameTest {
    private static int NUM_PLAYERS = 3;
    private Game game;

    @Before
    public void setUp() {
        Queue<Player> players = new LinkedList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();
        Map<Suit, Stack<Card>> fireworks = new HashMap<>();
        for (Suit s : Suit.values()) {
            fireworks.put(s, new Stack<>());
        }

        for (int i=0; i<NUM_PLAYERS; i++) {
            players.add(new AlwaysDiscardPlayer());
        }

        for (int i=0; i<8; i++) {
            tips.add(new Tip());
        }

        for (int i=0; i<3; i++) {
            fuses.add(new Fuse());
        }

        game = Game
                .builder()
                .players(players)
                .tips(tips)
                .fuses(fuses)
                .fireworks(fireworks)
                .deck(new Deck())
                .build();
    }

    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void SeedDealsFiveCardsToEachPlayer() {
        game.seed();

        for (Player p : game.getPlayers()) {
            assertThat(p.getHand().size(), is(5));
        }
    }

    @Test
    public void GameOverWhenAllFusesAreGone() {
        assertThat(game.gameOver(), is(false));

        game.getFuses().poll();
        game.getFuses().poll();
        game.getFuses().poll();

        assertThat(game.gameOver(), is(true));
        assertThat(game.isPlayersHaveWon(), is(false));
    }

    @Test
    public void GameOverWhenDeckIsEmptyAfterOneMoreRound() {
        game.seed();
        assertThat(game.gameOver(), is(false));

        // force fuses to empty
        game.setFuses(new LinkedList<>());

        assertThat(game.gameOver(), is(true));
        assertThat(game.isPlayersHaveWon(), is(false));
    }

    @Test
    public void GameOverWhenAllFivesHaveBeenPlayed() {
        game.seed();
        assertThat(game.gameOver(), is(false));

        // add all cards to fireworks
        for (Suit s : Suit.values()) {
            for (int i=1; i<=5; i++) {
                Card c = new Card(i, s);
                Stack<Card> stk = game.getFireworks().get(s);
                stk.push(c);
            }
        }

        assertThat(game.gameOver(), is(true));
        assertThat(game.isPlayersHaveWon(), is(true));
    }

    @Test
    public void GameOverWhenAfterPlayersFinalTurn() {
        game.seed();
        assertThat(game.gameOver(), is(false));

        class TestDeck extends Deck {
            @Override
            public int size() {
                return 0;
            }
        }

        // override the deck to an "empty" deck
        game.setDeck(new TestDeck());

        assertThat(game.getDeck().size(), is(0));
        assertThat(game.gameOver(), is(false));
        assertThat(game.isPlayersHaveWon(), is(false));

        for (Player p : game.getPlayers()) {
            p.setTakenLastAction(true);
        }
        assertThat(game.gameOver(), is(true));
        assertThat(game.isPlayersHaveWon(), is(false));
    }
}
