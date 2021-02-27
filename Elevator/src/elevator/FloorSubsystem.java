package elevator;

import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner; //Import this class to accept input

/**
 * Class that accepts the user input (currently in the form of a text file) And
 * sends/receives data to the Scheduler to inform the elevator
 * 
 * @author Nicolas Duciaume
 * @author Jameel Alidina
 */
public class FloorSubsystem implements Runnable {

	private String data; // holds the information from the input file
	private Scheduler scheduler; // Scheduler object used to receive and pass data

	private ArrayList<FloorRequest> listofRequests;

	private boolean test = true;

	/**
	 * Instantiates all the variables and tries to find and read the input file
	 * 
	 * @param FileLocation String that indicates the name and path of the input file
	 * @param scheduler    Object that shares data between threads
	 */
	public FloorSubsystem(String FileLocation, Scheduler scheduler) {
		this.scheduler = scheduler;
		this.listofRequests = new ArrayList<FloorRequest>();

		addFloorRequest(FileLocation);
	}

	public void addFloorRequest(String FileLocation) {

		Timestamp requestTime = new Timestamp(System.currentTimeMillis());
		long travelTime = 1; 
		long doorTime = 1;

		try {
			File myObj = new File(FileLocation); // Gets file from file location
			Scanner myReader = new Scanner(myObj);

			// if file has multiple lines of input, appends it all to one variable
			while (myReader.hasNextLine()) {
				data = myReader.nextLine(); // get elevator request from file
				String requestArray[] = data.split(" "); 
				
				String direction = (String) requestArray[2];
				Direction requestDirection;
				if (direction.equals("Up")) {
					requestDirection = Direction.UP;
				}else if (direction.equals("Down")) {
					requestDirection = Direction.DOWN;
				}else {
					requestDirection = Direction.STOPPED;
				}

				FloorRequest request = new FloorRequest(requestTime, travelTime, doorTime,
						Integer.parseInt(requestArray[1]), Integer.parseInt(requestArray[3]), requestDirection);
				listofRequests.add(request);
			}
			myReader.close();
		} catch (FileNotFoundException e) { // throws exception if file not found
			System.out.println("File not found.");
			e.printStackTrace();
		}
	}

	// closes/opens doors
	public void doStuff(String floor) {

	}

	/**
	 * Send and receives data from the scheduler
	 */
	@Override
	public void run() {
		while (true) {
			if (test == true) {
				for (FloorRequest r : listofRequests) {
					scheduler.receiveFromFloor("", r); // Send data to the Scheduler
					listofRequests.remove(r);
				}
				test = false;
			} else if (test == false) {
				scheduler.receiveFromFloor(data, null);
			}
			System.out.println("Floor Sent: " + data);
			data = ""; // once data is sent, clear data
			data = scheduler.sendToFloor(); // Receive data from the floor
			System.out.println("Floor Received: " + data);

			String[] temp = data.split(" ");
			if (temp[0].equals("arrived")) {
				// open and close door an lamps
				doStuff(temp[1]);
				data = "go";
			}

			try {
				Thread.sleep(1500); // change to 100 to see difference
			} catch (InterruptedException e) {
			}
		}
	}
}
