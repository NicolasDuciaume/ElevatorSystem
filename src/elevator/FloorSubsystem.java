package elevator;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;

public class FloorSubsystem implements Runnable{

    private String data;
    private Scheduler scheduler;

    public FloorSubsystem(String FileLocation, Scheduler scheduler){
        this.scheduler = scheduler;
        try {
            File myObj = new File(FileLocation);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = "" + myReader.nextLine();
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
            scheduler.recieveFromFloor(data);
            System.out.println("Floor Sent: " + data);
            data = "";
            data = scheduler.sendToFloor();
            System.out.println("Floor Received: " + data);
            try {
                Thread.sleep(1500); // change to 100 to see difference
            } catch (InterruptedException e) {}
        }
    }
}
