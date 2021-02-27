package elevator;

import java.util.PriorityQueue;
import java.util.Queue;

public class ElevatorSubsystem implements Runnable {
    private String location;
    private ElevatorSubsystem.ElevatorStates currentState;
    private Direction motorState;
    private Queue<FloorRequest> floorRequests;
    private boolean doorOpen;
    private FloorRequest data;
    private int[] elevatorButtons = new int[8];
    private boolean[] elevatorLamps = new boolean[8];
    private Direction directionLamp;
    private Scheduler scheduler;

    public ElevatorSubsystem(Scheduler scheduler) {
        this.scheduler = scheduler;
        this.currentState = ElevatorSubsystem.ElevatorStates.INITIAL_STATE;
        this.motorState = Direction.STOPPED;
        data = new FloorRequest();
        this.doorOpen = true;
        this.floorRequests = new PriorityQueue();
        boolean[] var5;
        int var4 = (var5 = this.elevatorLamps).length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Boolean currentLamp = var5[var3];
            currentLamp = false;
        }

        for(int i = 0; i < this.elevatorButtons.length; ++i) {
            this.elevatorButtons[i] = i + 1;
        }

        this.directionLamp = Direction.STOPPED;
    }

    public void parseData(FloorRequest request, String type) {
        if (type.equals("Direction")) {
            if (request.getDirection() == Direction.UP) {
                this.motorState = Direction.UP;
            } else if (request.getDirection() == Direction.DOWN) {
                this.motorState = Direction.DOWN;
            }
        }

        if (type.equals("Floor Number")) {
            this.elevatorLamps[request.getFloorRequestOrigin() - 1] = true;
        }

    }

    public void stateMachine() {
        switch (currentState) {
            case INITIAL_STATE:
            	data = (FloorRequest) scheduler.stateMachine("elevator",null ,""); //Receive data from Scheduler
//                data = scheduler.sendToElevator(); //Receive data from Scheduler
                System.out.println("Elevator Received: "+ data);
                currentState = ElevatorStates.STATE_1;
                break;
            case STATE_1:
                //TODO: Timer Event needed for future Iterations. Door open time,
                //movement time between floors.
                doorOpen = false;
                parseData(data,"Direction");
                currentState = ElevatorStates.STATE_2;
                break;
            case STATE_2:
                //Turn on lamps
                directionLamp = motorState;
                parseData(data,"Floor Number");
                //Listen to request implementation

                currentState = ElevatorStates.STATE_3;
                break;
            case STATE_3:
                //TODO: Timer Event needed for future Iterations. Door open time
                doorOpen = true;
                motorState = Direction.STOPPED;
                directionLamp = motorState;
                String msg = "arrived " + data.getFloorDestination();
                scheduler.stateMachine("elevator", null, msg); // Send data from elevator to Scheduler
//                scheduler.receiveFromElevator(msg); // Send data from elevator to Scheduler
                System.out.println("Elevator Sent: " + msg);
                currentState = ElevatorStates.INITIAL_STATE;
                break;
        }
    }

    public void run() {
        while(true) {
            this.stateMachine();

            try {
                Thread.sleep(1500L);
            } catch (InterruptedException var2) {
            }
        }
    }

    private static enum ElevatorStates {
        INITIAL_STATE,
        STATE_1,
        STATE_2,
        STATE_3;

        private ElevatorStates() {
        }
    }
}
