import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class SFrame extends JFrame {

    SFrame() {
;
        this.setTitle("GymGate");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // this sets the frame to full screen.
        //this.setResizable(false); // this resizing is disabled for the time being. Remove this to re-enable it.
        this.setVisible(true);

        ImageIcon image = new ImageIcon("logo.png");
        this.setIconImage(image.getImage());
        //frame.getContentPane().setBackground(new Color(0, 0, 0)); Use this to set an RGB background color.
    }
}
