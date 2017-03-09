
import java.util.ArrayList;

public class AI {
   /* public ArrayList<Integer> analyze(Position position, int depth, int maxDepth){
        int result;
        char x1='0',x2='0';
        int y1=0,y2=0;
        if (depth%2==0){
            double min=10000;
            boolean queen=false;
            boolean istake = isTake(position,0);
                for (int i=0;i<position.checkersBlack.size();i++) {
                    x1=(position.checkersBlack.get(i)).x;
                    y1=(position.checkersBlack.get(i)).y;
                    queen=(position.checkersBlack.get(i)).queen;
                }
                if (!queen)
                    for (int j = 1; j <= 6; j++)
                        switch (j) {
                            case 1:
                                if (canMove(position,x1,y1,(char)(x1+1),y2+1,istake)){
                                    x2=(char)(x1+1);
                                    y2=y1+1;
                                }
                            case 2:
                                if (canMove(position,x1,y1,(char)(x1-1),y2+1,istake)){
                                    x2=(char)(x1-1);
                                    y2=y1+1;
                                }
                            case 3:
                                if (canMove(position,x1,y1,(char)(x1+2),y2+2,istake)){
                                    x2=(char)(x1+2);
                                    y2=y1+2;
                                }
                            case 4:
                                if (canMove(position,x1,y1,(char)(x1-2),y2+2,istake)){
                                    x2=(char)(x1-2);
                                    y2=y1+2;
                                }
                            case 5:
                                if (canMove(position,x1,y1,(char)(x1-2),y2-2,istake)){
                                    x2=(char)(x1-2);
                                    y2=y1-2;
                                }
                            case 6:
                                if (canMove(position,x1,y1,(char)(x1+2),y2-2,istake)){
                                    x2=(char)(x1+2);
                                    y2=y1-2;
                                }
                                default: y2=0;
                        }


                }

    } */

    public static boolean isTake(Position position,int color){
        if (color==0){
            for (int i=0;i<position.checkersBlack.size();i++){
                char x=(position.checkersBlack.get(i)).x;
                int y=(position.checkersBlack.get(i)).y;
                if (!(position.checkersBlack.get(i)).queen) {
                    if ((position.Equels((char) (x + 1), y + 1) == 1) && (position.Equels((char) (x + 2), y + 2) == 0) && (!isBorder((char) (x + 2), y + 2)))
                        return true;
                    if ((position.Equels((char) (x - 1), y + 1) == 1) && (position.Equels((char) (x - 2), y + 2) == 0) && (!isBorder((char) (x - 2), y + 2)))
                        return true;
                    if ((position.Equels((char) (x - 1), y - 1) == 1) && (position.Equels((char) (x - 2), y - 2) == 0) && (!isBorder((char) (x - 2), y - 2)))
                        return true;
                    if ((position.Equels((char) (x + 1), y - 1) == 1) && (position.Equels((char) (x + 2), y - 2) == 0) && (!isBorder((char) (x + 2), y - 2)))
                        return true;
                }
                else
                    return isQueentake(position,x,y);
            }
        }
        if (color==1){
            for (int i=0;i<position.checkersWhite.size();i++){
            char x=(position.checkersWhite.get(i)).x;
            int y=(position.checkersWhite.get(i)).y;
            if (!(position.checkersWhite.get(i)).queen) {
                if ((position.Equels((char) (x + 1), y + 1) == -1) && (position.Equels((char) (x + 2), y + 2) == 0) && (!isBorder((char) (x + 2), y + 2)))
                    return true;
                if ((position.Equels((char) (x - 1), y + 1) == -1) && (position.Equels((char) (x - 2), y + 2) == 0) && (!isBorder((char) (x - 2), y + 2)))
                    return true;
                if ((position.Equels((char) (x - 1), y - 1) == -1) && (position.Equels((char) (x - 2), y - 2) == 0) && (!isBorder((char) (x - 2), y - 2)))
                    return true;
                if ((position.Equels((char) (x + 1), y - 1) == -1) && (position.Equels((char) (x + 2), y - 2) == 0) && (!isBorder((char) (x + 2), y - 2)))
                    return true;
            }
                else
                    return isQueentake(position,x,y);
            }
        }
        return false;
    }

    public static boolean isBorder(char x, int y){
        return (y > 8) || (y < 1) || ((int) x > (int) 'h') || ((int) x < (int) 'a');
    }

    public static boolean canMove(Position position, char x1, int y1, char x2, int y2, boolean istake){
        if (position.Equels(x1,y1)!=0) {
            if ((position.Equels(x1, y1) == -1) && (!position.isQueen(x1, y1)) && (!position.take || (position.checkersBlack.get(position.IndexOF(x1, y1))).move)) {
                if ((!istake) && ((x1 + 1 == (int) x2) || (x1 - 1 == (int) x2)) && (y1 - 1 == y2) && (position.Equels(x2, y2) == 0) && (!isBorder(x2, y2)))
                    return true;
                if (istake) {
                    if ((position.Equels((char) (x1 + 1), y1 + 1) == 1) && (position.Equels((char) (x1 + 2), y1 + 2) == 0) && (!isBorder((char) (x1 + 2), y1 + 2)) && ((x1 + 2 == (int) x2) && (y1 + 2 == y2)))
                        return true;
                    if ((position.Equels((char) (x1 - 1), y1 + 1) == 1) && (position.Equels((char) (x1 - 2), y1 + 2) == 0) && (!isBorder((char) (x1 - 2), y1 + 2)) && ((x1 - 2 == (int) x2) && (y1 + 2 == y2)))
                        return true;
                    if ((position.Equels((char) (x1 - 1), y1 - 1) == 1) && (position.Equels((char) (x1 - 2), y1 - 2) == 0) && (!isBorder((char) (x1 - 2), y1 - 2)) && ((x1 - 2 == (int) x2) && (y1 - 2 == y2)))
                        return true;
                    if ((position.Equels((char) (x1 + 1), y1 - 1) == 1) && (position.Equels((char) (x1 + 2), y1 - 2) == 0) && (!isBorder((char) (x1 + 2), y1 - 2)) && ((x1 + 2 == (int) x2) && (y1 - 2 == y2)))
                        return true;
                }
            }

            if ((position.Equels(x1, y1) == 1) && (!position.isQueen(x1, y1)) && (!position.take || (position.checkersWhite.get(position.IndexOF(x1, y1))).move)) {
                if ((!istake) && ((x1 + 1 == (int) x2) || (x1 - 1 == (int) x2)) && (y1 + 1 == y2) && (position.Equels(x2, y2) == 0) && (!isBorder(x2, y2)))
                    return true;
                if (istake) {
                    if ((position.Equels((char) (x1 + 1), y1 + 1) == -1) && (position.Equels((char) (x1 + 2), y1 + 2) == 0) && (!isBorder((char) (x1 + 2), y1 + 2)) && ((x1 + 2 == (int) x2) && (y1 + 2 == y2)))
                        return true;
                    if ((position.Equels((char) (x1 - 1), y1 + 1) == -1) && (position.Equels((char) (x1 - 2), y1 + 2) == 0) && (!isBorder((char) (x1 - 2), y1 + 2)) && ((x1 - 2 == (int) x2) && (y1 + 2 == y2)))
                        return true;
                    if ((position.Equels((char) (x1 - 1), y1 - 1) == -1) && (position.Equels((char) (x1 - 2), y1 - 2) == 0) && (!isBorder((char) (x1 - 2), y1 - 2)) && ((x1 - 2 == (int) x2) && (y1 - 2 == y2)))
                        return true;
                    if ((position.Equels((char) (x1 + 1), y1 - 1) == -1) && (position.Equels((char) (x1 + 2), y1 - 2) == 0) && (!isBorder((char) (x1 + 2), y1 - 2)) && ((x1 + 2 == (int) x2) && (y1 - 2 == y2)))
                        return true;
                }
            }
            if ((position.Equels(x1,y1) == 1 && (!position.take || (position.checkersWhite.get(position.IndexOF(x1, y1))).move)) || (position.Equels(x1,y1) == -1 && (!position.take || (position.checkersBlack.get(position.IndexOF(x1, y1))).move)))
            if ((position.isQueen(x1, y1)) && (position.Equels(x2, y2) == 0) && (!isBorder(x2, y2))) {
                for (int i = 1; i <= 7; i++)
                    if ((((x1 + i == (int) x2) && (y1 + i == y2)) || ((x1 - i == (int) x2) && (y1 + i == y2)) || ((x1 - i == (int) x2) && (y1 - i == y2)) || ((x1 + i == (int) x2) && (y1 - i == y2)))) {
                        boolean isQueenMultitakeBoolean = isQueenMultitake(position, x1, y1).isQueenMultitake;
                        ArrayList<Coord> moves;
                        moves=isQueenMultitake(position, x1, y1).moves;
                        if (isQueenMultitakeBoolean)
                            for (int j = 0; j < moves.size(); j++) {
                                if (((moves.get(j)).x == x2) && ((moves.get(j)).y == y2))
                                    return true;
                            }
                        else if ((isQueentake(position,x1,y1)) && (position.equelsDiagonal(x1,y1,x2,y2)==-(position.Equels(x1, y1))))
                            return true;
                        if (!(isQueentake(position,x1,y1)) && (position.equelsDiagonal(x1,y1,x2,y2)==0))
                            return true;

                    }


            }
        }
        return false;
    }

    public static moveQueen isQueenMultitake(Position position, char x, int y){
        boolean a=false;
        ArrayList<Coord> moves = new ArrayList<>();
        if ((position.Equels(x, y) == -1) && (position.isQueen(x, y))) {
            for (int j = 2; j <= 7; j++) {
                int white = 0, black = 0;
                if ((position.Equels((char) (x + j), y + j) == 0) && (!isBorder((char) (x + j), y + j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x + k), y + k) == 1))
                            white++;
                        if ((position.Equels((char) (x + k), y + k) == -1))
                            black++;
                    }
                if ((white == 1) && (black == 0)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x + k)) && ((position1.checkersWhite.get(i)).y == y + k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x + k)) && ((position1.checkersBlack.get(i)).y == y + k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                        position1.checkersBlack.add(new Checker((char) (x + j),y + j,true,false));
                        if (!a)
                     a=isQueentake(position1,(char) (x + j),y + j);
                    if (isQueentake(position1,(char) (x + j),y + j))
                    moves.add(new Coord((char) (x + j),y + j));

                }


            }
            for (int j = 2; j <= 7; j++) {
                int white = 0, black = 0;
                if ((position.Equels((char) (x - j), y + j) == 0) && (!isBorder((char) (x - j), y + j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x - k), y + k) == 1))
                            white++;
                        if ((position.Equels((char) (x - k), y + k) == -1))
                            black++;
                    }
                if ((white == 1) && (black == 0)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x - k)) && ((position1.checkersWhite.get(i)).y == y + k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x - k)) && ((position1.checkersBlack.get(i)).y == y + k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                    position1.checkersBlack.add(new Checker((char) (x - j),y + j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x - j),y + j);
                    if (isQueentake(position1,(char) (x - j),y + j))
                        moves.add(new Coord((char) (x - j),y + j));
                }
            }
            for (int j = 2; j <= 7; j++) {
                int white = 0, black = 0;
                if ((position.Equels((char) (x - j), y - j) == 0) && (!isBorder((char) (x - j), y - j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x - k), y - k) == 1))
                            white++;
                        if ((position.Equels((char) (x - k), y - k) == -1))
                            black++;
                    }
                if ((white == 1) && (black == 0)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x - k)) && ((position1.checkersWhite.get(i)).y == y - k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x - k)) && ((position1.checkersBlack.get(i)).y == y - k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                    position1.checkersBlack.add(new Checker((char) (x - j),y - j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x - j),y - j);
                    if (isQueentake(position1,(char) (x - j),y - j))
                        moves.add(new Coord((char) (x - j),y - j));
                }
            }
            for (int j = 2; j <= 7; j++) {
                int white = 0, black = 0;
                if ((position.Equels((char) (x + j), y - j) == 0) && (!isBorder((char) (x + j), y - j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x + k), y - k) == 1))
                            white++;
                        if ((position.Equels((char) (x + k), y - k) == -1))
                            black++;
                    }
                if ((white == 1) && (black == 0)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x + k)) && ((position1.checkersWhite.get(i)).y == y - k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x + k)) && ((position1.checkersBlack.get(i)).y == y - k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                    position1.checkersBlack.add(new Checker((char) (x + j),y - j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x + j),y - j);
                    if (isQueentake(position1,(char) (x + j),y - j))
                        moves.add(new Coord((char) (x + j),y - j));
                }
            }
        }

        if ((position.Equels(x, y) == 1) && (position.isQueen(x, y))){
            for (int j=2; j<=7; j++) {
                int white=0,black=0;
                if ((position.Equels((char) (x + j), y + j) == 0) && (!isBorder((char) (x + j), y + j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x +k), y + k) == 1))
                            white++;
                        if ((position.Equels((char) (x +k), y + k) == -1))
                            black++;
                    }
                if ((white == 0) && (black == 1)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                    for (int k = -5; k <= j; k++) {
                        for (int i = 0; i < position1.checkersWhite.size(); i++) {
                            if (((position1.checkersWhite.get(i)).x == (char) (x + k)) && ((position1.checkersWhite.get(i)).y == y + k))
                                position1.checkersWhite.remove(i);
                        }
                        for (int i = 0; i < position1.checkersBlack.size(); i++) {
                            if (((position1.checkersBlack.get(i)).x == (char) (x + k)) && ((position1.checkersBlack.get(i)).y == y + k))
                                position1.checkersBlack.remove(i);
                        }
                    }
                    position1.checkersWhite.add(new Checker((char) (x + j),y + j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x + j),y + j);
                    if (isQueentake(position1,(char) (x + j),y + j))
                        moves.add(new Coord((char) (x + j),y + j));
                }
            }
            for (int j=2; j<=7; j++) {
                int white=0,black=0;
                if ((position.Equels((char) (x - j), y + j) == 0) && (!isBorder((char) (x - j), y + j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x -k), y + k) == 1))
                            white++;
                        if ((position.Equels((char) (x -k), y + k) == -1))
                            black++;
                    }
                if ((white == 0) && (black == 1)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x - k)) && ((position1.checkersWhite.get(i)).y == y + k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x - k)) && ((position1.checkersBlack.get(i)).y == y + k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                    position1.checkersWhite.add(new Checker((char) (x - j),y + j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x - j),y + j);
                    if (isQueentake(position1,(char) (x - j),y + j))
                        moves.add(new Coord((char) (x - j),y + j));
                }
            }
            for (int j=2; j<=7; j++) {
                int white=0,black=0;
                if ((position.Equels((char) (x - j), y - j) == 0) && (!isBorder((char) (x - j), y - j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x -k), y - k) == 1))
                            white++;
                        if ((position.Equels((char) (x -k), y - k) == -1))
                            black++;
                    }
                if ((white == 0) && (black == 1)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x - k)) && ((position1.checkersWhite.get(i)).y == y - k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x - k)) && ((position1.checkersBlack.get(i)).y == y - k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                    position1.checkersWhite.add(new Checker((char) (x - j),y - j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x - j),y - j);
                    if (isQueentake(position1,(char) (x - j),y - j))
                        moves.add(new Coord((char) (x - j),y - j));
                }
            }
            for (int j=2; j<=7; j++) {
                int white=0,black=0;
                if ((position.Equels((char) (x + j), y - j) == 0) && (!isBorder((char) (x + j), y - j)))
                    for (int k = 1; k < j; k++) {
                        if ((position.Equels((char) (x +k), y - k) == 1))
                            white++;
                        if ((position.Equels((char) (x +k), y - k) == -1))
                            black++;
                    }
                if ((white == 0) && (black == 1)){
                    Position position1 = new Position(position.checkersWhite,position.checkersBlack,position.take);
                        for (int k = -5; k <= j; k++) {
                            for (int i = 0; i < position1.checkersWhite.size(); i++) {
                                if (((position1.checkersWhite.get(i)).x == (char) (x + k)) && ((position1.checkersWhite.get(i)).y == y - k))
                                    position1.checkersWhite.remove(i);
                            }
                            for (int i = 0; i < position1.checkersBlack.size(); i++) {
                                if (((position1.checkersBlack.get(i)).x == (char) (x + k)) && ((position1.checkersBlack.get(i)).y == y - k))
                                    position1.checkersBlack.remove(i);
                            }
                        }
                    position1.checkersWhite.add(new Checker((char) (x + j),y - j,true,false));
                    if (!a)
                        a=isQueentake(position1,(char) (x + j),y - j);
                    if (isQueentake(position1,(char) (x + j),y - j))
                        moves.add(new Coord((char) (x + j),y - j));
                }
            }
        }
        return new moveQueen(a,moves);
    }

    public static boolean isQueentake(Position position, char x, int y) {
        if (position.isQueen(x, y)) {
            for (int j = 2; j <= 7; j++){
                if (position.equelsDiagonal(x, y, (char) (x + j), y + j) == -position.Equels(x, y) && (position.Equels((char) (x + j),y+j)==0))
                    return true;
                if (position.equelsDiagonal(x, y, (char) (x - j), y + j) == -position.Equels(x, y) && (position.Equels((char) (x - j),y+j)==0))
                    return true;
                if (position.equelsDiagonal(x, y, (char) (x - j), y - j) == -position.Equels(x, y) && (position.Equels((char) (x - j),y-j)==0))
                    return true;
                if (position.equelsDiagonal(x, y, (char) (x + j), y - j) == -position.Equels(x, y) && (position.Equels((char) (x + j),y-j)==0))
                    return true;
            }
        }

        return false;
    }
}

class moveQueen{
    boolean isQueenMultitake;
    ArrayList<Coord> moves;
    moveQueen(boolean isQueenMultitake,ArrayList<Coord> moves){
        this.isQueenMultitake=isQueenMultitake;
        this.moves= (ArrayList<Coord>) moves.clone();
    }
}
class Coord{
    char x;
    int y;
    Coord(char x, int y){
        this.x=x;
        this.y=y;
    }
}
