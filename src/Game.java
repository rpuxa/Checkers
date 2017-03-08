import javafx.geometry.Pos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringJoiner;

public class Game {

    public static Position position;

    public static void main(String[] agrs){
        ArrayList<Checker> checkersWhite = new ArrayList<>();
        ArrayList<Checker> checkersBlack = new ArrayList<>();
        for (char x = 'a';x<='g';x+=2)
            for (int y = 1; y<=3;y++)
                if (y!=2)
                    checkersWhite.add(new Checker(x,y,false,false));
                else
                    checkersWhite.add(new Checker((char)(x+1),y,false,false));
        for (char x = 'b';x<='h';x+=2)
            for (int y = 6; y<=8;y++)
                if (y!=7)
                    checkersBlack.add(new Checker(x,y,false,false));
                else
                    checkersBlack.add(new Checker((char)(x-1),y,false,false));
        position = new Position(checkersWhite,checkersBlack,false);
      run();
    }

    public static void run(){
        final Scanner scanner = new Scanner(System.in);
        while (true){
            String playerMove = scanner.nextLine();
            char[] playerMoveChars=playerMove.toCharArray();
                position.replace(playerMoveChars[0],(int) playerMoveChars[1]-'0',playerMoveChars[3],(int) playerMoveChars[4] - '0');
                break;
        }
        System.out.println("Нажмите Enter для завершения игры.");
        try {
            System.in.read();
        } catch (IOException cannotHappen) { }
    }

    }

class Checker {
    char x;
    int y;
    boolean queen;
    boolean move;
    Checker(char x, int y, boolean queen, boolean move) {
        this.x = x;
        this.y = y;
        this.queen = queen;
        this.move = move;
    }


}
class Position {
    ArrayList<Checker> checkersWhite;
    ArrayList<Checker> checkersBlack;
    Position position;
    boolean take;

    Position(ArrayList<Checker> checkersWhite, ArrayList<Checker> checkersBlack, boolean take) {
        this.checkersBlack = checkersBlack;
        this.checkersWhite = checkersWhite;
        this.take = take;
    }
    Position(Position position) {
        this.position = position;
    }

    void replace(char x1, int y1, char x2, int y2) {
        for (int i = 0; i < checkersBlack.size(); i++)
            if (((checkersBlack.get(i)).x == x1) && ((checkersBlack.get(i)).y == y1))
                checkersBlack.set(i, new Checker(x2, y2,((y2 == 1) || (checkersBlack.get(i)).queen), AI.isTake(position,1)));
        for (int i = 0; i < checkersWhite.size(); i++)
            if (((checkersWhite.get(i)).x == x1) && ((checkersWhite.get(i)).y == y1))
                checkersWhite.set(i, new Checker(x2, y2, ((y2 == 8) || (checkersWhite.get(i)).queen), AI.isTake(position,0)));
    }

    int Equels(char x, int y){
        for (int i = 0; i < checkersBlack.size(); i++)
            if (((checkersBlack.get(i)).x == x) && ((checkersBlack.get(i)).y == y))
                return -1;
        for (int i = 0; i < checkersWhite.size(); i++)
            if (((checkersWhite.get(i)).x == x) && ((checkersWhite.get(i)).y == y))
                return 1;
        return 0;
    }

    boolean isQueen(char x, int y) {
        for (int i = 0; i < checkersBlack.size(); i++)
            if (((checkersBlack.get(i)).x == x) && ((checkersBlack.get(i)).y == y) && ((checkersBlack.get(i)).queen))
                return true;
        for (int i = 0; i < checkersWhite.size(); i++)
            if (((checkersWhite.get(i)).x == x) && ((checkersWhite.get(i)).y == y) && ((checkersBlack.get(i)).queen))
                return true;
        return false;
    }
    int IndexOF(char x, int y) {
        for (int i = 0; i < checkersBlack.size(); i++)
            if (((checkersBlack.get(i)).x == x) && ((checkersBlack.get(i)).y == y))
                return i;
        for (int i = 0; i < checkersWhite.size(); i++)
            if (((checkersWhite.get(i)).x == x) && ((checkersWhite.get(i)).y == y))
                return i;
        return -1;
    }
}