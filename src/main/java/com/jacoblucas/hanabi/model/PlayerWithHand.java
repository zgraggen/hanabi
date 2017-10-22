package com.jacoblucas.hanabi.model;

import java.util.List;

import com.jacoblucas.hanabi.player.Player;

import lombok.Getter;

@Getter
public class PlayerWithHand {
	private Player player;
	private List<Card> cards;
	
	public PlayerWithHand(Player player, List<Card> cards) {
		this.player = player;
		this.cards = cards;
	}
}
