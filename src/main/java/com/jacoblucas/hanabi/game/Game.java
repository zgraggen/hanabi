package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.Action;
import com.jacoblucas.hanabi.player.AlwaysDiscardPlayer;
import com.jacoblucas.hanabi.player.Player;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

// https://en.wikipedia.org/wiki/Hanabi_(card_game)

@Builder
@Getter
// Game class that controls the game of Hanabi.
public class Game {
    protected static int NUM_TIPS = 8;

    private Queue<Player> players;
    private Queue<Tip> tips;
    @Setter(AccessLevel.PROTECTED) private Queue<Fuse> fuses;
    @Setter(AccessLevel.PROTECTED) private Deck deck;
    private Map<Suit, Stack<Card>> fireworks;
    private boolean playersHaveWon = false;

    // Deals out the initial cards to the players
    protected void seed() {
        for (int i=0; i<5; i++) {
            for (Player p : players) {
                deck.deal(p);
            }
        }
        for (Suit s : Suit.values()) {
            fireworks.put(s, new Stack<>());
        }
    }

    // Runs the main loop of the game
    protected void run() {
        while (!gameOver()) {
            Player player = players.poll();
            signalPlayerAction(player);
            players.add(player);
        }
    }

    protected Action signalPlayerAction(Player player) {
        Action action = player.takeAction();
        switch (action.getActionType()) {
            case DISCARD:
                // discard the card
                player.removeCardFromHand(action.getCard());

                // give a replacement card for the card that was discarded
                deck.deal(player);

                // replace a tip
                if (tips.size() < NUM_TIPS) {
                    tips.add(new Tip());
                }

                System.out.println("Player " + player.getName() + " discarded a " + action.getCard());
            case PLAY:
                // give a replacement card for the card that was played
                // if played played a 5, add back a tip
                // TODO
            case TIP:
                // Give player some information
                // TODO
        }

        if (deck.size() == 0) {
            player.setTakenLastAction(true);
        }

        return action;
    }

    // Detects game over.
    // Players lose when all fuses are gone.
    // Players win if all fives have been played successfully.
    // Players lose when one full round has been played after the deck has been emptied if all 5's have not been played successfully.
    public boolean gameOver() {
        if (fuses.isEmpty()) {
            System.out.println("Game over - all fuses have been played! Players lose!");
            return true;
        }

        boolean allFivesHaveBeenPlayed = true;
        for (Suit s : Suit.values()) {
            Stack<Card> cards = fireworks.get(s);
            allFivesHaveBeenPlayed &= (cards.size() == 5 && cards.peek().getNumber() == 5);
        }

        if (allFivesHaveBeenPlayed) {
            System.out.println("Game over - all fives have been played! Players win!");
            playersHaveWon = true;
            return true;
        }

        if (deck.size() == 0) {
            boolean allPlayersTakenFinalAction = true;
            for (Player p : players) {
                allPlayersTakenFinalAction &= p.isTakenLastAction();
            }

            if (allPlayersTakenFinalAction) {
                System.out.println("Game over - deck is empty! Players lose!");
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        Queue<Player> players = new LinkedList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();
        Map<Suit, Stack<Card>> fireworks = new HashMap<>();

        // TODO: read in num players from command line
        int n = 3;
        for (int i=0; i<n; i++) {
            players.add(new AlwaysDiscardPlayer());
        }

        for (int i=0; i<8; i++) {
            tips.add(new Tip());
        }

        for (int i=0; i<3; i++) {
            fuses.add(new Fuse());
        }

        Game game = Game
                .builder()
                .players(players)
                .tips(tips)
                .fuses(fuses)
                .fireworks(fireworks)
                .deck(new Deck())
                .build();

        game.seed();

        game.run();
    }
}