import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MemoryGame extends JApplet {
	
	private final int GRID_WIDTH = 4; // The number of cells wide the grid is.
	private final int GRID_HEIGHT = 4; // The number of cells tall the grid is.
	
	private final Color BACKGROUND_COLOR = Color.gray;
	
	// The total number of cards
	private final int N_CARDS = GRID_WIDTH * GRID_HEIGHT;
	
	private ArrayList<Card> cards;
	
    /**
     * Construct the window
     */
    public void init() {
        // Set the background color.
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        cards = new ArrayList<Card>();
        
        // Construct the card deck
        // TODO: Ensure the cards are created in pairs and the deck is shuffled
        for (int i=0; i<N_CARDS; i++) {
        	cards.add(new Card());
        }
        
        this.addMouseListener(new MyMouseListener());
        
    }
    
    /**
     * paint method
     * @param g The applet's Graphics object.
     */
    public void paint(Graphics g) {
        // Call the superclass paint method.
        super.paint(g);
        
        // Get the size of the drawing area
        final int width = getContentPane().getWidth();
        final int height = getContentPane().getHeight();
        
        // Get the size of each individual cell
        final int cellWidth = width / GRID_WIDTH;
        final int cellHeight = height / GRID_HEIGHT;
        
        
        /*
         * Cards are numbered as such:
         * -----------------
         * | 0 | 1 | 2 | 3 |
         * -----------------
         * | 4 | 5 | 6 | 7 |
         * -----------------
         * | 8 | 9 | 10| 11|
         * -----------------
         */
        
        // Loop through the rows
        for (int row=0; row<GRID_HEIGHT; row++) {
        	// Loop through the columns
        	for (int col=0; col<GRID_WIDTH; col++) {
        		
        		/*
        		 * The current card number is the
        		 * current-row * number-of-cards-per-row + current-column
        		 */
        		final int cardNum = row*GRID_WIDTH + col;
        		
        		final Card card = cards.get(cardNum);
        		
        		// Calculate the x and y position of the cell
        		final int x = col*cellWidth;
        		final int y = row*cellHeight;
        		
        		this.drawCard(g, card, x, y, cellWidth, cellHeight);
        	}
        }
    }
    
    /**
     * Constructs a temporary graphics object for the card two draw to. This ensures that 
     * transformations done to the graphics objects by the card during drawing will not effect
     * the global behavior.
     * 
     * @param g The graphics object ultimately to be drawn to.
     * @param card The card to draw.
     * @param x The x-position to draw the card to.
     * @param y The y-position to draw the card to.
     * @param width The width of the card.
     * @param height The height of the card.
     */
    private void drawCard(Graphics g, Card card, final int x, final int y, final int width, final int height) {
    	// Create a temporary graphics object.
    	final Graphics2D g1 = (Graphics2D)g.create();
	    try {
	    	// Translate the coordinate system to the target drawing position.
	    	g1.translate(x, y);
	    	
			// Call the cards paint method.
	    	card.paint(g1, width, height);
	    } finally {
	    	g1.dispose();
	    }
    }
    
    private void onCardClicked(int cardNum) {
    	System.out.println("Card " + cardNum + " was clicked.");
    	
    	Card card = cards.get(cardNum);
    	
    	if (card.getCardState() == Card.CardState.FACE_DOWN) {
    		card.setCardState(Card.CardState.FACE_UP);
    	}
    	else if (card.getCardState() == Card.CardState.FACE_UP) {
    		card.setCardState(Card.CardState.FACE_DOWN);
    	}
    	
    	repaint();
    }
    
    
    // TODO: Clean this up 
    private class MyMouseListener extends MouseAdapter {
        public void mousePressed( MouseEvent e ) {
        	int mouseX = e.getX();
        	int mouseY = e.getY();
        	
        	// Get the size of the drawing area
            final int width = getContentPane().getWidth();
            final int height = getContentPane().getHeight();
            
            // Get the size of each individual cell
            final int cellWidth = width / GRID_WIDTH;
            final int cellHeight = height / GRID_HEIGHT;
        	
        	// Check if a playing card was clicked
            for (int row=0; row<GRID_HEIGHT; row++) {
            	for (int col=0; col<GRID_WIDTH; col++) {
            		
            		final int cardNum = row*GRID_WIDTH + col;
            		
            		// Calculate the x and y position of the cell
            		final int x = col*cellWidth;
            		final int y = row*cellHeight;
            		
            		Rectangle r = new Rectangle(x, y, cellWidth, cellHeight);
            		
            		if (r.contains(mouseX, mouseY)) {
            			onCardClicked(cardNum);
            		}
            	}
            }
        }
    }
}