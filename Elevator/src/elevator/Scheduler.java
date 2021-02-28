package elevator;

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

	}

	/**
	 * A send state machine
	 * 
	 * @return
	 */
	public Object sendStateMachine() {
		Object objectReturned = null;
		switch (currentState1) {
		case STATE_1:
			objectReturned = sendToElevator();
			currentState1 = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			objectReturned = sendToFloor();
			currentState1 = SchedulerStates.STATE_1;
			break;
		}
		return objectReturned;
	}

	/**
	 * A receive state machine 
	 * 
	 * @param floorRequest
	 * @param floorElevatorData
	 */
	public void receiveStateMachine(FloorRequest floorRequest, String floorElevatorData) {
		switch (currentState2) {
		case STATE_1:
			receiveFromElevator(floorElevatorData);
			currentState2 = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			receiveFromFloor(floorElevatorData, floorRequest);
			currentState2 = SchedulerStates.STATE_1;
			break;
		}
	}

	/**
	 * Receives data from the floor 
	 * 
	 * @param data An ready to go message or nothing
	 * @param floor a floor request
	 */
	public synchronized void receiveFromFloor(String data, FloorRequest floor) {
		while (!this.isDataFromFloor.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		if (floor != null) {
			this.checkPriority(floor.getFloorRequestOrigin());
			this.checkPriority(floor.getFloorDestination());
			this.floorToVisit = this.checkSend();
			this.isDataFromFloor = "ok";
		} else {
			this.floorToVisit = this.checkSend();
			this.isDataFromFloor = "ok";
		}

		this.emptyFloor = false;
		this.notifyAll();
	}

	/**
	 * receives data from the elevator
	 * 
	 * @param data the info received from the elevator, either an arrival message or a button press 
	 */
	public synchronized void receiveFromElevator(String data) {
		while (!this.dataFromElevator.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		//Splits the message then compares to see what needs to be done
		String[] splitElevatorMsg = data.split(" ");
		if (!splitElevatorMsg[0].equals("arrived") && !data.equals("waiting")) {
			this.checkPriority(Integer.parseInt(data));
			this.floorToVisit = this.checkSend();
			this.notifyAll();
		} else { // if button pressed
			if (splitElevatorMsg[0].equals("arrived")) {
				this.currentFloor = Integer.parseInt(splitElevatorMsg[1]);
			}

			this.dataFromElevator = data;
			this.emptyElevator = false;
			this.notifyAll();
		}

	}

	/**
	 * Sends data to the floor
	 * 
	 * @return the instructions to the floor, right now just an arriving message
	 * TODO: Turn on lamps on floor  
	 */
	public synchronized String sendToFloor() {
		while (this.dataFromElevator.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		String dataFromElevator = this.dataFromElevator;
		this.dataFromElevator = "";
		this.emptyFloor = true;
		this.notifyAll();
		return dataFromElevator;
	}

	/**
	 * @return sends the floor requested to the elevator
	 */
	public synchronized FloorRequest sendToElevator() {
		while (this.isDataFromFloor.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return new FloorRequest();
			}
		}
		this.isDataFromFloor = "";
		emptyElevator = true;
		notifyAll();
		return new FloorRequest(new Timestamp(System.currentTimeMillis()), -1L, -1L, this.currentFloor,
				this.floorToVisit, this.direction);
	}

	/**
	 * Adds the floor to the right queue  
	 * 
	 * @param floor thats is the floor that need to be added to the queue
	 */
	public synchronized void checkPriority(int floor) {
		if (this.currentFloor < floor) {
			this.upQueue.add(floor);
			Collections.sort(this.upQueue);
		} else if (this.currentFloor > floor) {
			this.downQueue.add(floor);
			Collections.sort(this.downQueue);
			Collections.reverse(this.downQueue);
		}

	}

	/**
	 * Check send basically organizes which QUEUE is going to the elevator first 
	 * 
	 * @return
	 */
	private synchronized int checkSend() {
		int toVisit = -1;
		if (this.upQueue.isEmpty() && !this.downQueue.isEmpty()) {
			this.direction = Direction.DOWN;
		} else if (!this.upQueue.isEmpty() && this.downQueue.isEmpty()) {
			this.direction = Direction.UP;
		}

		if (!this.upQueue.isEmpty() || !this.downQueue.isEmpty()) {
			if (this.direction == Direction.UP) {
				toVisit = (Integer) this.upQueue.get(0);
				this.upQueue.remove(0);
			} else if (this.direction == Direction.DOWN) {
				toVisit = (Integer) this.downQueue.get(0);
				this.downQueue.remove(0);
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
}