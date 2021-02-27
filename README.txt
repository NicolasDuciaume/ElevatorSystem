SYSC 3303 project (Group 9) version 1.0 02/05/2021

Contributors:

- Nicolas Duciaume   101124713
- Tooba Sheikh       101028915
- Chris D'Silva      101067295
- Nazifa Tanzim      101074707
- Jameel Alidina     101077040


GENERAL USAGE NOTES
====================

- Elevator project is written in Java making use of the Runnable Interface

- FloorSubsystem, ElevatorSubsystem, and Scheduler thread are run concurrently
  where the Floor and Elevator threads are clients of the scheduler
  
- Current version of application allows Scheduler to act as communication channel from 
  FloorSubsystem thread to the ElevatorSubsystem thread and vice versa
----------------------------------------------------------------------------------------

Files
===================
FloorSubsystem.java     - Floor Class
ElevatorSubsystem.java  - Elevator Class
Main.java               - Main class
Sheduler.java           - Scheduler
SchedulerTest.java      - Test suite
Direction.java          - Enum for UP,DOWN and STOPPED
FloorRequest.java       - Data Structure Class
UML Class Diagram.png   - UML Diagram
Sequence Diagram.png    - Sequence Diagram
State Diagram.png       - State Diagram
File.txt                - File to be read by FloorSubsystem.java



Running application
====================
Java application can be run in any java compiler from the Main.java class.
The floorSubsystem class is given the input file name and creates a FloorRequest with 
the information contained within that file. The FloorSubsystem then lauches the State machine
responsable for reading for class' while the elevatorSubsystem State machine launches the State 
machine for reading inputs. These two state machines then switch between their two options to either 
read from the ElevatorSubsystem/FloorSubsystem or to write to them. The State machine within
The Elevator Subsystem Uses the inputs it receives to control the lights and doors though its own 
State machine. The State Diagrams can be found included here.

BreakDown
====================
Nicolas Duciaume: README, SchedulerSubsystem statemachine, Error correction
Jameel Alidina: Direction, FloorRequest, SchedulerSubsystem statemachine
Tooba Sheikh: SchedulerSubsystem statemachine, Error correction
Chris D'Silva: Creating UML and Sequence Diagrams, ElevatorSubsystem statemachine
Nazifa Tanzim: Creating ElevatorSubsystem statemachine, Creating Tests
