package elevator;

public class Main {
	
    public static void main(String[] args){
        Thread floor, elevator;

        Scheduler schedular;
        
        // Getting location of elevator data file
        String dir = System.getProperty("user.dir");
        String filename = dir + "\\Test.txt";

        schedular = new Scheduler();
        floor = new Thread(new FloorSubsystem(filename, schedular), "Floor");
        elevator = new Thread(new ElevatorSubsystem(schedular), "Elevator");

        floor.start();
        elevator.start();
    }
}
