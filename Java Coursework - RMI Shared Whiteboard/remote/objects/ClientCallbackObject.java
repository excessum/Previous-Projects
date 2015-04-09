package remote.objects;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



import javax.swing.JComponent;

import remote.interfaces.ClientCallback;

public class ClientCallbackObject extends UnicastRemoteObject implements ClientCallback{
	private static final long serialVersionUID = 7324454884056282029L;
	private JComponent dp;
	public ClientCallbackObject(JComponent dp) throws RemoteException {
		super();
		this.dp = dp;
	}

	@Override
	public void callback(int version) throws RemoteException {
		dp.repaint();
	}
}
