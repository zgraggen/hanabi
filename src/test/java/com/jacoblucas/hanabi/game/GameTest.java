package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.AlwaysDiscardPlayer;
import com.jacoblucas.hanabi.player.AlwaysPlayPlayer;
import com.jacoblucas.hanabi.player.Player;
import com.jacoblucas.hanabi.player.RandomTipPlayer;
import com.jacoblucas.hanabi.action.TipAction;
import com.jacoblucas.hanabi.action.TipType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import static com.jacoblucas.hanabi.game.Game.NUM_TIPS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GameTest {
    private Game game;
    private AlwaysDiscardPlayer p1;
    private RandomTipPlayer p2;
    private AlwaysPlayPlayer p3;

    @Before
    public void setUp() {
        List<Player> players = new ArrayList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();
        Map<Suit, Stack<Card>> fireworks = new HashMap<>();
        Map<Player, List<Card>> playerHands = new HashMap<>();
        for (Suit s : Suit.values()) {
            fireworks.put(s, new Stack<>());
        }

        p1 = new AlwaysDiscardPlayer(UUID.randomUUID().toString());
        p2 = new RandomTipPlayer(UUID.randomUUID().toString());
        p3 = new AlwaysPlayPlayer(UUID.randomUUID().toString());
        players.add(p1);
        players.add(p2);
        players.add(p3);

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
                .playerHands(playerHands)
                .deck(new Deck())
                .build();

        game.seed();
    }

    @After
    public void tearDown() {
        game = null;
        p1 = null;
        p2 = null;
        p3 = null;
    }

    @Test
    public void SeedDealsFiveCardsToEachPlayer() {
        for (List<Card> hand : game.getPlayerHands().values()) {
            assertThat(hand.size(), is(5));
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
        assertThat(game.gameOver(), is(false));

        // force fuses to empty
        game.setFuses(new LinkedList<>());

        assertThat(game.gameOver(), is(true));
        assertThat(game.isPlayersHaveWon(), is(false));
    }

    @Test
    public void GameOverWhenAllFivesHaveBeenPlayed() {
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
    public void scoreSumsTheTopCardOfEachFirework() {
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

    @Test
    public void DiscardActionReplenishesPlayerHand() {
        game.signalPlayerAction(p1); // AlwaysDiscardPlayer
        assertThat(game.getPlayerHands().get(p1).size(), is(5));
    }

    @Test
    public void DiscardActionReplenishesTips() {
        game.getTips().poll();
        assertThat(game.getTips().size(), is(NUM_TIPS-1));

        game.signalPlayerAction(p1); // AlwaysDiscardPlayer

        assertThat(game.getTips().size(), is(NUM_TIPS));
    }

    @Test
    public void PlayActionReplenishesPlayerHand() {
        game.signalPlayerAction(p3); // AlwaysPlayPlayer
        assertThat(game.getPlayerHands().get(p1).size(), is(5));
    }

    @Test
    public void PlayActionForAFiveReplenishesATip() {
        game.getFireworks().get(Suit.GREEN).add(new Card(1, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(2, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(3, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(4, Suit.GREEN));

        game.getTips().poll();
        game.getPlayerHands().get(p3).remove(0);
        game.getPlayerHands().get(p3).add(0, new Card(5, Suit.GREEN));

        int numTips = game.getTips().size();
        game.signalPlayerAction(p3);

        assertThat(game.getTips().size(), is(numTips + 1));
    }

    @Test
    public void PlayActionForPlayableCardPlacesCardOnFirework() {
        game.getFireworks().get(Suit.BLUE).add(new Card(1, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(2, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(3, Suit.BLUE));

        game.getPlayerHands().get(p3).remove(0);
        Card card = new Card(4, Suit.BLUE);
        game.getPlayerHands().get(p3).add(0, card);

        game.signalPlayerAction(p3);

        assertThat(game.getFireworks().get(Suit.BLUE).size(), is(4));
        assertThat(game.getFireworks().get(Suit.BLUE).peek(), is(card));
    }

    @Test
    public void PlayActionForUnplayableCardTriggersFuse() {
        game.getFireworks().get(Suit.BLUE).add(new Card(1, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(2, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(3, Suit.BLUE));

        game.getPlayerHands().get(p3).remove(0);
        game.getPlayerHands().get(p3).add(0, new Card(2, Suit.BLUE));

        int numFuses = game.getFuses().size();
        game.signalPlayerAction(p3);

        assertThat(game.getFuses().size(), is(numFuses - 1));
    }

    @Test
    public void TestAllOnesArePlayable() {
        for (Suit s : Suit.values()) {
            Card card = new Card(1, s);
            assertThat(game.isCardPlayable(card), is(true));
        }
    }

    @Test
    public void TestIsCardPlayable() {
        game.getFireworks().get(Suit.BLUE).add(new Card(1, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(2, Suit.BLUE));
        game.getFireworks().get(Suit.BLUE).add(new Card(3, Suit.BLUE));

        game.getFireworks().get(Suit.GREEN).add(new Card(1, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(2, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(3, Suit.GREEN));
        game.getFireworks().get(Suit.GREEN).add(new Card(4, Suit.GREEN));

        game.getFireworks().get(Suit.WHITE).add(new Card(1, Suit.WHITE));

        // no red

        game.getFireworks().get(Suit.YELLOW).add(new Card(1, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(2, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(3, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(4, Suit.YELLOW));
        game.getFireworks().get(Suit.YELLOW).add(new Card(5, Suit.YELLOW));

        assertThat(game.isCardPlayable(new Card(1, Suit.RED)), is(true));
        assertThat(game.isCardPlayable(new Card(1, Suit.BLUE)), is(false));
        assertThat(game.isCardPlayable(new Card(3, Suit.YELLOW)), is(false));
        assertThat(game.isCardPlayable(new Card(2, Suit.WHITE)), is(true));
    }

    @Test
    public void TipActionInformsPlayer() {
        TipAction tip = (TipAction) game.signalPlayerAction(p2);
        Player receiver = tip.getReceivingPlayer();
        List<Integer> indices = tip.getImpactedCardIndices();
        TipType type = tip.getType();

        if (type == TipType.SUIT) {
            for (Integer i : indices) {
                assertThat(receiver.getKnownSuits().get(i), is(tip.getTipSuit()));
            }
        } else {
            for (Integer i : indices) {
                assertThat(receiver.getKnownNumbers().get(i), is(tip.getTipNumber()));
            }
        }
    }

    @Test
    public void TipActionConsumesATip() {
        int numTips = game.getTips().size();

        game.signalPlayerAction(p2);

        assertThat(game.getTips().size(), is(numTips - 1));
    }
}