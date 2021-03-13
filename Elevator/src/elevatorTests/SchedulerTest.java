package elevatorTests;

import elevator.ElevatorSubsystem;
import elevator.FloorRequest;
import elevator.FloorSubsystem;
import elevator.Scheduler;
import junit.framework.TestCase;

/**
 * 
 * @author Nazifa Tanzim 101074707
 *
 */
public class SchedulerTest extends TestCase {
	private Scheduler scheduler;
	private FloorSubsystem floor;
	private FloorRequest request;
	private int floorToVisit;

	protected void setUp() throws Exception {
		super.setUp();

		scheduler = new Scheduler();
		//floor = new FloorSubsystem("File.txt", scheduler);
		request = floor.getListOfRequests().remove(0);
		floorToVisit = 2;
	}

	/**
	 * Test state machine that is responsible for sending data to floor and elevator
	 */
	public void testSendStateMachine() {
		scheduler.receiveFromFloor("", request);
		
		// receive state machine should start in STATE_2 i.e. sending to elevator first
		assertEquals(scheduler.getCurrentState1().toString(), "STATE_1");
		assertEquals(scheduler.getDataFromFloor(), "ok");
		
		// execute STATE_2 i.e. send to elevator
		scheduler.sendStateMachine();
		
		assertEquals(scheduler.getCurrentState1().toString(), "STATE_2");
		assertEquals(scheduler.getDataFromFloor(), "");
	}

	/**
	 * Test state machine that is responsible for receiving data to floor and elevator
	 */
	public void testReceiveStateMachine() {
		// receive state machine should start in STATE_2 i.e. receiving from floor first
		assertEquals(scheduler.getCurrentState2().toString(), "STATE_2");
		
		scheduler.receiveStateMachine(request, ""); //Executing STATE_2
		
		// State should be set to STATE_! after completing STATE_2
		assertEquals(scheduler.getCurrentState2().toString(), "STATE_1");
		
	}
	
	/**
	 * Testing receiving from floor
	 */
	public void testReceiveFromFloor() {
		// Receive request from floor
		scheduler.receiveFromFloor("", request);
		
		assertEquals(scheduler.getFloorToVisit(), this.floorToVisit);
		assertEquals(scheduler.getDataFromFloor(), "ok");
	}

	/**
	 * Testing receiving from elevator
	 */
	public void testReceiveFromElevator() {
		String data = "arrived " + request.getFloorDestination();
		// Getting request from floor and sending it to elevator
		scheduler.receiveFromFloor("", request);
		
		assertEquals(scheduler.getFloorToVisit(), this.floorToVisit);
		assertEquals(scheduler.getDataFromFloor(), "ok");
		
		// Send request to elevator
		scheduler.sendToElevator();
		// Receiving the elevator's response to the request
		scheduler.receiveFromElevator(data);
		
		assertEquals(scheduler.getCurrentFloor(), request.getFloorDestination());
		assertEquals(scheduler.getDataFromFloor(), "");
		assert(scheduler.getDataFromElevator().equals(data));
		assert(!scheduler.isEmptyElevator());
	}
	
	/**
	 * Testing sending to floor
	 */
	public void testSendToFloor() {
		String data = "arrived " + request.getFloorDestination();
		
		// Sending request to elevator and receiving the response to be sent to the floor
		scheduler.receiveFromFloor("", request);
		scheduler.sendToElevator();	
		scheduler.receiveFromElevator(data);
		
		assertEquals(scheduler.getDataFromElevator(), data);
		assert(!scheduler.isEmptyFloor());
		
		scheduler.sendToFloor(); // sending response to floor
		
		assertEquals(scheduler.getDataFromElevator(), "");
		assert(scheduler.isEmptyFloor()); // floor is empty
		
	}
	
	/**
	 * Testing sending to elevator
	 */
	public void testSendToElevator() {
		// Receiving request from floor to be sent to the elevator
		scheduler.receiveFromFloor("", request);
		
		assertEquals(scheduler.getDataFromFloor(), "ok");
		assert(scheduler.isEmptyElevator());
		
		// Sending response to elevator
		scheduler.sendToElevator();
		
		assertEquals(scheduler.getDataFromFloor(),"");
		assert(scheduler.isEmptyElevator()); // elevator is not empty
	}

}
