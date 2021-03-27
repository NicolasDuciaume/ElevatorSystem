package elevator;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data structure that hold all the properties of the Elevator
 *
 * @author Nicolas Duciaume 101124713
 */
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

    /**
     * Initializes all variables
     *
     * @param name         Elevator's name
     * @param port         Elevator's port
     * @param address      Elevator's adress
     * @param currentFloor current floor of elevator
     */
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

    /**
     * @return the current floor
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Returns the error code
     *
     * @return the error code
     */
    public int getError() {
        return error;
    }

    /**
     * Set the error
     */
    public void setError(int x) {
        this.error = x;
    }

    /**
     * Set the current floor
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * @return the elevator's direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Set the direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * remove first value from the up queue
     */
    public void removeUp() {
        upQueue.remove(0);
    }

    /**
     * remove 1st value from the down queue
     */
    public void removeDown() {
        downQueue.remove(0);
    }

    /**
     * @return name of elevator
     */
    public String getName() {
        return name;
    }

    /**
     * Set name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the down queue
     */
    public ArrayList<Integer> getDownQueue() {
        return downQueue;
    }

    /**
     * @return the up queue
     */
    public ArrayList<Integer> getUpQueue() {
        return upQueue;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Add to the up queue queue
     */
    public void addToUp(int floorNum) {
        upQueue.add(floorNum);
    }

    /**
     * Add to the down queue
     */
    public void addToDown(int floorNum) {
        downQueue.add(floorNum);
    }

    /**
     * @return address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Set the address
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Sorts the arrays in the elevator
     */
    public void sortArrays() {
        Collections.sort(this.upQueue);
        Collections.sort(this.downQueue);
        Collections.reverse(this.downQueue);
    }
}
