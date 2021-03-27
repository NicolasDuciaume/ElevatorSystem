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

- FloorSubsystem, ElevatorSubsystem, and Scheduler run concurrently
  You start the Scheduler, then the FloorSubsystem and finaly the ElevatorSubystem which then all communicate
  to one another via UDP
  
- Current version of application allows Scheduler to act as communication channel from 
  FloorSubsystem thread to the ElevatorSubsystem thread and vice versa. Scheduler also is in
  charge of deciding which command to send to the elevator dependent on what is needed at the time
  
- No direct modification is needed for the code to run. Only changes to make for more elevators or floors are to
  be made inside the configuration file and floor request to be added to File.txt
----------------------------------------------------------------------------------------

Files
===================
FloorSubsystem.java     - Floor Class
ElevatorSubsystem.java  - Elevator Class
ElevatorData.java       - Holds elevator data for scheduler
Main.java               - Main class
Sheduler.java           - Scheduler
SchedulerTest.java      - Test suite
Direction.java          - Enum for UP,DOWN and STOPPED
FloorRequest.java       - Data Structure Class
UML Class Diagram.png   - UML Diagram
Sequence Diagram.png    - Sequence Diagram
State Diagram.png       - State Diagram
File.txt                - File to be read by FloorSubsystem.java
ReadPropertyFile.java   - Reads the configurations for the program to run with
config.properties       - Configuration file for the program


Running application
====================
Java application can be run in any java compiler by running first the Scheduler, then the
FloorSubstystem and finally the ElevatorSubsystem.
At the start of the programs the Scheduler listens to UDP requests from the FloorSubsystem and 
the ElevatorSubsystem(s). All program have an initialize method which for the FloorSubsystem is sending
a UDP message for the scheduler to catch its address and port then send back the amount of elevators to 
the FloorSubsystem. As for the ElevatorSubsystem(s) then send their UDP request to create the ElevatorData files for each elevators 
so that the scheduler can keep track of all of them and their address/ports. The process then launches as back in forth the State 
machines allow messages to be sent and received from one another through UDP while the scheduler catches the new request and 
formulates the best paths for the elevators to take dependent on how many elevators their are. Once all 
Request have been completed the FloorSubsystem stops and wait for new request while the elevator wait to be given them.

BreakDown
====================
Nicolas Duciaume: README, Timing, Error addition
Jameel Alidina: Floor lamps, Elevator Lamp, Error addition
Tooba Sheikh: Comments, Timing, Error addition
Chris D'Silva: Creating UML and Sequence Diagrams, Floor lamps, Elevator Lamp
Nazifa Tanzim:  Timing, Error addition, Creating Tests
