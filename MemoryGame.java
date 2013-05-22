import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MemoryGame extends JApplet {

	private final int GRID_WIDTH = 4; // The number of cells wide the grid is.
	private final int GRID_HEIGHT = 4; // The number of cells tall the grid is.

	private final String SYMBOL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// The background color for the playing area
	private final Color BACKGROUND_COLOR = Color.gray;

	// The total number of cards
	private final int N_CARDS = GRID_WIDTH * GRID_HEIGHT;

	private final Random rand = new Random();

	CardClickListener cardClickListener = new CardClickListener();

	/**
	 * Construct the window
	 */
	public void init() {
		// Set the background color.
		getContentPane().setBackground(BACKGROUND_COLOR);

		// Construct a grid layout for the cards
		GridLayout gridLayout = new GridLayout(GRID_WIDTH, GRID_HEIGHT);
		this.setLayout(gridLayout);

		// Array to temporarily hold the cards.
		Card cards[] = new Card[N_CARDS];

		// Create the cards and put them in the card array
		for (int i = 0; i < N_CARDS; i += 2) {

			// Get a random symbol
			final char symbol = getRandomSymbol();

			// Create two cards with the same symbol
			final Card card1 = new Card(symbol);
			final Card card2 = new Card(symbol);

			// Add our click listener to them
			card1.addClickListener(cardClickListener);
			card2.addClickListener(cardClickListener);

			// Place the cards in the card array
			cards[i] = card1;
			cards[i + 1] = card2;
		}

		// TODO: Shuffle the card array

		// Place the shuffled cards in the grid layout
		for (int i = 0; i < N_CARDS; i++) {
			this.add(cards[i]);
		}
	}

	/**
	 * Picks a random character from SYMBOL_ALPHABET and returns it.
	 * 
	 * @return The random character.
	 */
	private char getRandomSymbol() {
		final int length = SYMBOL_ALPHABET.length();
		return SYMBOL_ALPHABET.charAt(rand.nextInt(length));
	}

	private class CardClickListener implements Card.ClickListener {
		@Override
		public void cardClicked(Card card) {

			System.out.println("Card clicked!");

			// Flip the card
			card.flip();
		}
	}
}