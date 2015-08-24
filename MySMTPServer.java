import java.net.*;
import java.io.*;

public class MySMTPServer{

	// Server port
	static final int SERVER_PORT = 6013;
	
	public static void main(String[] args){
		try{
			// Server socket
			ServerSocket listenSocket = new ServerSocket(SERVER_PORT);

			// FileManager manages logging and emails
			FileManager fm = new FileManager();

			// Thread to run the FileManager in
			(new Thread(fm)).start();

			// Loop forever to receive all connections forever
			while(true){

				// Accept connection and pass off to new thread
				(new Thread(new Connection(listenSocket.accept(), fm))).start();

			}
		}catch(IOException ioe){
			System.err.println(ioe);
		}
	}
}