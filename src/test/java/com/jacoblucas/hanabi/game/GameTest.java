package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.Action;
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

import static com.jacoblucas.hanabi.game.Game.NUM_TIPS;
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

        for (int i = 0; i< NUM_TIPS; i++) {
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

    @Test
    public void DiscardActionReplenishesPlayerHand() {
        game.seed();

        Player p = game.getPlayers().peek(); // AlwaysDiscardPlayer
        game.signalPlayerAction(p);
        assertThat(p.getHand().size(), is(5));
    }

    @Test
    public void DiscardActionReplenishesTips() {
        game.seed();

        game.getTips().poll();
        assertThat(game.getTips().size(), is(NUM_TIPS-1));

        Player p = game.getPlayers().peek(); // AlwaysDiscardPlayer
        game.signalPlayerAction(p);

        assertThat(game.getTips().size(), is(NUM_TIPS));
    }

    @Test
    public void scoreSumsTheTopCardOfEachFirework() {
        game.seed();

        game.getFireworks().get(Suit.BLUE).add(new Card(1, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(2, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(3, Suit.BLUE)); // top card

        game.getFireworks().get(Suit.GREEN).add(new Card(1, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(2, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(3, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(4, Suit.GREEN)); // top card

        game.getFireworks().get(Suit.WHITE).add(new Card(1, Suit.WHITE));

        // no red

        game.getFireworks().get(Suit.YELLOW).add(new Card(1, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(2, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(3, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(4, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(5, Suit.YELLOW)); // top card

        assertThat(game.score(), is(13)); // 3 + 4 + 1 + 0 + 5
    }
}
