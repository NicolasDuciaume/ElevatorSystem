package elevator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

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
	
	private SchedulerStates currentState, currentState2;
	
	private enum SchedulerStates {
		INITIAL_STATE, STATE_1, STATE_2;
	}

	public Scheduler() {
		upQueue = new ArrayList<Integer>();
		downQueue = new ArrayList<Integer>();
		currentFloor = 1;
		isDataFromFloor = "";
		dataFromElevator = "";
		floorToVisit = -1;
		emptyFloor = true;
		emptyElevator = true;
		
		currentState = SchedulerStates.STATE_1;
		currentState2 = SchedulerStates.STATE_2;
		
		
	}

	public Object sendStateMachine() {
		Object objectReturned = null;
		switch(currentState) {
		case STATE_1:
			objectReturned = sendToElevator();
			currentState = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			objectReturned = sendToFloor();
			currentState = SchedulerStates.STATE_1;
			break;
		}
		return objectReturned;
	}

	public void receiveStateMachine(FloorRequest floorRequest,String floorElevatorData) {
		switch(currentState2) {
			case STATE_1:
				receiveFromElevator(floorElevatorData);
				currentState2 = SchedulerStates.STATE_2;
				break;
			case STATE_2:
				receiveFromFloor(floorElevatorData,floorRequest);
				currentState2 = SchedulerStates.STATE_1;
				break;
		}
	}
	
	public synchronized void receiveFromFloor(String data, FloorRequest floor) {
		while(!this.isDataFromFloor.equals("")) {
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

	public synchronized void receiveFromElevator(String data) {
		while(!this.dataFromElevator.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}

		String[] splitElevatorMsg = data.split(" ");
		if (!splitElevatorMsg[0].equals("arrived") && !data.equals("waiting")) {
			this.checkPriority(Integer.parseInt(data));
			this.floorToVisit = this.checkSend();
			this.notifyAll();
		} else {
			if (splitElevatorMsg[0].equals("arrived")) {
				this.currentFloor = Integer.parseInt(splitElevatorMsg[1]);
			}

			this.dataFromElevator = data;
			this.emptyElevator = false;
			this.notifyAll();
		}

	}

	public synchronized String sendToFloor() {
		while(this.dataFromElevator.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		String temp = this.dataFromElevator;
		this.dataFromElevator = "";
		this.emptyFloor = true;
		this.notifyAll();
		return temp;
	}

	public synchronized FloorRequest sendToElevator() {
		while(this.isDataFromFloor.equals("")) {
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
		return new FloorRequest(new Timestamp(System.currentTimeMillis()), -1L, -1L, this.currentFloor, this.floorToVisit, this.direction);
	}

	public synchronized void checkPriority(int temp) {
		if (this.currentFloor < temp) {
			this.upQueue.add(temp);
			Collections.sort(this.upQueue);
		} else if (this.currentFloor > temp) {
			this.downQueue.add(temp);
			Collections.sort(this.downQueue);
			Collections.reverse(this.downQueue);
		}

	}

	private synchronized int checkSend() {
		int toVisit = -1;
		if (this.upQueue.isEmpty() && !this.downQueue.isEmpty()) {
			this.direction = Direction.DOWN;
		} else if (!this.upQueue.isEmpty() && this.downQueue.isEmpty()) {
			this.direction = Direction.UP;
		}

		if (!this.upQueue.isEmpty() || !this.downQueue.isEmpty()) {
			if (this.direction == Direction.UP) {
				toVisit = (Integer)this.upQueue.get(0);
				this.upQueue.remove(0);
			} else if (this.direction == Direction.DOWN) {
				toVisit = (Integer)this.downQueue.get(0);
				this.downQueue.remove(0);
			}
		}

		return toVisit;
	}

	public String getDataFloor() {
		return this.isDataFromFloor;
	}

	public String getDataElevator() {
		return this.dataFromElevator;
	}

	public boolean isEmptyFloor() {
		return this.emptyFloor;
	}

	public boolean isEmptyElevator() {
		return this.emptyElevator;
	}
}