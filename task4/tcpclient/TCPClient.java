package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient
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
      //clientSocket.setKeepAlive(true);
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
