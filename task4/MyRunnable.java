import tcpclient.TCPClient;
import java.net.*;
import java.io.*;

public class MyRunnable implements Runnable
{
  private Socket connectionSocket;

  public MyRunnable(Socket connectionSocket)
  {
    this.connectionSocket = connectionSocket;
  }

  public void run()
  {
    try
    {
      int port = 8888;
      String clientSentence;
      String[] subString;
      String splitString;
      String hostname = "localhost";
      String serverInput = null;
      String serverOutput = null;
      String serverResponse;
      String statusLine = null;

      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
      DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
      StringBuilder builder = new StringBuilder();
      boolean statusLineCheck = true;

      while(((clientSentence = inFromClient.readLine()) != null) && (clientSentence.length() != 0))
      {
        if(statusLineCheck)
        {
          System.out.println("Client sentence is:\n" + clientSentence);
          splitString = clientSentence;
          subString = splitString.split(" |=|&");

          if(subString[1].matches("(?i).*/favicon.ico.*"))
          {
            statusLine = ("HTTP/1.1 404 Not Found\r\n\r\n");
          }
          else if((subString[0].equals("GET")) || (subString[1].matches("(?i).*/ask?.*")))
          {
            statusLine = ("HTTP/1.1 200 OK\r\n\r\n");
          }
          else
          {
            statusLine = ("HTTP/1.1 400 Bad Request\r\n\r\n");
          }

          int length = subString.length;
          System.out.println("Length of client sentence array is:\n" + length);

          if(length > 2)
          {
            hostname = subString[2];
            System.out.println("Hostname is:\n" + hostname);
          }

          if(length > 4)
          {
            port = Integer.parseInt(subString[4]);
            System.out.println("Port is:\n" + port);
          }

          if(length > 6)
          {
            serverInput = subString[6];
            System.out.println("Server input is:\n" + serverInput);
          }
        }

        statusLineCheck = false;
      }

      try
      {
        serverOutput = TCPClient.askServer(hostname, port, serverInput);
        if((serverOutput.equals("HTTP/1.1 404 Not Found\r\n\r\n")) || (serverOutput.equals("HTTP/1.1 400 Bad Request\r\n\r\n")))
        {
          statusLine = serverOutput;
        }
      }
      catch(IOException ex)
      {
        System.err.println(ex);
      }

      builder.append(statusLine);
      builder.append(serverOutput + "\r\n");
      serverResponse = builder.toString();
      System.out.println(serverResponse);
      outToClient.writeBytes(serverResponse);

      inFromClient.close();
      outToClient.close();
      connectionSocket.close();
    }
    catch(IOException ex)
    {
      System.err.println(ex);
    }
  }
}
