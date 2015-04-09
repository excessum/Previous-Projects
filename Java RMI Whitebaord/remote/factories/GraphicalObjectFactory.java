package remote.factories;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class GraphicalObjectFactory {
	private static GraphicalObjectFactory instance = null;
	
	protected GraphicalObjectFactory() {}
	
	public static GraphicalObjectFactory getInstance() {
	      if(instance == null) {
	         instance = new GraphicalObjectFactory();
	      }
	      return instance;
	}
	
	public Shape getShape(String actionCommand, Point startLocation, Point endLocation){
		
		Rectangle2D rectangle = new Rectangle(startLocation);
		rectangle.add(endLocation);
		
		
		if(actionCommand.equalsIgnoreCase("rectangle")){
			return rectangle;
		}
		else if(actionCommand.equalsIgnoreCase("square")){
			rectangle.setFrame(new Point((int)rectangle.getX(),  (int)rectangle.getY()), forceSquare(rectangle));
			return rectangle;
		}
		else if(actionCommand.equalsIgnoreCase("ellipse")){
			Ellipse2D ellipse = new Ellipse2D.Double();
			ellipse.setFrame(rectangle);
			return ellipse;
			
		}
		else if(actionCommand.equalsIgnoreCase("circle")){
			rectangle.setFrame(new Point((int)rectangle.getX(),  (int)rectangle.getY()), forceSquare(rectangle));
			Ellipse2D ellipse = new Ellipse2D.Double();
			ellipse.setFrame(rectangle);
			return ellipse;
		}
		else if(actionCommand.equalsIgnoreCase("line")){
			return new Line2D.Double(startLocation.x, startLocation.y, endLocation.x, endLocation.y);
		}
		
		return null;
	}

	private Dimension2D forceSquare(Rectangle2D rect) {
		double width = rect.getWidth();
		double height = rect.getHeight();
		
		if(width > height){
			height = width;
		}else{
			width = height;
		}
		return new Dimension((int)width, (int)height);
	}
	
}
