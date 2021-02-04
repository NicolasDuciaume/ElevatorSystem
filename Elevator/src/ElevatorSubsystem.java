package Elevator.src;

import java.util.*;

/**
 *
 * @author Nazifa Tanzim, 101074707
 * @author Chris D'Silva
 *
 */
public class ElevatorSubsystem implements Runnable{
    private String location;


    private enum motor{
        UP, DOWN, STOPPED;
    };
    // maybe we'll change this to a list of Button objects
    private List<Integer> buttons = new ArrayList<Integer>();
    private List<Boolean> elevatorLamps = new ArrayList<Boolean>();
    private boolean arrivalLamp;

    private String data;
    private Schedular scheduler;

    public ElevatorSubsystem(Schedular scheduler) {
        this.scheduler = scheduler;
    }

    private void setArrivalLamp() {

    }

    private void setLocation() {

    }

    private void setMotor() {

    }

    @Override
    public void run() {
        while(true){
            data = scheduler.sendToElevator();
            System.out.println("Elevator Received: " + data);
            scheduler.recieveFromElevator(data);
            System.out.println("Elevator Sent: " + data);
            data = "";
            try {
                Thread.sleep(1500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }

}
