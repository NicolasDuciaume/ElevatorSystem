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
	private Direction motorState;

	//Denotes the states of the elevator 
    private enum Direction{
        UP, DOWN, STOPPED;
    };
    
    private boolean doorOpen;
    
    // maybe we'll change this to a list of Button objects
    //TODO: Make sure to have the number of floors be configurable for future Iterations
    // 8 Floors in this case
    private int[] elevatorButtons = new int[8]; //List of buttons
    private boolean[] elevatorLamps = new boolean[8]; //List of lamps
    
    private Direction directionLamp; //true if lap should be lit up

    private String data; // info received from the Scheduler
    private Scheduler scheduler; //Scheduler object used to receive and pass data

    /**
     * Instantiates the variables  
     */
    public ElevatorSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        currentState = ElevatorStates.INITIAL_STATE;
        motorState = Direction.STOPPED;
        doorOpen = true;
        
        for(Boolean currentLamp : elevatorLamps) {
        	currentLamp = false;
        }
        
        for(int i = 0; i < elevatorButtons.length; i++) {
        	elevatorButtons[i] = i+1;
        }
        
        directionLamp = Direction.STOPPED;
        
    }
    
    public void parseData(String data, String type) {
    	//TODO: Check if data being parsed is from user or scheduler
    	//TODO: If there is a request while elevator is moving to a targeted floor
    	//		direction will have to be adjusted depending on the request
    	String[] split = data.split(" ");
    	if (type.equals("Direction")) {
			if (split[2].equals("Up")) {
				motorState = Direction.UP;
			} else {
				motorState = Direction.DOWN;
			}
		}
		
		if(type.equals("Floor Number")) {
			elevatorLamps[Integer.parseInt(split[1]) - 1] = true;
		}
    	
    }
    
	public void stateMachine() {
		switch (currentState) {
		case INITIAL_STATE:
			data = scheduler.sendToElevator(); //Receive data from Scheduler
			System.out.println("Elevator Received: "+ data);
			currentState = ElevatorStates.STATE_1;
			break;
		case STATE_1:
			//TODO: Timer Event needed for future Iterations. Door open time,
			//movement time between floors.
			doorOpen = false;
			parseData(data,"Direction");
			currentState = ElevatorStates.STATE_2;
			break;
		case STATE_2:
			//Turn on lamps
			directionLamp = motorState;
			parseData(data,"Floor Number");
			//Listen to request implementation
			
			currentState = ElevatorStates.STATE_3;
			break;
		case STATE_3:
			//TODO: Timer Event needed for future Iterations. Door open time
			doorOpen = true;
			motorState = Direction.STOPPED;
			directionLamp = motorState;
			scheduler.receiveFromElevator(data); // Send data from elevator to Scheduler
	        System.out.println("Elevator Sent: " + data);
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
            data = "";
            try {
                Thread.sleep(1500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }

}
