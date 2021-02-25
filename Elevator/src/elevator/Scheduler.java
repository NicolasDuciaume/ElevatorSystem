package elevator;
import java.util.*;

/**
 * Send and receives button presses, lamps and sensors information form floors and elevators. 
 * Schedules the route of the elevator
 * 
 * @author Tooba Sheikh 101028915
 * 
 */
public class Scheduler {
	
	//store the floors below and above current floor
	private ArrayList<Integer> upQueue;
	private ArrayList<Integer> downQueue;
	private int current = 1;
	public String direction = "Up";

	private String[] processed;
	
	//To differentiate between data received from floor vs elevator
	private String dataFloor = "";
	private String dataElevator = "";
	
	//boolean states for the floor and elevator
	private boolean emptyFloor = true;
	private boolean emptyElevator = true;
	
	public Scheduler(){
		upQueue = new ArrayList<>();
		downQueue = new ArrayList<>();
	}
	
	/**
	 * Receives data from the floor
	 * 
	 * @param data info receive from the floor
	 */
	public synchronized void receiveFromFloor(String data) {
		while (!dataFloor.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		
		//Changes the state of the floor and reset the data value
		String[] cutData = data.split(" ");
		if(cutData.length == 4){
			checkPriority(cutData[1]);
			checkPriority(cutData[3]);
			this.dataFloor = checkSend();
		}
		else{
			this.dataFloor = checkSend();
		}
		emptyFloor = false;
		notifyAll();
	}


	
	/**
	 * Receives data from the Elevator
	 * 
	 * @param data info receive from the elevator
	 */
	public synchronized void receiveFromElevator(String data) {
		while (!dataElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		String[] temp = data.split(" ");
		//Changes the state of the elevator and reset the data value
		if(!(temp[0].equals("arrived") || data.equals("waiting"))){
			checkPriority(data);
			this.dataFloor = checkSend();
			notifyAll();
		}
		else{
			if(temp[0].equals("arrived")){
				current = Integer.parseInt(temp[1]);
			}
			this.dataElevator = data;
			emptyElevator = false;
			notifyAll();
		}
	}
	
	/**
	 * Sends data to the floor
	 * 
	 * @return info to be sent to the floor
	 */
	public synchronized String sendToFloor() {
		while (dataElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		
		//Changes the state of the floor and reset the data value
		String temp = dataElevator;
		dataElevator = "";
		emptyFloor = true;
		notifyAll();
		
		return temp;
	}
	
	/**
	 * Sends data to the Elevator
	 * 
	 * @return info to be sent to the elevator
	 */
	public synchronized String sendToElevator() {
		while (dataFloor.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		
		//Changes the state of the elevator and reset the data value
		String temp = dataFloor;
		dataFloor = "";
		emptyElevator = true;
		notifyAll();
		return temp;
	}
	
	/**
	 * Checks priority of each floor requested, which to go to first
	 */
	public synchronized void checkPriority(String x) {
		int temp = Integer.parseInt(x);
		if(current < temp){
			upQueue.add(temp);
			Collections.sort(upQueue);
		}
		else if(current > temp){
			downQueue.add(temp);
			Collections.sort(downQueue);
			Collections.reverse(downQueue);
		}


	}

	public synchronized String checkSend(){
		String temp = "";
		if(upQueue.isEmpty() && !downQueue.isEmpty()){
			direction = "Down";
		}
		else if(!upQueue.isEmpty() && downQueue.isEmpty()){
			direction = "Up";
		}

		if(upQueue.isEmpty() && downQueue.isEmpty()){
			temp = "waiting";
		}
		else if(direction == "Up"){
			temp = upQueue.get(0).toString();
			upQueue.remove(0);
		}
		else if(direction.equals("Down")){
			temp = downQueue.get(0).toString();
			downQueue.remove(0);
		}
		return temp;
	}
	
	/**
	 * Getters for JUnit testing
	 */

	public String getDataFloor() {
		return this.dataFloor;
	}
	
	public String getDataElevator() {
		return this.dataElevator;
	}
	
	public boolean isEmptyFloor() {
		return this.emptyFloor;
	}
	
	public boolean isEmptyElevator() {
		return this.emptyElevator;
	}
	
}