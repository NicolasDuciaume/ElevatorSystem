package elevator;

import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner; //Import this class to accept input

/**
 * 
 * Floor subsystem handles the floors being requested 
 * and the message to be sent once the floors are arrived at 
 * 
 * @author Nicolas Duciaume   101124713
 * @author Jameel Alidina     101077040
 *
 */
public class FloorSubsystem implements Runnable {

	private String data;
	private Scheduler scheduler;
	private ArrayList<FloorRequest> listofRequests;
	public int requestCount = 0;

	/**
	 * Instantiates all the variables and tries to find and read the input file
	 * 
	 * @param FileLocation String that indicates the name and path of the input file
	 * @param scheduler    Object that shares data between threads
	 */
	public FloorSubsystem(String FileLocation, Scheduler scheduler) {
		this.scheduler = scheduler;
		this.listofRequests = new ArrayList<FloorRequest>();
		this.addFloorRequest(FileLocation);
	}

	/**
	 * Parses through a file with a list of requests from the floor and 
	 * creates a list of FloorRequest objects
	 * 
	 * @param FileLocation location of the file
	 */
	public void addFloorRequest(String FileLocation) {
		Timestamp requestTime = new Timestamp(System.currentTimeMillis());
		long travelTime = 1L;
		long doorTime = 1L;

		try {
			File myObj = new File(FileLocation);
			Scanner myReader = new Scanner(myObj);

			while (myReader.hasNextLine()) {
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

				FloorRequest request = new FloorRequest(requestTime, travelTime, doorTime,
						Integer.parseInt(requestArray[1]), Integer.parseInt(requestArray[3]), requestDirection);
				this.listofRequests.add(request);
			}

			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}

	}

	// TODO: Turn Lamps, buttons etc on
	public void setLampsSensors(String floor) {
	}

	/**
	 * Runs forever until the system exits, and communicates with the Schedular. 
	 */
	@Override
	public void run() {
		while (true) {
			// TODO: Change if statement to a loop so we can process more than 1 request
			if (requestCount == 0) {
				FloorRequest r = listofRequests.get(0);
				scheduler.receiveStateMachine(r, "");
				this.listofRequests.remove(r);
				requestCount++;
			} else {
				scheduler.receiveStateMachine(null, data);
			}

			System.out.println("Floor Sent: " + this.data);
			this.data = "";
			this.data = (String) scheduler.sendStateMachine();
			System.out.println("Floor Received: " + this.data);
			String[] splitElevatorResponse = this.data.split(" ");
			if (splitElevatorResponse[1].equals("-1")) {
				System.exit(0);
			}
			if (splitElevatorResponse[0].equals("arrived")) {
				this.setLampsSensors(splitElevatorResponse[1]);
				this.data = "go";
			}

			try {
				Thread.sleep(1500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getter for Unit Testing
	 * 
	 * @return listOfRequests
	 */
	public ArrayList<FloorRequest> getListOfRequests() {
		return this.listofRequests;
	}
}