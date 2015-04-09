package remote.interfaces;
import java.rmi.*;

import remote.objects.GraphicalObject;

public interface Shape extends Remote {
   int getVersion() throws RemoteException;
   GraphicalObject getAllState() throws RemoteException;
}