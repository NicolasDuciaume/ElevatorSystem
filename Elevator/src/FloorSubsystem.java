package Elevator.src;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;
public class FloorSubsystem implements Runnable{

    private String Data;
    private Schedular scheduler;

    public FloorSubsystem(String FileLocation, Schedular scheduler){
        this.scheduler = scheduler;
        try {
            File myObj = new File(FileLocation);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                Data = "" + myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            scheduler.recieveFromFloor(Data);
            Data = scheduler.sendToFloor();
            try {
                Thread.sleep(500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }
}
