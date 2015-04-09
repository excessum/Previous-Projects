package client;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import remote.objects.GraphicalObject;
import client.gui.SharedWBClient;

public class DrawingActionListener implements MouseMotionListener, MouseListener{
	private SharedWBClient gui;
	private String command = null;
	private Point startLocation;
	private boolean aborted = false;
	Point polyStop = null;
	Point polyStart = null;
	Path2D path;
	ArrayList<Shape> polyLines = new ArrayList<Shape>();
	public DrawingActionListener(SharedWBClient gui){
		this.gui = gui;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(!aborted){
			if(command.equalsIgnoreCase("pencil")){
				Point stop = arg0.getPoint();
				path.moveTo(startLocation.x, startLocation.y);
				path.lineTo(stop.x, stop.y);
				startLocation = stop;
				gui.getDrawingArea().setShapeBeingDrawn(new GraphicalObject(gui.getStrokeSize(), gui.getLineColor(), Color.BLACK, false,  path));
			}else if(command.equalsIgnoreCase("poly")){
				if(polyStop != null){
					Point stop = arg0.getPoint();
					Path2D tempPath = (Path2D) path.clone();
					tempPath.moveTo(startLocation.x,  startLocation.y);
					tempPath.lineTo(stop.x, stop.y);
					gui.getDrawingArea().setShapeBeingDrawn(new GraphicalObject(gui.getStrokeSize(), gui.getLineColor(), gui.getFillColor(), gui.getIsFilled(), tempPath));
				}else{
					gui.getDrawingArea().setShapeBeingDrawn(new GraphicalObject("line", startLocation, arg0.getPoint(), gui.getStrokeSize(), gui.getLineColor(), gui.getFillColor(), gui.getIsFilled()));
				}
			}else{
				gui.getDrawingArea().setShapeBeingDrawn(new GraphicalObject(command, startLocation, arg0.getPoint(), gui.getStrokeSize(), gui.getLineColor(), gui.getFillColor(), gui.getIsFilled()));
			}
		}
		gui.getDrawingArea().repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (SwingUtilities.isRightMouseButton(arg0)) {
		    aborted = true;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		command = gui.getSelectedCommand();
		if(!command.equalsIgnoreCase("poly") || polyStop == null){
			startLocation = arg0.getPoint();
			polyStart = arg0.getPoint();
			path = new Path2D.Double();
		}else{
			startLocation = new Point(polyStop.x, polyStop.y);
		}
		
		aborted = false;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//TODO: add graphical object to the server
		if(!aborted){
			try {
				if(command.equals("pencil")){
					gui.getShapeList().newShape(new GraphicalObject(gui.getStrokeSize(), gui.getLineColor(), Color.BLACK, false, path));
				}else if(command.equals("poly")){
					polyStop = arg0.getPoint();
					if((Math.abs(polyStop.x - polyStart.x) < 20) && (Math.abs(polyStop.y - polyStart.y) < 20)){
						path.moveTo(startLocation.x, startLocation.y);
						path.lineTo(polyStart.x, polyStart.y);
						gui.getShapeList().newShape(new GraphicalObject(gui.getStrokeSize(), gui.getLineColor(), gui.getFillColor(), gui.getIsFilled(), path));
						gui.getDrawingArea().finishedDrawing();
						polyStart = null;
						polyStop = null;
					}else{
						polyStop = arg0.getPoint();
						path.moveTo(startLocation.x, startLocation.y);
						path.lineTo(polyStop.x, polyStop.y);
					}
				}else{
					gui.getShapeList().newShape(new GraphicalObject(command, startLocation, arg0.getPoint(), gui.getStrokeSize(), gui.getLineColor(), gui.getFillColor(), gui.getIsFilled()));
				}
				if(!command.equalsIgnoreCase("poly")){
					gui.getDrawingArea().finishedDrawing();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}else{
			aborted = false;
		}
		gui.getDrawingArea().repaint();
	}

}
