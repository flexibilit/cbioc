package edu.asu.cse.cbioc.intex.server;

import java.io.*;
import java.net.*;

public class JavaProgram
{
	public static Socket kb_socket;
	public static BufferedWriter wr;
    
    public static String serverIP;
    public static int serverPort;

    public static String localIP;
    public static int localPort;

	public static void main(String argv[]) 
	{
        String facts =  new String();
		
        if(argv.length == 1){
        	serverPort = Integer.parseInt(argv[0]);
        }else{
        	serverPort = 5000;
        }
        
		serverIP = "149.169.227.215" ;

		ListenerThread listener = new ListenerThread(serverPort);
		
		listener.start(); 
		
		while(true)
		{
			try
			{
				Thread.sleep(5000);
			}
			catch(Exception e){}
		}
	}
}