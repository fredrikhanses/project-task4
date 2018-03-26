import java.net.*;
import java.io.*;

public class ConcHTTPAsk
{
  public static void main(String[] args) throws IOException
  {
    int port;

    try
    {
      port = Integer.parseInt(args[0]);
    }
    catch(Exception ex)
    {
      System.err.println("Usage: HTTPAsk port");
      return;
    }

    ServerSocket welcomeSocket = new ServerSocket(port);

    while(true)
    {
      Socket connectionSocket = welcomeSocket.accept();
      connectionSocket.setSoTimeout(3000);

      MyRunnable r = new MyRunnable(connectionSocket);
      new Thread(r).start();
    }
  }
}

class TCPClient
{
  public static String askServer(String hostname, int port, String ToServer) throws IOException
  {
    String serverOutput = "TEST";
    String fromServer;
    String firstLine;
    int i = 0;

    try
    {
      Socket clientSocket = new Socket(hostname, port);
      clientSocket.setSoTimeout(3000);

      DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      if(ToServer != null)
      {
        outToServer.writeBytes(ToServer + '\n');
      }
      else if(hostname.equals("time.nist.gov"))
      {
        outToServer.writeBytes("\n");
      }

      StringBuilder builder = new StringBuilder();

      firstLine = inFromServer.readLine();
      System.out.println("Hostname & port is:\n" + hostname + " & " + port);
      serverOutput = firstLine;
      System.out.println("First line:\n" + firstLine);

      builder.append(firstLine + '\n');

      System.out.println("Counting number of lines:");

      while(((fromServer = inFromServer.readLine()) != null) && (fromServer.length() != 0))
      {
        builder.append(fromServer + '\n');

        i++;

        System.out.print(i + ", ");

        if(i > 50)
        {
          System.out.println();
          break;
        }
      }

      System.out.println();

      serverOutput = builder.toString();

      outToServer.close();
      inFromServer.close();
      clientSocket.close();

      return serverOutput;
    }
    catch(SocketTimeoutException ex)
    {
      System.err.println(ex);
      return serverOutput;
    }
    catch(ConnectException ex)
    {
      System.err.println(ex);
      return "Package discarded";
    }
    catch(UnknownHostException ex)
    {
      System.err.println(ex);
      return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
    catch(SocketException ex)
    {
      System.err.println(ex);
      return serverOutput;
    }
  }

  public static String askServer(String hostname, int port) throws IOException
  {
    String serverOutput;

    serverOutput = askServer(hostname, port, null);

    return serverOutput;
  }
}

class MyRunnable implements Runnable
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
