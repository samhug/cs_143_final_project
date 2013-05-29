import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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

	JPanel cardPanel;
	JLabel scoreLabel;
	
	int score;
	
	/**
	 * Construct the window
	 */
	public void init() {
		
		scoreLabel = new JLabel();
		scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		scoreLabel.setHorizontalTextPosition(JLabel.RIGHT);
		updateScore(0);
		
		cardPanel = new JPanel();
		
		// Set the background color.
		cardPanel.setBackground(BACKGROUND_COLOR);
		
		// Construct a grid layout for the cards
		cardPanel.setLayout(new GridLayout(GRID_WIDTH, GRID_HEIGHT));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.gridx = 0;
		
		constraints.weighty = 10.0;
		constraints.gridy = 1;
		this.add(cardPanel, constraints);

		constraints.weighty = 0.25;
		constraints.gridy = 0;
		this.add(scoreLabel, constraints);
		
		// Array to temporarily hold the cards.
		ArrayList<Card> cards = new ArrayList<Card>(N_CARDS);

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
			cards.add(card1);
			cards.add(card2);
		}
		
		// code Jesse added to shuffle the cards
        Collections.shuffle(cards);
        // end of code Jesse added

		// Place the shuffled cards in the grid layout
		for (int i = 0; i < N_CARDS; i++) {
			cardPanel.add(cards.get(i));
		}
	}
	
	public void updateScore(int score) {
		this.score = score;
		scoreLabel.setText("Score: " + score);
	}
	
	public void incrementScore() {
		updateScore(score+1);
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
