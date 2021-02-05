<<<<<<< HEAD:src/FloorSubsystem.java
<<<<<<< HEAD:Elevator/src/FloorSubsystem.java

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; //Import this class to accept input

/**
 * Class that accepts the user input (currently in the form of a text file) 
 * And sends/receives data to the Scheduler to inform the elevator 
 * 
 * @author Nicolas Duciaume
 * @author Jameel Alidina
 */
public class FloorSubsystem implements Runnable{

    private String Data; //holds the information from the input file 
    private Schedular scheduler; //Scheduler object used to receive and pass data

    /**
     * Instantiates all the variables and tries to find and read the input file
     * 
     * @param FileLocation String that indicates the name and path of the input file
     * @param scheduler Object that shares data between threads
     */
    public FloorSubsystem(String FileLocation, Schedular scheduler){
=======
=======
package elevator;
>>>>>>> Added JUnit tests:src/elevator/FloorSubsystem.java
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;

public class FloorSubsystem implements Runnable{

    private String data;
    private Scheduler scheduler;

    public FloorSubsystem(String FileLocation, Scheduler scheduler){
>>>>>>> Restructed project folders:src/FloorSubsystem.java
        this.scheduler = scheduler;
        try {
            File myObj = new File(FileLocation); //Gets file from file location  
            Scanner myReader = new Scanner(myObj);
  
            //if file has multiple lines of input, appends it all to one variable
            while (myReader.hasNextLine()) {
<<<<<<< HEAD:Elevator/src/FloorSubsystem.java
                Data = "" + myReader.nextLine(); 
=======
                data = "" + myReader.nextLine();
>>>>>>> Restructed project folders:src/FloorSubsystem.java
            }
            myReader.close();
        } catch (FileNotFoundException e) { //throws exception if file not found
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
    
    /**
     * Send and receives data from the scheduler
     */
    @Override
    public void run() {
        while(true){
<<<<<<< HEAD:Elevator/src/FloorSubsystem.java
            scheduler.recieveFromFloor(Data);  //Send data to the Scheduler
            System.out.println("Floor Sent: " + Data);
            Data = ""; // once data is sent, clear data
            Data = scheduler.sendToFloor(); //Receive data from the floor
            System.out.println("Floor Received: " + Data);
=======
            scheduler.recieveFromFloor(data);
            System.out.println("Floor Sent: " + data);
            data = "";
            data = scheduler.sendToFloor();
            System.out.println("Floor Received: " + data);
>>>>>>> Restructed project folders:src/FloorSubsystem.java
            try {
                Thread.sleep(1500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }
}
