package com.jacoblucas.hanabi.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.jacoblucas.hanabi.action.Action;
import com.jacoblucas.hanabi.action.DiscardAction;
import com.jacoblucas.hanabi.action.PlayAction;
import com.jacoblucas.hanabi.action.TipAction;
import com.jacoblucas.hanabi.model.Card;
import com.jacoblucas.hanabi.model.PlayerWithHand;
import com.jacoblucas.hanabi.model.Suit;

public class Suggest1and2Player extends Player {

	public Suggest1and2Player(String name) {
		super(name);
	}

	@Override
	public Action takeAction(
			Map<Suit, Stack<Card>> fireworks,
			List<PlayerWithHand> playerHands,
			int remainingTips,
			int remainingFuses) {
		
		//Variables used for tipping
		List<Card> next = playerHands.get(0).getCards();
		List<Integer> tips = new ArrayList<>();
		int tipForNumberX = 1;
		
		//Tips for 2 (important tip)
		//TODO Expand this to also consider the player after the next
		for(Entry<Suit, Stack<Card>> s: fireworks.entrySet()) {
			if(!s.getValue().isEmpty() &&  s.getValue().peek().getNumber() == 1) {
				Suit suit = s.getValue().peek().getSuit();
				for(Card c: next) {
					if(c.getSuit() == suit && c.getNumber() == 2) {
						tipForNumberX = 2;
					}
				}
			}
		}
		
		if(tipForNumberX < 2) {
			//See if I can play a card and do it if so
			int highestKnown = 0;
			for(Integer i: getKnownNumbers()) {
				if(i != null && i > highestKnown) {
					highestKnown = i;
				}
			}
			if(highestKnown > 0 ) {
				for(int i=0; i < getKnownNumbers().size(); i++) {
					if(getKnownNumbers().get(i) != null && getKnownNumbers().get(i) == highestKnown) {
						return new PlayAction(i);
					}
				}
			}
			
			//Check how many tips. If 0 than discard oldest card.
			if(remainingTips == 0) {
				return new DiscardAction(0);
			}
		}
				

		//Tips for numberX (default is 1)
		if(tips.isEmpty()) {
			for(int i=0; i < next.size(); i++) {
				if(next.get(i).getNumber() == tipForNumberX){
					tips.add(i);
				}
			}
		}
		
		//If I can't give tip discard one.
		if(tips.isEmpty()) {
			return new DiscardAction(0);
		}else {
			return new TipAction(playerHands.get(0).getPlayer(), tipForNumberX, tips);	
		}
	}

}
