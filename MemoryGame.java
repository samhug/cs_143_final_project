import javax.swing.*;
import java.awt.*;


public class MemoryGame extends JApplet {
	
	private final int GRID_WIDTH = 4; // The number of cells wide the grid is.
	private final int GRID_HEIGHT = 4; // The number of cells tall the grid is.
	
	// The background color for the playing area
	private final Color BACKGROUND_COLOR = Color.gray;
	
	// The total number of cards
	private final int N_CARDS = GRID_WIDTH * GRID_HEIGHT;
	
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
        
        // Create the cards and put them in the grid layout
        // TODO: Need to ensure the cards are created in pairs and the deck is shuffled.
        for (int i=0; i<N_CARDS; i++) {
        	
        	// Create a new card
        	final Card card = new Card();
        	
        	// Add our click listener to it
        	card.addClickListener(cardClickListener);
        	
        	// Place the card in the grid layout
        	this.add(card);
        }
    }
    
    private class CardClickListener implements Card.ClickListener {
		@Override
		public void cardClicked(Card card) {
			
			System.out.println("Card clicked!");
			
			if (card.getCardState() == Card.CardState.FACE_DOWN) {
	    		card.setCardState(Card.CardState.FACE_UP);
	    	}
	    	else if (card.getCardState() == Card.CardState.FACE_UP) {
	    		card.setCardState(Card.CardState.FACE_DOWN);
	    	}
		}
    }
}