package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient
{

  public static String askServer(String hostname, int port, String ToServer) throws  IOException
  {

    String serverOutput = "Test";

    String fromServer;

    String firstLine;

    int i = 0;

    try
    {

    Socket clientSocket = new Socket(hostname, port);

    clientSocket.setSoTimeout(500);

    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    outToServer.writeBytes(ToServer + '\n');

    StringBuilder builder = new StringBuilder();

    firstLine = inFromServer.readLine();

    serverOutput = firstLine;

    builder.append(firstLine + '\n');

    while((fromServer = inFromServer.readLine()) != null)
    {
      builder.append(fromServer + '\n');

      i++;

      if(i > 100)
      {
        i = 0;
        break;
      }
    }

    serverOutput = builder.toString();

    outToServer.close();

    clientSocket.close();

    return serverOutput;
    }
    catch(SocketTimeoutException ex)
    {
      return serverOutput;
    }
    catch(ConnectException ex)
    {
      return "Package dropped";
    }
  }

  public static String askServer(String hostname, int port) throws  IOException
  {
    String serverOutput;

    serverOutput = askServer(hostname, port, null);

    return serverOutput;
  }
}
