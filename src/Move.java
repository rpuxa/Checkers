import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

class Move {
    static Point from;
    static Point to;
    static Position position;
    static JButton[][] buttons = new JButton[8][8];
    static boolean block = false;
    static Thread timer;

    static void change(int y, int x) {
        if (!block)
        if (from == null)
            from = new Point(x, y);
        else {
            if (Game.MakeLegalMove(new Position(position), from.x, from.y, x, y)) {
                to = new Point(x, y);
                block =true;
            }
            else
                from = new Point(x, y);
        }

    }

    static void replacePosition(Position position) {
        //position.reverce();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if ((i + j) % 2 == 0)
                    if (position.pos[i][j] != null) {
                        if (position.pieces[position.pos[i][j]].isWhite) {
                            if (position.pieces[position.pos[i][j]].isQueen) {
                                ImageIcon icon = new ImageIcon("Icons/Piece_white_Q.png");
                                buttons[i][j].setIcon(icon);
                            } else {
                                ImageIcon icon = new ImageIcon("Icons/Piece_white.png");
                                buttons[i][j].setIcon(icon);
                            }

                        } else {
                            if (position.pieces[position.pos[i][j]].isQueen) {
                                ImageIcon icon = new ImageIcon("Icons/Piece_black_Q_.png");
                                buttons[i][j].setIcon(icon);
                            } else {
                                ImageIcon icon = new ImageIcon("Icons/Piece_black2.png");
                                buttons[i][j].setIcon(icon);
                            }

                        }
                    } else {
                        ImageIcon icon = new ImageIcon("Icons/Black_cell.png");
                        buttons[i][j].setIcon(icon);
                    }
    }

    static void sounds(String sound){
        try {
            File soundFile = new File(sound);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setFramePosition(0);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
        }
    }
}
