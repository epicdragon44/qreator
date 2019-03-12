import javax.swing.*;
import java.awt.*;

public class DataFrame extends JFrame{
    private DataPanel dataPanel;
    public DataFrame() {
        super();
        setSize(new Dimension(1000, 1000));
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        dataPanel = new DataPanel(this);
        add(dataPanel);

        setVisible(true);
    }
}