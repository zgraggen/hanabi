package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.AlwaysDiscardPlayer;
import com.jacoblucas.hanabi.player.Player;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

// https://en.wikipedia.org/wiki/Hanabi_(card_game)

@Builder
@Getter
// Game class that controls the game of Hanabi.
public class Game {
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
    }

    // Runs the main loop of the game
    protected void run() {
        // TODO
    }

    // Detects game over.
    // Players lose when all fuses are gone.
    // Players win if all fives have been played successfully.
    // Players lose when one full round has been played after the deck has been emptied if all 5's have not been played successfully.
    public boolean gameOver() {
        if (fuses.isEmpty()) {
            return true;
        }

        boolean allFivesHaveBeenPlayed = true;
        for (Suit s : Suit.values()) {
            Stack<Card> cards = fireworks.get(s);
            allFivesHaveBeenPlayed &= (cards.size() == 5 && cards.peek().getNumber() == 5);
        }

        if (allFivesHaveBeenPlayed) {
            playersHaveWon = true;
            return true;
        }

        if (deck.size() == 0) {
            boolean allPlayersTakenFinalAction = true;
            for (Player p : players) {
                allPlayersTakenFinalAction &= p.isTakenLastAction();
            }

            if (allPlayersTakenFinalAction) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        Queue<Player> players = new LinkedList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();

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
                .deck(new Deck())
                .build();

        game.seed();
    }
}