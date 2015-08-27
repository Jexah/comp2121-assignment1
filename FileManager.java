import java.net.*;
import java.io.*;
import java.util.LinkedList;

public class FileManager implements Runnable{

	// Define local variables
	private Integer mailCounter;
	private PrintWriter logFile;
	private LinkedList<String> logQueue;

	public FileManager(){
		// Create emails directory if it doesn't exist
		(new File("emails")).mkdir();
		
		(new File("log.txt")).delete();

		logQueue = new LinkedList<String>();
		mailCounter = 0;
	}

	// Log with no user specified
	public void log(String str){
		log(str, null);
	}

	// Format string and add to queue.
	public synchronized void log(String str, Socket clientSocket){
		// Get current date.
		String currentDate = new java.util.Date().toString();

		if(clientSocket == null){
			// The log request does not have a user reference, so we use the local system
			logQueue.add(currentDate + "|SYSTEM|" + str);
		}else{
			// The log request contains a user reference, so we use it.
			logQueue.add(currentDate + "|" + clientSocket.getInetAddress().getHostName() + "|" + str);
		}

		writeLog();
	}

	// Append formatted string to log file and print to console.
	public synchronized void writeLog(){

		try{
			// Create log file if it doesn't exist, then open for appending.
			
			logFile = new PrintWriter(new BufferedWriter(new FileWriter("log.txt", true)));

			for(int i = logQueue.size(); --i == 0;){
				logFile.println(logQueue.removeFirst());
			}

			// Attempt to close the file
			closeFile(logFile);
		}catch(Exception e){
			e.printStackTrace();
			closeFile(logFile);
		}

	}

	// Save email to disk.
	public synchronized void email(String dataFrom, String dataTo, String dataSubject, String dataDate, String dataBody){
		// Define email writer
		PrintWriter newEmail = null;

		try{
			// Parse arguments and write to new emailj.txt file
			newEmail = new PrintWriter(new BufferedWriter(new FileWriter("emails/email" + mailCounter + ".txt")));
			newEmail.println("Message " + mailCounter);
			newEmail.println("From: " + dataFrom);
			newEmail.println("To: " + dataTo);
			newEmail.println("Date: " + dataDate);
			newEmail.println("Subject: " + dataSubject);
			newEmail.println("Body: " + dataBody);
			closeFile(newEmail);
			mailCounter++;
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