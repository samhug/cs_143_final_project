import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MemoryGame extends JApplet {
	
	private final int GRID_WIDTH = 4;
	private final int GRID_HEIGHT = 4;
	
	private final Color GRID_BORDER_COLOR = Color.black;
	private final Color GRID_BACKGROUND_COLOR = Color.gray;
	
    /**
     * Construct the window
     */
    public void init() {
        // Set the background color.
        getContentPane().setBackground(GRID_BACKGROUND_COLOR);
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
        
        
        
        // Loop through the rows
        for (int y=0; y<GRID_HEIGHT; y++) {
        	// Loop through the columns
        	for (int x=0; x<GRID_WIDTH; x++) {
        		
        		// Calculate the x and y position of the cell
        		final int xOffset = x*cellWidth;
        		final int yOffset = y*cellHeight;
        		
        		// Draw the cell border rectangle
        		g.setColor(GRID_BORDER_COLOR);
        		g.drawRect(xOffset, yOffset, cellWidth, cellHeight);
        	}
        }
    }
    
    
    /*
     * This dosn't do anything yet....
     */
    private class MyMouseListener extends MouseAdapter {
        public void mousePressed( MouseEvent e ) {
        	int x = e.getX();
        	int y = e.getY();
        }
    }
}