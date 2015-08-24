# COMP2121 Assignment 1
## Basic SMTP Interface
This project is developed in Java and you must have JDK (Java development kit) installed to compile and run.


## Compilation
Clone the repo and simply run `javac MySMTPServer.java Connection.java Filemanager.java` in the source directory.

## Run
To run the interface, simply run `java MySMTPServer`.

## Test
To test the server, use any TCP client (such as `telnet` on Windows or `tcpclient` on Linux).

Valid commands are listed:

<table>
    <tr>
        <td>Command</td>
        <td>Function</td>
    </tr>
    <tr>
        <td><pre>HELO</pre></td>
        <td>Returns what the server thinks is your host, and what the server thinks is it's host.</td>
    </tr>
    <tr>
        <td><pre>MAIL FROM:<localdomain@domain.tld></pre></td>
        <td>Specifies the email address to send the email from.</td>
    </tr>
    <tr>
        <td><pre>RCPT TO:  <localdomain@domain.tld></pre></td>
        <td>Specifies the email address to send the email to.</td>
    </tr>
    <tr>
        <td><pre>DATA</pre></td>
        <td>Begin the email content.</td>
    </tr>
    <tr>
        <td><pre>QUIT</pre></td>
        <td>Disconnect from the server.</td>
    </tr>
</table>

DATA properties are listed below:

<table>
    <tr>
        <td>Property</td>
        <td>Function</td>
    </tr>
    <tr>
        <td><pre>MIME-Version: [version#]</pre></td>
        <td>Specify the MIME Version of the email.</td>
    </tr>
    <tr>
        <td><pre>Date:[date]</pre></td>
        <td>Specify the date that the email was written on.</td>
    </tr>
    <tr>
        <td><pre>To:[name]</pre></td>
        <td>Specify the cosmetic recipient.</td>
    </tr>
    <tr>
        <td><pre>From:[name]</pre></td>
        <td>Specify the cosmetic sender.</td>
    </tr>
    <tr>
        <td><pre>Subject:[subject]</pre></td>
        <td>Specify the subject.</td>
    </tr>
    <tr>
        <td><pre>Content-Type:[content_type]</pre></td>
        <td>Specify the content type.</td>
    </tr>
</table>