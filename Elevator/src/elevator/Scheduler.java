package elevator;
import java.sql.Timestamp;
import java.util.*;

/**
 * Send and receives button presses, lamps and sensors information form floors and elevators. 
 * Schedules the route of the elevator
 * 
 * @author Tooba Sheikh 101028915
 * @author Jameel Alidina 101077040
 * 
 */
public class Scheduler {
	
	//store the floors below and above current floor
	private ArrayList<Integer> upQueue;
	private ArrayList<Integer> downQueue;
	private int currentFloor = -1;
	public Direction direction;

	private String[] processed;
	
	//To differentiate between data received from floor vs elevator
	private boolean isDataFromFloor = false; // is there a current request from floor
	private String dataFromElevator = "";
	
	private int floorToVisit = -1;
	
	//boolean states for the floor and elevator
	private boolean emptyFloor = true;
	private boolean emptyElevator = true;
	
	public Scheduler(){
		upQueue = new ArrayList<>();
		downQueue = new ArrayList<>();
	}
	
	/**
	 * Receives data from the floor
	 * 
	 * @param data info receive from the floor
	 */
	public synchronized void receiveFromFloor(String data, FloorRequest floor) {
		while (!isDataFromFloor) {// not empty
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	
		//Changes the state of the floor and reset the data value
		if(floor != null){
			checkPriority(floor.getFloorRequestOrigin());
			checkPriority(floor.getFloorDestination());
			this.floorToVisit = checkSend();  
			this.isDataFromFloor = !isDataFromFloor;
		}
		else{
			this.floorToVisit = checkSend();
		}
		emptyFloor = false;
		notifyAll();
	}


	
	/**
	 * Receives data from the Elevator
	 * 
	 * @param data info receive from the elevator
	 */
	public synchronized void receiveFromElevator(FloorRequest request) {
		while (!dataFromElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return;
			}
		}
		//Changes the state of the elevator and reset the data value
		if(!(temp[0].equals("arrived") || this.direction == Direction.STOPPED)){
			checkPriority(Integer.parseInt(data));
			this.floorToVisit = checkSend();
			notifyAll();
		}
		else{
			if(temp[0].equals("arrived")){
				currentFloor = Integer.parseInt(temp[1]);
			}
			this.dataFromElevator = data;
			emptyElevator = false;
			notifyAll();
		}
	}
	
	/**
	 * Sends data to the floor
	 * 
	 * @return info to be sent to the floor
	 */
	public synchronized String sendToFloor() {
		while (dataFromElevator.equals("")) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		
		//Changes the state of the floor and reset the data value
		String temp = dataFromElevator;
		dataFromElevator = "";
		emptyFloor = true;
		notifyAll();
		
		return temp;
	}
	
	/**
	 * Sends data to the Elevator
	 * 
	 * @return info to be sent to the elevator
	 */
	public synchronized FloorRequest sendToElevator() {
		while (!isDataFromFloor) {
			try {
				wait();
			} catch (InterruptedException e) {
				return new FloorRequest();
			}
		}
		return new FloorRequest(new Timestamp(System.currentTimeMillis()), -1, -1, this.currentFloor, this.floorToVisit, this.direction);
		
		//Changes the state of the elevator and reset the data value
		
		
	}
	
	/**
	 * Checks priority of each floor requested, which to go to first
	 * add floor to visit to queue
	 */
	public synchronized void checkPriority(int temp) {
		if(currentFloor < temp){
			upQueue.add(temp);
			Collections.sort(upQueue);
		}
		else if(currentFloor > temp){
			downQueue.add(temp);
			Collections.sort(downQueue);
			Collections.reverse(downQueue);
		}
	}
	/**
	 * returns the floor to be visited
	 * @return
	 */

	private synchronized int checkSend(){
		int toVisit = -1;
		if(upQueue.isEmpty() && !downQueue.isEmpty()){
			direction = Direction.DOWN;
		}
		else if(!upQueue.isEmpty() && downQueue.isEmpty()){
			direction = Direction.UP;
		}

		if(upQueue.isEmpty() && downQueue.isEmpty()){
			//do nothing
		}
		else if(direction == Direction.UP){
			toVisit = upQueue.get(0);
			upQueue.remove(0);
		}
		else if(direction == Direction.DOWN){
			toVisit = downQueue.get(0);
			downQueue.remove(0);
		}
		return toVisit;
	}
	
	/**
	 * Getters for JUnit testing
	 */

	public boolean getDataFloor() {
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
