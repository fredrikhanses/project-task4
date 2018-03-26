public class HTTPResponse
{
  public static void main(String[] args) throws Exception
  {
    String clientSentence;
    //String clientSentenceEcho;
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

        while((clientSentence = inFromClient.readLine()) != null && clientSentence.length() != 0))
        {
          int x;
          byte data[] = new byte[1024];

        x = in.read(data);

        String t = "HTTP/1.1 200 OK\r\n";
        byte[] bb = t.getBytes("UTF-8");
        out.write(bb);

        t = "Content-Length: 124\r\n";
        bb = t.getBytes("UTF-8");
        out.write(bb);
        t = "Content-Type: text/html\r\n\r\n";
        bb = t.getBytes("UTF-8");
        out.write(bb);

        String response = "<html><head><title>HTML content via java socket</title></head><body><h2>Hi! Every Body.</h2></body></html>";
        out.write(response.getBytes("UTF-8"));

        t = "Connection: Closed";
        bb = t.getBytes("UTF-8");
        out.write(bb);

        out.flush();

        s.close();
        svr.close();
        System.out.println("closing all");
      }
    }
  }
