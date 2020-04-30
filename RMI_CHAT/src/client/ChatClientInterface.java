package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Luca Benzi
 * RMI Progetto Distributed Computing Maggio 2019
 *
 */
public interface ChatClientInterface extends Remote{

	public void messageFromServer(String message) throws RemoteException;

	public void updateUserList(String[] currentUsers) throws RemoteException;
	
}
