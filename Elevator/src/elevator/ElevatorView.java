package elevator;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ElevatorView extends JFrame {
	private static final long serialVersionUID = 1L;
	private Scheduler model;
	private Container container;

	public static ReadPropertyFile r = new ReadPropertyFile();
	private int columns = r.getNumElevators();
	private int rows = 6; // number of properties
	private JTextArea properties[][];
	private Color bgCol;
	private String propTitle[] = { "Elevator Name", "Timestamp", "Status", "Current Floor", "Destination", "Direction Lamp" };

	public ElevatorView(Scheduler model) {
		super("Elevator");
		
		this.model = model;
		
		// background color
		bgCol = Color.DARK_GRAY;
		
		// Container to hold elevator view properties
		container = getContentPane();
		container.setLayout(new GridLayout(rows, columns));
		container.setBackground(bgCol);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1920, 1080);

		// Initialize grid
		initializeGrid();

		setVisible(true);
	}

	/**
	 * Initialize each grid
	 * 
	 */
	private void initializeGrid() {
		properties = new JTextArea[rows][columns];
		
		// Creating sections
		JPanel grid[][] = new JPanel[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				grid[i][j] = new JPanel();
				
				grid[i][j].setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.WHITE));
				grid[i][j].setOpaque(false);

				properties[i][j] = new JTextArea(propTitle[i]);
				properties[i][j].setFont(new Font("Consolas", Font.BOLD, 20));
				properties[i][j].setForeground(Color.WHITE);
				properties[i][j].setText(propTitle[i]);
				properties[i][j].setBackground(bgCol);

				grid[i][j].add(properties[i][j]);

				container.add(grid[i][j]);
			}

		}
		
	}
	
	/**
	 * Setting properties of each elevator
	 * 
	 * @param column
	 * @param e
	 */
	private void setProperty(int column, ElevatorData e) {		
			properties[0][column].setText("Elevator Name: " + e.getName());
			properties[1][column].setText("Timestamp: " + e.getTimestamp());
			properties[2][column].setText("Status: " + e.getStatus() + " at " + String.valueOf(e.getCurrentFloor()));
			properties[3][column].setText("Current Floor: " + String.valueOf(e.getCurrentFloor()));
			
			//System.out.println("setting destination");
			if(e.getDestination() != -1) {
				//System.out.println("dest is not -1");
				properties[4][column].setText("Destination: " + e.getDestination());
			}else {
				//System.out.println("dest is -1");
				properties[4][column].setText("Destination: None");
			}

			if(e.getDirection() == Direction.UP){
				properties[5][column].setText("Direction Lamp: UP");
			}else if(e.getDirection() == Direction.DOWN){
				properties[5][column].setText("Direction Lamp: DOWN");
			}else{
				properties[5][column].setText("Direction Lamp: STOPPED");
			}
	}

	/**
	 * Refreshes view with latest updates from the model
	 * 
	 */
	public void refresh() {
		ArrayList<ElevatorData> elevators = model.getElevators();
		ArrayList<String> timestamps = model.getTimeStamps();
		
		// Updating each property of the elevator
		for(ElevatorData e : elevators) {
			String num = e.getName().split("Elevator")[1];
			e.setTimestamp(timestamps.get(Integer.parseInt(num) - 1));
			//System.out.println(num);
			setProperty((Integer.parseInt(num) - 1), e);
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		new ElevatorView(scheduler);

	}
}
