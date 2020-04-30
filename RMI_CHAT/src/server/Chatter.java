package server;

import client.ChatClientInterface;


/**
 * @author Luca Benzi
 * RMI Progetto Distributed Computing Maggio 2019
 *
 */
public class Chatter {

	public String name;
	public ChatClientInterface client;
	
	public Chatter(String name, ChatClientInterface client){
		this.name = name;
		this.client = client;
	}

	
	public String getName(){
		return name;
	}
	public ChatClientInterface getClient(){
		return client;
	}
	
	
}
