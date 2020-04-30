package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import client.ChatClientInterface;

/**
 * @author Luca Benzi
 * RMI Progetto Distributed Computing Maggio 2019
 *
 */
public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
	String linea = "---------------------------------------------\n";
	private Vector<Chatter> chatters;
	private static final long serialVersionUID = 1L;
	
	public ChatServer() throws RemoteException {
		super();
		chatters = new Vector<Chatter>(10, 1);
	}
	

	public static void main(String[] args) {
		startRMIRegistry();	
		String hostName = "localhost";
		String serviceName = "GroupChatService";
		
		if(args.length == 2){
			hostName = args[0];
			serviceName = args[1];
		}
		
		try{
			ChatServerInterface hello = new ChatServer();
			Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
			System.out.println("Gruppo Chat RMI Server in funzione...");
		}
		catch(Exception e){
			System.out.println("Server ha dei problemi");
		}	
	}

	
	public static void startRMIRegistry() {
		try{
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI Server pronto");
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public String sayHello(String ClientName) throws RemoteException {
		System.out.println(ClientName + " invia un messaggio...");
		return "Ciao " + ClientName + " dalla chat Server";
	}
	
	public void updateChat(String name, String nextPost) throws RemoteException {
		String message =  name + " : " + nextPost + "\n";
		sendToAll(message);
	}
	

	@Override
	public void passIDentity(RemoteRef ref) throws RemoteException {	
		try{
			System.out.println(linea + ref.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}//end passIDentity

	@Override
	public void registerListener(String[] details) throws RemoteException {	
		System.out.println(new Date(System.currentTimeMillis()));
		System.out.println(details[0] + " si è unito alla chat");
		System.out.println(details[0] + "'s hostname : " + details[1]);
		System.out.println(details[0] + "'s RMI servizio : " + details[2]);
		registerChatter(details);
	}

	private void registerChatter(String[] details){		
		try{
			ChatClientInterface nextClient = ( ChatClientInterface )Naming.lookup("rmi://" + details[1] + "/" + details[2]);
			
			chatters.addElement(new Chatter(details[0], nextClient));
			
			nextClient.messageFromServer("[Server] : Ciao " + details[0] + " ora sei libero di utilizzare questa chat.\n");
			
			sendToAll("[Server] : " + details[0] + " è entrato nel gruppo.\n");
			
			updateUserList();		
		}
		catch(RemoteException | MalformedURLException | NotBoundException e){
			e.printStackTrace();
		}
	}
	

	private void updateUserList() {
		String[] currentUsers = getUserList();	
		for(Chatter c : chatters){
			try {
				c.getClient().updateUserList(currentUsers);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	

	private String[] getUserList(){
		String[] allUsers = new String[chatters.size()];
		for(int i = 0; i< allUsers.length; i++){
			allUsers[i] = chatters.elementAt(i).getName();
		}
		return allUsers;
	}
	
	public void sendToAll(String newMessage){	
		for(Chatter c : chatters){
			try {
				c.getClient().messageFromServer(newMessage);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}


	@Override
	public void leaveChat(String userName) throws RemoteException{
		
		for(Chatter c : chatters){
			if(c.getName().equals(userName)){
				System.out.println(linea + userName + " ha lasciato la chat");
				System.out.println(new Date(System.currentTimeMillis()));
				chatters.remove(c);
				break;
			}
		}		
		if(!chatters.isEmpty()){
			updateUserList();
		}			
	}
	

	@Override
	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException{
		Chatter pc;
		for(int i : privateGroup){
			pc= chatters.elementAt(i);
			pc.getClient().messageFromServer(privateMessage);
		}
	}
	
}



