package elevator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ElevatorView extends JFrame {
	private Scheduler model;
	private Container container;

	public static ReadPropertyFile r = new ReadPropertyFile();
	private int columns = r.getNumElevators();
	private int rows = 5; // number of properties
	private JLabel properties[][];
	String propTitle[] = { "Elevator Name", "Timestamp", "Status", "Current Floor", "Destination" };

	public ElevatorView(Scheduler model) {
		super("Elevator");

		// Container to hold elevator view properties
		container = getContentPane();
		container.setLayout(new GridLayout(rows, columns));
		container.setBackground(Color.PINK);

		this.model = model;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 800);

		// Initialize grid
		initializeGrid();

		setVisible(true);
	}

	private void initializeGrid() {
		properties = new JLabel[rows][columns];
		// Creating sections
		JPanel grid[][] = new JPanel[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				grid[i][j] = new JPanel();
//				grid[i][j].setName("Elevator " + (i + 1));
				grid[i][j].setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.BLACK));
				grid[i][j].setOpaque(false);

				properties[i][j] = new JLabel(propTitle[i]);
				
				properties[i][j].setText(propTitle[i]);
				grid[i][j].add(properties[i][j]);

				container.add(grid[i][j]);
			}

		}
		
	}

	/**
	 * Refreshes view with latest updates from the model
	 * 
	 */
	public void refresh() {
		ArrayList<ElevatorData> elevators = model.getElevators();
		System.out.println("in refresh()");
		for (int i = 0; i < rows; i++) {
			System.out.println("in for loop");
			for (int j = 0; j < columns; j++) {
				switch (i) {
				case 0: // property "Elevator Name"
					System.out.println("case 0");
					properties[i][j].setText(propTitle[i] + ": " + elevators.get(j).getName());
					break;
					
				case 1:// property "Timestamp"
					properties[i][j].setText(propTitle[i] + ": " + elevators.get(j).getTimestamp());
					System.out.println("case 1");
					break;

				case 2:// property "Status"
					properties[i][j].setText(propTitle[i] + ": " + String.valueOf(elevators.get(j).getStatus()));
					System.out.println("case 2");
					break;
					
				case 3:// property "Current Floor"
					properties[i][j].setText(propTitle[i] + ": " + String.valueOf(elevators.get(j).getCurrentFloor()));
					System.out.println("case 3");
					break;//				case 4:// property "Destination"
					// properties[i][j].setText(elevators.get(j));
				}
			}
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
				
		ElevatorView view = new ElevatorView(scheduler);
		        
//		long timerLimit = Long.parseLong("1000000000");
//		long startTime = System.nanoTime();
//		while (Math.abs(startTime - System.nanoTime()) > timerLimit) {
//			System.out.println("timed out - time to refresh");
//			// Refreshing the view
//			view.refresh();
//
//			// Resetting timer
//			startTime = System.nanoTime();
//		}
	}
}
