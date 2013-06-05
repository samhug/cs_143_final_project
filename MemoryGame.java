import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MemoryGame extends JApplet {

	private static final int TIME_DELAY = 1000;

	private class Level {
		
		String description;
		String name;
		int width;
		int height;
		int matchCount;
		
		public Level(String name, String description, int width, int height, int matchCount) {
			this.description = description;
			this.name = name;
			this.width = width;
			this.height = height;
			this.matchCount = matchCount;
		}
		
		public String toString() {
			return description+" "+name;
		}
	}
	
	private final Level[] LEVELS = {
			/*  NAME, DESCRIPTION, WIDTH, HEIGHT, MATCH_SIZE */
			new Level("Level 1", "MATCH TWO OF A KIND                       ", 2, 2, 2),
			new Level("Level 2", "MATCH TWO OF A KIND                       ", 4, 3, 2),
			new Level("Level 3", "MATCH THREE OF A KIND                     ", 4, 3, 3),
			new Level("Level 4", "MATCH TWO OF A KIND                       ", 4, 5, 2),
			new Level("Level 5", "MATCH THREE OF A KIND                     ", 4, 6, 3),
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
						
						play(getCodeBase(), "cardsMatch.wav");
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
									
									play(getCodeBase(), "levelComplete.wav");
									System.out.println("You won!");
									
									String playerName = JOptionPane.showInputDialog("Awesome Job!\n Please enter your name for the scoreboard.");
									new ScoreDialog(null, true, playerName, currentLevel, score);
									
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
	
	private class ScoreDialog extends JDialog {
		
		JButton closeButton;
		JLabel scoreboardLabel;
		
		public ScoreDialog(JFrame owner, boolean modal, final String playerName, final Level level, final int score) {
			super(owner, "Scoreboard", modal);
			
			scoreboardLabel = new JLabel("Please wait while we upload your score...");
			
			closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae){
                	closeDialog();
                }
            });
			closeButton.setEnabled(false);
			
			Box box = Box.createVerticalBox();
			box.add(scoreboardLabel);
			box.add(closeButton);
	
			add(box);
			
			
			Timer timer = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String scoreboard = submitScore(playerName, level.name, score);
					scoreboardLabel.setText(scoreboard);
					closeButton.setEnabled(true);
					pack();
				}
			});
			timer.setRepeats(false);
			timer.start();
			
			pack();
			setVisible(true);
			
		}
	
		public void closeDialog() {
			setModal(false);
			dispose();
		}
		
		public String submitScore(String playerName, String levelName, int score) {
			String highscores = "<html>";
			
			System.out.println("Publishing score...");
			
			try {
				String urlParameters = "level="+ URLEncoder.encode(levelName) +"&name=" + URLEncoder.encode(playerName) + "&score=" + URLEncoder.encode(Integer.toString(score));
				URL url;
				if (getCodeBase().getProtocol().startsWith("http")) {
					url = new URL(getCodeBase(), "highscores");
				} else {
					url = new URL("http://memorygam3.appspot.com/highscores");
				}
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("POST"); 
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				connection.setUseCaches(false);
				
				DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
				writer.writeBytes(urlParameters);
				writer.flush();
				
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = reader.readLine()) != null) {
					highscores += line + "<br>";
				}
				
				writer.close();
				reader.close();
				connection.disconnect();
	
			} catch (IOException e) {
				return "Error: Unable to submit score.";
			}
			
			System.out.println("Score published...");
			
			return highscores+"</html>";
		}
	}
}

