//package elevatorTests;
//
//import junit.framework.TestCase;
//
//import elevator.*;
//
///**
// * 
// * @author Nazifa Tanzim 101074707
// *
// */
//public class ElevatorSubsystemTest extends TestCase {
//	Scheduler scheduler;
//	private ElevatorSubsystem elevator;
//	private FloorSubsystem floor;
//	private FloorRequest request;
//
//	protected void setUp() throws Exception {
//		super.setUp();
//
//		scheduler = new Scheduler();
//		elevator = new ElevatorSubsystem("Elevator 1");
//		floor = new FloorSubsystem("File_test.txt");
//		request = floor.getListOfRequests().remove(0);
//	}
//
//	/**
//	 * Checks that ElevatorSubsystem goes through every state
//	 * 
//	 * After stateMachine() is called, all attributes should be set according to the
//	 * previous state but the state itself should be updated
//	 */
//	public void testStateMachine() {
//		// ElevatorSubsystem should start in the INITIAL_STATE
//		assertEquals(elevator.getCurrentState().toString(), "INITIAL_STATE");
//		assertEquals(elevator.getMotorState().toString(), "STOPPED");
//		assertEquals(elevator.getDirectionLamp().toString(), "STOPPED");
//		assert (elevator.isDoorOpen()); // Door should be open
//
//		// Sending new request from floor to scheduler for elevator to receive in the next state
//		floor.TestSend();
//		scheduler.sendToElevator();
//
//		// Executing INITIAL_STATE
//		elevator.stateMachine();
//		
//		assertEquals(elevator.getCurrentState().toString(), "STATE_1");
//		assert (elevator.isDoorOpen()); // Door should be closed
//
//		// Executing STATE_1
//		elevator.stateMachine();
//		
//		assertEquals(elevator.getCurrentState().toString(), "STATE_2");
//		assertEquals(elevator.getMotorState().toString(), "UP");
//		assertEquals(elevator.getDirectionLamp().toString(), "STOPPED");
//		assert (!elevator.isDoorOpen()); // Door should be closed
//
//		// Executing STATE_2
//		elevator.stateMachine();
//		
//		assertEquals(elevator.getCurrentState().toString(), "STATE_3");
//		assertEquals(elevator.getMotorState().toString(), "UP");
//		assertEquals(elevator.getDirectionLamp().toString(), "UP");
//		assert (!elevator.isDoorOpen()); // Door should be open
//
//		// Executing STATE_3
//		elevator.stateMachine();
//		
//		assertEquals(elevator.getCurrentState().toString(), "INITIAL_STATE");
//		assertEquals(elevator.getMotorState().toString(), "STOPPED");
//		assertEquals(elevator.getDirectionLamp().toString(), "STOPPED");
//		assert (elevator.isDoorOpen()); // Door should be open
//	}
//}
