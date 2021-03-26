package elevator;

import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
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
	private Scheduler scheduler;
	private ArrayList<FloorRequest> listofRequests;
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendReceiveSocket;
	private String wait = "waiting";
	private int lastRequest;
	private int numOfElevators = 0;
	public int requestCount = 0;

	private static ReadPropertyFile r = new ReadPropertyFile();

	/**
	 * Instantiates all the variables and tries to find and read the input file
	 *
	 * @param FileLocation String that indicates the name and path of the input file
	 *
	 */
	public FloorSubsystem(String FileLocation) {
		// this.scheduler = scheduler;
		this.listofRequests = new ArrayList<FloorRequest>();
		this.addFloorRequest(FileLocation);
		FloorRequest floorRequest = listofRequests.get(listofRequests.size() - 1);
		this.lastRequest = floorRequest.getFloorDestination();
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
			this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getFloorPort());
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
		Timestamp requestTime = new Timestamp(System.currentTimeMillis());
		long travelTime = 1L;
		long doorTime = 1L;

		try {
			File myObj = new File(fileLocation);
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

				FloorRequest request = new FloorRequest(requestArray[0], travelTime, doorTime,
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
				this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getFloorPort());
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
				System.out.println("Floor has nothing to send");
				wait = "";
				while(true){
				testing();}
			}
		} else {
			FloorRequest floorRequest = listofRequests.get(0);
			wait = "waiting";
			String floorRequestData = floorRequest.toString();
			byte[] toSend = floorRequestData.getBytes();
			try {
				this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getFloorPort());
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
			/*for(String s : elevators){
				System.out.println(s);
			}*/
			for (int i = 0; i < numOfElevators; i++) {
				String splitResponse = splitElevatorResponse[i];
				String[] individualElevator = splitResponse.split("-");
				elevators[Integer.parseInt(individualElevator[0].substring(individualElevator[0].length() - 1))
						- 1] = splitResponse;
				if (individualElevator[1].equals("arrived")) {
					this.setLampsSensors(individualElevator[2]);
					floorStatus = "go";
				}
				if (individualElevator[1].equals("moving")) {
					floorStatus = "go";
				}
				if (individualElevator[1].equals("door_closing")) {
					floorStatus = "go";
				}
				if (individualElevator[1].equals("door_closed")) {
					floorStatus = "go";
				}
				if (individualElevator[1].equals("door_opening")) {
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
					this.sendPacket = new DatagramPacket(toSend2, toSend2.length, InetAddress.getLocalHost(), r.getFloorPort());
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
					if (individualElevator[1].equals("moving")) {
						floorStatus = "go";
					}
					if (individualElevator[1].equals("door_closing")) {
						floorStatus = "go";
					}
					if (individualElevator[1].equals("door_closed")) {
						floorStatus = "go";
					}
					if (individualElevator[1].equals("door_opening")) {
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
				System.out.println("Floor received: " + print);
			}
			listofRequests.remove(0);
		}
	}

	public void testing(){
		wait = "go";
		String floorRequestData = wait.toString();
		byte[] toSend = floorRequestData.getBytes();
		try {
			this.sendPacket = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), r.getFloorPort());
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
			String[] individualElevator = splitResponse.split("-");
			elevators[Integer.parseInt(individualElevator[0].substring(individualElevator[0].length() - 1))
					- 1] = splitResponse;
			if (individualElevator[1].equals("arrived")) {
				this.setLampsSensors(individualElevator[2]);
				floorStatus = "go";
			}
			if (individualElevator[1].equals("moving")) {
				floorStatus = "go";
			}
			if (individualElevator[1].equals("door_closing")) {
				floorStatus = "go";
			}
			if (individualElevator[1].equals("door_closed")) {
				floorStatus = "go";
			}
			if (individualElevator[1].equals("door_opening")) {
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
				this.sendPacket = new DatagramPacket(toSend2, toSend2.length, InetAddress.getLocalHost(), r.getFloorPort());
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
				if (individualElevator[1].equals("moving")) {
					floorStatus = "go";
				}
				if (individualElevator[1].equals("door_closing")) {
					floorStatus = "go";
				}
				if (individualElevator[1].equals("door_closed")) {
					floorStatus = "go";
				}
				if (individualElevator[1].equals("door_opening")) {
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
			System.out.println("Floor received: " + print);
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