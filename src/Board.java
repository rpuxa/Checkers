import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Objects;

public class Board extends JFrame {

    @Override
    protected void frameInit() {
        super.frameInit();

        final Dimension screenSize = getToolkit().getScreenSize();

        setResizable(false);

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
        new Editor().setVisible(true);
    }
}

class Editor extends JFrame {

    String choice = "Icons/Black_cell.png";


    @Override
    protected void frameInit() {
        super.frameInit();

        final Dimension screenSize = getToolkit().getScreenSize();

        setResizable(false);

        final int width = screenSize.width / 3, height = screenSize.height / 3;
        setSize(width, width+80);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - width / 2+100, centerY - height / 2+100);

        GridLayout layout = new GridLayout(9,8);
        setLayout(layout);

        choice = "Icons/Black_cell.png";

        for (int i = 7; i >= 0; i--)
            for (int j = 0; j < 8; ++j) {
                final int i1 = i,j2 =j;
                Edit.buttons[i][j] = new JButton();
                if ((i+j) % 2 == 0)
                    Edit.buttons[i][j].setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final int x = i1,y = j2;
                            ImageIcon icon = new ImageIcon(choice);
                            Edit.buttons[x][y].setIcon(icon);
                        }
                    });
                add(Edit.buttons[i][j]);
                if ((i+j) % 2 == 0){
                    ImageIcon icon = new ImageIcon("Icons/Black_cell.png");
                    Edit.buttons[i][j].setIcon(icon);
                }
                else {
                    ImageIcon icon = new ImageIcon("Icons/White_cell.png");
                    Edit.buttons[i][j].setIcon(icon);
                }

            }
            String[] editorButtons = {"Icons/Black_cell.png","Icons/Piece_white.png","Icons/Piece_black2.png","Icons/Piece_white_Q.png","Icons/Piece_black_Q_.png"};
        for (int i = 0; i <= 4; i++) {
            final int index = i;
            JButton button = new JButton();
            button.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    choice = editorButtons[index];
                }
            });
            ImageIcon icon = new ImageIcon(editorButtons[index]);
            button.setIcon(icon);
            add(button);
        }
        JButton clear = new JButton("Clear");
        clear.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++)
                        if ((i+j) % 2 == 0) {
                        ImageIcon icon = new ImageIcon("Icons/Black_cell.png");
                        Edit.buttons[i][j].setIcon(icon);
                    }
            }
        });
        clear.setText("Clear");
        add(clear);

        JButton accept = new JButton("Accept");
        accept.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Piece[] pieces = new Piece[24];
                Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
                ArrayList<Integer> livePieces = new ArrayList<>();
                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++)
                        if ((i+j) % 2 == 0){
                            Icon icon = Edit.buttons[j][i].getIcon();
                            String name = icon.toString();
                            if (Objects.equals(name, "Icons/Piece_white.png")){
                                pos[i][j] = livePieces.size();
                                pieces[livePieces.size()] = new Piece(true,false);
                                livePieces.add(livePieces.size());
                            }
                            if (Objects.equals(name, "Icons/Piece_black2.png")){
                                pos[i][j] = livePieces.size();
                                pieces[livePieces.size()] = new Piece(false,false);
                                livePieces.add(livePieces.size());
                            }
                            if (Objects.equals(name, "Icons/Piece_white_Q.png")){
                                pos[i][j] = livePieces.size();
                                pieces[livePieces.size()] = new Piece(true,true);
                                livePieces.add(livePieces.size());
                            }
                            if (Objects.equals(name, "Icons/Piece_black_Q_.png")){
                                pos[i][j] = livePieces.size();
                                pieces[livePieces.size()] = new Piece(false,true);
                                livePieces.add(livePieces.size());
                            }
                        }
                Position position = new Position(pieces, pos, livePieces, new ArrayList<>(), false, null);
                Game.position = new Position(position);
                Move.replacePosition(new Position(position));
            }
        });
        accept.setText("Accept");
        add(accept);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Edit.start = true;
            }
        });
    }

}

class Edit{
    static boolean start = false;
    static JButton[][] buttons = new JButton[8][8];
}
