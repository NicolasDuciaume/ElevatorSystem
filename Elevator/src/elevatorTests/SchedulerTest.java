//package elevatorTests;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//import elevator.*;
//import junit.framework.TestCase;
//
///**
// * 
// * @author Nazifa Tanzim 101074707
// *
// */
//public class SchedulerTest extends TestCase {
//	private Scheduler scheduler;
//	private FloorSubsystem floor;
//	private FloorRequest floorRequest;
//	private byte[] data;
//	private int floorToVisit;
//	private DatagramSocket socket;
//
//	protected void setUp() throws Exception {
//		super.setUp();
//
//		scheduler = new Scheduler();
//		scheduler.InitializePort(1);
//
//		floor = new FloorSubsystem("File_test.txt");
//		floor.Initialize();
//
//		floorRequest = floor.getListOfRequests().remove(0);
//		data = floorRequest.toString().getBytes();
//		floorToVisit = 2;
//
//		socket = new DatagramSocket(50);
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		scheduler.closeSockets();
//		socket.close();
//	}
//
//	/**
//	 * Test state machine that is responsible for receiving data to floor and
//	 * elevator
//	 */
////	public void testReceiveStateMachine() {
////		// receive state machine should start in STATE_2 i.e. receiving from floor first
////		assertEquals(scheduler.getCurrentState2().toString(), "STATE_2");
////		
////		scheduler.receiveStateMachine(data); //Executing STATE_2
////		
////		// State should be set to STATE_! after completing STATE_2
////		assertEquals(scheduler.getCurrentState2().toString(), "STATE_1");
////		
////	}
//
//	/**
//	 * Testing receiving from floor
//	 * 
//	 * @throws SocketException
//	 */
//	public void testReceiveFromFloor() throws SocketException {
//		// Send a message to scheduler as the floor
//		DatagramPacket sendPacket;
//		try {
//			byte[] s = "go".getBytes();
//			sendPacket = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 69);
//
//			socket.send(sendPacket);
//
//			scheduler.getFloorSocket().send(sendPacket);
//			System.out.println("packet sent to port 50");
//			scheduler.receiveFromFloor(sendPacket.getData());
//			System.out.println("packet sent to port 69");
//
//			DatagramPacket p = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 50);
//
//			socket.receive(p);
//
//			assertEquals(scheduler.getReceivePacket(), p);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Testing receiving from elevator
//	 */
//	public void testReceiveFromElevator() {
//		// Send a message to scheduler as the elevator
//		DatagramPacket sendPacket;
//		try {
//			byte[] s = "waiting".getBytes();
//			sendPacket = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 420);
//
//			socket.send(sendPacket);
//
//			scheduler.getElevatorSocket().send(sendPacket);
//			System.out.println("packet sent to port 50");
//			scheduler.receiveFromElevator(sendPacket.getData());
//			System.out.println("packet sent to port 69");
//
//			DatagramPacket p = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 50);
//
//			socket.receive(p);
//
//			assertEquals(scheduler.getReceivePacket(), p);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Testing sending to floor
//	 */
//	public void testSendToFloor() {
//		// Send a message to floor
//		DatagramPacket sendPacket;
//		try {
//			byte[] s = "waiting".getBytes();
//			sendPacket = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 69);
//
//			socket.send(sendPacket);
//
//			scheduler.getElevatorSocket().send(sendPacket);
//			System.out.println("packet sent to port 50");
//			scheduler.sendToFloor();
//			System.out.println("packet sent to port 69");
//
//			DatagramPacket p = new DatagramPacket(s, s.length);
//			DatagramPacket p2 = new DatagramPacket(s, s.length);
//
//			socket.receive(p);
//			scheduler.getFloorSocket().receive(p2);
//			assertEquals(p2, p);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Testing sending to elevator
//	 */
//	public void testSendToElevator() {
//		// Send a message to scheduler as the elevator
//		DatagramPacket sendPacket;
//		try {
//			byte[] s = "waiting".getBytes();
//			sendPacket = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 420);
//
//			socket.send(sendPacket);
//
//			scheduler.getElevatorSocket().send(sendPacket);
//			System.out.println("packet sent to port 50");
//			scheduler.receiveFromFloor(sendPacket.getData());
//			System.out.println("packet sent to port 69");
//
//			DatagramPacket p = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 50);
//			DatagramPacket p2 = new DatagramPacket(s, s.length, InetAddress.getLocalHost(), 50);
//
//			// Receiving both packets and comparing to make sure they're the same/sent properly
//			socket.receive(p);
//			scheduler.getElevatorSocket().receive(p2);
//			assertEquals(p2, p);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
