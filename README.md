# hanabi

Java implementation of the Game of Hanabi. See https://en.wikipedia.org/wiki/Hanabi_(card_game) for the full rules.

To play, run the main method in the Game class.

To build a player, extend the Player abstract class and implement the takeAction function. You'll have access to everything in the game, except your own hand of course! Return the action you want your Player implementation to take. You can examine other player's hands, see what cards have been played in the game, and see the number of remaining tips and fuses to decide which action (Discard, Play, or Tip) is most appropriate.

Have fun!
