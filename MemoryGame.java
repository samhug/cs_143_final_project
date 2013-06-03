import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MemoryGame extends JApplet {

	private static final int TIME_DELAY = 1000;

	private final int GRID_WIDTH = 4; // The number of cells wide the grid is.
	private final int GRID_HEIGHT = 4; // The number of cells tall the grid is.

	private final String SYMBOL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// The background color for the playing area
	private final Color BACKGROUND_COLOR = Color.gray;

	// The total number of cards
	private final int N_CARDS = GRID_WIDTH * GRID_HEIGHT;

	private final Random rand = new Random();

	CardClickListener cardClickListener = new CardClickListener();

	Card firstCard;
	Card secondCard;

	JPanel cardPanel;
	JLabel scoreLabel;
	
	GameState gameState;

	int score;
	int nMatches;

	/**
	 * Construct the window
	 */
	public void init() {
		// Set the size of the window
		setSize(600, 800);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.gridx = 0;

		
		Box menuLayout = Box.createHorizontalBox();
		
		JButton newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
				System.out.println("New Game");
			}
		});
		menuLayout.add(newGameButton);
		
		scoreLabel = new JLabel();
		scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		scoreLabel.setHorizontalTextPosition(JLabel.RIGHT);
		updateScore(0);
		menuLayout.add(scoreLabel);
		
		constraints.weighty = 0.25;
		constraints.gridy = 0;
		this.add(menuLayout, constraints);
		

		cardPanel = new JPanel();

		// Set the background color.
		cardPanel.setBackground(BACKGROUND_COLOR);

		constraints.weighty = 10.0;
		constraints.gridy = 1;
		this.add(cardPanel, constraints);

		// Initialize a new game
		newGame();
	}
	
	public void newGame() {
		// Remove any remnants of any previous game
		cardPanel.removeAll();
		nMatches = 0;
		updateScore(0);
		gameState = GameState.NO_CARDS_UP;
		
		// Construct a grid layout for the cards
		cardPanel.setLayout(new GridLayout(GRID_WIDTH, GRID_HEIGHT));
		
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

		// shuffle the cards
        Collections.shuffle(cards);

		// Place the shuffled cards in the grid layout
		for (int i = 0; i < N_CARDS; i++) {
			cardPanel.add(cards.get(i));
		}

		cardPanel.revalidate();
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
			
			if (card.getCardState() == Card.CardState.OUT_OF_PLAY) {
				return;
			}
			
			if (gameState == GameState.TWO_CARDS_UP) {
				/* Two cards have already been flipped. The player must wait for them to
				 * flip back over before he may continue the game. */
			}
			
			else if (gameState == GameState.NO_CARDS_UP) {
				/* No cards have been flipped yet. Flip the card and wait for the
				 * player to flip another one. */
				gameState = GameState.ONE_CARD_UP;
				
				System.out.println("First card flipped.");
				
				firstCard = card;
				firstCard.flip();
			}
			
			else if (gameState == GameState.ONE_CARD_UP) {
				/* One card has been flipped so far. Flip the second card and check if they match.
				 * Then set a timer to either flip them back over, or disable them,
				 * depending on if the matched. */
				if (card == firstCard) {
					return;
				}
				
				gameState = GameState.TWO_CARDS_UP;
				
				System.out.println("Second card flipped.");
				
				secondCard = card;
				secondCard.flip();
				incrementScore();
				
				// Check if the two cards match
				if(firstCard.getSymbol() == secondCard.getSymbol()) {
					
					// Timer to delay between card flips
					Timer timer = new Timer(TIME_DELAY, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							firstCard.match();
							secondCard.match();
							
							nMatches++;
							if (nMatches == N_CARDS/2) {
								// The game is over
								gameState = GameState.GAME_OVER;
								
								System.out.println("You won!");
								JOptionPane.showMessageDialog(null, "Awesome Job!!");
							} else {
								// Reset the game state
								gameState = GameState.NO_CARDS_UP;
							}
						}
					});
					timer.setRepeats(false);
					timer.start();

					System.out.println("You found a match!");
				}
				else {
					
					// Timer to delay between card flips
					Timer timer = new Timer(TIME_DELAY, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							firstCard.flip();
							secondCard.flip();
							
							// Reset the game state
							gameState = GameState.NO_CARDS_UP;
						}
					});
					timer.setRepeats(false);
					timer.start();
				}
				
			}
		}
	}
	
	public enum GameState {
		NO_CARDS_UP, // All the cards are face down, we're waiting for the player to click one.
		ONE_CARD_UP, // The user has flipped one card, we're waiting for a second to match it with.
		TWO_CARDS_UP, // Two cards have been flipped, and we're waiting for them to flip back over.
		GAME_OVER, // The player found all the matches, and has completed the game.
	}
}

