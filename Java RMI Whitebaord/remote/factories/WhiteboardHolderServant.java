package remote.factories;
import java.rmi.RemoteException;
import java.util.HashMap;
import remote.interfaces.WhiteBoard;
import remote.interfaces.WhiteBoardHolder;

public class WhiteboardHolderServant implements WhiteBoardHolder{
	private HashMap<String, WhiteBoard> whiteboards;
    
    public WhiteboardHolderServant()throws RemoteException{
        whiteboards = new HashMap<String, WhiteBoard>();
    }

  	public WhiteBoard getWhiteboard(String key, boolean isPublic) throws RemoteException{
  		if(whiteboards.containsKey(key)){
  			return whiteboards.get(key) ;
  		}
  		WhiteBoard wb = new WhiteBoardServant(isPublic);
  	    whiteboards.put(key, wb);
  	    return wb;
     }


	@Override
	public HashMap<String, WhiteBoard> getHashTable() throws RemoteException {
		return this.whiteboards;
	}

    
}