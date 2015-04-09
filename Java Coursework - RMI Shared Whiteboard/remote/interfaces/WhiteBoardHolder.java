package remote.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;


public interface WhiteBoardHolder extends Remote {
	public WhiteBoard getWhiteboard(String key, boolean isPublic) throws RemoteException;
	public HashMap<String, WhiteBoard> getHashTable() throws RemoteException;
}
