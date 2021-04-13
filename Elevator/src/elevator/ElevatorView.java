package elevator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ElevatorView extends JFrame {
	private static final long serialVersionUID = 1L;
	private Scheduler model;
	private Container container;
	
	public static ReadPropertyFile r = new ReadPropertyFile();
	private int columns = r.getNumElevators() + 1;
	private int rows = r.getNumFloors();
	private int width = 500, height = 1000;

	private int currFloor[];
	private JLabel grid[][];
	private Color bgCol;
	private String propTitle[] = { "Elevator Name", "Timestamp", "Status", "Current Floor", "Destination",
			"Direction Lamp" };
	
	private int currentFloor = rows - 1; 
	private String imageName = "";
	private String currImageName = "";
	private Image elevatorImage;
	final JFrame[] elevatorFrames = new JFrame[r.getNumElevators()];
	final JFrame[] floorStatuses = new JFrame[r.getNumFloors()];
	private JTextArea properties[];

	public ElevatorView(Scheduler model) {
		super("Elevator");
		currFloor = new int[columns - 1];
		
		for(int i = 0; i < columns - 1; i++){
			currFloor[i] = rows - 1;
		}
		this.model = model;

		// background color
		bgCol = Color.LIGHT_GRAY;

		// Container to hold elevator view properties
		container = getContentPane();
		container.setLayout(new GridLayout(rows, columns));
		container.setBackground(bgCol);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height);

		initializeElevatorFrames();

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

		for(int i = 0; i < r.getNumElevators(); i++){
			grid[rows-1][i+1].setIcon(new ImageIcon(elevator));
			addElevatorMouseListener(rows-1,i+1);
		}

//		final JFrame[] floorStatuses = new JFrame[r.getNumFloors()];
		for(int i = 0; i < floorStatuses.length; i++){
			floorStatuses[i] = new JFrame();
			addFloorMouseListener(i);
		}
	}
		
	public void addElevatorMouseListener(int floor, int elevator){
		String elevatorTitle = "Elevator " + elevator;
		grid[floor][elevator].addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				if(j[elevator-1].isVisible()) {
//				}else {
//					System.out.println("Mouse Clicked");
//					j[elevator-1].setTitle(elevatorTitle);
//					j[elevator-1].setSize(400, 400);
//					j[elevator-1].setVisible(true);
//				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(elevatorFrames[elevator-1].isVisible()) {
				}else {
					System.out.println("Mouse Pressed");
					elevatorFrames[elevator-1].setTitle(elevatorTitle);
					elevatorFrames[elevator-1].setSize(400, 400);
					elevatorFrames[elevator-1].setVisible(true);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
	}

	/**
	 * Make floor frames clickable
	 * 
	 * @param floorIndex
	 */
	public void addFloorMouseListener(int floorIndex){
		grid[floorIndex][0].addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(floorStatuses[floorIndex].isVisible()) {
				}else {
					System.out.println("Mouse Clicked");
					floorStatuses[floorIndex].setSize(400, 400);
					floorStatuses[floorIndex].setTitle("Floor " + (rows - floorIndex));
					floorStatuses[floorIndex].setVisible(true);
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
	}
	
	public void removeMouseListener(int floor, int elevator){
		MouseListener[] m = grid[floor][elevator].getMouseListeners();
		grid[floor][elevator].removeMouseListener(m[0]);
	}

	public void initializeElevatorFrames(){
		String propTitle[] = {"Timestamp", "Status", "Current Floor", "Destination" };
		properties = new JTextArea[rows];

		for(int i = 0; i < elevatorFrames.length; i++) {
			elevatorFrames[i] = new JFrame();
			elevatorFrames[i].setLayout(new GridLayout(propTitle.length, 1));
			for(int j = 0; j < propTitle.length;j++) {
				properties[j] = new JTextArea(propTitle[j]);
				properties[j].setFont(new Font("Consolas", Font.BOLD, 20));
				properties[j].setForeground(Color.BLACK);
				properties[j].setText(propTitle[j]);
				properties[j].setEditable(false);
				properties[j].setBackground(bgCol);
				elevatorFrames[i].add(properties[j]);
			}
		}
	}

//	HashMap<Integer, String> map = null;
//    try
//    {
//       FileInputStream fis = new FileInputStream("hashmap.ser");
//       ObjectInputStream ois = new ObjectInputStream(fis);
//       map = (HashMap) ois.readObject();
//       ois.close();
//       fis.close();
//    }catch(IOException ioe)
//    {
//       ioe.printStackTrace();
//       return;
//    }catch(ClassNotFoundException c)
//    {
//       System.out.println("Class not found");
//       c.printStackTrace();
//       return;
//    }
//    
	public void updateElevatorFrames(int frameNum,ElevatorData e){
		JFrame j = elevatorFrames[frameNum];
		System.out.println("Components: " + j.getContentPane().getComponents().length);
		Component[] textAreas = j.getContentPane().getComponents();
		JTextArea[] update = new JTextArea[4];
		for(int i = 0; i < textAreas.length; i++) {
			update[i] = (JTextArea)textAreas[i];
		}
		System.out.println("Timestamp: " + e.getTimestamp());
		update[0].setText("Timestamp: " + e.getTimestamp());
		update[1].setText("Status: " + e.getStatus() + " at " + String.valueOf(e.getCurrentFloor()));
		update[2].setText("Current Floor: " + String.valueOf(e.getCurrentFloor()));

//		properties[0].setText("Timestamp: " + e.getTimestamp());
//		properties[1].setText("Status: " + e.getStatus() + " at " + String.valueOf(e.getCurrentFloor()));
//		properties[2].setText("Current Floor: " + String.valueOf(e.getCurrentFloor()));

		System.out.println("setting destination");
		if(e.getDestination() != -1) {
			System.out.println("dest is not -1");
			update[3].setText("Destination: " + e.getDestination());
//			properties[3].setText("Destination: " + e.getDestination());
		}else {
			System.out.println("dest is -1");
			update[3].setText("Destination: None");
//			properties[3].setText("Destination: None");
		}
//		for(int i = 0; i < properties.length; i++){
//			elevatorFrames[frameNum].add(properties[i]);
//		}
	}
	
	/**
	 * Placing elevator images in the appropriate spot
	 */
	private void placeElevator(int floorNum, int elevatorNum, String imageName) {
		elevatorImage = new ImageIcon(this.getClass().getResource(imageName)).getImage();
		Image elevator = elevatorImage.getScaledInstance(width/columns, height/rows, java.awt.Image.SCALE_SMOOTH);
		
		grid[floorNum][elevatorNum].setIcon(new ImageIcon(elevator));
	}

	/**
	 * Refreshes view with latest updates from the model
	 * 
	 */
	public void refresh() {
		ArrayList<ElevatorData> elevators = model.getElevators();
		ArrayList<String> timestamps = model.getTimeStamps();
		
		// Updating each property of the elevator
		for (ElevatorData e : elevators) {
			int num = Integer.parseInt(e.getName().split("Elevator")[1]);
			e.setTimestamp(timestamps.get(num - 1));
			
			// Highlight elevator's destination if it is set
			if(e.getDestination() != -1) {
				grid[rows - e.getDestination()][num].setBackground(Color.YELLOW);
				grid[rows - e.getDestination()][num].setOpaque(true);
			}
			
			if(e.getStatus().contains("doors")) {
				imageName = "elevator_doors_stuck.png";
			}else if(e.getStatus().contains("stuck between floors")) {
				imageName = "elevator_error.png";
			}else if(e.getStatus().contains("open") || e.getStatus().contains("clos")){
				imageName = "elevator_door_open.png";
			}else {
				imageName = "elevator_image.png";
			}
			
			System.out.println(e.getName() + ": " + e.getStatus());
			
			if(e.getStatus().contains("arr") || e.getStatus().contains("wait")){
				System.out.println();
				grid[currFloor[num-1]][num].setOpaque(false);
			}
				
			if(currFloor[num-1] != rows - e.getCurrentFloor() || currImageName != imageName)
			{			
				grid[currFloor[num-1]][num].setIcon(null);
				removeMouseListener(currFloor[num-1],num);
				placeElevator(rows - e.getCurrentFloor(), num, imageName);

				addElevatorMouseListener(rows - e.getCurrentFloor(),num);
				updateElevatorFrames(num-1,e);

				currFloor[num-1] = rows - e.getCurrentFloor();
				currImageName = imageName;
			}
		}
	}

	/**
	 * Setting properties of each elevator
	 *
	 */

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

//		new ElevatorView(scheduler);

	}
}
	