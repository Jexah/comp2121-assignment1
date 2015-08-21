import java.net.*;
import java.io.*;

public class MySMTPServer{
	public static void main(String[] args){
		try{
			int serverPort = 6013;
			ServerSocket listenSocket = new ServerSocket(serverPort);
			FileManager fm = new FileManager();
			Thread fmt = new Thread(fm);
			fmt.start();
			while(true){
				Socket clientSocket = listenSocket.accept();
				Thread t = new Thread(new Connection(clientSocket, fm));
				t.start();
			}
		}catch(IOException ioe){
			System.err.println(ioe);
		}
	}
}