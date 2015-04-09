package remote.objects;

import java.awt.Point;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.io.Serializable;

import remote.factories.GraphicalObjectFactory;

public class GraphicalObject implements Serializable {
	private static final long serialVersionUID = 4735232887060971196L;

	private String type;
	private Color lineColour;
	private Color fillColour;
	private boolean isFilled;
	private Point startLocation;
	private Point endLocation;
	private int strokeSize;
	private Shape drawObject;
	private static GraphicalObjectFactory shapeFactory = GraphicalObjectFactory
			.getInstance();

	/**
	 * Unused constructor
	 */
	public GraphicalObject() {
	}

	/**
	 * Create a new graphical object which is from Java2D shapes, currently
	 * supported objects are: -Ellipse -Circle -Rectangle -Square
	 * 
	 * @param type
	 *            The type of shape to be created
	 * @param startLocation
	 *            The location of initial click
	 * @param endLocation
	 *            The location mouse is released
	 * @param lineColour
	 * @param fillColour
	 * @param isFilled
	 *            Whether the object is filled or not
	 */
	public GraphicalObject(String type, Point startLocation, Point endLocation,
			int strokeSize, Color lineColour, Color fillColour, boolean isFilled) {
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.strokeSize = strokeSize;
		this.lineColour = lineColour;
		this.fillColour = fillColour;
		this.isFilled = isFilled;
		this.drawObject = shapeFactory.getShape(type, this.startLocation,
				this.endLocation);
	}

	/**
	 * Create a new graphical object which is formed from Paths, currently
	 * supported objects are: -Line
	 * 
	 * @param strokeSize
	 * @param lineColor
	 * @param drawPath
	 */
	public GraphicalObject(int strokeSize, Color lineColor, Color fillColor, boolean isFilled, Path2D drawPath) {
		this.strokeSize = strokeSize;
		this.lineColour = lineColor;
		this.drawObject = drawPath;
		this.fillColour = fillColor;
		this.isFilled = isFilled;
	}

	/**
	 * Method used to get the shape to be drawn by Graphics2D
	 * 
	 * @return
	 */
	public Shape getShape() {
		return drawObject;
	}

	/**
	 * Called whenever an attribute on the graphical object is changed to get a
	 * new drawable object reflecting the change.
	 */
	private void changeDrawObject() {
		this.drawObject = shapeFactory.getShape(type, this.startLocation,
				this.endLocation);
	}

	/**
	 * @return the lineColour
	 */
	public Color getLineColour() {
		return lineColour;
	}

	/**
	 * @param lineColour
	 *            the lineColour to set
	 */
	public void setLineColour(Color lineColour) {
		changeDrawObject();
		this.lineColour = lineColour;
	}

	/**
	 * @return the fillColour
	 */
	public Color getFillColour() {
		return fillColour;
	}

	/**
	 * @param fillColour
	 *            the fillColour to set
	 */
	public void setFillColour(Color fillColour) {
		this.fillColour = fillColour;
	}

	/**
	 * @return the stroke
	 */
	public int getStrokeSize() {
		return strokeSize;
	}

	/**
	 * @param stroke
	 *            the stroke to set
	 */
	public void setStrokeSize(int strokeSize) {
		this.strokeSize = strokeSize;
	}

	/**
	 * @return the isFilled
	 */
	public boolean getIsFilled() {
		return isFilled;
	}

	/**
	 * @param isFilled
	 *            the isFilled to set
	 */
	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

}