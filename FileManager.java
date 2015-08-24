import java.net.*;
import java.io.*;
import java.util.LinkedList;

public class FileManager implements Runnable{

	// Define local variables
	private Integer mailCounter;

	public FileManager(){
		// Create emails directory if it doesn't exist
		(new File("emails")).mkdir();

		mailCounter = 0;
	}

	// Log with no user specified
	public void log(String str){
		log(str, null);
	}

	// Append formatted string to log file and print to console.
	public synchronized void log(String str, Socket clientSocket){
		// Get current date.
		String currentDate = new java.util.Date().toString();
		PrintWriter logFile = null;
		try{

			// Create log file if it doesn't exist, then open for appending.
			logFile = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));

			if(clientSocket == null){
				// The log request does not have a user reference, so we use the local system
				logFile.println(currentDate + "|SYSTEM|" + str);
				System.out.println(currentDate + "|SYSTEM|" + str);
			}else{
				// The log request contains a user reference, so we use it.
				logFile.println(currentDate + "|" + clientSocket.getInetAddress().getHostName() + "|" + str);
				System.out.println(currentDate + "|" + clientSocket.getInetAddress().getHostName() + "|" + str);
			}

			// Attempt to close the file
			closeFile(logFile);
		}catch(IOException e){
			System.out.println("Failed to open log file for appending.");
			closeFile(logFile);
		}catch(Exception e){
			System.out.println("Failed to save log data.");
			closeFile(logFile);
		}
	}

	// Save email to disk.
	public synchronized void email(String sender, String recipient, String subject, String body){
		// Get the date of email saved.
		String currentDate = new java.util.Date().toString();

		// Define email writer
		PrintWriter newEmail = null;

		try{
			// Parse arguments and write to new emailj.txt file
			newEmail = new PrintWriter(new BufferedWriter(new FileWriter("emails/email" + mailCounter + ".txt")));
			newEmail.println("Message " + mailCounter);
			newEmail.println("From: " + sender);
			newEmail.println("To: " + recipient);
			newEmail.println("Date: " + currentDate);
			newEmail.println("Subject: " + subject);
			newEmail.println("Body: " + body);
			closeFile(newEmail);
		}catch(IOException e){
			log("Failed to open email file for writing.");
			closeFile(newEmail);
		}catch(Exception e){
			log("Could not write to email file.");
			closeFile(newEmail);
		}
	}

	// Close a PrintWriter stream
	private void closeFile(PrintWriter w){

		// If the PrintWriter is null, no need to close the stream
		if(w == null){
			return;
		}

		try{
			// PrintWriter is not null, attempt to close stream, otherwise throw error.
			w.close();
		}catch(Exception e){
			log("Failed to close file stream");
		}
	}

	// Server started, and FileManager created successfully.
	public void run(){
		log("Server started successfully");
	}
}