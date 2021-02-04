
public class Main {
	
    public static void main(String[] args){
        Thread floor, elevator;

        Scheduler schedular;

        schedular = new Scheduler();
        floor = new Thread(new FloorSubsystem("D:/ElevatorSystem/Elevator/src/File.txt",schedular), "Floor");
        elevator = new Thread(new ElevatorSubsystem(schedular), "Elevator");

        floor.start();
        elevator.start();
    }
}
