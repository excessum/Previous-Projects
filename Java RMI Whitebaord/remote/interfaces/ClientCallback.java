package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface ClientCallback extends Remote {
	void callback(int version) throws RemoteException;
};