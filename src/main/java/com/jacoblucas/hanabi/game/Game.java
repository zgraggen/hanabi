package com.jacoblucas.hanabi.game;

import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.Deck;
import com.jacoblucas.hanabi.model.Fuse;
import com.jacoblucas.hanabi.model.PlayerWithHand;
import com.jacoblucas.hanabi.model.Suit;
import com.jacoblucas.hanabi.model.Tip;
import com.jacoblucas.hanabi.action.Action;
import com.jacoblucas.hanabi.action.ActionType;
import com.jacoblucas.hanabi.player.Suggest1and2Player;
import com.jacoblucas.hanabi.player.Player;
import com.jacoblucas.hanabi.action.TipAction;
import com.jacoblucas.hanabi.action.TipType;
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

    @Builder.Default @Setter(AccessLevel.PRIVATE) private int play = 0;
    private List<Player> players;
    @Getter(AccessLevel.PROTECTED) private Map<Player, List<Card>> playerHands;
    private Queue<Tip> tips;
    @Setter(AccessLevel.PROTECTED) private Queue<Fuse> fuses;
    @Setter(AccessLevel.PROTECTED) private Deck deck;
    private Map<Suit, Stack<Card>> fireworks;
    @Builder.Default
    private boolean playersHaveWon = false;

    // Initialises player hands and the fireworks, Deals out the initial cards to the players
    void seed() {
        for (Player p : players) {
            playerHands.put(p, new ArrayList<>());
        }
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
            Player player = players.get(play%players.size());
            signalPlayerAction(player);
            play++;
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
    
    Action getValidAction(Player player) {
    	int actionAttemps = 3;
    	Action action = null;
    	while(actionAttemps > 0) {
    		action = player.takeAction(fireworks, getOtherPlayerHands(player), tips.size(), fuses.size());
            if(action.getActionType() == ActionType.TIP && tips.isEmpty()) {
                actionAttemps--;
            } else {
            	return action;
            }
    	}
    	System.out.println("Player " + player.getName() + " attempted 3 times to give a tip, when no tips where available.");
    	System.exit(1);
    	return action;
    }

    Action signalPlayerAction(Player player) {
    	Action action = getValidAction(player);
        
        switch (action.getActionType()) {
            case DISCARD:
                // discard the card
                Card discardedCard = removeCardFromHand(player, action.getImpactedCardIndices().get(0));

                // give a replacement card for the card that was discarded
                Card newCard = deck.deal();
                player.cardHasBeenUsed(action.getImpactedCardIndices().get(0));
                playerHands.get(player).add(newCard);

                // replace a tip
                if (tips.size() < NUM_TIPS) {
                    tips.add(new Tip());
                }

                System.out.println(player.getName() + " discarded a " + discardedCard);
                break;

            case PLAY:
                Card playedCard = removeCardFromHand(player, action.getImpactedCardIndices().get(0));
                System.out.println(player.getName() + " played a " + playedCard);

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
                    System.out.println(player.getName() + " triggered a fuse (" + playedCard + " cannot be played)!");
                }

                // give a replacement card for the card that was played
                newCard = deck.deal();
                player.cardHasBeenUsed(action.getImpactedCardIndices().get(0));
                playerHands.get(player).add(newCard);

                break;

            case TIP:
                // take off a tip
                tips.poll();

                // Give player some information
                TipAction tip = (TipAction) action;

                Player receivingPlayer = tip.getReceivingPlayer();
                if (tip.getType() == TipType.NUMBER) {
                    receivingPlayer.receiveNumberTip(tip.getTipNumber(), tip.getImpactedCardIndices());
                } else {
                    receivingPlayer.receiveSuitTip(tip.getTipSuit(), tip.getImpactedCardIndices());
                }

                System.out.println(player.getName() + " gave a tip to Player '" + receivingPlayer.getName() + "' : Cards at " + tip.getImpactedCardIndices() + " are " + (tip.getType() == TipType.NUMBER ? tip.getTipNumber() : tip.getTipSuit()));
                if(tips.isEmpty()) {
                	System.out.println("Carefull now! No Tips remaining!");
                }
                break;
        }

        if (deck.size() == 0) {
            player.setTakenLastAction(true);
            System.out.println(player.getName() + " has taken their last turn!");
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

    // TODO: sort this by player turn, add a turn position into the player class?
    private List<PlayerWithHand> getOtherPlayerHands(Player player) {
        List<PlayerWithHand> others = new ArrayList<>();
        for(int i=play+1; i < play + players.size(); i++) {
        	Player p = players.get(i%players.size());
        	others.add(new PlayerWithHand(p, playerHands.get(p)));
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
        List<Player> players = new ArrayList<>();
        Queue<Tip> tips = new LinkedList<>();
        Queue<Fuse> fuses = new LinkedList<>();
        Map<Suit, Stack<Card>> fireworks = new HashMap<>();
        Map<Player, List<Card>> playerHands = new HashMap<>();

        // TODO: read in num players from command line
//        Player discarder = new AlwaysDiscardPlayer("Discarder");
//        Player player = new AlwaysPlayPlayer("Player");
//        Player tipper = new RandomTipPlayer("Tipper");
//        players.add(discarder);
//        players.add(player);
//        players.add(tipper);
        
      Player b1 = new Suggest1and2Player("bot 1");
      Player b2 = new Suggest1and2Player("bot 2");
      Player b3 = new Suggest1and2Player("bot 3");
      players.add(b1);
      players.add(b2);
      players.add(b3);

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