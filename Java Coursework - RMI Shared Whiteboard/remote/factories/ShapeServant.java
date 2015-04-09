package remote.factories;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import remote.interfaces.Shape;
import remote.objects.GraphicalObject;



public class ShapeServant extends UnicastRemoteObject implements Shape {
	private static final long serialVersionUID = -7045737355709946440L;
	
	int myVersion;
    GraphicalObject theG;
    boolean isPublic;
     
    public ShapeServant(GraphicalObject g, int version)throws RemoteException{
    	theG = g;
 		myVersion = version;
    }
    
    public ShapeServant(GraphicalObject g, int version, boolean isPublic) throws RemoteException{
    	this.theG = g;
 		this.myVersion = version;
 		this.isPublic = isPublic;
    }
    
    
	public int getVersion() throws RemoteException {
	    return myVersion;
	}
	
   public GraphicalObject  getAllState() throws RemoteException{
        return theG;
   }
    
}