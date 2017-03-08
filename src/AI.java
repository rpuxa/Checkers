import javafx.geometry.Pos;

import java.util.ArrayList;

public class AI {
    public ArrayList<Integer> analyze(Position position, int depth, int maxDepth){
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
    }

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

    private boolean isBorder(char x,int y){
        return (y > 8) || (y < 1) || ((int) x > (int) 'h') || ((int) x < (int) 'a');
    }

    private boolean canMove(Position position,char x1,int y1,char x2,int y2,boolean istake){
        if ((!position.take || (position.checkersBlack.get(position.IndexOF(x1, y1))).move)) {
            if ((position.Equels(x1, y1) == -1) && (!position.isQueen(x1, y1))) {
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

            if ((position.Equels(x1, y1) == 1) && (!position.isQueen(x1, y1))) {
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

            if ((position.Equels(x1, y1) == -1) && (position.isQueen(x1, y1)) && (position.Equels(x2, y2) == 0) && (!isBorder(x2, y2))) {
                for (int i = 1; i <= 7; i++)
                    if ((((x1 + i == (int) x2) && (y1 + i == y2)) || ((x1 - i == (int) x2) && (y1 + i == y2)) || ((x1 - i == (int) x2) && (y1 - i == y2)) || ((x1 + i == (int) x2) && (y1 - i == y2))))

            }
        }
        return false;
    }

    private boolean isQueenMultitake(Position position,char x,int y){
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersBlack.size(); i++) {
                            if (((position1.checkersBlack.get(i)).x==(char) (x + k)) && ((position1.checkersBlack.get(i)).y==y + k))
                                position1.checkersBlack.remove(i);
                        }
                    if (isQueentake(position1,(char) (x + j),y + j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersBlack.size(); i++) {
                            if (((position1.checkersBlack.get(i)).x==(char) (x + k)) && ((position1.checkersBlack.get(i)).y==y + k))
                                position1.checkersBlack.remove(i);
                        }
                    if (isQueentake(position1,(char) (x - j),y + j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersBlack.size(); i++) {
                            if (((position1.checkersBlack.get(i)).x==(char) (x + k)) && ((position1.checkersBlack.get(i)).y==y + k))
                                position1.checkersBlack.remove(i);
                        }
                    if (isQueentake(position1,(char) (x - j),y - j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersBlack.size(); i++) {
                            if (((position1.checkersBlack.get(i)).x==(char) (x + k)) && ((position1.checkersBlack.get(i)).y==y + k))
                                position1.checkersBlack.remove(i);
                        }
                    if (isQueentake(position1,(char) (x + j),y - j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersWhite.size(); i++) {
                            if (((position1.checkersWhite.get(i)).x==(char) (x + k)) && ((position1.checkersWhite.get(i)).y==y + k))
                                position1.checkersWhite.remove(i);
                        }
                    if (isQueentake(position1,(char) (x + j),y + j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersWhite.size(); i++) {
                            if (((position1.checkersWhite.get(i)).x==(char) (x + k)) && ((position1.checkersWhite.get(i)).y==y + k))
                                position1.checkersWhite.remove(i);
                        }
                    if (isQueentake(position1,(char) (x - j),y + j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersWhite.size(); i++) {
                            if (((position1.checkersWhite.get(i)).x==(char) (x + k)) && ((position1.checkersWhite.get(i)).y==y + k))
                                position1.checkersWhite.remove(i);
                        }
                    if (isQueentake(position1,(char) (x - j),y - j))
                        return true;
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
                    Position position1 = new Position(position);
                    for (int k = 1; k < j; k++)
                        for (int i = 0; i < position1.checkersWhite.size(); i++) {
                            if (((position1.checkersWhite.get(i)).x==(char) (x + k)) && ((position1.checkersWhite.get(i)).y==y + k))
                                position1.checkersWhite.remove(i);
                        }
                    if (isQueentake(position1,(char) (x + j),y - j))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean isQueentake(Position position,char x,int y) {
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
                if ((white == 1) && (black == 0))
                    return true;
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
                if ((white == 1) && (black == 0))
                    return true;
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
                if ((white == 1) && (black == 0))
                    return true;
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
                if ((white == 1) && (black == 0))
                    return true;
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
                if ((white==0) && (black==1))
                    return true;
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
                if ((white==0) && (black==1))
                    return true;
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
                if ((white==0) && (black==1))
                    return true;
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
                if ((white==0) && (black==1))
                    return true;
            }
        }
        return false;
    }
}
