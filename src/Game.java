import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    public static Position position;

    public static void main(String[] agrs){
        ArrayList<Checker> checkersWhite = new ArrayList<>();
        ArrayList<Checker> checkersBlack = new ArrayList<>();
       /* for (char x = 'a';x<='g';x+=2)
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
                    checkersBlack.add(new Checker((char)(x-1),y,false,false));*/
        checkersWhite.add(new Checker('c',3,true,false));
        checkersWhite.add(new Checker('c',5,true,false));
        checkersBlack.add(new Checker('d',4,true,false));
        checkersBlack.add(new Checker('d',6,true,false));
      //  checkersWhite.add(new Checker('e',5,false,false));
        position = new Position(checkersWhite,checkersBlack,false);
      //run();
        System.out.println((AI.canMove(position,'c',3,'e',5,true)));
        position.replace('c',3,'e',5);
        System.out.println((AI.canMove(position,'c',5,'e',7,true)));
        System.out.println((AI.canMove(position,'e',5,'c',7,true)));
        //System.out.println(AI.isQueentake(position,'e',5));
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
    boolean take;

    Position(ArrayList<Checker> checkersWhite, ArrayList<Checker> checkersBlack, boolean take) {
        this.checkersBlack = (ArrayList<Checker>) checkersBlack.clone();
        this.checkersWhite = (ArrayList<Checker>) checkersWhite.clone();
        this.take = take;
    }

    void replace(char x1, int y1, char x2, int y2) {
        for (int i = 0; i < checkersBlack.size(); i++)
            if (((checkersBlack.get(i)).x == x1) && ((checkersBlack.get(i)).y == y1))
                checkersBlack.set(i, new Checker(x2, y2,((y2 == 1) || (checkersBlack.get(i)).queen), AI.isTake(new Position(checkersWhite,checkersBlack,take),1)));
        for (int i = 0; i < checkersWhite.size(); i++)
            if (((checkersWhite.get(i)).x == x1) && ((checkersWhite.get(i)).y == y1))
                checkersWhite.set(i, new Checker(x2, y2, ((y2 == 8) || (checkersWhite.get(i)).queen), AI.isTake(new Position(checkersWhite,checkersBlack,take),0)));
        if ((x1-x2<0) && (y1-y2<0))
            for (int k = 1; k < Math.abs(x1-x2); k++) {
                for (int i = 0; i < checkersWhite.size(); i++) {
                    if (((checkersWhite.get(i)).x == (char) (x1 + k)) && ((checkersWhite.get(i)).y == y1 + k)) {
                        checkersWhite.remove(i);
                        take = true;
                    }
                }
                for (int i = 0; i < checkersBlack.size(); i++) {
                    if (((checkersBlack.get(i)).x == (char) (x1 + k)) && ((checkersBlack.get(i)).y == y1 + k)) {
                        checkersBlack.remove(i);
                        take = true;
                    }
                }
            }
        if ((x1-x2>0) && (y1-y2<0))
            for (int k = 1; k < Math.abs(x1-x2); k++) {
                for (int i = 0; i < checkersWhite.size(); i++) {
                    if (((checkersWhite.get(i)).x == (char) (x1 - k)) && ((checkersWhite.get(i)).y == y1 + k)){
                        checkersWhite.remove(i);
                        take = true;
                    }
                }
                for (int i = 0; i < checkersBlack.size(); i++) {
                    if (((checkersBlack.get(i)).x == (char) (x1 - k)) && ((checkersBlack.get(i)).y == y1 + k)){
                        checkersBlack.remove(i);
                        take = true;
                    }
                }
            }
        if ((x1-x2>0) && (y1-y2>0))
            for (int k = 1; k < Math.abs(x1-x2); k++) {
                for (int i = 0; i < checkersWhite.size(); i++) {
                    if (((checkersWhite.get(i)).x == (char) (x1 - k)) && ((checkersWhite.get(i)).y == y1 - k)){
                        checkersWhite.remove(i);
                        take = true;
                    }
                }
                for (int i = 0; i < checkersBlack.size(); i++) {
                    if (((checkersBlack.get(i)).x == (char) (x1 - k)) && ((checkersBlack.get(i)).y == y1 - k)){
                        checkersBlack.remove(i);
                        take = true;
                    }
                }
            }
        if ((x1-x2<0) && (y1-y2>0))
            for (int k = 1; k < Math.abs(x1-x2); k++) {
                for (int i = 0; i < checkersWhite.size(); i++) {
                    if (((checkersWhite.get(i)).x == (char) (x1 + k)) && ((checkersWhite.get(i)).y == y1 - k)){
                        checkersWhite.remove(i);
                        take = true;
                    }
                }
                for (int i = 0; i < checkersBlack.size(); i++) {
                    if (((checkersBlack.get(i)).x == (char) (x1 + k)) && ((checkersBlack.get(i)).y == y1 - k)){
                        checkersBlack.remove(i);
                        take = true;
                    }
                }
            }

        if (Equels(x2,y2)==-1){
            for (int i=0;i<checkersBlack.size();i++){
                char x=(checkersBlack.get(i)).x;
                int y=(checkersBlack.get(i)).y;
                if (!(checkersBlack.get(i)).queen && checkersBlack.get(i).move) {
                    if ((Equels((char) (x + 1), y + 1) == 1) && (Equels((char) (x + 2), y + 2) == 0) && (!AI.isBorder((char) (x + 2), y + 2)))
                        return;
                    if ((Equels((char) (x - 1), y + 1) == 1) && (Equels((char) (x - 2), y + 2) == 0) && (!AI.isBorder((char) (x - 2), y + 2)))
                        return;
                    if ((Equels((char) (x - 1), y - 1) == 1) && (Equels((char) (x - 2), y - 2) == 0) && (!AI.isBorder((char) (x - 2), y - 2)))
                        return;
                    if ((Equels((char) (x + 1), y - 1) == 1) && (Equels((char) (x + 2), y - 2) == 0) && (!AI.isBorder((char) (x + 2), y - 2)))
                        return;
                }
                else
                    if (AI.isQueentake(new Position(checkersWhite,checkersBlack,take),x,y))
                        return;
            }
        }
        if (Equels(x2,y2)==1){
            for (int i=0;i<checkersWhite.size();i++){
                char x=(checkersWhite.get(i)).x;
                int y=(checkersWhite.get(i)).y;
                if (!(checkersWhite.get(i)).queen && checkersWhite.get(i).move) {
                    if ((Equels((char) (x + 1), y + 1) == -1) && (Equels((char) (x + 2), y + 2) == 0) && (!AI.isBorder((char) (x + 2), y + 2)))
                        return;
                    if ((Equels((char) (x - 1), y + 1) == -1) && (Equels((char) (x - 2), y + 2) == 0) && (!AI.isBorder((char) (x - 2), y + 2)))
                        return;
                    if ((Equels((char) (x - 1), y - 1) == -1) && (Equels((char) (x - 2), y - 2) == 0) && (!AI.isBorder((char) (x - 2), y - 2)))
                        return;
                    if ((Equels((char) (x + 1), y - 1) == -1) && (Equels((char) (x + 2), y - 2) == 0) && (!AI.isBorder((char) (x + 2), y - 2)))
                        return;
                }
                else
                if (AI.isQueentake(new Position(checkersWhite,checkersBlack,take),x,y))
                    return;
            }
        }
        for (int i=0;i<checkersWhite.size();i++)
            checkersWhite.set(i,new Checker((checkersWhite.get(i)).x, (checkersWhite.get(i)).y, (checkersWhite.get(i)).queen, false));
        for (int i=0;i<checkersBlack.size();i++)
            checkersBlack.set(i,new Checker((checkersBlack.get(i)).x, (checkersBlack.get(i)).y, (checkersBlack.get(i)).queen, false));
        take=false;
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
            if (((checkersWhite.get(i)).x == x) && ((checkersWhite.get(i)).y == y) && ((checkersWhite.get(i)).queen))
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
    int equelsDiagonal(char x1, int y1,char x2, int y2){
        if ((Math.abs(x1-x2)==1) && (Math.abs(y1-y2)==1))
            return 0;
        int white = 0, black = 0;
        if ((x2-x1>0) && (y2-y1>0))
            for (int k = 1; k < Math.abs(y1-y2); k++) {
                if ((Equels((char) (x1 + k), y1 + k) == 1))
                    white++;
                if ((Equels((char) (x1 + k), y1 + k) == -1))
                    black++;
            }
            else if ((x2-x1<0) && (y2-y1>0))
            for (int k = 1; k < Math.abs(y1-y2); k++) {
                if ((Equels((char) (x1 - k), y1 + k) == 1))
                    white++;
                if ((Equels((char) (x1 - k), y1 + k) == -1))
                    black++;
            }
            else if ((x2-x1<0) && (y2-y1<0))
            for (int k = 1; k < Math.abs(y1-y2); k++) {
                if ((Equels((char) (x1 - k), y1 - k) == 1))
                    white++;
                if ((Equels((char) (x1 - k), y1 - k) == -1))
                    black++;
            }
        else if ((x2-x1>0) && (y2-y1<0))
            for (int k = 1; k < Math.abs(y1-y2); k++) {
                if ((Equels((char) (x1 + k), y1 - k) == 1))
                    white++;
                if ((Equels((char) (x1 + k), y1 - k) == -1))
                    black++;
            }


        if ((white == 1) && (black == 0))
            return 1;
        if ((white == 0) && (black == 1))
            return -1;
        if ((white == 0) && (black == 0))
            return 0;
        if ((white != 0) && (black != 0))
            return 2;
        return 0;
    }
}