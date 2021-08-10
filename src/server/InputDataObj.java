package server;

import java.io.Serializable;

// input object for processing on the server
public class InputDataObj implements Serializable{
	private long id;
	private String message;
	
	public InputDataObj(String message, long id){
		this.id = id;
		this.message = message;			
	}
	
	public long getId() {
		return id;
	}

	public String getMessage() {
		return message;		
	}

}
