package elevator;

import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * Schedular handles communication between Elevator and Floors using a state machine
 * 
 * @author Tooba Sheikh      101028915
 * @author Jameel Alidina    101077040
 * @author Nicolas Duciaume  101124713
 * @author Chris D'Silva     101067295
 */
public class Scheduler {

	private ArrayList<Integer> upQueue;
	private ArrayList<Integer> downQueue;
	private int currentFloor;
	public Direction direction;
	private String[] processed;
	private String isDataFromFloor;
	private String dataFromElevator;
	private int floorToVisit;
	private boolean emptyFloor;
	private boolean emptyElevator;
	private SchedulerStates currentState1, currentState2;
	private DatagramPacket receivePacket, sendPacket;
	private DatagramSocket sendReceiveSocketFloor, sendReceiveSocketElevators;
	private int portFloor;
	private InetAddress addressFloor;
	private ArrayList<ElevatorData> elevators;

	/**
	 *Enum for the states
	 *
	 */
	public enum SchedulerStates {
		INITIAL_STATE, STATE_1, STATE_2;
	}

	/**
	 * Initializes all the variables
	 */
	public Scheduler() {
		upQueue = new ArrayList<Integer>();
		downQueue = new ArrayList<Integer>();
		currentFloor = 1;
		isDataFromFloor = "";
		dataFromElevator = "";
		floorToVisit = -1;
		emptyFloor = true;
		emptyElevator = true;

		currentState1 = SchedulerStates.STATE_1;
		currentState2 = SchedulerStates.STATE_2;

		elevators = new ArrayList<>();

		try {
			sendReceiveSocketFloor = new DatagramSocket(69);
			sendReceiveSocketElevators = new DatagramSocket(420);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

	}

	private void sendAndReceive() {
		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		while (true) {
			this.receiveStateMachine(data);
			this.sendStateMachine();
		}
	}

	/**
	 * A send state machine
	 * 
	 * @return
	 */
	public void sendStateMachine() {

		switch (currentState1) {
		case STATE_1:
			sendToElevator();
			currentState1 = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			sendToFloor();
			currentState1 = SchedulerStates.STATE_1;
			break;
		}
		return ;
	}

	/**
	 * A receive state machine 
	 *
	 */
	public void receiveStateMachine(byte[] data) {
		switch (currentState2) {
		case STATE_1:
			receiveFromElevator(data);
			currentState2 = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			receiveFromFloor(data);
			currentState2 = SchedulerStates.STATE_1;
			break;
		}
	}

	/**
	 * Receives data from the floor 
	 * 
	 * @param data An ready to go message or nothing
	 */
	public synchronized void receiveFromFloor(byte[] data) {
		try {
			sendReceiveSocketFloor.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
		String[] cut = name.split(" ");
		if(!cut[0].equals("go")){
			checkPriority(Integer.parseInt(cut[1]));
			checkPriority(Integer.parseInt(cut[3]));
			System.out.println(name);
		}
	}

	/**
	 * receives data from the elevator
	 * 
	 * @param data the info received from the elevator, either an arrival message or a button press 
	 */
	public synchronized void receiveFromElevator(byte[] data) {
		ElevatorData temp = elevators.get(0);
		try {
			sendReceiveSocketElevators.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength());

		//Splits the message then compares to see what needs to be done
		String[] splitElevatorMsg = name.split(" ");
		if (!splitElevatorMsg[0].equals("arrived")) {
			this.checkPriority(Integer.parseInt(splitElevatorMsg[0]));
		} else { // if button pressed
			if (splitElevatorMsg[0].equals("arrived")) {
				temp.setCurrentFloor(Integer.parseInt(splitElevatorMsg[1]));
			}
		}

	}

	/**
	 * Sends data to the floor
	 * 
	 * @return the instructions to the floor, right now just an arriving message
	 * TODO: Turn on lamps on floor  
	 */
	public synchronized void sendToFloor() {
		ElevatorData temp = elevators.get(0);
		String dat = "arrived " + temp.getCurrentFloor();
		byte[] toSend = dat.getBytes();
		this.sendPacket = new DatagramPacket(toSend, toSend.length, addressFloor, portFloor);
		try {
			this.sendReceiveSocketFloor.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @return sends the floor requested to the elevator
	 */
	public synchronized void sendToElevator() {
		ElevatorData temp = elevators.get(0);
		byte[] toSend = new byte[100];
		int t = checkSend();
		if(t == -1){
			String wait = "waiting";
			toSend = wait.getBytes();
		}
		else{
			String dat = t + " " + temp.getDirection();
			toSend = dat.getBytes();
		}

		this.sendPacket = new DatagramPacket(toSend, toSend.length, temp.getAddress(), temp.getPort());

		try {
			this.sendReceiveSocketElevators.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Adds the floor to the right queue  
	 * 
	 * @param floor thats is the floor that need to be added to the queue
	 */
	public synchronized void checkPriority(int floor) {
		ElevatorData temp = elevators.get(0);

		if (temp.getCurrentFloor() < floor) {
			temp.addToUp(floor);
			temp.sortArrays();
		} else if (temp.getCurrentFloor() > floor) {
			temp.addToDown(floor);
			temp.sortArrays();
		}
	}

	/**
	 * Check send basically organizes which QUEUE is going to the elevator first 
	 * 
	 * @return
	 */
	private synchronized int checkSend() {
		ElevatorData temp = elevators.get(0);
		int toVisit = -1;
		if (temp.getUpQueue().isEmpty() && !temp.getDownQueue().isEmpty()) {
			temp.setDirection(Direction.DOWN);
		} else if (!temp.getUpQueue().isEmpty() && this.downQueue.isEmpty()) {
			temp.setDirection(Direction.UP);
		}

		if (!temp.getUpQueue().isEmpty() || !temp.getDownQueue().isEmpty()) {
			if (temp.getDirection() == Direction.UP) {
				toVisit = (Integer) temp.getUpQueue().get(0);
				temp.removeUp();
			} else if (temp.getDirection() == Direction.DOWN) {
				toVisit = (Integer) temp.getDownQueue().get(0);
				temp.removeDown();
			}
		}

		return toVisit;
	}

	/* For testing purposes */
	public String getDataFromElevator() {
		return this.dataFromElevator;
	}

	public boolean isEmptyFloor() {
		return this.emptyFloor;
	}

	public boolean isEmptyElevator() {
		return this.emptyElevator;
	}
	
	public SchedulerStates getCurrentState1() {
		return this.currentState1;
	}
	
	public SchedulerStates getCurrentState2() {
		return this.currentState2;
	}
	
	public Direction getDirection() {
		return this.direction;
	}
	
	public int getFloorToVisit() {
		return this.floorToVisit;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	public String getIsDataFromFloor() {
		return this.isDataFromFloor;
	}

	public void InitializePort(int numOfElevators){
		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		try {
			sendReceiveSocketFloor.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		portFloor = receivePacket.getPort();
		addressFloor = receivePacket.getAddress();

		for(int x = 1; x == numOfElevators; x++){
			data = new byte[100];
			receivePacket = new DatagramPacket(data, data.length);
			try {
				sendReceiveSocketElevators.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength()) + " 1" ;
			elevators.add(new ElevatorData(name, receivePacket.getPort(),receivePacket.getAddress(), 0));
		}

		System.out.println("Floor port is: " + portFloor + " and address is: " + addressFloor);
		for(int z = 0; z < elevators.size(); z++){
			ElevatorData temp = elevators.get(z);
			System.out.println(temp.getName() + " port is: " + temp.getPort() + " and address is: " + temp.getAddress());
		}
	}

	private static String toString(byte[] temp) {
		StringBuilder builder = new StringBuilder();
		for (byte b : temp) {
			builder.append(String.format("%02X ", b));
		}
		return builder.toString();
	}

	public static void main(String[] args){
		Scheduler scheduler = new Scheduler();
		scheduler.InitializePort(1);
		scheduler.sendAndReceive();
	}
}