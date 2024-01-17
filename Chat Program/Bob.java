import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Bob {

    //private static final int sPort = 8000;   //The server will be listening on this port number
    //Socket requestSocket;           //socket connect to the server
    int sPort = 8001;    //The server will be listening on this port number
    ServerSocket sSocket;   //serversocket used to lisen on port number 8000
    Socket connection = null; //socket for the connection with the client
    String message;    //message received from the client
    String MESSAGE;    //uppercase message send to the client
    ObjectOutputStream out;  //stream write to the socket
    ObjectInputStream in;    //stream read from the socket

    public static void main(String[] args) throws Exception {
        System.out.println("Bob is running.");
        Writer w = new Writer();
        w.start();
        //new Writer(listener.accept(),clientNum).start();
        Bob b = new Bob();
        b.run();
    }

    void run()
    {
        try{
            System.out.println("the server port number is: " + sPort);
            //create a serversocket
            sSocket = new ServerSocket(sPort);
            //Wait for connection
            //System.out.println("Waiting for connection");
            //accept a connection from the client
            connection = sSocket.accept();
            //System.out.println();
            //System.out.println("Connection received from " +
                    //connection.getInetAddress().getHostName());
            //initialize Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            try{
                while(true)
                {
                    //receive the message sent from the client
                    message = (String)in.readObject();
                    //show the message to the user
                    System.out.println("Alice: " + message);
                    //Capitalize all letters in the message
                    MESSAGE = message.toUpperCase();
                    //send MESSAGE back to the client
                    sendMessage(MESSAGE);
                }
            }
            catch(ClassNotFoundException classnot){
                System.err.println("Data received in unknown format");
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //Close connections
            try{
                in.close();
                out.close();
                sSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            //System.out.println("Send message: " + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Writer extends Thread {
        Socket requestSocket;           //socket connect to the server
        ObjectOutputStream out2;         //stream write to the socket
        ObjectInputStream in2;          //stream read from the socket
        String message;                //message send to the server
        String MESSAGE;                //capitalized message read from the server

        /*public Handler(Socket connection, int no) {
            this.connection = connection;
            this.no = no;
        }*/

        public void run() {
            try{
                Scanner input = new Scanner(System.in);
                System.out.print("Hello, please enter a target port number for Alice: ");
                int pNum = input.nextInt();
                //create a socket to connect to the server
                requestSocket = new Socket("localhost", pNum);
                System.out.println("Connected to Alice in port " + pNum);
                //initialize inputStream and outputStream
                out2 = new ObjectOutputStream(requestSocket.getOutputStream());
                out2.flush();
                in2 = new ObjectInputStream(requestSocket.getInputStream());
                //get Input from standard input
                BufferedReader bufferedReader = new BufferedReader(new
                        InputStreamReader(System.in));
                while(true)
                {
                    //System.out.print("Enter chat: ");
                    //read a sentence from the standard input
                    message = bufferedReader.readLine();
                    //Send the sentence to the server
                    sendMessage(message);
                    //Receive the upperCase sentence from the server
                    MESSAGE = (String)in2.readObject();
                    //show the message to the user
                    //System.out.println("Receive message: " + MESSAGE);
                }
            }
            catch (ConnectException e) {
                System.err.println("Connection refused. You need to initiate a server first.");
            }
            catch ( ClassNotFoundException e ) {
                System.err.println("Class not found");
            }
            catch(UnknownHostException unknownHost){
                System.err.println("You are trying to connect to an unknown host!");
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
            finally{
                //Close connections
                try{
                    in2.close();
                    out2.close();
                    requestSocket.close();
                }
                catch(IOException ioException){
                    ioException.printStackTrace();
                }
            }
        }

        //send a message to the output stream
        public void sendMessage(String msg)
        {
            try{
                out2.writeObject(msg);
                out2.flush();
                //System.out.println("Send message: " + msg);
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }

}
