package edu.asu.cse.cbioc.intex.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.asu.cse.cbioc.intex.IntEx;

class ListenerThread extends Thread
{

	private int port;
	public static boolean running = true;
	public String PMID = new String();
	public static String facts = new String();


	public ListenerThread(int port)
	{
		this.port = port;
	}

	public void run()
	{
		try 
		{

			ServerSocket srv = new ServerSocket(port);
			System.out.println("Waiting for connection...");
			
			while (running){

				// Wait for connection from client.
				Socket socket = srv.accept();
				
				
				System.out.println("Connection accepted...");
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				//String str;
				System.out.println("Waiting for incomming messages...");
				
				//while ((PMID = rd.readLine()) != null) 
				if((PMID = rd.readLine()) != null)
				{
					System.out.println(PMID);	
					facts= extract(PMID);
				}
				//System.out.println("Begin sending");
				//writer.write("asdfasdfasfd");
				writer.write(facts);
				writer.flush();
				System.out.println("End sending");
			}
		} 
		catch (IOException e) 
		{
			System.out.println(e);
		}
		
	}
	public static String extract( String PMID)
	{
		String facts = new String();
		
		System.out.println("Now in Extract");
		try {
			int pubMedId = Integer.parseInt(PMID);
			IntEx ie = IntEx.extractFromPubMed(pubMedId);
			facts = ie.getBioMedAbstract().getInteractions().toString();
				
			System.out.println(facts);
		} catch(NumberFormatException nfe){
			System.err.println("Invalid PubMedID!");
		}catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return facts;
	}
	
}
