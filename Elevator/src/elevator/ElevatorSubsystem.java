package elevator;

import java.util.PriorityQueue;
import java.util.Queue;

public class ElevatorSubsystem implements Runnable {

	private String location;
	private ElevatorStates currentState;
	private Direction motorState;
	private Queue<FloorRequest> floorRequests;
	private boolean doorOpen;
	private FloorRequest data;
	private int[] elevatorButtons;
	private boolean[] elevatorLamps;
	private Direction directionLamp;
	private Scheduler scheduler;

	public ElevatorSubsystem(Scheduler scheduler) {
		this.scheduler = scheduler;
		currentState = ElevatorStates.INITIAL_STATE;
		motorState = Direction.STOPPED;
		data = new FloorRequest();
		doorOpen = true;
		floorRequests = new PriorityQueue<FloorRequest>();
		elevatorLamps = new boolean[8];
		
		for (int i = 0; i < elevatorLamps.length; ++i) {
			elevatorLamps[i] = false;
		}
		
		elevatorButtons = new int[8];
		for (int i = 0; i < this.elevatorButtons.length; ++i) {
			elevatorButtons[i] = i + 1;
		}

		directionLamp = Direction.STOPPED;
	}

	public void parseData(FloorRequest request, String type) {
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

	public void stateMachine() {
		switch (currentState) {
		case INITIAL_STATE:
			data = (FloorRequest) scheduler.sendStateMachine(); // Receive data from Scheduler
//                data = scheduler.sendToElevator(); //Receive data from Scheduler
			System.out.println("Elevator Received: " + data);
			if (data == null) {
				currentState = ElevatorStates.INITIAL_STATE;
			} else {
				currentState = ElevatorStates.STATE_1;
			}
			break;
		case STATE_1:
			// TODO: Timer Event needed for future Iterations. Door open time,
			// movement time between floors.
			doorOpen = false;
			parseData(data, "Direction");
			currentState = ElevatorStates.STATE_2;
			break;
		case STATE_2:
			// Turn on lamps
			directionLamp = motorState;
			parseData(data, "Floor Number");
			// Listen to request implementation

			currentState = ElevatorStates.STATE_3;
			break;
		case STATE_3:
			// TODO: Timer Event needed for future Iterations. Door open time
			doorOpen = true;
			motorState = Direction.STOPPED;
			directionLamp = motorState;
			String msg = "arrived " + data.getFloorDestination();
			scheduler.receiveStateMachine(null, msg); // Send data from elevator to Scheduler
//                scheduler.receiveFromElevator(msg); // Send data from elevator to Scheduler
			System.out.println("Elevator Sent: " + msg);
			currentState = ElevatorStates.INITIAL_STATE;
			break;
		}
	}

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

	private static enum ElevatorStates {
		INITIAL_STATE, STATE_1, STATE_2, STATE_3;

		private ElevatorStates() {
		}
	}
}
