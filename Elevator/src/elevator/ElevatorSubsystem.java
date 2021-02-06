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
   
	//
	private String location;

	//Denotes the states of the elevator 
    private enum motor{
        UP, DOWN, STOPPED;
    };
    
    // maybe we'll change this to a list of Button objects
    private List<Integer> buttons = new ArrayList<Integer>(); //List of buttons
    private List<Boolean> elevatorLamps = new ArrayList<Boolean>(); //List of lamps
    private boolean arrivalLamp; //true if lap should be lit up

    private String data; // info received from the Scheduler
    private Schedular scheduler; //Scheduler object used to receive and pass data

    /**
     * Instantiates the variables  
     */
    public ElevatorSubsystem(Schedular scheduler) {
        this.scheduler = scheduler;
    }

    private void setArrivalLamp() {

    }

    private void setLocation() {

    }

    private void setMotor() {

    }

    /**
     * Sends and receives data from the scheduler 
     */
    @Override
    public void run() {
        while(true){
            data = scheduler.sendToElevator(); // Receive data from Scheduler
            System.out.println("Elevator Received: " + data);
            scheduler.recieveFromElevator(data); // Send data from elevator to Scheduler
            System.out.println("Elevator Sent: " + data);
            data = "";
            try {
                Thread.sleep(1500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }

}
