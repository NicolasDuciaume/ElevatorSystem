package elevator;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 
 * Scheduler handles communication between Elevator and Floors using a state
 * machine
 * 
 * @author Tooba Sheikh 101028915
 * @author Jameel Alidina 101077040
 * @author Nicolas Duciaume 101124713
 * @author Chris D'Silva 101067295
 */
public class Scheduler {

	public Direction direction;
	private String isDataFromFloor, dataFromElevator;
	private int floorToVisit, portFloor, currentFloor;
	private boolean emptyFloor, emptyElevator;
	private SchedulerStates currentState1, currentState2;
	private DatagramPacket receivePacket, sendPacket;
	private DatagramSocket sendReceiveSocketFloor, sendReceiveSocketElevators;
	private InetAddress addressFloor;
	private ArrayList<ElevatorData> elevators;
	private int waiting = 0;
	private int elevatorBeingUsed = 0;
	private int maxElevator = 0;
	private int count = 0;
	private String mess;

	/**
	 * Enum for the states
	 *
	 */
	public enum SchedulerStates {
		INITIAL_STATE, STATE_1, STATE_2;
	}

	/**
	 * Initializes all the variables
	 */
	public Scheduler() {
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

	private void elevatorChange(){
		if(elevatorBeingUsed == maxElevator){
			elevatorBeingUsed = 0;
		}
		else{
			elevatorBeingUsed++;
		}
	}

	private void sendAndReceive() {
		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		while (true) {
			if(count == 2){
				elevatorChange();
				count = 0;
			}
			this.receiveStateMachine(data);
			this.sendStateMachine();
			//elevatorChange();
		}
	}

	/**
	 * A send state machine
	 * 
	 * @return
	 */
	public void sendStateMachine() {
		//elevatorChange();
		switch (currentState1) {
		case STATE_1:
			sendToElevator();
			sendToElevator();
			currentState1 = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			sendToFloor();
			currentState1 = SchedulerStates.STATE_1;
			count++;
			break;
		}
		return;
	}

	/**
	 * A receive state machine
	 *
	 */
	public void receiveStateMachine(byte[] data) {
		switch (currentState2) {
		case STATE_1:
			receiveFromElevator(data);
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
		if (!cut[0].equals("go")) {
			// TODO: Need to figure out the direction to go to the floor where we pick up people
			checkPriority(Integer.parseInt(cut[1]), cut[2], Integer.parseInt(cut[3]));
			//checkPriority(Integer.parseInt(cut[3]), cut[2]);
			System.out.println(name);
		}
	}

	/**
	 * Receives data from the elevator
	 * 
	 * @param data the info received from the elevator, either an arrival message or
	 *             a button press
	 */
	public synchronized void receiveFromElevator(byte[] data) {
		ElevatorData temp = elevators.get(elevatorBeingUsed);
		try {
			sendReceiveSocketElevators.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength());

		// Splits the message then compares to see what needs to be done
		String[] splitElevatorMsg = name.split("-");
		if (!splitElevatorMsg[1].equals("arrived")) { // if button pressed
			if(!splitElevatorMsg[1].equals("waiting")){
			this.checkPriority(-1, null,Integer.parseInt(splitElevatorMsg[0]));}
			else {
				waiting++;
				if(mess.equals("")){
					mess = mess + splitElevatorMsg[0] + "-waiting" ;
				}
				else {
					mess = mess + " " + splitElevatorMsg[0] + "-waiting" ;
					waiting--;
				}
			}
		} else { // if arrived to floor
			temp.setCurrentFloor(Integer.parseInt(splitElevatorMsg[2]));
			if(mess.equals("")){
				mess = mess + name;
			}
			else {
				mess = mess + " " + name;
				waiting--;
			}
		}
		elevatorChange();

	}

	/**
	 * Sends data to the floor
	 * 
	 * @return the instructions to the floor, right now just an arriving message
	 *         TODO: Turn on lamps on floor
	 */
	public synchronized void sendToFloor() {
		byte[] toSend = new byte[100];
		if(waiting == elevators.size()){
			String dataString = "waiting";
			toSend = dataString.getBytes();
			mess = "";
		}
		else {
		String dataString = mess;
		toSend = dataString.getBytes();
		mess = "";
		}
		this.sendPacket = new DatagramPacket(toSend, toSend.length, addressFloor, portFloor);
		try {
			this.sendReceiveSocketFloor.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//elevatorChange();
	}

	/**
	 * @return sends the floor requested to the elevator
	 */
	public synchronized void sendToElevator() {
		ElevatorData temp = elevators.get(elevatorBeingUsed);
		byte[] toSend = new byte[100];
		// send the appropriate floor request based on the elevator
		int t = checkSend(temp);
		if (t == -1) {
			String wait = "waiting";
			toSend = wait.getBytes();
		} else {
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
		elevatorChange();
	}
	
	/**
	 * 
	 * @param differenceMap
	 * @param floor
	 * @param down
	 */
	private void getElevatorFromDifference(HashMap<String,Integer> differenceMap,int floor,boolean down,int floor2, String dir) {
		int minDifference = Collections.min(differenceMap.values());
		for(String currElevatorName : differenceMap.keySet()) {
			if(differenceMap.get(currElevatorName).equals(minDifference)) {
				for(ElevatorData e : elevators) {
					if(e.getName().equals(currElevatorName)) {
						if(down) {
							e.addToDown(floor);
							if(dir.equals("UP")){
								e.addToUp(floor2);
							}
							else{
								e.addToDown(floor2);
							}
							break;
						}else {
							e.addToUp(floor);
							if(dir.equals("UP")){
								e.addToUp(floor2);
							}
							else{
								e.addToDown(floor2);
							}
							break;
						}
					}
				}
			}
		}
	}


	/**
	 * Adds the floor to the right queue
	 * 
	 * @param origin thats is the floor that need to be added to the queue
	 */
	public synchronized void checkPriority(int origin, String floorButtonDirection, int floor) {

		HashMap<String, Integer> differenceUp = new HashMap<>();
		HashMap<String, Integer> differenceDown = new HashMap<>();
		HashMap<String, Integer> differenceStopped = new HashMap<>();



		for (ElevatorData currElevator : elevators) {
			// difference - used to calculate difference from the elevators current floor
			// and the new floor request
			int difference = currElevator.getCurrentFloor() - origin;
			if (currElevator.getDirection().equals(Direction.DOWN)) {
				if (difference > 0) {
					differenceDown.put(currElevator.name, difference);
//					downElevators.add(currElevator);
				}
			} else if (currElevator.getDirection().equals(Direction.UP)) {
				if (difference < 0) {
					differenceUp.put(currElevator.name, difference);
//					upElevators.add(currElevator);
				}
			} else if (currElevator.getDirection().equals(Direction.STOPPED)) {
				differenceStopped.put(currElevator.getName(), difference);
//				stoppedElevators.add(currElevator);
			} else {
				// if direction is not correct

			}

		}

		switch (floorButtonDirection) {
			case "DOWN":
			// when we have elevators going down
			if (!differenceDown.isEmpty()) {
				getElevatorFromDifference(differenceDown, origin, true,floor, floorButtonDirection);
			} else if (!differenceStopped.isEmpty()) {
				getElevatorFromDifference(differenceStopped, origin, true,floor, floorButtonDirection);

			} else {
				// Case where there are no elevators going down or stopped, so we assign the
				// floor request to an
				// elevator with the least amount of requests (ie. the size of their up and down
				// queues)
				ElevatorData minElevatorReq = elevators.get(elevatorBeingUsed);
				int minSum = minElevatorReq.getUpQueue().size() + minElevatorReq.getDownQueue().size();
				for (ElevatorData e : elevators) {
					int sumOfSizeQueues = e.getDownQueue().size() + e.getUpQueue().size();
					if (sumOfSizeQueues < minSum) {
						minElevatorReq = e;
						minSum = sumOfSizeQueues;
					}
				}

				for (ElevatorData e : elevators) {
					if (minElevatorReq.getName().equals(e.getName())) {
						e.addToDown(origin);
						e.addToDown(floor);
						break;
					}
				}
				
			}
			break;
			
		case "UP":
			// when we have elevators going down
			if (!differenceUp.isEmpty()) {

				getElevatorFromDifference(differenceUp, origin, false , floor , floorButtonDirection);
			} else if (!differenceStopped.isEmpty()) {

				getElevatorFromDifference(differenceStopped, origin, false, floor, floorButtonDirection);

			} else {
				// Case where there are no elevators going down or stopped, so we assign the
				// floor request to an
				// elevator with the least amount of requests (ie. the size of their up and down
				// queues)
				ElevatorData minElevatorReq = elevators.get(elevatorBeingUsed);
				int minSum = minElevatorReq.getUpQueue().size() + minElevatorReq.getDownQueue().size();
				for (ElevatorData e : elevators) {
					int sumOfSizeQueues = e.getDownQueue().size() + e.getUpQueue().size();
					if (sumOfSizeQueues < minSum) {
						minElevatorReq = e;
						minSum = sumOfSizeQueues;
					}
				}

				for (ElevatorData e : elevators) {
					if (minElevatorReq.getName().equals(e.getName())) {
						e.addToUp(origin);
						e.addToUp(floor);
						break;
					}
				}
				

			}
			break;
			
		default:
			break;
		}

	}

	/**
	 * Check send basically organizes which QUEUE is going to the elevator first
	 * 
	 * @return
	 */
	private synchronized int checkSend(ElevatorData elevator) {
		int toVisit = -1;
		// If up queue is empty and down queue is not empty, set the elevator direction
		// to down
		if (elevator.getUpQueue().isEmpty() && !elevator.getDownQueue().isEmpty()) {
			elevator.setDirection(Direction.DOWN);
		}
		// If up queue is not empty and down queue is empty, set the elevator direction
		// to up
		else if (!elevator.getUpQueue().isEmpty() && elevator.getDownQueue().isEmpty()) {
			elevator.setDirection(Direction.UP);
		}

		// If up queue is not empty or down queue is not empty
		if (!elevator.getUpQueue().isEmpty() || !elevator.getDownQueue().isEmpty()) {
			// If the elevator direction is up, get floor to visit from up queue
			if (elevator.getDirection() == Direction.UP) {
				toVisit = (Integer) elevator.getUpQueue().get(0);
				elevator.removeUp();
			}
			// If the elevator direction is up, get floor to visit from down queue
			else if (elevator.getDirection() == Direction.DOWN) {
				toVisit = (Integer) elevator.getDownQueue().get(0);
				elevator.removeDown();
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

	/**
	 * 
	 * @param numOfElevators
	 */
	public void InitializePort(int numOfElevators) {
		maxElevator = numOfElevators;
		maxElevator--;
		System.out.println(maxElevator);
		System.out.println(elevatorBeingUsed);
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


		String dataString = "" + numOfElevators;
		byte[] toSend = dataString.getBytes();
		mess = "";
		this.sendPacket = new DatagramPacket(toSend, toSend.length, addressFloor, portFloor);
		try {
			this.sendReceiveSocketFloor.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while(elevators.size() != numOfElevators){
			data = new byte[100];
			receivePacket = new DatagramPacket(data, data.length);
			try {
				sendReceiveSocketElevators.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
			elevators.add(new ElevatorData(name, receivePacket.getPort(), receivePacket.getAddress(), 0));
			if(elevators.size() == numOfElevators){
				break;
			}
		}


		System.out.println("Floor port is: " + portFloor + " and address is: " + addressFloor);
		for (int z = 0; z < elevators.size(); z++) {
			ElevatorData temp = elevators.get(z);
			System.out
					.println(temp.getName() + " port is: " + temp.getPort() + " and address is: " + temp.getAddress());
		}
	}

	private static String toString(byte[] temp) {
		StringBuilder builder = new StringBuilder();
		for (byte b : temp) {
			builder.append(String.format("%02X ", b));
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		ReadPropertyFile r = new ReadPropertyFile();

		scheduler.InitializePort(2);
		scheduler.sendAndReceive();
	}
}