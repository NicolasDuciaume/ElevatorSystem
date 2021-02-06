package elevator;
import java.util.*;

/**
 * Send and receives button presses, lamps and sensors information form floors and elevators. 
 * Schedules the route of the elevator
 * 
 * @author Tooba Sheikh 101028915
 * 
 */
public class Schedular {
	
	//store the floors below and above current floor
	private Queue<Object> upQueue;
	private Queue<Object> downQueue;
	
	//To differentiate between data received from floor vs elevator
	private String dataFloor = "";
	private String dataElevator = "";
	
	//boolean states for the floor and elevator
	private boolean emptyFloor = true;
	private boolean emptyElevator = true;
	
	public Schedular(){
		upQueue = new PriorityQueue<>();
		downQueue = new PriorityQueue<>();
	}
	
	/**
	 * Receives data from the floor
	 * 
	 * @param data info receive from the floor
	 */
	public synchronized void recieveFromFloor(String data) {
		while (!dataFloor.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		
		//Changes the state of the floor and reset the data value
		this.dataFloor = data;
		emptyFloor = false;
		notifyAll();
	}
	
	/**
	 * Receives data from the Elevator
	 * 
	 * @param data info receive from the elevator
	 */
	public synchronized void recieveFromElevator(String data) {
		while (!dataElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		
		//Changes the state of the elevator and reset the data value
		this.dataElevator = data;
		emptyElevator = false;
		notifyAll();
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
	public synchronized void checkPriority() {
		
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