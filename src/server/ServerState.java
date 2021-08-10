package server;

import java.io.IOException;

// the object of the class defines the Server state 
public enum ServerState {

	WAITING {
		@Override
		public ServerState nextState() {
			return PROCESSING;
		}
		
		@Override
		public String serverStatus() {
			return "Processing";
		}

		@Override
		public void handleData(DataProcServer dataProcServer) {
			dataProcServer.serverState = nextState();
		}
	},
	PROCESSING {
		@Override
		public ServerState nextState() {
			return SENDING;
		}
		
		@Override
		public String serverStatus() {
			return "Sending";
		}

		@Override
		public void handleData(DataProcServer dataProcServer) throws InterruptedException {
			long startTime = System.currentTimeMillis();
			InputDataObj inputDataObj = dataProcServer.getInputObj();
			if (inputDataObj != null) {
				dataProcServer.setOutputObj(dataProcServer.buildOutputDataObj(inputDataObj));
			}
			long currentTime = System.currentTimeMillis();
			long timeDiff = currentTime - startTime;
			if (timeDiff < dataProcServer.procTimeLimit) {
				Thread.sleep(dataProcServer.procTimeLimit - timeDiff);
			}
			dataProcServer.serverState = nextState();
		}
	},
	SENDING {
		@Override
		public ServerState nextState() {
			return WAITING;
		}
		
		@Override
		public String serverStatus() {
			return "Waiting";
		}

		@Override
		public void handleData(DataProcServer dataProcServer) throws InterruptedException, IOException {
			long startTime = System.currentTimeMillis();
			OutputDataObj outputDataObj = dataProcServer.getOutputObj();
			if (outputDataObj != null) {
				dataProcServer.out.writeObject(outputDataObj);
			}
			long currentTime = System.currentTimeMillis();
			long timeDiff = currentTime - startTime;
			if (timeDiff < dataProcServer.procTimeLimit) {
				Thread.sleep(dataProcServer.procTimeLimit - timeDiff);
			}
			dataProcServer.serverState = nextState();

		}
	};

	// put the class object in the next state
	protected abstract ServerState nextState();
		
	// name of current server status
	protected abstract String serverStatus();

	// process the data and put the class object into the next state
	protected abstract void handleData(DataProcServer dataProcServer) throws InterruptedException, IOException;
	
	// count of server states
	protected static int stagesCount = 3;

}
