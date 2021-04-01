package elevator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ElevatorView extends JFrame{
    private Scheduler model;

    public ElevatorView(Scheduler model){
        super("Elevator");
        this.model = model;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        JPanel border = new JPanel(new BorderLayout());
        border.setBorder(new EmptyBorder(2, 3, 2, 3));
        JPanel borderLayout = new JPanel(new GridBagLayout());
        borderLayout.setBorder(new EmptyBorder(250, 250, 250, 250));
        border.add(borderLayout, BorderLayout.CENTER);
        add(border);
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        ElevatorView view = new ElevatorView(scheduler);
    }
}
