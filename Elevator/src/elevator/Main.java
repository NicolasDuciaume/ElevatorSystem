package elevator;
/**
 * Main class to create and run threads  
 */
public class Main {
    public static void main(String[] args){
        
    	Thread Floor, Elevator; // Declaring two threads 
        Schedular schedular;

        //Initializing
        schedular = new Schedular();
        Floor = new Thread(new FloorSubsystem("File.txt",schedular), "Floor");
        Elevator = new Thread(new ElevatorSubsystem(schedular), "Elevator");

        //Starting the threads
        Floor.start();
        Elevator.start();
    }
}
