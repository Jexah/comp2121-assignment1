import java.net.*;
import java.io.*;

class Connection implements Runnable{
	BufferedReader in;
	BufferedWriter out;
	Socket clientSocket;
	FileManager fm;

	java.util.Timer timeout; 
	public Boolean keepRunning;

	public Connection(Socket aClientSocket, FileManager theFm){
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
		if(timeout != null)
		{
			timeout.cancel();
		}
		timeout = new java.util.Timer();
		timeout.schedule(new java.util.TimerTask(){
			public void run(){
				keepRunning = false;
			}
		}, 300000);
	}

	public void run(){
		try{
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
			fm.log("Connected", clientSocket);
			out.write("220 " + clientSocket.getLocalAddress().getHostName() + " v1.0 ready at " + (new java.util.Date().toString()) + "\r\n");
			out.flush();
			while(keepRunning){
				if(!in.ready()){
					continue;
				}
				refreshTimer();
				String request = in.readLine();
				String[] args = request.split(" ");
				switch(args[0].toLowerCase()){
					case "helo":
						out.write("250 G'day, I'm " + clientSocket.getLocalAddress().getHostName() + ", I thought you were " + clientSocket.getInetAddress().getHostName() + "\r\n");
						out.flush();
						break;
					case "mail":
						if(args[1].substring(0, 5).toLowerCase().equals("from:")){
							if(sender == null){
								sender = request.substring(request.indexOf(":") + 1).trim();
								String[] senderSplit = sender.split("@");
								String local = senderSplit[0];
								if(local.length() == 0 || sender.contains(" ") || sender.contains("[") || sender.contains("]") || sender.contains("\\")){
									out.write("550 From mailbox not available\r\n");
									out.flush();
									sender = null;
									break;
								}
								String domain = senderSplit[1];
								if(domain.length() < 11 || domain.length() == 12 || !domain.substring(domain.length() - 11).equals("usyd.edu.au")){
									out.write("550 From mailbox not available\r\n");
									out.flush();
									sender = null;
									break;
								}
								if(domain.length() > 12 && !domain.substring(domain.length() - 12, 1).equals(".")){
									out.write("550 From mailbox not available\r\n");
									out.flush();
									sender = null;
									break;
								}
								out.write("250 <" + sender + "> sender received OK\r\n");
								out.flush();
								break;
							}
							out.write("503 Sender already specified\r\n");
							out.flush();
						}else{
							sorry();
						}
						break;
					case "rcpt":
						if(args[1].substring(0, 3).toLowerCase().equals("to:")){
							recipient = request.substring(request.indexOf(":") + 1).trim();
							out.write("250 <" + recipient + "> user accepted\r\n");
							out.flush();
						}else{
							sorry();
						}
						break;
					case "data":
						if(sender == null){
							out.write("503 I need a Mail command first\r\n");
							out.flush();
						}else if(sender == null){
							out.write("503 I need a Rcpt command first\r\n");
							out.flush();
						}
						out.write("354 Enter the mail - end with a '.' on a line\r\n");
						out.flush();
						String data;
						while(!(data = in.readLine()).equals(".")){
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
						out.write("250 I got that one thanks\r\n");
						out.flush();
						if(sender != null && recipient != null){
							fm.email(sender, recipient, subject, body);
						}
						break;
					case "quit":
						out.write("221 " + clientSocket.getLocalAddress().getHostName() + "; I'm closing the connection, bye bye " + clientSocket.getInetAddress().getHostName() + "\r\n");
						out.flush();
						return;
					default:
						sorry();
						break;
				}
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			if(clientSocket != null){
				try{
					clientSocket.close();
					fm.log("Disconnected", clientSocket);
				}catch(IOException e){
					System.out.println("Failed to close socket.");
				}
			}
		}
	}


	private void sorry(){
		try{
			out.write("500 Sorry, I don't recognise that command\r\n");
			out.flush();
		}catch(Exception e){
			System.out.println("Failed to send failure to acknowledge command.");
		}
	}
}