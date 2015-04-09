package remote.factories;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import remote.interfaces.ClientCallback;
import remote.interfaces.Shape;
import remote.interfaces.WhiteBoard;
import remote.objects.GraphicalObject;

public class WhiteBoardServant extends UnicastRemoteObject implements WhiteBoard{

    /**
	 * 
	 */
	private static final long serialVersionUID = 3095416073588016685L;
	private Vector<Shape> theList;
    private int version;
    private boolean isPublic;
    private Vector<ClientCallback> clients;

    
    public WhiteBoardServant(boolean isPublic)throws RemoteException{
        theList = new Vector<Shape>();
        clients = new Vector<ClientCallback>();
        version = 0;
        this.isPublic = isPublic;
    }

  	public Shape newShape(GraphicalObject g) throws RemoteException{
  	    version++;
       	Shape s = new ShapeServant( g, version);
        theList.addElement(s);  
        doCallbacks();
        return s;
     }

   	public  Vector<Shape> allShapes()throws RemoteException{
        return theList;
    }

    public int getVersion() throws RemoteException{
        return version;
    } 
    
    public void clearList() throws RemoteException{
    	theList.clear();
    }

	@Override
	public void register(ClientCallback callback) throws RemoteException {
		if(!clients.contains(callback)){
			clients.add(callback);
		}
		
	}

	@Override
	public void deregister(ClientCallback callBack) throws RemoteException {
		clients.remove(callBack);	
	}

	@Override
	public boolean isPublic() throws RemoteException {
		return this.isPublic;
	}

	@Override
	public void doCallbacks() throws RemoteException {
		for(ClientCallback client: clients){
			client.callback(version);
		}
	}
    
}