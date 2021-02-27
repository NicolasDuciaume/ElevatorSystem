package elevator;
import java.util.*;

/**
 * Elevator class that receives and sends data from the scheduler
 * Data will contain elevator direction and floor number
 *
 * @author Nazifa Tanzim, 101074707
 * @author Chris D'Silva  101067295
 *
 */
public class ElevatorSubsystem implements Runnable{
   
	private String location;
	
	private enum ElevatorStates{
		INITIAL_STATE, STATE_1, STATE_2, STATE_3;
	}
	
	private ElevatorStates currentState;
	private Direction motorState; // current direction of elevator
	
	private Queue<FloorRequest> floorRequests;
    
    private boolean doorOpen;
    
    // maybe we'll change this to a list of Button objects
    //TODO: Make sure to have the number of floors be configurable for future Iterations
    // 8 Floors in this case
    private int[] elevatorButtons = new int[8]; //List of buttons
    private boolean[] elevatorLamps = new boolean[8]; //List of lamps
    
    private Direction directionLamp; //true if lap should be lit up
    private Scheduler scheduler; //Scheduler object used to receive and pass data
    

    /**
     * Instantiates the variables  
     */
    public ElevatorSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        currentState = ElevatorStates.INITIAL_STATE;
        motorState = Direction.STOPPED;
        doorOpen = true;
        
        this.floorRequests = new PriorityQueue<FloorRequest>();
        
        for(Boolean currentLamp : elevatorLamps) {
        	currentLamp = false;
        }
        
        for(int i = 0; i < elevatorButtons.length; i++) {
        	elevatorButtons[i] = i+1;
        }
        
        directionLamp = Direction.STOPPED;
        
    }
    
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
		
		if(type.equals("Floor Number")) {
			elevatorLamps[request.getFloorRequestOrigin() - 1] = true; //
		}
    	
    }
    
	public void stateMachine() {
		switch (currentState) {
		case INITIAL_STATE: // getting data from scheduler in this state
			FloorRequest currentRequest = scheduler.sendToElevator();
			floorRequests.add(currentRequest);
			System.out.println("Elevator Received floor request");
			currentState = ElevatorStates.STATE_1;
			break;
		case STATE_1:
			//TODO: Timer Event needed for future Iterations. Door open time,
			//movement time between floors.
			doorOpen = false;
			parseData(floorRequests.peek(), "Direction");
			currentState = ElevatorStates.STATE_2;
			break;
		case STATE_2:
			//Turn on lamps
			directionLamp = motorState;
			parseData(floorRequests.peek(),"Floor Number");
			//Listen to request implementation
			
			currentState = ElevatorStates.STATE_3;
			break;
		case STATE_3:
			//TODO: Timer Event needed for future Iterations. Door open time
			doorOpen = true;
			motorState = Direction.STOPPED;
			directionLamp = motorState;
			scheduler.receiveFromElevator(floorRequests.remove()); // Send data from elevator to Scheduler
	        System.out.println("Elevator Sent data");
	        currentState = ElevatorStates.INITIAL_STATE;
			break;
		}
	}

    /**
     * Sends and receives data from the scheduler 
     */
    @Override
    public void run() {
        while(true){
//            data = scheduler.sendToElevator(); // Receive data from Scheduler
//            System.out.println("Elevator Received: " + data);
            stateMachine();
//            scheduler.receiveFromElevator(data); // Send data from elevator to Scheduler
//            System.out.println("Elevator Sent: " + data);
            try {
                Thread.sleep(1500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }

}
