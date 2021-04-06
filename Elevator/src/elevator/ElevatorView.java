package elevator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ElevatorView extends JFrame {
	private static final long serialVersionUID = 1L;
	private Scheduler model;
	private Container container;

	public static ReadPropertyFile r = new ReadPropertyFile();
	private int columns = r.getNumElevators() + 1;
	private int rows = r.getNumFloors();
	private int width = 500, height = 1000;

//	private JLabel properties[][];
	private JLabel grid[][];
	private Color bgCol;
	private String propTitle[] = { "Elevator Name", "Timestamp", "Status", "Current Floor", "Destination",
			"Direction Lamp" };
	private Image elevatorImage;

	public ElevatorView(Scheduler model) {
		super("Elevator");

		this.model = model;

		// background color
		bgCol = Color.LIGHT_GRAY;

		// Container to hold elevator view properties
		container = getContentPane();
		container.setLayout(new GridLayout(rows, columns));
		container.setBackground(bgCol);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height);

		// Initialize grid
		initializeGrid();

		setVisible(true);
	}

	/**
	 * Initialize each grid
	 * 
	 */
	private void initializeGrid() {
//		properties = new JTextArea[rows][columns];

		// Initializing grid
		grid = new JLabel[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				grid[i][j] = new JLabel();
				grid[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
				grid[i][j].setVisible(true);

				container.add(grid[i][j]);
			}
			grid[i][0].setText("FLOOR " + String.valueOf(rows-i));
			grid[i][0].setFont(new Font("Consolas", Font.BOLD, 20));
		}
		
		elevatorImage = new ImageIcon(this.getClass().getResource("elevator_image.png")).getImage();
		Image elevator = elevatorImage.getScaledInstance(width/columns, height/rows, java.awt.Image.SCALE_SMOOTH);
		
		grid[rows-1][1].setIcon(new ImageIcon(elevator));
		grid[rows-1][2].setIcon(new ImageIcon(elevator));
		grid[rows-1][3].setIcon(new ImageIcon(elevator));
		grid[rows-1][4].setIcon(new ImageIcon(elevator));
		
	}
	
	/**
	 * Placing elevator images in the appropriate spot
	 */
	private void placeElevator(int floorNum, int elevatorNum) {
		elevatorImage = new ImageIcon(this.getClass().getResource("elevator_image.png")).getImage();
		Image elevator = elevatorImage.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		
		grid[floorNum][elevatorNum].setIcon(new ImageIcon(elevator));
	}
	
	/**
	 * Refreshes view with latest updates from the model
	 * 
	 */
	public void refresh() {
		ArrayList<ElevatorData> elevators = model.getElevators();
		ArrayList<String> timestamps = model.getTimeStamps();

		// Clearing all elevator grid squares before updating with latest values
		for (int i = 1; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				grid[i][j].setIcon(null);
				grid[i][j].setOpaque(false);
			}
		}
		
		// Updating each property of the elevator
		for (ElevatorData e : elevators) {
			int num = Integer.parseInt(e.getName().split("Elevator")[1]);
			
			// Highlight elevator's destination if it is set
			if(e.getDestination() != -1) {
				grid[rows - e.getDestination()][num].setBackground(Color.YELLOW);
				grid[rows - e.getDestination()][num].setOpaque(true);
			}
			// Update elevator's current location
			placeElevator(rows - e.getCurrentFloor(), num);
		}
	}

	/**
	 * Setting properties of each elevator
	 * 
	 * @param column
	 * @param e
	 */
//	private void setProperty(int column, ElevatorData e) {
//		properties[0][column].setText("Elevator Name: " + e.getName());
//		properties[1][column].setText("Timestamp: " + e.getTimestamp());
//		if (e.getStatus().contains("doorstuck") || e.getStatus().contains("reset")) {
//			properties[2][column].setBackground(Color.RED);
//		} else if (e.getStatus().contains("between floors")) {
//			properties[2][column].setBackground(Color.RED);
//		} else {
//			properties[2][column].setBackground(bgCol);
//		}
//		properties[2][column].setText("Status: " + e.getStatus() + " at " + String.valueOf(e.getCurrentFloor()));
//		properties[3][column].setText("Current Floor: " + String.valueOf(e.getCurrentFloor()));
//
//		// System.out.println("setting destination");
//		if (e.getDestination() != -1) {
//			// System.out.println("dest is not -1");
//			properties[4][column].setText("Destination: " + e.getDestination());
//		} else {
//			// System.out.println("dest is -1");
//			properties[4][column].setText("Destination: None");
//		}
//
//		if (e.getDirection() == Direction.UP) {
//			properties[5][column].setText("Direction Lamp: UP");
//		} else if (e.getDirection() == Direction.DOWN) {
//			properties[5][column].setText("Direction Lamp: DOWN");
//		} else {
//			properties[5][column].setText("Direction Lamp: STOPPED");
//		}
//	}
	
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		new ElevatorView(scheduler);

	}
}
