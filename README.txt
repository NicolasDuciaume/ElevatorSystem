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

Running application
====================
Java application can be run in any java compiler from the Main.java class.
The FloorSubsystem will read an input string from a file and use the scheduler
to send the string to the ElevatorSystem. The ElevatorSubsystem will also use
the Scheduler to send the string back to the FloorSubsystem. The console will
display messages indicating the FloorSubsystem and ElevatorSubsystem have 
received/sent strings. One must terminate the program manually when finished.
