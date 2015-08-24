# Basic SMTP interface - COMP2121 Assignment 1
This project is developed in Java and you must have JDK (Java development kit) installed to compile and run.


## Compilation
Clone the repo and simply run `javac MySMTPServer.java Connection.java Filemanager.java` in the source directory.

## Run
To run the interface, simply run `java MySMTPServer`.

## Test
To test the server, use any TCP client (such as `telnet` on Windows or `tcpclient` on Linux).

Valid commands are listed:
* `HELO                                ` Returns what the server thinks is your host, and what the server thinks is it's host.
* `MAIL FROM:<localdomain@domain.tld>` ` Specifies the email address to send the email from.
* `RCPT TO:  <localdomain@domain.tld>` ` Specifies the email address to send the email to.
* `DATA                                ` Begin the email content.
  * `MIME-Version:                     ` Specify the MIME Version of the email.
  * `Date:                             ` Specify the date that the email was written on.
  * `To:                               ` Specify the cosmetic recipient.
  * `From:                             ` Specify the cosmetic sender.
  * `Subject:                          ` Specify the subject.
  * `Content-Type:                     ` Specify the content type.
* `QUIT                                ` Disconnect from the server.