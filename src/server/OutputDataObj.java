package server;

import java.io.Serializable;

// output object that is created by the server as a result of processing the input object
public class OutputDataObj implements Serializable{	
	private long id;
	private String data;
	
	public OutputDataObj(String data, long id){
		this.id = id;
		this.data = data;		
	}
	
	public OutputDataObj(String data){
		this.id = 0;
		this.data = data;		
	}
	
	public String getData() {
		return data;
	}
	
	public long getId() {
		return id;
	}
	
	public void setData(String data) {
		this.data = data;	
	}
}
