import java.awt.*;


public class Card {
	
	private final Color CELL_BORDER_COLOR = Color.black;
	private final Color CARD_BORDER_COLOR = Color.green;
	private final Color CARD_FACE_COLOR = Color.white;
	private final Color CARD_BACK_COLOR = Color.cyan;
	private final Color TEXT_COLOR = Color.red;
	
	// Playing cards are not square. This is a ratio of the length of sides `WIDTH / HEIGHT`
	private final double ASPECT_RATIO = (double)5/7;
	
	// This is not a pixel count its a percentage of the total vertical height. 
	private final double PADDING = 0.1;
	
	public enum CardState {
		FACE_DOWN,  // The card is face down, only the patterned side is visible.
		FACE_UP,    // The card is face up, the front side is visible.
		OUT_OF_PLAY // The card is out of play, it has already been matched.
	}
	
	private CardState cardState;
	
	public Card() {
		
		// Set the initial state of the card
		setCardState(CardState.FACE_DOWN);
	}
	
	
	/**
	 * Paint the card and its surrounding border to the given graphics object.
	 * 
	 * NOTE: The given width and height correspond to the size of the grid cell,
	 *       NOT the card size. The size of the card is calculated as the largest
	 *       rectangle, where the ratio of it's sides is `ASPECT_RATIO`, that will
	 *       fit in the grid cell.
	 * 
	 * @param g The graphics object to paint to.
	 * @param width The width of the containing cell.
	 * @param height The height of the containing cell.
	 */
	public void paint(Graphics g, int width, int height) {
		
		// Draw the cell border rectangle
		g.setColor(CELL_BORDER_COLOR);
		g.drawRect(0, 0, width, height);
		
		Rectangle cardRect = calculateCardRect(width, height);
		
		g.translate(cardRect.x, cardRect.y);
		
		if (cardState == CardState.FACE_DOWN) {
			g.setColor(CARD_BACK_COLOR);
			g.fillRect(0, 0, cardRect.width, cardRect.height);
			
			g.setColor(CARD_BORDER_COLOR);
			g.drawRect(0, 0, cardRect.width, cardRect.height);
		} else {
			g.setColor(CARD_FACE_COLOR);
			g.fillRect(0, 0, cardRect.width, cardRect.height);
			
			g.setColor(CARD_BORDER_COLOR);
			g.drawRect(0, 0, cardRect.width, cardRect.height);
			
			// Draw a symbol in the center of the card
			//TODO: Center text and adjust font size
			g.setColor(TEXT_COLOR);
			g.drawString("A", cardRect.width/2, cardRect.height/2);
		}
	}
	
	/**
	 * Calculates the maximum size of the card with the correct aspect-ratio within
	 * the given bounds.
	 * 
	 * @param boundWidth The maximum width.
	 * @param boundHeight The maximum height
	 * @return A rectangle
	 */
	public Rectangle calculateCardRect(int boundWidth, int boundHeight) {
		
		// Calculate the size of the card
		int cardHeight = (int)(boundHeight - boundHeight*PADDING);
		int cardWidth = (int)Math.min(boundWidth - boundWidth*PADDING, cardHeight * ASPECT_RATIO);
		cardHeight = (int)Math.min(cardHeight, boundWidth / ASPECT_RATIO);
		
		return new Rectangle((boundWidth - cardWidth) / 2, (boundHeight - cardHeight) / 2, cardWidth, cardHeight);
	}

	/**
	 * @return the cardState
	 */
	public CardState getCardState() {
		return cardState;
	}

	/**
	 * @param cardState the card state to set
	 */
	public void setCardState(CardState cardState) {
		this.cardState = cardState;
	}
	
}
