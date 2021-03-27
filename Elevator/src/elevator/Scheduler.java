package elevator;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * 
 * Scheduler handles communication between Elevator and Floors using a state
 * machine
 * 
 * @author Tooba Sheikh 101028915
 * @author Jameel Alidina 101077040
 * @author Nicolas Duciaume 101124713
 * @author Chris D'Silva 101067295
 * @author Nazifa Tanzim 101074707
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
	private Random rand;

	private static ReadPropertyFile r = new ReadPropertyFile();

	/**
	 * Enum for the states
	 *
	 */
	public enum SchedulerStates {
		STATE_1, 
		STATE_2;
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
		rand = new Random();

		currentState1 = SchedulerStates.STATE_1;
		currentState2 = SchedulerStates.STATE_2;

		elevators = new ArrayList<>();

		try {
			sendReceiveSocketFloor = new DatagramSocket(r.getFloorPort());
			sendReceiveSocketElevators = new DatagramSocket(r.getElevatorPort());
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Changes the number of elevator being used
	 */
	private void elevatorChange() {
		if (elevatorBeingUsed >= maxElevator) {
			elevatorBeingUsed = 0;
		} else {
			elevatorBeingUsed++;
		}
	}

	/**
	 * Calls the state machines and update the elevator changes
	 */
	private void sendAndReceive() {
		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		while (true) {
			if (count == 2) {
				elevatorChange();
				count = 0;
			}
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
		case STATE_1: //Send to Elevator
			for (int i = 0; i <= maxElevator; i++) {
				sendToElevator();
			}
			currentState1 = SchedulerStates.STATE_2;
			break;
		case STATE_2://Send to floor
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
		case STATE_1://Receive from elevator
			for (int i = 0; i <= maxElevator; i++) {
				boolean b = receiveFromElevator(data);
				if (!b) {
					i--;
				}
			}
			currentState2 = SchedulerStates.STATE_2;
			break;
		case STATE_2: //receive from floor
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
		//Try to receive packet from floor
		try {
			sendReceiveSocketFloor.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//turn packet to string and split it into an array of strings
		String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
		String[] cut = name.split(" ");
		
		//if string is a floor request
		if (!cut[0].equals("go")) {
			if(!cut[0].equals("error")){//if floor request is not an error
				//Sort the floor request to one of the elevators
				checkPriority(Integer.parseInt(cut[1]), cut[2], Integer.parseInt(cut[3]));
			}
			else{//If error than select one of the elevators to handle error
				int temp = rand.nextInt(elevators.size());
				System.out.println(temp);
				elevators.get(temp).setError(Integer.parseInt(cut[1]));//Sets type of error
			}
			// TODO: Need to figure out the direction to go to the floor where we pick up
			// people
		}
	}

	/**
	 * Receives data from the elevator
	 * 
	 * @param data the info received from the elevator, either an arrival message or
	 *             a button press
	 */
	public synchronized boolean receiveFromElevator(byte[] data) {
		//Gets the elevator in use and tries to receive packet
		ElevatorData temp = elevators.get(elevatorBeingUsed);
		try {
			sendReceiveSocketElevators.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Converts the packet to string and splits it into an array of strings
		String name = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
		String[] splitElevatorMsg = name.split("-");
		String[] test = mess.split(" "); //split message string into an array of strings
		
		// If message was not blank and was split into an array 
		// then append the packet string to the message depending 
		// on the info contained within message
		for (String tt : test) {
			String[] test2 = tt.split("-");
			if (test2[0].equals(splitElevatorMsg[0])) {
				if (test2[1].equals("moving") && !splitElevatorMsg[1].equals("moving")) {
					mess = "";
					for (String tt2 : test) {
						if (tt2.equals(tt)) {
							if (mess.equals("")) {
								mess = mess + name;
							} else {
								mess = mess + " " + name;
							}
						} else {
							if (mess.equals("")) {
								mess = mess + tt2;
							} else {
								mess = mess + " " + tt2;
							}
						}
					}
				}
				else {
					if (test2[1].equals("moving") && splitElevatorMsg[1].equals("moving")) {
						if (Integer.parseInt(test2[2]) != Integer.parseInt(splitElevatorMsg[2])) {
							mess = "";
							for (String tt2 : test) {
								if (tt2.equals(tt)) {
									if (mess.equals("")) {
										mess = mess + name;
									} else {
										mess = mess + " " + name;
									}
								} else {
									if (mess.equals("")) {
										mess = mess + tt2;
									} else {
										mess = mess + " " + tt2;
									}
								}
							}
						}
					}
				}
				return false;
			}
		}
		

		// if elevator hasn't arrived
		if (!splitElevatorMsg[1].equals("arrived")) { 
			if (splitElevatorMsg[1].equals("added")) {
				//button pressed, send new instruction to sort into Queues
				this.checkPriority(-1, null, Integer.parseInt(splitElevatorMsg[0]));
			} else {//Elevator in a state of in between  movements
				if (splitElevatorMsg[1].equals("moving")) {//If elevator moving between floor
					if (mess.equals("")) {  
						//if message in empty, set message as data received from elevator
						mess = mess + splitElevatorMsg[0] + "-moving-" + splitElevatorMsg[2];
					} else {
						//if message not empty, append data received from elevator to message
						mess = mess + " " + splitElevatorMsg[0] + "-moving-" + splitElevatorMsg[2];
						waiting--;
					}
				} else if (splitElevatorMsg[1].equals("waiting")) { //If elevator waiting for new instruction
					waiting++;
					if (mess.equals("")) {  
						//if message in empty, set message as data received from elevator
						mess = mess + splitElevatorMsg[0] + "-waiting";
					} else {
						//if message not empty, append data received from elevator to message
						mess = mess + " " + splitElevatorMsg[0] + "-waiting";
						waiting--;
					}
				} else if (splitElevatorMsg[1].equals("door_closing")) { //If elevators door closing
					if (mess.equals("")) {
						//if message in empty, set message as data received from elevator
						mess = mess + splitElevatorMsg[0] + "-door_closing";
					} else { //if message not empty, append data received from elevator to message
						mess = mess + " " + splitElevatorMsg[0] + "-door_closing";
						waiting--;
					}

				}else if (splitElevatorMsg[1].equals("door_closed")) { //If elevators door closed
					if (mess.equals("")) { 
						//if message in empty, set message as data received from elevator
						mess = mess + splitElevatorMsg[0] + "-door_closed-" + splitElevatorMsg[2];
					} else { 
						//if message not empty, append data received from elevator to message
						mess = mess + " " + splitElevatorMsg[0] + "-door_closed-" + splitElevatorMsg[2];
						waiting--;
					}

				} else if (splitElevatorMsg[1].equals("door_opening")) { //If elevator doors opening
					if (mess.equals("")) {  
						//if message in empty, set message as data received from elevator
						mess = mess + splitElevatorMsg[0] + "-door_opening";
					} else {
						//if message not empty, append data received from elevator to message
						mess = mess + " " + splitElevatorMsg[0] + "-door_opening";
						waiting--;
					}
				} else if (splitElevatorMsg[1].equals("error")) { //If elevator has a fatal stuck between floors error
					//remove from allowed to use elevators
					ElevatorData tempRem = elevators.get(elevatorBeingUsed);
					elevators.remove(elevatorBeingUsed);
					maxElevator--;
					System.out.println(elevators.size());
					
					//Get an elevator from the list of elevators
					int x = rand.nextInt(elevators.size());
					ElevatorData tempEl = elevators.get(x);
					
					//Reorganize the items from the queues of the broken elevator to another elevator
					for(int up: tempRem.getUpQueue()){
						tempEl.addToUp(up);
					}
					for(int down: tempRem.getDownQueue()){
						tempEl.addToDown(down);
					}
					//Send error info to the floor subsystem
					if (mess.equals("")) {  
						//if message in empty, set message as data received from elevator
						mess = mess + splitElevatorMsg[0] + "-error-" + splitElevatorMsg[2];
					} else {
						//if message not empty, append data received from elevator to message
						mess = mess + " " + splitElevatorMsg[0] + "-error-" + splitElevatorMsg[2];
						waiting--;
					}
				}
			}
		} else { // if elevator arrived to floor
			temp.setCurrentFloor(Integer.parseInt(splitElevatorMsg[2]));
			if (mess.equals("")) {  //if message in empty, set message as data received from elevator
				mess = mess + name;
			} else { //if message not empty, append data received from elevator to message
				mess = mess + " " + name;
				waiting--;
			}
		}
		//Changes the number of elevator being used
		elevatorChange();
		return true;
	}

	/**
	 * Sends data to the floor
	 * 
	 * @return the instructions to the floor, right now just an arriving message
	 *         TODO: Turn on lamps on floor
	 */
	public synchronized void sendToFloor() {
		//create a byte array 
		byte[] toSend = new byte[100];
		System.out.println("Sending");
		
		//If all elevator in use than wait
		if (waiting == elevators.size()) {
			String dataString = "waiting";
			toSend = dataString.getBytes();
			mess = "";
		} else {//if there is at least one elevator available than setup the data to send 
			String dataString = mess;
			toSend = dataString.getBytes();
			mess = "";
		}
		
		//Create and try to send the packet to the floor subsystem
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
		ElevatorData temp = elevators.get(elevatorBeingUsed);
		byte[] toSend = new byte[100];
		// send the appropriate floor request based on the elevator

		if(temp.getError() != 0){ //if sending error to elevator
			String e = "error " + temp.getError();
			temp.setError(0);
			toSend = e.getBytes();
		}
		else{//if sending floor request to elevator
			int t = checkSend(temp); //get the floor to visit
			if (t == -1) { //if floor to visit is negative then, send wait message to elevator
				String wait = "waiting";
				toSend = wait.getBytes();
			} else { //if floor to visit is a valid floor than send direction and floor to the elevator
				String dat = t + " " + temp.getDirection();
				toSend = dat.getBytes();
			}
		}

		//Create and try to send the packet to the elevator subsystem
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
	 * Uses the differences to figure out which elevator is going to be sent
	 * 
	 * @param differenceMap stores all the differences between floors
	 * @param floor stores the origin floor
	 * @param down boolean that chooses whether the origin is up or down from the current elevator location
	 * @param floor2 stores the destination 
	 * @param direction stores the direction 
	 */
	private void getElevatorFromDifference(HashMap<String, Integer> differenceMap, int floor, boolean down, int floor2,
			String dir) {
		int minDifference = Collections.min(differenceMap.values());
		for (String currElevatorName : differenceMap.keySet()) {
			if (differenceMap.get(currElevatorName).equals(minDifference)) {
				for (ElevatorData e : elevators) {
					if (e.getName().equals(currElevatorName)) {
						if (down) {
							e.addToDown(floor);
							if (dir.equals("UP")) {
								e.addToUp(floor2);
							} else {
								e.addToDown(floor2);
							}
							break;
						} else {
							e.addToUp(floor);
							if (dir.equals("UP")) {
								e.addToUp(floor2);
							} else {
								e.addToDown(floor2);
							}
							break;
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * Adds the floor to the right queue
	 * 
	 * @param origin thats is the floor that need to be added to the queue
	 * @param Direction received from the floor
	 * @param floor is the destination
	 */
	public synchronized void checkPriority(int origin, String floorButtonDirection, int floor) {
		
		// Splits Elevator into different hashmaps depending on their direction
		HashMap<String, Integer> differenceUp = new HashMap<>();
		HashMap<String, Integer> differenceDown = new HashMap<>();
		HashMap<String, Integer> differenceStopped = new HashMap<>();

		//Calculates the differences and add to above maps
		for (ElevatorData currElevator : elevators) {
			// difference - used to calculate difference from the elevators current floor
			// and the new floor request
			int difference = currElevator.getCurrentFloor() - origin;
			if (currElevator.getDirection().equals(Direction.DOWN)) {
				if (difference > 0) {
					differenceDown.put(currElevator.name, difference);
				}
			} else if (currElevator.getDirection().equals(Direction.UP)) {
				if (difference < 0) {
					differenceUp.put(currElevator.name, difference);
				}
			} else if (currElevator.getDirection().equals(Direction.STOPPED)) {
				differenceStopped.put(currElevator.getName(), difference);
			} else {
				// if direction is not correct

			}

		}

		switch (floorButtonDirection) {
		case "DOWN": // Elevator is going down
			if (!differenceDown.isEmpty()) {
				getElevatorFromDifference(differenceDown, origin, true, floor, floorButtonDirection);
			} else if (!differenceStopped.isEmpty()) {
				getElevatorFromDifference(differenceStopped, origin, true, floor, floorButtonDirection);

			} else {
				// Case where there are no elevators going down or stopped, so we assign the
				// floor request to an elevator with the least amount of requests 
				//(ie. the size of their up and down queues)
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

		case "UP": // Elevator is going up
			if (!differenceUp.isEmpty()) {

				getElevatorFromDifference(differenceUp, origin, false, floor, floorButtonDirection);
			} else if (!differenceStopped.isEmpty()) {

				getElevatorFromDifference(differenceStopped, origin, false, floor, floorButtonDirection);

			} else {
				// Case where there are no elevators going down or stopped, so we assign the
				// floor request to an elevator with the least amount of requests 
				//(ie. the size of their up and down queues)
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
	public synchronized int checkSend(ElevatorData elevator) {
		int toVisit = -1;
		
		int currFloor = elevator.getCurrentFloor();
		//System.out.println("In check send");
		
		// If up queue is empty and down queue is not empty, set the elevator direction
		// to down
		if (elevator.getUpQueue().isEmpty() && !elevator.getDownQueue().isEmpty()) {
			//System.out.println("printing down queue");
			for(int i : elevator.getDownQueue()) {
				//System.out.println(i);
			}
			elevator.setDirection(Direction.DOWN);
		}
		// If up queue is not empty and down queue is empty, set the elevator direction
		// to up
		else if (!elevator.getUpQueue().isEmpty() && elevator.getDownQueue().isEmpty()) {
			//System.out.println("printing up queue");
			for(int i : elevator.getUpQueue()) {
				//System.out.println(i);
			}
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

	

	/**
	 * Initializes the packet sockets and the number of elevators
	 * 
	 * @param numOfElevators
	 */
	public void InitializePort(int numOfElevators) {
		maxElevator = numOfElevators;
		maxElevator--;
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

		while (elevators.size() != numOfElevators) {
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
			if (elevators.size() == numOfElevators) {
				break;
			}
		}

		//prints out the floor and elevator ports and address
		System.out.println("Floor port is: " + portFloor + " and address is: " + addressFloor);
		for (int z = 0; z < elevators.size(); z++) {
			ElevatorData temp = elevators.get(z);
			System.out
					.println(temp.getName() + " port is: " + temp.getPort() + " and address is: " + temp.getAddress());
		}
	}

	/**
	 *  A to string method for the byte array
	 *  Converts byte array to string
	 *  
	 *  @return string
	 */
	private static String toString(byte[] temp) {
		StringBuilder builder = new StringBuilder();
		for (byte b : temp) {
			builder.append(String.format("%02X ", b));
		}
		return builder.toString();
	}

	/* For testing purposes */
	public void closeSockets() {
		this.sendReceiveSocketElevators.close();
		this.sendReceiveSocketFloor.close();
	}

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

	public DatagramPacket getReceivePacket() {
		return this.receivePacket;
	}

	public DatagramPacket getSendPacket() {
		return this.sendPacket;
	}

	public DatagramSocket getFloorSocket() {
		return this.sendReceiveSocketFloor;
	}

	public DatagramSocket getElevatorSocket() {
		return this.sendReceiveSocketElevators;
	}

	public ArrayList<ElevatorData> getElevators() {
		return this.elevators;
	}

	public void setElevators(ArrayList<ElevatorData> e) {
		this.elevators = e;
	}
	
	/**
	 * Initializes and runs the thread
	 * @param args
	 */
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		scheduler.InitializePort(r.getNumElevators());
		scheduler.sendAndReceive();
	}
}