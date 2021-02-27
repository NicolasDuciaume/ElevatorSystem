package elevator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class Scheduler {
	private ArrayList<Integer> upQueue = new ArrayList<Integer>();
	private ArrayList<Integer> downQueue = new ArrayList<Integer>();
	private int currentFloor = 1;
	public Direction direction;
	private String[] processed;
	private String isDataFromFloor = "";
	private String dataFromElevator = "";
	private int floorToVisit = -1;
	private boolean emptyFloor = true;
	private boolean emptyElevator = true;
	
	private SchedulerStates currentState, currentState2;
	
	private enum SchedulerStates {
		INITIAL_STATE, STATE_1, STATE_2;
	}

	public Scheduler() {
		currentState = SchedulerStates.STATE_1;
		currentState2 = SchedulerStates.STATE_2;
	}

	public Object stateMachine(String floorOrElevator,FloorRequest r,String floorElevatorData) {
		Object temp = null;
		switch(currentState) {
		case STATE_1:
			temp = sendToElevator();
			currentState = SchedulerStates.STATE_2;
			break;
		case STATE_2:
			temp = sendToFloor();
			currentState = SchedulerStates.STATE_1;
			break;
		}
		return temp;
	}

	public Object stateMachine2(String floorOrElevator,FloorRequest r,String floorElevatorData) {
		switch(currentState2) {
			case STATE_1:
				receiveFromElevator(floorElevatorData);
				currentState2 = SchedulerStates.STATE_2;
				break;
			case STATE_2:
				receiveFromFloor(floorElevatorData,r);
				currentState2 = SchedulerStates.STATE_1;
				break;
		}
		return null;
	}
	
	public synchronized void receiveFromFloor(String data, FloorRequest floor) {
		while(!this.isDataFromFloor.equals("")) {
			try {
				this.wait();
			} catch (InterruptedException var4) {
				var4.printStackTrace();
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
			} catch (InterruptedException var3) {
				return;
			}
		}

		String[] temp = data.split(" ");
		if (!temp[0].equals("arrived") && !data.equals("waiting")) {
			this.checkPriority(Integer.parseInt(data));
			this.floorToVisit = this.checkSend();
			this.notifyAll();
		} else {
			if (temp[0].equals("arrived")) {
				this.currentFloor = Integer.parseInt(temp[1]);
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
			} catch (InterruptedException var2) {
				return null;
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
			} catch (InterruptedException var2) {
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