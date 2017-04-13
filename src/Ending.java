import javafx.geometry.Pos;

import java.io.*;
import java.util.*;

public class Ending {

    public static void main(String[] args) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Endings/Two_Figure_Endings.dat"))) {
           // endings.put(2, (Map<PosInfoT, Short[]>) ois.readObject());
        } catch (Exception ex) {
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Endings/Three_Figure_Endings.dat"))) {
       //     endings.put(3, (Map<PosInfoT, Short>) ois.readObject());
        } catch (Exception ex) {
        }
      // new Board().setVisible(true);
        endingAnalyze();

    }

    static Map<Integer,Map<PosInfoT,Short[]>> endings = new HashMap<>();

    static void endingAnalyze() {
        for (int x1 = 0; x1 < 8; x1++)
            for (int y1 = 0; y1 < 8; y1++)
                if ((x1 + y1) % 2 == 0)
                    for (int queen1 = 0; queen1 <= 1; queen1++)
                        for (int x2 = 0; x2 < 8; x2++)
                            for (int y2 = 0; y2 < 8; y2++)
                                if ((x2 + y2) % 2 == 0)
                                    for (int queen2 = 0; queen2 <= 1; queen2++)
                                      for (int x3 = 0; x3 < 8; x3++)
                                            for (int y3 = 0; y3 < 8; y3++)
                                                if ((x3 + y3) % 2 == 0)
                                                    for (int queen3 = 0; queen3 <= 1; queen3++)
                                                    for (int color = 0; color <= 1; color++)
                                                        for (short turn = 0; turn <= 1; turn++){
                                                                try {
                                                                    Position newPosition = create(new int[]{x1, x2,x3}, new int[]{y1, y2,x3}, new boolean[]{queen1==1,queen2==1,queen3==1}, new boolean[]{true, false,color==1});

                                                                    PosInfoT posInfoT = new PosInfoT(newPosition,turn == 1);

                                                                    try {
                                                                        System.out.println(endings.get(3).size());
                                                                    } catch (Exception e) {
                                                                    }

                                                                    try {
                                                                        short d = endings.get(newPosition.livePieces.size()).get(posInfoT)[0];
                                                                    } catch (NullPointerException e) {
                                                                        short result = analyze(newPosition, turn,0,false);
                                                                        endings.computeIfAbsent(newPosition.livePieces.size(), k -> new HashMap<>());
                                                                        endings.get(newPosition.livePieces.size()).put(posInfoT, new Short[]{result,0});
                                                                    }
                                                                } catch (NullPointerException ignore) {
                                                                }
                                                            }




            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Endings/Two_Figure_Endings.dat"))) {
                oos.writeObject(endings2.get(2));
            } catch (Exception ignore) {
                System.out.println();
            }
        System.out.println();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Endings/Three_Figure_Endings.dat"))) {
           oos.writeObject(endings2.get(3));
        } catch (Exception ignore) {
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Endings/Four_Figure_Endings.dat"))) {
              // oos.writeObject(endings2.get(4));
        } catch (Exception ignore) {
        }

    }

    static Position create(int[] x, int[] y, boolean[] queen, boolean[] isWhite){
        Piece[] pieces = new Piece[24];
        Integer[][] pos = new Integer[8][8];
        ArrayList<Integer> livePieces = new ArrayList<>();

        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < i; j++)
                if (x[i]==x[j] && y[i]==y[j])
                    return null;

        for (int i = 0; i < x.length; i++) {
            livePieces.add(i);
            pieces[i] = new Piece(isWhite[i], queen[i] || isWhite[i] && y[i]==7 || !isWhite[i] && y[i]==0);
            pos[x[i]][y[i]] = i;
        }
        return new Position(pieces, pos, livePieces, new ArrayList<>(), false, null);
    }

    private static short analyze(Position position, short depth, int c, boolean turnQ) {
        if (depth != 0 && position.movePiece != null)
            depth--;

        if (position.movePiece == null)
        try {
            Short[] hash = endings.get(position.livePieces.size()).get(new PosInfoT(position,depth % 2 == 1));
            if (hash[1]<=c)
                return hash[0];
        } catch (NullPointerException ignore) {}

        int whiteQueens = 0, blackQueens = 0;
        for (int a:
                position.livePieces) {
            if (position.pieces[a].isQueen && position.pieces[a].isWhite)
                whiteQueens++;
            if (position.pieces[a].isQueen && !position.pieces[a].isWhite)
                blackQueens++;
        }

        if (position.take || turnQ)
            c=0;
        if (c>=4 && ((whiteQueens==1 && blackQueens<=2 && blackQueens!=0 || blackQueens == 1 && whiteQueens<=2 && whiteQueens!=0) && position.livePieces.size()==blackQueens+whiteQueens))
            return 0;
        if (c>=20 && ((whiteQueens==1 && blackQueens<=2 && blackQueens!=0 || blackQueens == 1 && whiteQueens<=2 && whiteQueens!=0) && position.livePieces.size()>blackQueens+whiteQueens))
            return 0;

        position.update(depth % 2 == 1);

        short min = (short) (-depth);
        short max = depth;

        for (Point[] move :
                position.validMoves) {
            Position newPosition = Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y);

            short result = analyze(newPosition, (short) (depth + 1), c + 1,!position.pieces[position.pos[move[0].x][move[0].y]].isQueen && ((depth % 2 == 1) ? move[1].y==7 : move[1].y==0));

            if (depth % 2 == 0) {
                if (result < min)
                    min = result;
            } else
                if (result > max)
                    max = result;

        }
        endings.computeIfAbsent(position.livePieces.size(), k -> new HashMap<>());
        endings.get(position.livePieces.size()).put(new PosInfoT(position,depth % 2 == 1), new Short[]{(depth % 2 == 0) ? min : max,(short)c});
        return (depth % 2 == 0) ? min : max;
    }
}

class Draw implements Serializable {
    //если участник, имеющий три дамки (и более) против одной дамки противника, за 15 ходов не возьмёт дамку противника;
    int count1;
    //если в позиции, в которой оба соперника имеют дамки, не изменилось соотношение сил (то есть не было взятия, и ни одна простая шашка не стала дамкой) на протяжении:
    // в 2-х и 3-х фигурных окончаниях — 5 ходов,
    int count2;
    //в 4-х и 5-и фигурных окончаниях — 30 ходов,
    int count3;
    //в 6-и и 7-и фигурных окончаниях — 60 ходов;
    int count4;
    //если в течение 15 ходов игроки делали ходы только дамками, не передвигая простых шашек и не производя взятия.
    int count5;
    //2 дамки против 1

    Draw() {
    }
    Draw(Draw draw){
        count1 = draw.count1;
        count2 = draw.count2;
        count3 = draw.count3;
        count4 = draw.count4;
        count5 = draw.count5;
    }

    void count(boolean take, boolean queensMove, boolean becameQueen, boolean queens3vs1, int liveF, boolean haveQueens) {
        if (queens3vs1)
            count1++;
        else
            count1 = 0;
        if (!take && haveQueens && !becameQueen && (liveF == 2 || liveF == 3))
            count2++;
        else
            count2 = 0;
        if (!take && haveQueens && !becameQueen && (liveF == 4 || liveF == 5))
            count3++;
        else
            count3 = 0;
        if (!take && haveQueens && !becameQueen && (liveF == 6 || liveF == 7))
            count4++;
        else
            count4 = 0;
        if (!take && queensMove)
            count5++;
        else
            count5 = 0;


    }

    boolean isDraw(boolean take, boolean queensMove, boolean becameQueen, boolean queens3vs1, int liveF, boolean haveQueens) {
        count(take, queensMove, becameQueen, queens3vs1, liveF, haveQueens);
        return count1 >= 30 || count2 >= 10 || count3 >= 60 || count4 >= 120 || count5 >= 30;
    }
}

class PosInfoT implements Serializable{
    short[] pieces;
    boolean white;

    PosInfoT(Position position,boolean white) {
        int count = 0;
        this.pieces = new short[position.livePieces.size()];
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (position.pos[x][y] != null) {
                    pieces[count] = Debut.compressPiece(position.pieces[position.pos[x][y]], x, y);
                    count++;
                }
                this.white = white;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PosInfoT)) return false;

        PosInfoT other = (PosInfoT) obj;

        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != other.pieces[i])
                return false;

        return (white == other.white);

    }

    public int hashCode(){
        return 2*Arrays.hashCode(pieces)+((white) ? 1 : 0);
    }
}
