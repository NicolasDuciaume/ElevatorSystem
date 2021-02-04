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
	private String dataFloor = "";
	private String dataElevator = "";
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
		while (!dataFloor.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		this.dataFloor = data;
		emptyFloor = false;
		notifyAll();
	}
	
	/**
	 * Receives data from the Elevator
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public synchronized void recieveFromElevator(String data) {
		while (!dataElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		this.dataElevator = data;
		emptyElevator = false;
		notifyAll();
	}
	
	/**
	 * Sends data to the floor
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public synchronized String sendToFloor() {
		while (dataElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		String temp = dataElevator;
		dataElevator = "";
		emptyFloor = true;
		notifyAll();
		return temp;
	}
	
	/**
	 * Sends data to the Elevator
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public synchronized String sendToElevator() {
		while (dataFloor.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		String temp = dataFloor;
		dataFloor = "";
		emptyElevator = true;
		notifyAll();
		return temp;
	}
	
	/**
	 * Checks priority of each floor requested, which to go to first
	 *
	 */
	public synchronized void checkPriority() {
		
	}
	
}
