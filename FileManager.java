import java.net.*;
import java.io.*;
import java.util.LinkedList;

public class FileManager implements Runnable{

	PrintWriter log;
	LinkedList<String> logWrite;
	Integer mailCounter;

	public FileManager(){
		logWrite = new LinkedList<String>();
		mailCounter = 0;
	}

	public void log(String str){
		log(str, null);
	}

	public synchronized void log(String str, Socket clientSocket){
		String currentDate = new java.util.Date().toString();
		if(clientSocket == null){
			logWrite.add(currentDate + "|SYSTEM|" + str);
		}else{
			logWrite.add(currentDate + "|" + clientSocket.getInetAddress().getHostName() + "|" + str);	
		}

		try{
			log = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));
			for(int i = logWrite.size(); i-- != 0;){
				String strkk = logWrite.removeFirst();
				log.println(strkk);
				System.out.println(strkk);
			}
			closeFile(log);
		}catch(IOException e){
			System.out.println("Failed to open log file for appending.");
			closeFile(log);
		}catch(Exception e){
			closeFile(log);
			System.out.println("Could not write to log file.");
		}
	}

	public synchronized void email(String sender, String recipient, String subject, String body){
		String currentDate = new java.util.Date().toString();
		PrintWriter newEmail = null;
		try{
			newEmail = new PrintWriter(new BufferedWriter(new FileWriter("emails/email" + mailCounter + ".txt")));
			newEmail.println("Message " + mailCounter);
			newEmail.println("From: " + sender);
			newEmail.println("To: " + recipient);
			newEmail.println("Date: " + currentDate);
			newEmail.println("Subject: " + subject);
			newEmail.println("Body: " + body);
			closeFile(newEmail);
		}catch(IOException e){
			System.out.println("Failed to open email file for writing.");
			closeFile(newEmail);
		}catch(Exception e){
			closeFile(newEmail);
			System.out.println("Could not write to email file.");
		}
	}

	private void closeFile(PrintWriter w){
		if(w == null){
			return;
		}

		try{
			w.close();
		}catch(Exception e){
			System.out.println("Failed to close file stream");
			log("Failed to close file stream");
		}
	}

	public void run(){
		log("Server started");
	}
}