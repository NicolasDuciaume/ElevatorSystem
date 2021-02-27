package elevator;

import java.sql.Timestamp;

/**
 * Main class to create and run threads  
 */
public class Main {
    public static void main(String[] args){
        
    	Thread Floor, Elevator; // Declaring two threads 
        Scheduler schedular;

        //Initializing
        schedular = new Scheduler();
        Floor = new Thread(new FloorSubsystem("File.txt",schedular), "Floor");
        Elevator = new Thread(new ElevatorSubsystem(schedular), "Elevator");

        //Starting the threads
        Floor.start();
        Elevator.start();
    }
}
