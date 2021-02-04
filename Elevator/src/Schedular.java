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
		while (!emptyFloor) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		this.data = data;
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
		while (!emptyElevator) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		this.data = data;
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
		while (emptyFloor) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		String temp = data;
		data = "";
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
		while (emptyElevator) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		String temp = data;
		data = "";
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
