package client.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import remote.interfaces.Shape;
import remote.interfaces.WhiteBoard;
import remote.objects.GraphicalObject;

public class DrawingPanel extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7908110557570998348L;
	WhiteBoard whiteBoard;
	private GraphicalObject beingDrawn = null;
	
	public DrawingPanel(WhiteBoard whiteBoard){
		final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(0, 2000));
        /*
        JScrollPane scroll = new JScrollPane(panel);
        scroll.getVerticalScrollBar().setUnitIncrement(15);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        setVisible(true);
        */
        setBackground(Color.WHITE);
        this.whiteBoard = whiteBoard;
		this.repaint();
		//whiteBoard.newShape(g)
	}
	
	public void paintComponent(Graphics g){
		Graphics2D graphics = (Graphics2D)g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		Vector<Shape> shapes = null;
		try{
			shapes = whiteBoard.allShapes();
		}catch (Exception RemoteException){
			RemoteException.printStackTrace();
			System.exit(1);
		}
		for(Shape s: shapes){
			GraphicalObject currentObject = null;
			try {
				currentObject = s.getAllState();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			if(currentObject.getIsFilled()){
				graphics.setPaint(currentObject.getFillColour());
				graphics.fill(currentObject.getShape());
			}
			graphics.setColor(currentObject.getLineColour());
			graphics.setStroke(new BasicStroke(currentObject.getStrokeSize()));
			graphics.draw(currentObject.getShape());
			
		}
		
		if(this.beingDrawn != null){
			if(beingDrawn.getIsFilled()){
				graphics.setPaint(beingDrawn.getFillColour());
				graphics.fill(beingDrawn.getShape());
			}
			graphics.setColor(beingDrawn.getLineColour());
			graphics.setStroke(new BasicStroke(beingDrawn.getStrokeSize()));
			graphics.draw(beingDrawn.getShape());
		}
		g.dispose();
	}
	
	public void setShapeBeingDrawn(GraphicalObject s){
		this.beingDrawn = s;
	}
	
	public void finishedDrawing(){
		this.beingDrawn = null;
	}
	
	public void saveImage(String filePath){
		Container c = this;
		BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
		c.paint(im.getGraphics());
		try {
			ImageIO.write(im, "PNG", new File(filePath+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
