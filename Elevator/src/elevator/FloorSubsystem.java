package elevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class FloorSubsystem implements Runnable {
    private String data;
    private Scheduler scheduler;
    private ArrayList<FloorRequest> listofRequests;
    private boolean test = true;
    public int tits = 0;

    public FloorSubsystem(String FileLocation, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.listofRequests = new ArrayList();
        this.addFloorRequest(FileLocation);
    }

    public void addFloorRequest(String FileLocation) {
        Timestamp requestTime = new Timestamp(System.currentTimeMillis());
        long travelTime = 1L;
        long doorTime = 1L;

        try {
            File myObj = new File(FileLocation);
            Scanner myReader = new Scanner(myObj);

            while(myReader.hasNextLine()) {
                this.data = myReader.nextLine();
                String[] requestArray = this.data.split(" ");
                String direction = requestArray[2];
                Direction requestDirection;
                if (direction.equals("Up")) {
                    requestDirection = Direction.UP;
                } else if (direction.equals("Down")) {
                    requestDirection = Direction.DOWN;
                } else {
                    requestDirection = Direction.STOPPED;
                }

                FloorRequest request = new FloorRequest(requestTime, travelTime, doorTime, Integer.parseInt(requestArray[1]), Integer.parseInt(requestArray[3]), requestDirection);
                this.listofRequests.add(request);
            }

            myReader.close();
        } catch (FileNotFoundException var13) {
            System.out.println("File not found.");
            var13.printStackTrace();
        }

    }

    public void doStuff(String floor) {
    }

    public void run() {
        while(true) {
            if(tits == 0){
                FloorRequest r = listofRequests.get(0);
                this.scheduler.receiveFromFloor("", r);
                this.listofRequests.remove(r);
                tits++;
            }
            else{
                this.scheduler.receiveFromFloor(data, null);
            }

            System.out.println("Floor Sent: " + this.data);
            this.data = "";
            this.data = this.scheduler.sendToFloor();
            System.out.println("Floor Received: " + this.data);
            String[] temp = this.data.split(" ");
            if (temp[0].equals("arrived")) {
                this.doStuff(temp[1]);
                this.data = "go";
            }

            try {
                Thread.sleep(1500L);
            } catch (InterruptedException var3) {
            }
        }
    }
}