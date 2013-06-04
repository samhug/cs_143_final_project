import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MemoryGame extends JApplet {

	private static final int TIME_DELAY = 1000;

	private class Level {
		
		String name;
		int width;
		int height;
		int matchCount;
		
		public Level(String name, int width, int height, int matchCount) {
			this.name = name;
			this.width = width;
			this.height = height;
			this.matchCount = matchCount;
		}
		
		public String toString() {
			return name;
		}
	}
	
	private final Level[] LEVELS = {
			/*  NAME, WIDTH, HEIGHT, MATCH_SIZE */
			new Level("Level 1", 2, 2, 2),
			new Level("Level 2", 4, 3, 2),
			new Level("Level 3", 4, 3, 3),
		};
	
	
	private final String SYMBOL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// The background color for the playing area
	private final Color BACKGROUND_COLOR = Color.gray;

	private final Random rand = new Random();

	// The total number of cards
	private int nCards;
	
	CardClickListener cardClickListener = new CardClickListener();

	ArrayList<Card> flippedCards;

	JPanel cardPanel;
	JLabel scoreLabel;
	JSpinner levelSpinner;
	
	GameState gameState;
	Level currentLevel;

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
		
		SpinnerListModel levelModel = new SpinnerListModel(LEVELS);
		levelSpinner = new JSpinner(levelModel);
		menuLayout.add(levelSpinner);
		
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
		flippedCards = new ArrayList<Card>();
		gameState = GameState.NO_CARDS_UP;
		
		currentLevel = (Level)levelSpinner.getValue();
		nCards = currentLevel.width * currentLevel.height;
		
		play(getCodeBase(), "cardShuffle.wav");
		
		// Construct a grid layout for the cards
		cardPanel.setLayout(new GridLayout(currentLevel.width, currentLevel.height));
		
		// Array to temporarily hold the cards.
		ArrayList<Card> cards = new ArrayList<Card>(nCards);

		// Create the cards and put them in the card array
		for (int i = 0; i < nCards; i += currentLevel.matchCount) {

			// Get a random symbol
			final char symbol = getRandomSymbol();

			for (int n=0; n<currentLevel.matchCount; n++) {
				
				// Create two cards with the same symbol
				Card card = new Card(symbol);
	
				// Add our click listener to them
				card.addClickListener(cardClickListener);
	
				// Place the cards in the card array
				cards.add(card);
			}
		}

		// shuffle the cards
        Collections.shuffle(cards);

		// Place the shuffled cards in the grid layout
		for (int i = 0; i < nCards; i++) {
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
			
			if (gameState == GameState.ENOUGH_CARDS_UP) {
				/* Enough cards have already been flipped. The player must wait for them to
				 * flip back over before he may continue the game. */
				return;
			}
			
			play(getCodeBase(), "cardFlipped.wav");
			
			if (gameState == GameState.NO_CARDS_UP) {
				/* No cards have been flipped yet. Flip the card and wait for the
				 * player to flip another one. */
				gameState = GameState.NOT_ENOUGH_CARDS_UP;
				
				System.out.println("First card flipped.");
				
				flippedCards.add(card);
				card.flip();
			}
			
			else if (gameState == GameState.NOT_ENOUGH_CARDS_UP) {
				/* One or more cards have been flipped so far. Flip the next card and check if they match.
				 * Then set a timer to either flip them back over, or disable them,
				 * depending on if the matched. */
				if (flippedCards.contains(card)) {
					return;
				}
		
				System.out.println("Next card flipped.");
				
				card.flip();
				
				flippedCards.add(card);
				
				// Check if the two cards match
				if(card.getSymbol() == flippedCards.get(0).getSymbol()) {
					
					if (flippedCards.size() >= currentLevel.matchCount) {
						gameState = GameState.ENOUGH_CARDS_UP;
						
						play(getCodeBase(), "cool.wav");
						incrementScore();
						
						// Timer to delay between card flips
						Timer timer = new Timer(TIME_DELAY, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								
								for(Card card : flippedCards) {
									card.match();
								}
								flippedCards.clear();
								
								nMatches++;
								if (nMatches == nCards/currentLevel.matchCount) {
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
				}
				else {
					gameState = GameState.ENOUGH_CARDS_UP;
					incrementScore();
					
					// Timer to delay between card flips
					Timer timer = new Timer(TIME_DELAY, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							
							for(Card card : flippedCards) {
								card.flip();
							}
							flippedCards.clear();
							
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
		NOT_ENOUGH_CARDS_UP, // The user has flipped one or more cards, we're waiting for more to match it with.
		ENOUGH_CARDS_UP, // Enough cards have been flipped, and we're waiting for them to flip back over.
		GAME_OVER, // The player found all the matches, and has completed the game.
	}
}

