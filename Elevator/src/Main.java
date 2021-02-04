package Elevator.src;

public class Main {
    public static void main(String[] args){
        Thread Floor, Elevator;

        Schedular schedular;

        schedular = new Schedular();
        Floor = new Thread(new FloorSubsystem("D:/ElevatorSystem/Elevator/src/File.txt",schedular), "Floor");
        Elevator = new Thread(new ElevatorSubsystem(schedular), "Elevator");

        Floor.start();
        Elevator.start();
    }
}
