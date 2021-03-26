package elevator;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Elevator subsystem that handles the different states of the elevator,
 * receives directions of which floor to visit from the scheduler, and informing
 * the scheduler when the elevator arrives successfully at destination.
 * 
 * @author Chris D'Silva 101067295
 * @author Nazifa Tanzim 101074707
 */
public class ElevatorSubsystem implements Runnable {

	private int location = 1; // current location of the elevator
	private ElevatorStates currentState; // current state of the elevator
	private Direction motorState; // The motor state is whether the elevator is moving
	private Queue<FloorRequest> floorRequests; // Queue of requests that the elevator has to full fill
	private boolean doorOpen; // Whether the door is open
	private FloorRequest data; // The current request
	private int[] elevatorButtons; // array of buttons
	private boolean[] elevatorLamps; // array of lamps
	private Direction directionLamp; // Directional lamp
	private Scheduler scheduler; // Scheduler object used to receive and pass data
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	private String[] packetString = new String[2];
	private String name;
	private static int countWaiting = 0;
	private long time = 0;
	private long time2 = 0;
	private int moving = 1;
	private int currFloor, destFloor;

	private static ReadPropertyFile r = new ReadPropertyFile();
	private static long time_open_close_doors = r.getTimeToOpenCloseDoors();
	private static long time_between_floors = r.getTimeBetweenFloors();

	/**
	 * Instantiates the variables
	 */
	public ElevatorSubsystem(String name) {
		// this.scheduler = scheduler;
		System.out.println(time_open_close_doors);
		this.name = name;
		currentState = ElevatorStates.INITIAL_STATE;
		motorState = Direction.STOPPED;
		data = new FloorRequest();
		doorOpen = true;
		floorRequests = new PriorityQueue<FloorRequest>();
		elevatorLamps = new boolean[r.getNumFloors()];
		currFloor = 1;
		destFloor = 0;

		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		// Initializing the lamps
		for (int i = 0; i < elevatorLamps.length; ++i) {
			elevatorLamps[i] = false;
		}

		// Initializing the buttons
		elevatorButtons = new int[r.getNumFloors()];
		for (int i = 0; i < this.elevatorButtons.length; ++i) {
			elevatorButtons[i] = i + 1;
		}

		// Initializing the directionLamp
		directionLamp = Direction.STOPPED;
	}

	/**
	 * Init packet and socket for elevator
	 */
	public void Initialize() {
		byte[] toSend = new byte[100];

		toSend = this.name.getBytes();
		try {
			this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getElevatorPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			this.sendReceiveSocket.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * Depending on the current state, sets the direction in which the elevator
	 * needs to move or if stationary, then turns on lamps
	 * 
	 *
	 * @param type Used to identify whether the elevator needs directions or need
	 *             the floor lamps.
	 */
	private void parseData(String direction, String type) {
		// TODO: Check if data being parsed is from user or scheduler
		// TODO: If there is a request while elevator is moving to a targeted floor
		// direction will have to be adjusted depending on the request
		if (type.equals("Direction")) {
			if(this.currFloor < this.destFloor) {
				this.motorState = Direction.UP;
			} else if (this.currFloor > this.destFloor){
				this.motorState = Direction.DOWN;
			} else {
				this.motorState = Direction.STOPPED;
			}
		}

		if (type.equals("Floor Number")) {
			this.elevatorLamps[Integer.parseInt(direction) - 1] = true;
			this.destFloor = Integer.parseInt(direction);
		}

	}

	/**
	 * State Machine that will complete ElevatorSubsystem operations
	 */
	public void stateMachine() {

		switch (currentState) {
		case INITIAL_STATE: // Elevator stopped with doors open
			byte[] data = new byte[100];
			receivePacket = new DatagramPacket(data, data.length);
			try {
				sendReceiveSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			} // Receive data from Scheduler
			String receivePacketData = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
			System.out.println(
					this.name + " Received: " + new String(receivePacket.getData(), 0, this.receivePacket.getLength()));
			if (receivePacketData.equals("waiting")) {
				currentState = ElevatorStates.INITIAL_STATE;
				String elevatorWithRequest = name + "-" + receivePacketData;
				byte[] toSend = elevatorWithRequest.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getElevatorPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				countWaiting++;
				//if (countWaiting >= 10)
					//System.exit(0);
			} else {
				currentState = ElevatorStates.STATE_1;
				countWaiting = 0;
			}
			time = System.nanoTime();
			break;
		case STATE_1: // doors close			
			if(System.nanoTime() >= (time_open_close_doors + time)){
				doorOpen = false;
				
				System.out.println(name + " door closed");

				this.packetString = (new String(receivePacket.getData(), 0, this.receivePacket.getLength())).split(" ");
				parseData(this.packetString[1], "Direction");
				String msg1 = name + "-door_closed";
				byte[] toSend1 = msg1.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend1, toSend1.length, InetAddress.getLocalHost(), r.getElevatorPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				currentState = ElevatorStates.STATE_2;
				time = System.nanoTime();
			} else {
				System.out.println(name + " door closing");
				String msg1 = name + "-door_closing";
				byte[] toSend1 = msg1.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend1, toSend1.length, InetAddress.getLocalHost(), r.getElevatorPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				currentState = ElevatorStates.STATE_1;
			}
			break;
		case STATE_2: // Transition to moving
			// Turn on lamps
			directionLamp = motorState;
			parseData(this.packetString[0], "Floor Number");
			parseData(this.packetString[1], "Direction");
			// Listen to request implementation
			currentState = ElevatorStates.STATE_3;
			time = System.nanoTime();
			time2 = System.nanoTime();
			break;
		case STATE_3: // Elevator moving
			long x = time_between_floors * Math.abs(this.destFloor - this.currFloor);
			if(System.nanoTime()  <= (x + time)){
				long currTime = System.nanoTime();
				
				if(currTime - time2 >= time_between_floors){
					if(this.currFloor < this.destFloor){
						this.currFloor++;
						time2 = System.nanoTime();
					}
					else if(this.currFloor > this.destFloor){
						this.currFloor--;
						time2 = System.nanoTime();
					}
					
				}
				String msg = name + "-moving-" + this.currFloor;
				byte[] toSend = msg.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getElevatorPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				currentState = ElevatorStates.STATE_3;
			}
			else{
				time = System.nanoTime(); // reset time before switching states
				currentState = ElevatorStates.STATE_4;
			}
			break;
		case STATE_4: // reach destination
			
			if(System.nanoTime() >= (time_open_close_doors + time)){
				doorOpen = true;
				motorState = Direction.STOPPED;
				directionLamp = motorState;
				location = Integer.parseInt(this.packetString[0]);
				String msg = name + "-arrived-" + this.packetString[0];
				byte[] toSend = msg.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getElevatorPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				System.out.println(this.name + " Sent: " + msg);
				currentState = ElevatorStates.INITIAL_STATE;
			} else {
				System.out.println(name + " door opening");
				String msg1 = name + "-door_opening";
				byte[] toSend1 = msg1.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend1, toSend1.length, InetAddress.getLocalHost(), r.getElevatorPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				currentState = ElevatorStates.STATE_4;
			}
			
			break;
		}
	}

	/**
	 * Calls the state machine continuously while thread is active
	 */
	@Override
	public void run() {
		System.out.println(name + " started!");
		Initialize();
		while (true) {
			this.stateMachine();

			try {
				Thread.sleep(1500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getters for Unit Testing
	 * 
	 */
	public Direction getMotorState() {
		return this.motorState;
	}

	public boolean isDoorOpen() {
		return this.doorOpen;
	}

	public Direction getDirectionLamp() {
		return this.directionLamp;
	}

	public FloorRequest getData() {
		return this.data;
	}

	public ElevatorStates getCurrentState() {
		return this.currentState;
	}

	/**
	 * Enum containing all the states
	 *
	 */
	public static enum ElevatorStates {
		INITIAL_STATE, // Elevator stopped with doors open
		STATE_1, // Close doors
		STATE_2, // Elevator moving
		STATE_3, // Reach destination
		STATE_4;
		private ElevatorStates() {
		}
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

//		ReadPropertyFile r = new ReadPropertyFile();

		Thread elevatorThreads[] = new Thread[r.getNumElevators()];

		for (int i = 0; i < r.getNumElevators(); i++) {
			elevatorThreads[i] = new Thread(new ElevatorSubsystem("Elevator" + (i + 1)), "Elevator" + (i + 1));
			elevatorThreads[i].start();
		}
	}
}
