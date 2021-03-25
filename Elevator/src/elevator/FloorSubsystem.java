package elevator;


import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.*;
import java.util.Scanner; //Import this class to accept input

/**
 * 
 * Floor subsystem handles the floors being requested and the message to be sent
 * once the floors are arrived at
 * 
 * @author Nicolas Duciaume 101124713
 * @author Jameel Alidina 101077040
 *
 */
public class FloorSubsystem implements Runnable {

	private String data;
	private ArrayList<FloorRequest> listofRequests;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	private String wait = "waiting";
	private int numOfElevators = 0;
	public int requestCount = 0;
	private ReadPropertyFile configFile;
	private Map<Integer,Boolean[]> floorLamps; 

	/**
	 * Instantiates all the variables and tries to find and read the input file
	 * 
	 * @param FileLocation String that indicates the name and path of the input file
	 *
	 */
	public FloorSubsystem(String FileLocation) {
		// this.scheduler = scheduler;
		this.listofRequests = new ArrayList<FloorRequest>();
		configFile = new ReadPropertyFile();
		floorLamps = new HashMap<Integer,Boolean[]>();
		for(int i = 0; i < configFile.getNumFloors(); i++) {
			Boolean[] b = {false,false};
			floorLamps.put(i+1, b);
		}
		this.addFloorRequest(FileLocation);
		
		
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	public void Initialize() {
		byte[] toSend = new byte[100];
		try {
			this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), 69);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			this.sendReceiveSocket.send(this.sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String toPrint = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
		numOfElevators = Integer.parseInt(toPrint);
	}

	private static String toString(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(String.format("%02X ", b));
		}
		return builder.toString();
	}

	/**
	 * Parses through a file with a list of requests from the floor and creates a
	 * list of FloorRequest objects
	 * 
	 * @param fileLocation location of the file
	 */
	public void addFloorRequest(String fileLocation) {
		long travelTime = 1L;
		long doorTime = 1L;

		try {
			File myObj = new File(fileLocation);
			Scanner myReader = new Scanner(myObj);

			while (myReader.hasNextLine()) {
				this.data = myReader.nextLine();
				String[] requestArray = this.data.split(" ");
				String direction = requestArray[2];
				Boolean[] currLampStatus = floorLamps.get(Integer.parseInt(requestArray[1]));
				Direction requestDirection;
				if (direction.equals("Up")) {
					currLampStatus[0] = true;
					requestDirection = Direction.UP;
				} else if (direction.equals("Down")) {
					requestDirection = Direction.DOWN;
					currLampStatus[1] = true;
				} else {
					requestDirection = Direction.STOPPED;
				}

				
				FloorRequest request = new FloorRequest(requestArray[0], travelTime, doorTime,
						Integer.parseInt(requestArray[1]), Integer.parseInt(requestArray[3]), requestDirection);
				this.listofRequests.add(request);
			}

			for(Integer i : floorLamps.keySet()) {
				Boolean[] b = floorLamps.get(i);
				System.out.println("Floor " + i +"	UP: " + b[0] + " " + "DOWN: " + b[1]);
			}
			
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}

	}

	// TODO: Turn Lamps, buttons etc on/off
	public void setLampsSensors(String floor) {
		
		//Turn off Floor lamp at the floor it was requested at when elevator arrives at requested floor
//		Boolean[] b = floorLamps.get(Integer.parseInt(floor));
//		if(b[0]) {
//			b[0] = false;
//			System.out.println("Floor " + floor + " lamp UP turned off");
//		}
//		if(b[1]) {
//			b[1] = false;
//			System.out.println("Floor " + floor + " lamp DOWN turned off");
//		}
//		
	}
	
	private void setFloorLampsOff(String floor) {
		//Turn off Floor lamp at the floor it was requested at when elevator arrives at requested floor
		Boolean[] b = floorLamps.get(Integer.parseInt(floor));
		if(b[0]) {
			b[0] = false;
			System.out.println("Floor " + floor + " lamp UP turned off");
		}
		if(b[1]) {
			b[1] = false;
			System.out.println("Floor " + floor + " lamp DOWN turned off");
		}
	}
	
	

	/**
	 * Runs forever until the system exits, and communicates with the Schedular.
	 */
	@Override
	public void run() {
		while (true) {
			// TODO: Change if statement to a loop so we can process more than 1 request
			if (requestCount == 0) {
				FloorRequest floorRequest = listofRequests.get(0);
				// scheduler.receiveStateMachine(r, "");
				this.listofRequests.remove(floorRequest);
				requestCount++;
			} else {
				// scheduler.receiveStateMachine(null, data);
			}

			System.out.println("Floor Sent: " + this.data);
			this.data = "";
			// this.data = (String) scheduler.sendStateMachine();
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

	public void TestSend() {
		if (listofRequests.size() == 0) {
			String status = "go";
			byte[] toSend = status.getBytes();
			try {
				this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), 69);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			try {
				this.sendReceiveSocket.send(this.sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			if (wait.equals("waiting")) {
				byte[] data = new byte[100];
				receivePacket = new DatagramPacket(data, data.length);
				try {
					sendReceiveSocket.receive(receivePacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				} 
				// Receive data from Scheduler
				String toPrint = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
				String[] splitElevatorResponse = (new String(receivePacket.getData(), 0,
						this.receivePacket.getLength())).split(" ");
				if (splitElevatorResponse[1].equals("arrived")) {
					this.setLampsSensors(splitElevatorResponse[2]);
				}
				System.out.println("Floor received: " + toPrint);
				System.out.println("Floor has nothing to send");
				wait = "";
			}
		} else {
			FloorRequest floorRequest = listofRequests.get(0);
			wait = "waiting";
			String floorRequestData = floorRequest.toString();
			byte[] toSend = floorRequestData.getBytes();
			try {
				this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), 69);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			try {
				this.sendReceiveSocket.send(this.sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Floor Sent: " + floorRequestData);

			byte[] data = new byte[100];
			receivePacket = new DatagramPacket(data, data.length);
			try {
				sendReceiveSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			// Receive data from Scheduler
			String floorStatus = "";
			String toPrint = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
			String[] splitElevatorResponse = (new String(receivePacket.getData(), 0, this.receivePacket.getLength()))
					.split(" ");

			String[] elevators = new String[numOfElevators];
			for (int i = 0; i < numOfElevators; i++) {
				String splitResponse = splitElevatorResponse[i];
				// System.out.println(t);
				String[] individualElevator = splitResponse.split("-");
				elevators[Integer.parseInt(individualElevator[0].substring(individualElevator[0].length() - 1))
						- 1] = splitResponse;
				if (individualElevator[1].equals("arrived")) {
					this.setLampsSensors(individualElevator[2]);
					setFloorLampsOff(individualElevator[2]);
					floorStatus = "go";
				}
			}

			String print = "";

			for (String p : elevators) {
				if (print.equals("")) {
					print = p;
				} else {
					print = print + " " + p;
				}
			}

			System.out.println("Floor received: " + print);

			boolean elevatorWait = true;
			for (int i = 0; i < numOfElevators; i++) {
				String splitResponse = splitElevatorResponse[i];
				String[] individualElevator = splitResponse.split("-");
				if (individualElevator[1].equals("waiting")) {
					elevatorWait = false;
				}
			}

			while (elevatorWait) {
				byte[] toSend2 = floorStatus.getBytes();
				try {
					this.sendPacket = new DatagramPacket(toSend2, toSend2.length, InetAddress.getLocalHost(), 69);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.exit(1);
				}

				try {
					this.sendReceiveSocket.send(this.sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				System.out.println("Floor Sent: " + floorStatus);

				data = new byte[100];
				receivePacket = new DatagramPacket(data, data.length);
				try {
					sendReceiveSocket.receive(receivePacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				// Receive data from Scheduler
				floorStatus = "";
				toPrint = new String(receivePacket.getData(), 0, this.receivePacket.getLength());
				splitElevatorResponse = (new String(receivePacket.getData(), 0, this.receivePacket.getLength()))
						.split(" ");

				for (int i = 0; i < splitElevatorResponse.length; i++) {
					String splitResponse = splitElevatorResponse[i];
					String[] individualElevator = splitResponse.split("-");
					elevators[Integer.parseInt(individualElevator[0].substring(individualElevator[0].length() - 1))
							- 1] = splitResponse;
					if (individualElevator[1].equals("arrived")) {
						this.setLampsSensors(individualElevator[2]);
						floorStatus = "go";
					}
				}

				print = "";

				for (String p : elevators) {
					if (print.equals("")) {
						print = p;
					} else {
						print = print + " " + p;
					}
				}

				elevatorWait = true;
				for (int x = 0; x < numOfElevators; x++) {
					String t = splitElevatorResponse[x];
					String[] individualElevator = t.split("-");
					if (individualElevator[1].equals("waiting")) {
						elevatorWait = false;
					}
				}
				System.out.println("Floor received: " + toPrint);
			}
			listofRequests.remove(0);
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

	public static void main(String[] args) {
		FloorSubsystem floor = new FloorSubsystem("File.txt");
		floor.Initialize();
		while (true) {
			floor.TestSend();
		}
	}
}