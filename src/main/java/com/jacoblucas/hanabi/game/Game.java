package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.Player;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// https://en.wikipedia.org/wiki/Hanabi_(card_game)

@Builder
@Getter
// Game class that controls the game of Hanabi.
public class Game {
    private List<Player> players;
    private Queue<Tip> tips;
    private Queue<Fuse> fuses;
    private Deck deck;

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

    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();

        // TODO: read in num players from command line
        int n = 3;
        for (int i=0; i<n; i++) {
            players.add(new Player());
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