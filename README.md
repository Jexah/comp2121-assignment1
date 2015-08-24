# Basic SMTP interface - COMP2121 Assignment 1
This project is developed in Java and you must have JDK (Java development kit) installed to compile and run.


## Compilation
Clone the repo and simply run `javac MySMTPServer.java Connection.java Filemanager.java` in the source directory.

## Run
To run the interface, simply run `java MySMTPServer`.

## Test
To test the server, use any TCP client (such as `telnet` on Windows or `tcpclient` on Linux).

Valid commands are listed:
| Command                              | Function                                                                              |
| ------------------------------------ |:------------------------------------------------------------------------------------- |
| 'HELO'                               | Returns what the server thinks is your host, and what the server thinks is it's host. |
| `MAIL FROM:<localdomain@domain.tld>` | Specifies the email address to send the email from.                                   |
| `RCPT TO:  <localdomain@domain.tld>` | Specifies the email address to send the email to.                                     |
| `DATA`                               | Begin the email content.                                                              |
| `QUIT`                               | Disconnect from the server.                                                           |

DATA properties are listed below:
| Property                             | Function                                                                              |
| ------------------------------------ |:------------------------------------------------------------------------------------- |
| 'MIME-Version: <version#>            | Specify the MIME Version of the email.                                                |
| `Date:<date>`                        | Specify the date that the email was written on.                                       |
| `To:<name>`                          | Specify the cosmetic recipient.                                                       |
| `From:<name>`                        | Specify the cosmetic sender.                                                          |
| `Subject:<subject>`                  | Specify the subject.                                                                  |
| `Content-Type:<content_type>`        | Specify the content type.                                                             |