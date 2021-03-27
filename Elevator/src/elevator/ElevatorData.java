package elevator;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElevatorData {
	String name = "";
	int port;
	private InetAddress address;
	private ArrayList<Integer> upQueue;
	private ArrayList<Integer> downQueue;
	private int currentFloor;
	private Direction direction;
	private ReadPropertyFile r;
	private int error;
	
	public ElevatorData(String name, int port, InetAddress address, int currentFloor) {
		this.name = name;
		this.port = port;
		this.address = address;
		upQueue = new ArrayList<>();
		downQueue = new ArrayList<>();
		this.currentFloor = currentFloor;
		direction = Direction.STOPPED;
		r = new ReadPropertyFile();
		this.error = 0;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public int getError(){return error;}

	public void setError(int x) {this.error = x;}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void removeUp() {
		upQueue.remove(0);
	}

	public void removeDown() {
		downQueue.remove(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Integer> getDownQueue() {
		return downQueue;
	}

	public ArrayList<Integer> getUpQueue() {
		return upQueue;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void addToUp(int floorNum) {
		upQueue.add(floorNum);
	}

	public void addToDown(int floorNum) {
		downQueue.add(floorNum);
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public void sortArrays() {
		Collections.sort(this.upQueue);
		Collections.sort(this.downQueue);
		Collections.reverse(this.downQueue);
	}
}
