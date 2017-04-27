package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.player.Action;
import com.jacoblucas.hanabi.player.AlwaysDiscardPlayer;
import com.jacoblucas.hanabi.player.AlwaysPlayPlayer;
import com.jacoblucas.hanabi.player.Player;
import com.jacoblucas.hanabi.player.RandomTipPlayer;
import com.jacoblucas.hanabi.player.TipAction;
import com.jacoblucas.hanabi.player.TipType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    @Getter(AccessLevel.PROTECTED) private Map<Player, List<Card>> playerHands;
    private Queue<Tip> tips;
    @Setter(AccessLevel.PROTECTED) private Queue<Fuse> fuses;
    @Setter(AccessLevel.PROTECTED) private Deck deck;
    private Map<Suit, Stack<Card>> fireworks;
    private boolean playersHaveWon = false;

    // Deals out the initial cards to the players
    void seed() {
        for (int i=0; i<5; i++) {
            for (Player p : players) {
                Card c = deck.deal();
                playerHands.get(p).add(c);
            }
        }
        for (Suit s : Suit.values()) {
            fireworks.put(s, new Stack<>());
        }
    }

    // Runs the main loop of the game
    private void run() {
        while (!gameOver()) {
            Player player = players.poll();
            signalPlayerAction(player);
            players.add(player);
        }

        score();
    }

    int score() {
        int score = 0;
        for (Suit s : fireworks.keySet()) {
            Stack<Card> cards = fireworks.get(s);
            if (!cards.isEmpty()) {
                score += cards.peek().getNumber();
            }
        }

        if (score == 25) {
            System.out.println("Score = 25! Legendary, everyone left speechless, stars in their eyes!");
        } else if (score >= 21 && score < 25) {
            System.out.println("Score = " + score + "! Amazing, they will be talking about it for weeks!");
        } else if (score >= 16 && score < 21) {
            System.out.println("Score = " + score + "! Excellent. Crowd pleasing.");
        } else if (score >= 11 && score < 16) {
            System.out.println("Score = " + score + "! Honorable attempt, but quickly forgotten...");
        } else if (score >= 6 && score < 11) {
            System.out.println("Score = " + score + "! Mediocre, just a hint of scattered applause...");
        } else {
            System.out.println("Score = " + score + "! Horrible, booed by the crowd...");
        }

        return score;
    }

    Action signalPlayerAction(Player player) {
        Action action = player.takeAction(fireworks, getOtherPlayerHands(player));

        switch (action.getActionType()) {
            case DISCARD:
                // discard the card
                Card discardedCard = removeCardFromHand(player, action.getImpactedCardIndices().get(0));

                // give a replacement card for the card that was discarded
                Card newCard = deck.deal();
                playerHands.get(player).add(newCard);

                // replace a tip
                if (tips.size() < NUM_TIPS) {
                    tips.add(new Tip());
                }

                System.out.println("Player " + player.getName() + " discarded a " + discardedCard);
                break;

            case PLAY:
                Card playedCard = removeCardFromHand(player, action.getImpactedCardIndices().get(0));
                System.out.println("Player " + player.getName() + " played a " + playedCard);

                if (isCardPlayable(playedCard)) {
                    // put the card on the table
                    fireworks.get(playedCard.getSuit()).add(playedCard);

                    // get back a tip if player has played a 5
                    if (playedCard.getNumber() == 5 && tips.size() < NUM_TIPS) {
                        System.out.println("Woo hoo! The " + playedCard.getSuit() + " firework has been completed!");
                        tips.add(new Tip());
                    }

                } else {
                    // take off a fuse
                    fuses.poll();
                    System.out.println("Player " + player.getName() + " triggered a fuse (" + playedCard + " cannot be played)!");
                }

                // give a replacement card for the card that was played
                newCard = deck.deal();
                playerHands.get(player).add(newCard);

                break;

            case TIP:
                // Give player some information
                TipAction tip = (TipAction) action;

                // TODO: inform player somehow

                System.out.println("Player " + player.getName() + " gave a tip to Player '" + tip.getReceivingPlayer().getName() + "' : Cards at " + tip.getImpactedCardIndices() + " are " + (tip.getType() == TipType.NUMBER ? tip.getTipNumber() : tip.getTipSuit()));
                break;
        }

        if (deck.size() == 0) {
            player.setTakenLastAction(true);
            System.out.println("Player " + player.getName() + " has taken their last turn!");
        }

        return action;
    }

    // a card is playable if the top card of the firework of it's suit is 1 less than the card number,
    // or if it's a one and that firework has zero cards
    boolean isCardPlayable(Card playedCard) {
        Stack<Card> stk = fireworks.get(playedCard.getSuit());

        if (stk.isEmpty()) {
            return playedCard.getNumber() == 1;
        }

        return stk.peek().getNumber() == playedCard.getNumber() - 1;
    }

    private Map<Player, List<Card>> getOtherPlayerHands(Player player) {
        Map<Player, List<Card>> others = new HashMap<>();
        for (Player p : playerHands.keySet()) {
            if (p != player) {
                others.put(p, playerHands.get(p));
            }
        }
        return others;
    }

    private Card removeCardFromHand(Player player, int cardIndexInPlayerHand) {
        return playerHands.get(player).remove(cardIndexInPlayerHand);
    }

    // Detects game over.
    // Players lose when all fuses are gone.
    // Players win if all fives have been played successfully.
    // Players lose when one full round has been played after the deck has been emptied if all 5's have not been played successfully.
    boolean gameOver() {
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
        Map<Player, List<Card>> playerHands = new HashMap<>();

        // TODO: read in num players from command line
        Player discarder = new AlwaysDiscardPlayer("Discarder");
        Player player = new AlwaysPlayPlayer("Player");
        Player tipper = new RandomTipPlayer("Tipper");
        players.add(discarder);
        players.add(player);
        players.add(tipper);
        playerHands.put(discarder, new ArrayList<>());
        playerHands.put(player, new ArrayList<>());
        playerHands.put(tipper, new ArrayList<>());

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
                .playerHands(playerHands)
                .deck(new Deck())
                .build();

        game.seed();

        game.run();
    }
}