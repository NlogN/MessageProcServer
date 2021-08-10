package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

//class of the Server that exchanges messages with the Client
public class DataProcServer {
	protected ServerState serverState; // current Server state
	protected long procTimeLimit; // object processing time limit

	private String propFileName; // configuration file name

	private volatile InputDataObj inputObjBufferValue; // buffer for input object
	private volatile OutputDataObj outputObjBufferValue; // buffer for output object

	protected ObjectInputStream in;
	protected ObjectOutputStream out;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private String stopCommand; // message that stops the server

	public DataProcServer(String propFileName, String stopCommand) {
		this.propFileName = propFileName;		
		this.procTimeLimit = getProcTime() * 1000;
		this.serverState = ServerState.WAITING;
		this.stopCommand = stopCommand;
	}
	
	public DataProcServer(int procTimeLimit, String stopCommand) {			
		this.procTimeLimit = procTimeLimit * 1000;
		this.serverState = ServerState.WAITING;
		this.stopCommand = stopCommand;
	}

	protected InputDataObj getInputObj() {
		return inputObjBufferValue;
	}

	protected OutputDataObj getOutputObj() {
		return outputObjBufferValue;
	}

	protected void setInputObj(InputDataObj inputDataObj) {
		inputObjBufferValue = inputDataObj;
	}

	protected void setOutputObj(OutputDataObj outputDataObj) {
		outputObjBufferValue = outputDataObj;
	}

	// clear buffers for input and output objects
	protected void clearBuffers() {
		this.inputObjBufferValue = null;
		this.outputObjBufferValue = null;
	}

	public void start(int port) throws IOException, ClassNotFoundException {
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();

		out = new ObjectOutputStream(clientSocket.getOutputStream());
		in = new ObjectInputStream(clientSocket.getInputStream());

		DataProcThread dataProcessingThread = new DataProcThread();
		dataProcessingThread.start();

		InputDataObj inputObj;
		while ((inputObj = (InputDataObj) in.readObject()) != null) {
			if (stopCommand.equals(inputObj.getMessage())) {
				dataProcessingThread.stop();
				synchronized (serverState) {
					serverState.notify();
				}
				
				// command to stop the exchange of objects
				out.writeObject(new OutputDataObj("the data exchange was finished"));
				out.flush();			
				
				break;
			} else {
				if (serverState.equals(ServerState.WAITING)) {
					this.setInputObj(inputObj);

					synchronized (serverState) {
						serverState.notify();
					}
				} else {
					out.writeObject(new OutputDataObj("The server is currently loaded and can't process the object with id = " + inputObj.getId()));
					out.flush();
					System.out.println("server rejected the processing of the object with id = " + inputObj.getId());
				}
			}
		}
		
	}
	
	public class DataProcThread implements Runnable {
	    private Thread thread;
	    private final AtomicBoolean isRunning = new AtomicBoolean(false);	   
	 
	    public void start() {
	        thread = new Thread(this);
	        thread.start();
	    }
	 
	    public void stop(){	    
	        isRunning.set(false);
	    }

		public void run() {
			isRunning.set(true);
			while (isRunning.get()) {
				if (getInputObj() != null) {
					for (int i = 0; i < serverState.stagesCount; i++) {
						try {
							serverState.handleData(DataProcServer.this);
						} catch (InterruptedException | IOException e) {
							System.out.println("an error occurred while processing input object with id = "
									+ getInputObj().getId() + " on stage " + serverState.serverStatus());
						}
					}
				}

				clearBuffers();
				serverState = ServerState.WAITING;
				if (isRunning.get()) {
					try {
						synchronized (serverState) {
							serverState.wait();
						}
					} catch (InterruptedException e) {
						System.err.println("data processing thread was interrupted");
					}
				}

			}
		}
	}

	public void stop() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();		
	}

	// processes the input object and returns the object for the server response
	protected OutputDataObj buildOutputDataObj(InputDataObj inputDataObj) {
		OutputDataObj newOutputDataObj = new OutputDataObj(inputDataObj.getMessage().toUpperCase(),
				inputDataObj.getId());
		return newOutputDataObj;
	}

	// returns the parameter of the message processing time (in seconds) from the
	// configuration file
	private int getProcTime() {
		String linePrefix = "proc.time=";
		int secNum = 0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(propFileName)));
			String line;
			line = reader.readLine();
			if (line != null) {
				if(line.startsWith(linePrefix)) {
					secNum = Integer.parseInt(line.substring(linePrefix.length()));
				}				
			}
		} catch (IOException e) {
			System.err.println("an error occurred while reading data from the configuration file");
		}
		
		return secNum;
	}

	public static void main(String[] args) {
		String propFileName = "prop";
		String stopCommand = "stop";
		DataProcServer server = new DataProcServer(propFileName, stopCommand);
		int portNumder = 2000;
		try {
			server.start(portNumder);			
			server.stop();			
		} catch (ClassNotFoundException | IOException e) {
			System.err.println(e);
		}
		
	}

}
