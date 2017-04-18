package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GameTest {
    private static int NUM_PLAYERS = 3;
    private Game game;

    @Before
    public void setUp() {
        List<Player> players = new ArrayList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();

        for (int i=0; i<NUM_PLAYERS; i++) {
            players.add(new Player());
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
                .deck(new Deck())
                .build();
    }

    @Test
    public void SeedDealsFiveCardsToEachPlayer() {
        game.seed();

        for (Player p : game.getPlayers()) {
            assertThat(p.getHand().size(), is(5));
        }
    }
}
