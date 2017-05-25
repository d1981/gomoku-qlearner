import java.io.*;
import java.net.*;


public class RacketClient {
    protected Socket racketSocket;			              // socket for communicating w/ server
    protected PrintWriter gridOut;                       // takes care of output stream for sockets
    protected BufferedReader gridIn;			         // bufferedreader for input reading
    
   
    public RacketClient(String h, int p) {
	   registerWithGrid(h, p);
    }
   
    public void registerWithGrid(String h, int p) {
        try {
	         // connects to h machine on port p
            racketSocket = new Socket(h, p);

	         // create output stream to communicate with grid
            gridOut = new PrintWriter(racketSocket.getOutputStream(), true); 
	         
            //buffered reader reads from input stream from grid
            gridIn = new BufferedReader(new InputStreamReader(racketSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + h);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + h);
            System.exit(1);
        }
    }
    
    
}