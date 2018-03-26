import java.net.*;
import java.io.*;

public class HTTPEcho
{
  public static void main(String[] args) throws Exception
  {
    String clientSentence;
    String clientSentenceEcho;
    int port = Integer.parseInt(args[0]);
    ServerSocket welcomeSocket = new ServerSocket(port);

    while(true)
    {
      Socket connectionSocket = welcomeSocket.accept();
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
      DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
      StringBuilder builder = new StringBuilder();
      boolean statusLine = true;

      while(((clientSentence = inFromClient.readLine()) != null) && (clientSentence.length() != 0))
      {
        if(statusLine)
        {
          builder.append("HTTP/1.1 200 OK\r\n\r\n");
          statusLine = false;
        }
        builder.append(clientSentence + "\r\n");
      }

      clientSentenceEcho = builder.toString();
      outToClient.writeBytes(clientSentenceEcho);

      inFromClient.close();
      outToClient.close();
      connectionSocket.close();
    }
  }
}
