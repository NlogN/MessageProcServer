package test;

import java.io.IOException;

import server.InputDataObj;
import server.OutputDataObj;

public class ClientServerTest {

	public static void serverResponseTest() {
		int portNumder = 2000;
		Client client = new Client();

		try {
			client.startConnection("127.0.0.1", portNumder);

			String requestMessage1 = "qqwefgh";
			System.out.println("the message sent by the Client: " + requestMessage1);
			OutputDataObj response1 = client.sendMessage(new InputDataObj(requestMessage1, 1));			
			System.out.println("Server response: " + response1.getData());

			Thread.sleep(100);
			String requestMessage2 = "bnmnm";
			System.out.println("the message sent by the Client: " + requestMessage2);
			OutputDataObj response2 = client.sendMessage(new InputDataObj(requestMessage2, 2));			
			System.out.println("Server response: " + response2.getData());

			Thread.sleep(3000);
			String requestMessage3 = "ghjtyjtj";
			System.out.println("the message sent by the Client: " + requestMessage3);
			OutputDataObj response3 = client.sendMessage(new InputDataObj(requestMessage3, 3));			
			System.out.println("Server response: " + response3.getData());

			Thread.sleep(1500);
			String requestMessage4 = "sfhfgh";
			System.out.println("the message sent by the Client: " + requestMessage4);
			OutputDataObj response4 = client.sendMessage(new InputDataObj(requestMessage4, 4));			
			System.out.println("Server response: " + response4.getData());

			Thread.sleep(1000);
			// sending a command to stop the exchange of objects
			String requestMessage5 = "stop";
			
			System.out.println("the message sent by the Client: " + requestMessage5);
			OutputDataObj response5 = client.sendMessage(new InputDataObj(requestMessage5, 5));			
			System.out.println("Server response: " + response5.getData());

			client.stopConnection();

		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			System.err.println(e);
		}
	}

	public static void main(String[] args) {
		serverResponseTest();
	}

}