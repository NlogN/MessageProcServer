package test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import server.InputDataObj;
import server.OutputDataObj;

// class of the Client that exchanges messages with the Server
public class Client {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    // send an object to the server
    // returns the server response
    public OutputDataObj sendMessage(InputDataObj obj) throws IOException, ClassNotFoundException {   
        out.writeObject(obj);    	
        out.flush();
        
        OutputDataObj processedDataObj = (OutputDataObj)in.readObject();
      
        return processedDataObj;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}