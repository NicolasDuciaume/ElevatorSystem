package elevator;
import java.util.*;

/**
 * Elevator subsystem that handles the different states of the elevator, 
 * receives directions of which floor to visit from the scheduler,
 * and informing the scheduler when the elevator arrives successfully at destination.  
 * 
 * @author Chris D'Silva      101067295
 * @author Nazifa Tanzim      101074707
 */
public class ElevatorSubsystem implements Runnable {

	private String location; //current location of the elevator
	private ElevatorStates currentState; //current state of the elevator
	private Direction motorState; // The motor state is whether the elevator is moving 
	private Queue<FloorRequest> floorRequests; //Queue of requests that the elevator has to full fill
	private boolean doorOpen; //Whether the door is open
	private FloorRequest data; //The current request
	private int[] elevatorButtons; // array of buttons
	private boolean[] elevatorLamps; // array of lamps
	private Direction directionLamp; //Directional lamp
	private Scheduler scheduler; //Scheduler object used to receive and pass data

	/**
     * Instantiates the variables  
     */
	public ElevatorSubsystem(Scheduler scheduler) {
		this.scheduler = scheduler;
		currentState = ElevatorStates.INITIAL_STATE;
		motorState = Direction.STOPPED;
		data = new FloorRequest();
		doorOpen = true;
		floorRequests = new PriorityQueue<FloorRequest>();
		elevatorLamps = new boolean[8];

		//Initializing the lamps
		for (int i = 0; i < elevatorLamps.length; ++i) {
			elevatorLamps[i] = false;
		}

		//Initializing the buttons
		// TODO make number of buttons configurable
		elevatorButtons = new int[8];
		for (int i = 0; i < this.elevatorButtons.length; ++i) {
			elevatorButtons[i] = i + 1;
		}

		//Initializing the directionLamp
		directionLamp = Direction.STOPPED;
	}

	/**
     * Depending on the current state, sets the direction in which the elevator needs to move
     * or if stationary, then turns on lamps 
     * 
     * @param request object containing the next floor to visit information 
     * @param type Used to identify whether the elevator needs directions or need the floor lamps.
     */
	public void parseData(FloorRequest request, String type) {
		//TODO: Check if data being parsed is from user or scheduler
    	//TODO: If there is a request while elevator is moving to a targeted floor
    	//		direction will have to be adjusted depending on the request
		if (type.equals("Direction")) {
			if (request.getDirection() == Direction.UP) {
				motorState = Direction.UP;
			} else if (request.getDirection() == Direction.DOWN) {
				motorState = Direction.DOWN;
			}
		}

		if (type.equals("Floor Number")) {
			this.elevatorLamps[request.getFloorRequestOrigin() - 1] = true;
		}

	}

	/**
	 * State Machine that will complete ElevatorSubsystem operations
	 */
	public void stateMachine() {
		switch (currentState) {
		case INITIAL_STATE: //Elevator stopped with doors open
			data = (FloorRequest) scheduler.sendStateMachine(); // Receive data from Scheduler

			System.out.println("Elevator Received: " + data);
			if (data == null) {
				currentState = ElevatorStates.INITIAL_STATE;
			} else {
				currentState = ElevatorStates.STATE_1;
			}
			break;
		case STATE_1: //doors close
			// TODO: Timer Event needed for future Iterations. Door open time,
			// movement time between floors.
			doorOpen = false;
			parseData(data, "Direction");
			currentState = ElevatorStates.STATE_2;
			break;
		case STATE_2: //Elevator moving
			// Turn on lamps
			directionLamp = motorState;
			parseData(data, "Floor Number");
			// Listen to request implementation

			currentState = ElevatorStates.STATE_3;
			break;
		case STATE_3: //reach destination
			// TODO: Timer Event needed for future Iterations. Door open time
			doorOpen = true;
			motorState = Direction.STOPPED;
			directionLamp = motorState;
			String msg = "arrived " + data.getFloorDestination();
			scheduler.receiveStateMachine(null, msg); // Send data from elevator to Scheduler
			System.out.println("Elevator Sent: " + msg);
			currentState = ElevatorStates.INITIAL_STATE;
			break;
		}
	}

	/**
     * Calls the state machine continuously while thread is active
     */
	@Override
	public void run() {
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
		INITIAL_STATE, //Elevator stopped with doors open
		STATE_1, //Close doors
	    STATE_2, //Elevator moving
	    STATE_3; //Reach destination

		private ElevatorStates() {
		}
	}
}
