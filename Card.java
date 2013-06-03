import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class Card extends JComponent {

	private final Color CARD_BORDER_COLOR = Color.green;
	private final Color CARD_FACE_COLOR = Color.white;
	private final Color CARD_BACK_COLOR = Color.cyan;
	private final Color SYMBOL_COLOR = Color.red;
	private final Color OUT_COLOR = Color.black;

	/*
	 * Creates borders for the card cell. One normal, and one to display when
	 * the mouse hovers over it.
	 */
	private final Border BORDER_DEFAULT = BorderFactory.createLineBorder(Color.black);
	private final Border BORDER_HOVER = BorderFactory.createLineBorder(Color.yellow);

	private final Font SYMBOL_FONT = new Font("Arial", Font.PLAIN, 1);
	private final double SYMBOL_FONT_SIZE_FACTOR = 0.6;

	// Playing cards are not square. This is a ratio of the length of sides `WIDTH / HEIGHT`
	private final double ASPECT_RATIO = (double) 5 / 7;

	// This is not a pixel count its a percentage of the total vertical height.
	private final double PADDING = 0.1;

	private final char symbol;

	private CardState cardState;
	private Rectangle2D cardRect;

	private List<ClickListener> listeners = new ArrayList<ClickListener>();

	/**
	 * Constructs a card object.
	 * 
	 * @param symbol
	 *            The symbol to display on the face of the card.
	 */
	public Card(final char symbol) {
		this.symbol = symbol;

		// Set the initial state of the card
		setCardState(CardState.FACE_DOWN);

		this.setBorder(BORDER_DEFAULT);

		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				/*
				 * If the mouse clicked while it was over the card, notify all
				 * of the ClickListeners
				 */
				if (cardRect.contains(e.getPoint())) {
					for (ClickListener listener : listeners) {
						listener.cardClicked((Card) e.getComponent());
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				/*
				 * When the mouse moves into the cards cell, change the border color.
				 */
				setBorder(BORDER_HOVER);
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				/*
				 * When the mouse moves out of the cards cell, change the border
				 * color back to normal.
				 */
				setBorder(BORDER_DEFAULT);
				repaint();
			}
		});
	}

	/**
	 * Paint the card and its surrounding border to the given graphics object.
	 * 
	 * NOTE: The given width and height correspond to the size of the grid cell,
	 * NOT the card size. The size of the card is calculated as the largest
	 * rectangle, where the ratio of it's sides is `ASPECT_RATIO`, that will fit
	 * in the grid cell.
	 * 
	 * @param g
	 *            The graphics object to paint to.
	 * @param width
	 *            The width of the containing cell.
	 * @param height
	 *            The height of the containing cell.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		final Graphics2D g2d = (Graphics2D) g.create();
		if (cardState == CardState.OUT_OF_PLAY) {
			g2d.setColor(OUT_COLOR);
			g2d.fill(cardRect);

			g2d.setColor(CARD_BORDER_COLOR);
			g2d.draw(cardRect);
			repaint();
		}
		try {
			final Dimension size = this.getSize();
			final String symbol = Character.toString(this.symbol);

			// Calculate the size of the card given the size of the cell.
			cardRect = calculateCardRect(size.getWidth(), size.getHeight());
			
			if (cardState == CardState.FACE_DOWN) {
				g2d.setColor(CARD_BACK_COLOR);
				g2d.fill(cardRect);

				g2d.setColor(CARD_BORDER_COLOR);
				g2d.draw(cardRect);
			} else {
				g2d.setColor(CARD_FACE_COLOR);
				g2d.fill(cardRect);

				g2d.setColor(CARD_BORDER_COLOR);
				g2d.draw(cardRect);

				// Draw the symbol in the center of the card
				g2d.setColor(SYMBOL_COLOR);

				final Font font = SYMBOL_FONT.deriveFont(
						(float) (cardRect.getHeight() * SYMBOL_FONT_SIZE_FACTOR));
				g2d.setFont(font);

				final Rectangle2D symbolBounds = font.getStringBounds(symbol,
						g2d.getFontRenderContext());
				g2d.translate(
						cardRect.getCenterX() - symbolBounds.getCenterX(),
						cardRect.getCenterY() - symbolBounds.getCenterY());

				g2d.drawString(symbol, 0, 0);
			}
			
		} finally {
			g2d.dispose();
		}
		
	}

	/**
	 * Flips the card. If it's face-down, make it face-up. If its face-up, make
	 * it face-down.
	 */
	public void flip() {
		if (getCardState() == Card.CardState.FACE_DOWN) {
			setCardState(Card.CardState.FACE_UP);
		} else if (getCardState() == Card.CardState.FACE_UP) {
			setCardState(Card.CardState.FACE_DOWN);
		}
	}
	public void match(){
		setCardState(Card.CardState.OUT_OF_PLAY);
		
	}

	/**
	 * Calculates the maximum size of the card with the correct aspect-ratio
	 * within the given bounds.
	 * 
	 * @param boundWidth
	 *            The maximum width.
	 * @param boundHeight
	 *            The maximum height
	 * @return A rectangle
	 */
	public Rectangle2D calculateCardRect(double boundWidth, double boundHeight) {

		// Calculate the size of the card
		double cardHeight = boundHeight - boundHeight * PADDING;
		double cardWidth = Math.min(boundWidth - boundWidth * PADDING,
				cardHeight * ASPECT_RATIO);
		cardHeight = Math.min(cardHeight, boundWidth / ASPECT_RATIO);

		return new Rectangle2D.Double((boundWidth - cardWidth) / 2,
				(boundHeight - cardHeight) / 2, cardWidth, cardHeight);
	}

	/**
	 * @return the cardState
	 */
	public CardState getCardState() {
		return cardState;
	}

	/**
	 * @param cardState
	 *            the card state to set
	 */
	public void setCardState(CardState cardState) {
		this.cardState = cardState;
		repaint();
	}
	
	public char getSymbol() {
		return symbol;
	}

	/**
	 * Adds a ClickListener to this Card object.
	 * 
	 * @param listener
	 *            The ClickListener to add.
	 */
	public void addClickListener(ClickListener listener) {
		listeners.add(listener);
	}

	public interface ClickListener {
		public void cardClicked(Card card);
	}

	public enum CardState {
		FACE_DOWN, // The card is face down, only the patterned side is visible.
		FACE_UP, // The card is face up, the front side is visible.
		OUT_OF_PLAY, // The card is out of play, it has already been matched.
	}
}

