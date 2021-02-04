package Elevator.src;

import java.util.*;

/**
 * Send and receives button presses, lamps and sensors information form floors and elevators. 
 * Schedules the route of the elevator
 * 
 *	@author Tooba Sheikh
 * 
 */
public class Schedular {
	
	private Queue<Object> upQueue;
	private Queue<Object> downQueue;
	private String data;
	private boolean emptyFloor = true;
	private boolean emptyElevator = true;
	
	public Schedular(){
		upQueue = new PriorityQueue<>();
		downQueue = new PriorityQueue<>();
	}
	
	/**
	 * Receives data from the floor
	 *  @param
	 * @param
	 * @param
	 * @param data
	 */
	public synchronized void recieveFromFloor(String data) {
		
	}
	
	/**
	 * Receives data from the Elevator
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public synchronized void recieveFromElevator() {
		
	}
	
	/**
	 * Sends data to the floor
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public synchronized String sendToFloor() {
		return "";
	}
	
	/**
	 * Sends data to the Elevator
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public synchronized String sendToElevator() {
		return "";
	}
	
	/**
	 * Checks priority of each floor requested, which to go to first
	 *
	 */
	public synchronized void checkPriority() {
		
	}
	
}
