import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Window extends JFrame {

    @Override
    protected void frameInit() {
        super.frameInit();

        final Dimension screenSize = getToolkit().getScreenSize();

        final int width = screenSize.width / 3, height = screenSize.height / 3;
        setSize(width, width);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - width / 2, centerY - height / 2);

        GridLayout layout = new GridLayout(8,8);
        setLayout(layout);

        for (int i = 7; i >= 0; i--)
            for (int j = 0; j < 8; ++j) {
                final int i1 = i,j2 =j;
            Move.buttons[j][i] = new JButton();
            if ((i+j) % 2 == 0)
                Move.buttons[j][i].setAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                       final int x = i1,y = j2;
                       Move.change(x,y);
                    }
                });
                if((i+j)%2==1) {
                    ImageIcon icon = new ImageIcon("Icons/White_cell.png");
                    Move.buttons[j][i].setIcon(icon);
                }

            add(Move.buttons[j][i]);
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new Window().setVisible(true);
    }
}
