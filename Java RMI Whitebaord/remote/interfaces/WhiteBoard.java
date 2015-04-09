package remote.interfaces;
import java.rmi.*;
import java.util.Vector;

import remote.objects.GraphicalObject;

public interface WhiteBoard extends Remote {
  	Shape newShape(GraphicalObject g) throws RemoteException;  	    
    Vector<Shape> allShapes()throws RemoteException;
    int getVersion() throws RemoteException;
    void clearList() throws RemoteException;
    boolean isPublic() throws RemoteException;
    void register(ClientCallback callback) throws RemoteException;
    void deregister(ClientCallback callback) throws RemoteException;
    void doCallbacks() throws RemoteException;
}