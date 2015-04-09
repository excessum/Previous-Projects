package server;

import java.net.Inet4Address;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import remote.factories.WhiteboardHolderServant;
import remote.interfaces.WhiteBoardHolder;

public class SharedWBServer {

	public static void main(String args[]) {
		String registryURL;
		int portNumber;

		try {
			String portInput = JOptionPane
					.showInputDialog("Enter a port to bind to (default: 1099)");
			if (portInput.equalsIgnoreCase("")) {
				portNumber = 1099;
			} else {
				portNumber = Integer.parseInt(portInput);
			}

			try {
				Registry registry = LocateRegistry.getRegistry(portNumber);
				registry.list();
			} catch (RemoteException e) {
				@SuppressWarnings("unused")
				Registry registry = LocateRegistry.createRegistry(portNumber);
			}

			WhiteBoardHolder exportedObj = new WhiteboardHolderServant();
			WhiteBoardHolder stub = (WhiteBoardHolder) UnicastRemoteObject
					.exportObject(exportedObj, 0);
			registryURL = "rmi://"
					+ Inet4Address.getLocalHost().getHostAddress() + ":"
					+ portNumber + "/WhiteBoard";
			Naming.rebind(registryURL, stub);
			System.out.println("Whiteboard Server ready.");
		} catch (Exception re) {
			System.out.println("Exception in HelloServer.main: " + re);
		}
	}

}
