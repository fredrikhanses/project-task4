import java.net.*;
import java.io.*;

public class HTTPEcho
{
  public static void main(String[] args) throws Exception
  {
    String clientSentence;
    String clientSentenceEcho;
    int port = Integer.parseInt(args[0]);
    //int i = 0;

    ServerSocket welcomeSocket = new ServerSocket(port);
    System.out.println("Started server on port " + port);

    while(true)
    {
      Socket connectionSocket = welcomeSocket.accept();
      System.out.println("Accepted connection from client");
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
      DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

      StringBuilder builder = new StringBuilder();

      boolean first = true;

      while(((clientSentence = inFromClient.readLine()) != null) && (clientSentence.length() != 0) && (clientSentence != "\r\n"))
      {
        if(first)
        {
          builder.append("HTTP/1.1 200 OK\r\n\r\n");
          first = false;
        }
        builder.append(clientSentence + "\r\n");
        //outToClient.writeBytes(clientSentence + '\n');
        /*
        i++;

        if(i > 100)
        {
          i = 0;
          break;
        }
        */
      }

      clientSentenceEcho = builder.toString();

      System.out.println("Writing echo to client");

      outToClient.writeBytes(clientSentenceEcho);

      System.out.println("Closing connection with client");

      inFromClient.close();

      outToClient.close();

      connectionSocket.close();
    }
  }
}
