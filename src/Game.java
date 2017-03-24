
import java.util.*;

public class Game {

    static Position position;
    static double time = 20;
    static long st;
    final static int BOARD_SIZE = 8;

    private static void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            while (true) {
                position.update(true);
                boolean take = position.takeWhite;
                String fromPosition = scanner.next();
                if (!Objects.equals(fromPosition, "0")) {
                    String toPosition = scanner.next();
                    char[] fromPositionChars = fromPosition.toCharArray();
                    char[] toPositionChars = toPosition.toCharArray();
                    int x1 = (int) fromPositionChars[0] - 'a', y1 = (int) fromPositionChars[1] - '1', x2 = (int) toPositionChars[0] - 'a', y2 = (int) toPositionChars[1] - '1';
                    position = replace(position, x1, y1, x2, y2);
                    if (!take)
                        break;
                    if (x1 < x2 && y1 < y2) {
                        int[][] directions = {
                                {1, 1}, {-1, 1}, {1, -1}};
                        if (!position.take(x2, y2, directions))
                            break;
                    } else if (x1 > x2 && y1 < y2) {
                        int[][] directions = {
                                {-1, -1}, {1, 1}, {-1, 1}};
                        if (!position.take(x2, y2, directions))
                            break;
                    } else if (x1 > x2 && y1 > y2) {
                        int[][] directions = {
                                {-1, -1}, {-1, 1}, {1, -1}};
                        if (!position.take(x2, y2, directions))
                            break;
                    } else if (x1 < x2 && y1 > y2) {
                        int[][] directions = {
                                {-1, -1}, {1, 1}, {1, -1}};
                        if (!position.take(x2, y2, directions))
                            break;
                    }
                } else
                    break;
            }
            st = System.currentTimeMillis();
            AiRun.bfs();
            time = ((double) (System.currentTimeMillis() - st) / 1000 <= 0.001) ? time : (double) (System.currentTimeMillis() - st) / 1000;
            System.out.println(time + " сек.");
            System.out.println("-----------------------------");
            System.out.println("Ваш ход:");
        }
    }


    public static void main(String[] agrs) {
      Piece[] pieces = new Piece[24];
        Integer[][] pos = new Integer[BOARD_SIZE][BOARD_SIZE];
        ArrayList<Integer> livePieces = new ArrayList<>();
        for (int i = 0; i <= 23; i++)
            livePieces.add(i, i);
        for (int i = 0; i <= 11; i++)
            pieces[i] = new Piece(true, false);
        for (int i = 12; i <= 23; i++)
            pieces[i] = new Piece(false, false);
        int i = 0;
        for (int x = 0; x <= 6; x += 2)
            for (int y = 0; y <= 2; y++) {
                if (y % 2 == 0)
                    pos[x][y] = i;
                else
                    pos[x + 1][y] = i;
                i++;
            }
        for (int x = 7; x >= 1; x -= 2)
            for (int y = 7; y >= 5; y--) {
                if (y % 2 == 1)
                    pos[x][y] = i;
                else
                    pos[x - 1][y] = i;
                i++;
            }

      /*  Piece[] pieces = {new Piece(false,true),new Piece(false,true), new Piece(false,false),new Piece(false,false),new Piece(true,false), new Piece(true,true)};
        Integer[][] pos = new Integer[8][8];
        ArrayList<Integer> livePieces = new ArrayList<>();
        pos[6][0]=0;
        pos[4][0]=1;
        pos[7][5]=2;
        pos[6][6]=3;
        pos[0][2]=4;
        pos[0][4]=5;
        livePieces.add(0,0);
        livePieces.add(1,1);
        livePieces.add(2,2);
        livePieces.add(3,3);
        livePieces.add(4,4);
        livePieces.add(5,5);
*/
 /*       Piece[] pieces = {new Piece(false,true),new Piece(true,true),new Piece(true,true),new Piece(true,true)};
        Integer[][] pos = new Integer[8][8];
        ArrayList<Integer> livePieces = new ArrayList<>();
        pos[4][0]=0;
        pos[0][0]=1;
        pos[1][1]=2;
        pos[7][1]=3;
        livePieces.add(0,0);
        livePieces.add(1,1);
        livePieces.add(2,2);
        livePieces.add(3,3);
        */
        position = new Position(pieces, pos, livePieces, new ArrayList<>(), new ArrayList<>(), false, false, null);
        position.update(true);
        run();

    }


    static Position replace(Position position1, int x1, int y1, int x2, int y2) {
        boolean isTurnWhite = position1.pieces[position1.pos[x1][y1]].isWhite;
        position1.update(isTurnWhite);
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (position1.pos[i][j] != null && (y1 < y2 && (x1 < x2 && i > x1 && i < x2 && j > y1 && j < y2 && x1 - y1 == i - j || x1 > x2 && i < x1 && i > x2 && j > y1 && j < y2 && x1 + y1 == i + j)
                        || y1 > y2 && (x1 < x2 && i > x1 && i < x2 && j < y1 && j > y2 && x1 + y1 == i + j || x1 > x2 && i < x1 && i > x2 && j < y1 && j > y2 && x1 - y1 == i - j))) {
                    position1.livePieces.remove(position1.pos[i][j]);
                    position1.pos[i][j] = null;
                }
            }
        position1.pos[x2][y2] = position1.pos[x1][y1];
        position1.pos[x1][y1] = null;
        if ((position1.pieces[position1.pos[x2][y2]].isWhite && y2 == 7 || !position1.pieces[position1.pos[x2][y2]].isWhite && y2 == 0))
            position1.pieces[position1.pos[x2][y2]].isQueen = true;
        if (position1.pieces[position1.pos[x2][y2]].isWhite && position1.takeWhite || !position1.pieces[position1.pos[x2][y2]].isWhite && position1.takeBlack)
            position1.movePiece = new Point(x2, y2);
        if (x1 < x2 && y1 < y2) {
            int[][] directions = {
                    {1, 1}, {-1, 1}, {1, -1}};
            if (!position1.take(x2, y2, directions))
                position1.movePiece = null;
        } else if (x1 > x2 && y1 < y2) {
            int[][] directions = {
                    {-1, -1}, {1, 1}, {-1, 1}};
            if (!position1.take(x2, y2, directions))
                position1.movePiece = null;
        } else if (x1 > x2 && y1 > y2) {
            int[][] directions = {
                    {-1, -1}, {-1, 1}, {1, -1}};
            if (!position1.take(x2, y2, directions))
                position1.movePiece = null;
        } else if (x1 < x2 && y1 > y2) {
            int[][] directions = {
                    {-1, -1}, {1, 1}, {1, -1}};
            if (!position1.take(x2, y2, directions))
                position1.movePiece = null;
        }
        return new Position(position1);
    }
}

class Point{
    int x;
    int y;
    Point(int x, int y){
        this.x=x;
        this.y=y;
    }
}

class Piece{
    boolean isWhite;
    boolean isQueen;
    Piece(boolean isWhite, boolean isQueen){
        this.isWhite=isWhite;
        this.isQueen=isQueen;
    }
}

class Position {
    Piece[] pieces = new Piece[24];
    Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
    ArrayList<Integer> livePieces;
    ArrayList<Point[]> validMovesWhite;
    ArrayList<Point[]> validMovesBlack;
    boolean takeWhite;
    boolean takeBlack;
    Point movePiece;

    Position(Piece[] pieces, Integer[][] pos, ArrayList<Integer> livePieces, List<Point[]> validMovesWhite, List<Point[]> validMovesBlack, boolean takeWhite, boolean takeBlack, Point movePiece) {
        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != null)
                this.pieces[i] = new Piece(pieces[i].isWhite, pieces[i].isQueen);
        for (int i = 0; i <= 7; ++i) {
            this.pos[i] = pos[i].clone();
        }
        this.livePieces = new ArrayList<>(livePieces);
        this.validMovesWhite = new ArrayList<>(validMovesWhite);
        this.validMovesBlack = new ArrayList<>(validMovesBlack);
        this.takeWhite = takeWhite;
        this.takeBlack = takeBlack;
        if (movePiece != null)
            this.movePiece = new Point(movePiece.x, movePiece.y);
        else
            this.movePiece = null;
    }

    Position(Position position) {
        for (int i = 0; i < pieces.length; ++i)
            if (position.pieces[i] != null)
                this.pieces[i] = new Piece(position.pieces[i].isWhite, position.pieces[i].isQueen);
        for (int i = 0; i < Game.BOARD_SIZE; ++i) {
            this.pos[i] = position.pos[i].clone();
        }
        this.livePieces = new ArrayList<>(position.livePieces);
        this.validMovesWhite = new ArrayList<>(position.validMovesWhite);
        this.validMovesBlack = new ArrayList<>(position.validMovesBlack);
        this.takeWhite = position.takeWhite;
        this.takeBlack = position.takeBlack;
        if (position.movePiece != null)
            this.movePiece = new Point(position.movePiece.x, position.movePiece.y);
        else
            this.movePiece = null;

    }

    public void update(boolean isTurnWhite) {
        validMovesWhite.clear();
        validMovesBlack.clear();
        boolean takeWhite = false, takeBlack = false;
        final int[][] directions = {
                {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
        };
        for (Integer pieceNumber :
                livePieces) {
            int x = -1, y = 0;
            if (movePiece == null) {
                label:
                for (int i = 0; i < Game.BOARD_SIZE; i++)
                    for (int j = 0; j < Game.BOARD_SIZE; j++)
                        if (pieces[pieceNumber].isWhite == isTurnWhite && Objects.equals(pos[i][j], pieceNumber)) {
                            x = i;
                            y = j;
                            break label;
                        }
            } else {
                x = movePiece.x;
                y = movePiece.y;
            }
            if (x != -1)
                if (!pieces[pos[x][y]].isQueen) {
                    for (int[] direction : directions)
                        if (isInBoard(x + direction[0], y + direction[1]))
                            if ((isTurnWhite && direction[1] == 1 || !isTurnWhite && direction[1] == -1) && ((!takeWhite && isTurnWhite) || ((!takeBlack && !isTurnWhite))) && pos[x + direction[0]][y + direction[1]] == null) {
                                if (isTurnWhite)
                                    validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                                else
                                    validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            } else if (pos[x + direction[0]][y + direction[1]] != null && isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pos[x][y]].isWhite == !pieces[pos[x + direction[0]][y + direction[1]]].isWhite && pos[x + 2 * direction[0]][y + 2 * direction[1]] == null)
                                if (isTurnWhite) {
                                    if (!takeWhite)
                                        validMovesWhite.clear();
                                    takeWhite = true;
                                    validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                                } else {
                                    if (!takeBlack)
                                        validMovesBlack.clear();
                                    takeBlack = true;
                                    validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                                }
                } else {
                    for (int[] direction : directions) {
                        boolean isQueenTake = false, isQueenMultitake = false;
                        ArrayList<Point[]> validMovesQueen = new ArrayList<>();
                        for (int i = 1; isInBoard(x + i * direction[0], y + i * direction[1]); i++) {
                            if (pos[x + i * direction[0]][y + i * direction[1]] != null && (isQueenTake || isTurnWhite == pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite || (pos[x + i * direction[0]][y + i * direction[1]] != null
                                    && (!isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) || pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] != null))))
                                break;
                            else if (!isQueenTake && ((!takeWhite && isTurnWhite) || ((!takeBlack && !isTurnWhite))) && pos[x + i * direction[0]][y + i * direction[1]] == null) {
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                            } else if (!isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null) {
                                isQueenMultitake = isQueenMultitake || takeQueen(x + i * direction[0], y + i * direction[1], direction, isTurnWhite);
                                if (isQueenMultitake)
                                    validMovesQueen.clear();
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});

                            } else if (isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null && takeQueen(x + i * direction[0], y + i * direction[1], direction, isTurnWhite)) {
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                            } else if (pos[x + i * direction[0]][y + i * direction[1]] != null && !isQueenTake && pieces[pos[x][y]].isWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null) {
                                isQueenTake = true;
                                if (isTurnWhite) {
                                    if (!takeWhite)
                                        validMovesWhite.clear();
                                    takeWhite = true;
                                } else if (!takeBlack) {
                                    validMovesBlack.clear();
                                    takeBlack = true;
                                }
                                validMovesQueen.clear();
                            }
                        }
                        if (isTurnWhite)
                            validMovesWhite.addAll(0, validMovesQueen);
                        else
                            validMovesBlack.addAll(0, validMovesQueen);
                    }
                }
            if (movePiece != null)
                break;
        }


        this.takeWhite = takeWhite;
        this.takeBlack = takeBlack;
        NumPos a = getNumpos(isTurnWhite);
        ArrayDeque<Integer> history =AiRun.historyEuristic.get(a);
        if (history!=null){
            ArrayList<Point[]> moves = new ArrayList();
            for (int i:
                 history) {
                    moves.add((isTurnWhite) ? validMovesWhite.get(i) : validMovesBlack.get(i));
            }
            for (int i:
                    history) {
                if (isTurnWhite)
                       validMovesWhite.remove(moves.get(i));
                else
                    validMovesBlack.remove(moves.get(i));
            }

            moves.addAll((isTurnWhite) ? validMovesWhite : validMovesBlack);
            if (isTurnWhite)
                validMovesWhite = new ArrayList<>(moves);
            else
                validMovesBlack = new ArrayList<>(moves);
        }
    }

    private boolean isInBoard(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }

    private boolean takeQueen(int x, int y, int[] direction2, boolean isTurnWhite) {
        int[][] directions = {{-direction2[0], direction2[1]}, {direction2[0], -direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1; isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                if (pos[x + i * direction[0]][y + i * direction[1]] != null)
                    return isTurnWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null;
        return false;
    }

    boolean take(int x, int y, int[][] directions) {
        if (pieces[pos[x][y]].isQueen) {
            for (int[] direction : directions)
                for (int i = 1; isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                    if (pos[x + i * direction[0]][y + i * direction[1]] != null) {
                        return pieces[pos[x][y]].isWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null;
                    }
        } else {
            for (int[] direction : directions)
                if (isInBoard(x + direction[0], y + direction[1]))
                    if (pos[x + direction[0]][y + direction[1]] != null && isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pos[x][y]].isWhite == !pieces[pos[x + direction[0]][y + direction[1]]].isWhite && pos[x + 2 * direction[0]][y + 2 * direction[1]] == null)
                        return true;
        }
        return false;
    }

    public NumPos getNumpos(boolean isTurnWhite) {
        long[] numpos = {1, 1, 1, 1};
        for (int i = 0; i < Game.BOARD_SIZE; i++)
            for (int j = 0; j < Game.BOARD_SIZE; j++) {
                numpos[i / 2] *= 10L;
                if ((i + j) % 2 == 0 && pos[i][j] != null) {
                    if (pieces[pos[i][j]].isWhite)
                        numpos[i / 2] += (pieces[pos[i][j]].isQueen) ? 1L : 2L;
                    else
                        numpos[i / 2] += (pieces[pos[i][j]].isQueen) ? 3L : 4L;
                }
            }
        numpos[3] *= 10L;
        numpos[3] += (isTurnWhite) ? 2:3;
        if (movePiece!=null)
            numpos[2] += 10*movePiece.x+movePiece.y;

        return new NumPos(numpos);
    }
}
