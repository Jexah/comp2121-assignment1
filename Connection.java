import java.net.*;
import java.io.*;

class Connection implements Runnable{

	// Socket, reader, and writer
	private BufferedReader in;
	private BufferedWriter out;
	private Socket clientSocket;

	// FileManager reference populated by FileManager generated in Main method.
	private FileManager fm;

	// Timeout timer to disconnect after given time without activity
	private java.util.Timer timeout; 
	private static final int TIMEOUT_MINUTES = 1;

	// If this is true the thread will keep running, else it will clean up and exit.
	public Boolean keepRunning;

	public Connection(Socket aClientSocket, FileManager theFm){
		// Set up local variables and begin timeout counter.
		try{
			clientSocket = aClientSocket;
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			fm = theFm;
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		keepRunning = true;
		timeout = null;
		refreshTimer();
	}

	private void refreshTimer(){
		// If the timer has been created previously, cancel the schedule
		if(timeout != null){
			timeout.cancel();
		}

		// Bomb the reference and create a new timer
		timeout = new java.util.Timer();

		// Schedule the timeout
		timeout.schedule(new java.util.TimerTask(){
			public void run(){
				keepRunning = false;
			}
		}, 60000 * TIMEOUT_MINUTES);

	}

	private void writeToClient(String str){
		try{
			out.write(str);
			out.flush();
		}catch(IOException e){
			fm.log(e.getMessage(), clientSocket);
		}
	}

	public void run(){

		// Declare relevent variables
		String sender = null;
		String recipient = null;
		String realDate = null;
		String contentType = null;

		String mime = null;
		String date = null;
		String from = null;
		String to = null;
		String subject = null;
		String body = "";

		String currentDate = new java.util.Date().toString();

		// Log successfully connected
		fm.log("Connected", clientSocket);

		// Tell the client that server is ready to receive input.
		writeToClient("220 " + clientSocket.getLocalAddress().getHostName() + " v1.0 ready at " + (new java.util.Date().toString()) + "\r\n");

		try{
			while(keepRunning){
				// If the BufferedReader has nothing to read, go to next iteration.
				if(!in.ready()){
					continue;
				}

				// Server has received input from client, refresh timeout.
				refreshTimer();

				// Read line and break up to read command
				String request = in.readLine();
				String[] args = request.split(" ");

				switch(args[0].toLowerCase()){
					case "helo":
						// HELO
						writeToClient("250 G'day, I'm " + clientSocket.getLocalAddress().getHostName() + ", I thought you were " + clientSocket.getInetAddress().getHostName() + "\r\n");
						break;
					case "mail":
						if(args[1].substring(0, 5).toLowerCase().equals("from:")){
							// MAIL FROM:
							// Sender has not been defined
							if(sender == null){
								// Get sender parts
								sender = request.substring(request.indexOf(":") + 1).trim();
								String[] senderSplit = sender.split("@");

								// Check local part is valid (as well as some general checks)
								String local = senderSplit[0];
								if(local.length() == 0 || sender.contains(" ") || sender.contains("[") || sender.contains("]") || sender.contains("\\")){
									writeToClient("550 From mailbox not available\r\n");
									sender = null;
									break;
								}

								// Check valid domain
								String domain = senderSplit[1];
								if(domain.length() < 11 || domain.length() == 12 || !domain.substring(domain.length() - 11).equals("usyd.edu.au")){
									writeToClient("550 From mailbox not available\r\n");
									sender = null;
									break;
								}
								if(domain.length() > 12 && !domain.substring(domain.length() - 12, 1).equals(".")){
									writeToClient("550 From mailbox not available\r\n");
									sender = null;
									break;
								}

								// Code got this far, therefore sender is valid
								writeToClient("250 <" + sender + "> sender received OK\r\n");
								break;
							}
							// Sender has already been specified
							writeToClient("503 Sender already specified\r\n");
						}else{
							// Only "mail" was found, not "mail from:"
							sorry();
						}
						break;
					case "rcpt":
						if(args[1].substring(0, 3).toLowerCase().equals("to:")){
							// REPT TO:
							recipient = request.substring(request.indexOf(":") + 1).trim();
							writeToClient("250 <" + recipient + "> user accepted\r\n");
						}else{
							// Only "rcpt" was found, not "rcpt to:"
							sorry();
						}
						break;
					case "data":
						// DATA

						if(sender == null){
							// User must specify who the mail is from before sending.
							writeToClient("503 I need a Mail command first\r\n");
						}else if(recipient == null){
							// User must specify who the mail is to before sending.
							writeToClient("503 I need a Rcpt command first\r\n");
						}
						writeToClient("354 Enter the mail - end with a '.' on a line\r\n");
						String data;

						// Loop through the inputs as they are received, break if the input is ".\n"
						while(!(data = in.readLine()).equals(".")){
							// Input received, reset timeout
							refreshTimer();

							switch(data.split(":")[0]){
								case "MIME-Version":
									if(mime == null && data.substring(0, 12).equals("MIME-Version")){
										mime = data.substring(data.indexOf(":") + 1).trim();
										break;
									}
								case "Date":
									if(date == null && data.substring(0, 4).equals("Date")){
										date = data.substring(data.indexOf(":") + 1).trim();
										break;
									}
								case "From":
									if(from == null && data.substring(0, 4).equals("From")){
										from = data.substring(data.indexOf(":") + 1).trim();
										break;
									}
								case "To":
									if(to == null && data.substring(0, 2).equals("To")){
										to = data.substring(data.indexOf(":") + 1).trim();
										break;
									}
								case "Subject":
									if(subject == null && data.substring(0, 7).equals("Subject")){
										subject = data.substring(data.indexOf(":") + 1).trim();
										break;
									}
								case "Content-Type":
									if(contentType == null && data.substring(0, 12).equals("Content-Type")){
										contentType = data.substring(data.indexOf(":") + 1).trim();
										break;
									}
								default:
									body += data + "\r\n";
									break;
							}
						}
						// Send acknowledgement
						writeToClient("250 I got that one thanks\r\n");

						// All is good, write the email
						if(sender != null && recipient != null){
							fm.email(
								from 	!= null ? from 		: "", 
								to 		!= null ? to 		: "", 
								subject != null ? subject 	: "", 
								date 	!= null ? date 		: "", 
								body 	!= null ? body 		: ""
							);
						}
						break;
					case "quit":
						// QUIT
						writeToClient("221 " + clientSocket.getLocalAddress().getHostName() + "; I'm closing the connection, bye bye " + clientSocket.getInetAddress().getHostName() + "\r\n");
						return;
					default:
						// Not a known command					
						sorry();
						break;
				}
			}
		}catch(IOException e){
			fm.log(e.getMessage());
		}finally{
			if(clientSocket != null){
				try{
					clientSocket.close();
					fm.log("Disconnected", clientSocket);
				}catch(IOException e){
					fm.log(e.getMessage(), clientSocket);
				}
			}
		}
	}

	// Tell the user the command is not valid
	private void sorry(){
		writeToClient("500 Sorry, I don't recognise that command\r\n");
	}
}