import javax.swing.*;
import java.io.*;
import java.util.*;

public class Game {

    static Position position;
    static long st = System.currentTimeMillis();
    final static int BOARD_SIZE = 8;
    static ArrayList<Thread> threads = new ArrayList<>();
    static ArrayList<Long> threadsNum = new ArrayList<>();
    static ArrayList<Point[]> threadResult = new ArrayList<>();
    static int threadNumber = -1;
    static int threadsCount = 0;
    static int timeToMove = 0;
    static Double score = 0.0;
    static Thread timer;
    static Map<Integer, Map<Long, Moves>> hashValidMoves = new TreeMap<>();
    static int movesInGame = 0;
    static boolean firstWinMassage = true;
    static ArrayList<BaseGame> games;
    static Map<PosInfo, DebutPos> debut;
    static Coeffs coeffs = new Coeffs();
    static byte[][][] endings = new byte[2][][];
    static Draw draw = new Draw();

    private static void run() throws InterruptedException {
        boolean isQ;
        int x1, x2, y1, y2;
        Scanner scanner = new Scanner(System.in);
        label:
        while (true) {
                long size = 0;
                for (int i = 0; i < movesInGame; i++) {
                    try {
                        hashValidMoves.get(movesInGame).clear();
                    }
                    catch (Exception ignored) {}
                }


                System.out.println("Размер : " + size);
            Move.block = false;
            movesInGame += 2;
            Thread.sleep(100);
            threadResult.clear();
            ThreadsRunner(new Position(position));
            while (true) {
                Move.block = false;
                position.update(true);
                if (position.validMoves.size() == 0) {
                    System.out.println("Компьютер победил!");
                    Move.timer.interrupt();
                    JOptionPane.showMessageDialog(null, "Компьютер победил!");
                    String n = scanner.next();
                    break label;
                }
                Move.position = new Position(position);
                System.out.println("Ждем хода...");

                while (Move.to == null || !Move.block) {
                    Thread.sleep(100);
                }

                System.out.println("Ходим...");
                x1 = Move.from.x;
                y1 = Move.from.y;
                x2 = Move.to.x;
                y2 = Move.to.y;
                isQ = position.pieces[position.pos[x1][y1]].isQueen;
                position = MakeMove(position, x1, y1, x2, y2);
/*
                Move.block = true;
                AiRun aiRun = new AiRun(timeToMove,-9);
                Position rPosition = new Position(position);
                rPosition.reverce();
                st = System.currentTimeMillis();
                for (Point[] moves : aiRun.bfs(new Position(rPosition)))
                    Game.position = MakeMove(position, 7-moves[0].x, 7-moves[0].y, 7-moves[1].x, 7-moves[1].y);
                 Thread.sleep(600);
                 */
                Move.sounds("Sounds/whiteTurn.wav");
                if (position.movePiece != null)
                    Move.from = new Point(Move.to.x, Move.to.y);
                else
                    Move.from = null;
                Move.to = null;
                Move.replacePosition(new Position(position));
                System.out.println("Обязательный ход: " + ((position.movePiece != null) ? (position.movePiece.x + " " + position.movePiece.y) : "нет"));
                if (position.movePiece == null)
                    break;
            }
            int whiteQueens = 0, blackQueens = 0;
            for (int c:
                    position.livePieces) {
                if (position.pieces[c].isQueen && position.pieces[c].isWhite)
                    whiteQueens++;
                if (position.pieces[c].isQueen && !position.pieces[c].isWhite)
                    blackQueens++;
            }
            boolean isDraw = (draw.isDraw(position.take, isQ, y2 == 7 && !isQ, (blackQueens >= 3 && whiteQueens == 1 || whiteQueens >= 3 && blackQueens == 1) && position.livePieces.size() == whiteQueens + blackQueens, position.livePieces.size(), (whiteQueens > 0 && blackQueens > 0)));

            if (isDraw) {
                System.out.println("Ничья!");
                Move.timer.interrupt();
                JOptionPane.showMessageDialog(null, "Ничья!");
                Move.block = true;
                String n = scanner.next();
                break label;
            }

            position.update(false);
            if (position.validMoves.size() == 0) {
                System.out.println("Вы победили!");
                Move.timer.interrupt();
                JOptionPane.showMessageDialog(null, "Вы победили!");
                Move.block = true;
                String n = scanner.next();
                break label;
            }
            System.out.println("Идет анализ...");
            ThreadsStop(new Position(position));
            for (Point[] moves : threadResult) {
                isQ = position.pieces[position.pos[moves[0].x][moves[0].y]].isQueen;
                Game.position = MakeMove(position, moves[0].x, moves[0].y, moves[1].x, moves[1].y);
            }
            System.out.println("Ход: " + (char) (threadResult.get(0)[0].x + 'a') + (threadResult.get(0)[0].y + 1) + " " + (char) (threadResult.get(threadResult.size() - 1)[1].x + 'a') + (threadResult.get(threadResult.size() - 1)[1].y + 1));
            if ((System.currentTimeMillis() - st)>timeToMove+5000)
                hashValidMoves.clear();
            for (int c:
                    position.livePieces) {
                if (position.pieces[c].isQueen && position.pieces[c].isWhite)
                    whiteQueens++;
                if (position.pieces[c].isQueen && !position.pieces[c].isWhite)
                    blackQueens++;
            }
            isDraw = (draw.isDraw(position.take, isQ, y2 == 0 && !isQ, (blackQueens >= 3 && whiteQueens == 1 || whiteQueens >= 3 && blackQueens == 1) && position.livePieces.size() == whiteQueens + blackQueens, position.livePieces.size(), (whiteQueens > 0 && blackQueens > 0)));

            if (isDraw) {
                System.out.println("Ничья!");
                Move.timer.interrupt();
                JOptionPane.showMessageDialog(null, "Ничья!");
                Move.block = true;
                String n = scanner.next();
                break label;
            }
            timer = new Thread(() -> {
                //try {
                //  while (true) {
                //    Thread.sleep(10);
                //       Game.st = System.currentTimeMillis();
                //  }
                // } catch (InterruptedException ignored) {
                // }
            });
            timer.start();
            if (score != -1000.0) {
                if (score != 10000)
                    System.out.println("Оценка: " + score);
                if (firstWinMassage && score < -290 && score > -300) {
                    JOptionPane.showMessageDialog(null, "Компьютер нашел победу в " + (int) ((300 + score) * 100) + " полуходов");
                    firstWinMassage = false;
                }
            }
            else
                System.out.println("Ход из базы");
            System.out.println((double) ((System.currentTimeMillis() - st)) / 1000 + " сек.");
            System.out.println("-----------------------------");
            System.out.println("Ваш ход:");
            Move.replacePosition(new Position(position));
            Move.sounds("Sounds/blackTurn.wav");
            Move.block = false;
        }
    }

    static boolean MakeLegalMove(Position position, int x1, int y1, int x2, int y2) {
        position.update(true);
        for (int i = 0; i < position.validMoves.size(); i++) {
            Point[] points = position.validMoves.get(i).clone();
            if (points[0].x == x1 && points[0].y == y1 && points[1].x == x2 && points[1].y == y2)
                return true;
        }
        return false;
    }

    private static void ThreadsRunner(Position position) throws InterruptedException {
        position.update(true);
        for (int i = 0; i < position.validMoves.size(); i++) {
            Point[] move = position.validMoves.get(i).clone();
            Position position1 = new Position(MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y));
            if (position1.movePiece != null)
                ThreadsRunner(new Position(position1));
            else {
                AiRun aiRun = new AiRun(timeToMove, threadsCount);
                threads.add(threadsCount, new Thread(() -> {
                    ArrayList<Point[]> result = null;
                    try {
                        result = new ArrayList<>(aiRun.bfs(new Position(position1)));
                    } catch (InterruptedException ignored) {
                    }
                    if (Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName()))
                        threadResult = new ArrayList<>(result);
                }, threadsCount + " "));
                threadsNum.add(threadsCount, position1.getNumPos(false));
                // if (threadsCount==0)
                // threads.get(threadsCount).start();
                Thread.sleep(100);
                threadsCount++;
            }
        }
    }

    private static void ThreadsStop(Position position) throws InterruptedException {
        long numPos = position.getNumPos(false);
        Thread thread = threads.get(0);
        for (int i = 0; i < threadsCount; i++)
            if (threadsNum.get(i).equals(numPos)) {
                thread = threads.get(i);
                String name = thread.getName();
                name = name.replaceAll(" ", "");
                threadNumber = Integer.parseInt(name);
                break;
            }
        threads.clear();
        threadsNum.clear();
        threadsCount = 0;
        while (timer.isAlive())
            timer.interrupt();
        //  if (threadNumber!=0)
        st = System.currentTimeMillis();
        thread.start();
        System.out.println("WAIT");
        thread.join();
        System.out.println("Continue");
        Thread.sleep(10);
        threadNumber = -1;
    }

    private static void loading() throws InterruptedException {
        timer = new Thread(() -> {
            //  try {
            //     while (true) {
            //          Thread.sleep(10);
            //           Game.st = System.currentTimeMillis();
            //          System.out.print("");
            //      }
            //   } catch (InterruptedException ignored) {}
        });
        timer.start();

        File file = new File("Debut.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Debut.dat"))) {
                debut = (Map<PosInfo, DebutPos>) ois.readObject();
            } catch (Exception ex) {
                System.out.println();
            }
        }

        file = new File("Coeffs.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Coeffs.dat"))) {
                coeffs = (Coeffs) ois.readObject();
            } catch (Exception ex) {
            }
        }

        new Thread(() -> {
           final File file2 = new File("Endings/Two_Figure_Endings.dat");
            if (file2.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Endings/Two_Figure_Endings.dat"))) {
                    endings[0] = (byte[][]) ois.readObject();
                } catch (Exception ex) {
                    System.out.println();
                }
            }
        }).start();

        Thread thread =  new Thread(() -> {
            final File file2 = new File("Endings/Three_Figure_Endings.dat");
            if (file2.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Endings/Three_Figure_Endings.dat"))) {
                    endings[1] = (byte[][]) ois.readObject();
                } catch (Exception ex) {
                    System.out.println();
                }
            }
        });

        thread.start();
        thread.join();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Запуск программы...");

        loading();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Время на ход (в миллисекундах):");
        try {
            timeToMove = Integer.parseInt(args[0]);
            System.out.println(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            timeToMove = scanner.nextInt();
        }
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

        position = new Position(pieces, pos, livePieces, new ArrayList<>(), false, null);
        new Board().setVisible(true);
        Editor editor = new Editor();
        editor.setVisible(true);
        Move.position = new Position(position);
        Move.replacePosition(new Position(position));
        Clock clock = new Clock();
        clock.setVisible(true);
        while (!Edit.start) {
            Thread.sleep(100);
        }
        System.out.println("BEGIN");
        //  clock.start(1200);
        run();

    }

    static Position MakeMove(Position position1, int x1, int y1, int x2, int y2) {
        boolean isTurnWhite = false;
        try {
            isTurnWhite = position1.pieces[position1.pos[x1][y1]].isWhite;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (position1.take)
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

class Piece implements Serializable{
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
    ArrayList<Point[]> validMoves;
    boolean take;
    Point movePiece;

    Position(Piece[] pieces, Integer[][] pos, ArrayList<Integer> livePieces, List<Point[]> validMoves, boolean take, Point movePiece) {
        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != null)
                this.pieces[i] = new Piece(pieces[i].isWhite, pieces[i].isQueen);
        for (int i = 0; i <= 7; ++i) {
            this.pos[i] = pos[i].clone();
        }
        this.livePieces = new ArrayList<>(livePieces);
        this.validMoves = new ArrayList<>(validMoves);
        this.take = take;
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
        this.validMoves = new ArrayList<>();
        this.take = position.take;
        if (position.movePiece != null)
            this.movePiece = new Point(position.movePiece.x, position.movePiece.y);
        else
            this.movePiece = null;

    }



    void reverce(){
        Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != null)
                this.pieces[i] = new Piece(!pieces[i].isWhite, pieces[i].isQueen);
        for (int i = 0; i <= 7; ++i)
            for (int j = 0; j <= 7; j++)
            pos[i][j] = this.pos[7-i][7-j];
        for (int i = 0; i < 8; i++)
            this.pos[i] = pos[i].clone();
        this.livePieces = new ArrayList<>(livePieces);
        this.validMoves = new ArrayList<>();
        if (movePiece != null)
            this.movePiece = new Point(7-movePiece.x, 7-movePiece.y);
        else
            this.movePiece = null;
    }

    Integer[] getIndex(){
        int c = livePieces.size();
        int count = 0;
        int[] coord = new int[c];
        int[] m = new int[c];
        for (int x = 0; x < Game.BOARD_SIZE; x++)
            for (int y = 0; y < Game.BOARD_SIZE; y++)
            if (pos[x][y]!=null){
                coord[count] = 4 * x + y / 2;
                m[count] = (pieces[pos[x][y]].isWhite ? 0 : 2) + (pieces[pos[x][y]].isQueen ? 1 : 0);
                count++;
            }
            int summ = 0;
        for (int i = 1; i <= c; i++) {
            int multi = 1;
            for (int j = 0; j < i; j++)
                multi *= coord[i-1]-j;
            summ += multi/fact(i);
        }
        int matherial = 0;
        for (int i = 0; i < c; i++) {
            matherial += m[i]*Math.pow(4,i);
        }
        return new Integer[]{summ,matherial};
    }

    int fact(int a){
        int result = 1;
        if (a==0)
            return 1;
        for (int i = 1; i <= a; i++)
            result *= i;
        return result;
    }

    void update(boolean isTurnWhite) {
        validMoves.clear();
        take = false;
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
                        if (AiRun.isInBoard(x + direction[0], y + direction[1]))
                            if ((isTurnWhite && direction[1] == 1 || !isTurnWhite && direction[1] == -1) && !take && pos[x + direction[0]][y + direction[1]] == null) {
                                validMoves.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            } else if (pos[x + direction[0]][y + direction[1]] != null && AiRun.isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pos[x][y]].isWhite == !pieces[pos[x + direction[0]][y + direction[1]]].isWhite && pos[x + 2 * direction[0]][y + 2 * direction[1]] == null) {
                                if (!take)
                                    validMoves.clear();
                                take = true;
                                validMoves.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                            }
                } else {
                    for (int[] direction : directions) {
                        boolean isQueenTake = false, isQueenMultitake = false;
                        ArrayList<Point[]> validMovesQueen = new ArrayList<>();
                        for (int i = 1; AiRun.isInBoard(x + i * direction[0], y + i * direction[1]); i++) {
                            if (pos[x + i * direction[0]][y + i * direction[1]] != null && (isQueenTake || isTurnWhite == pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite || (pos[x + i * direction[0]][y + i * direction[1]] != null
                                    && (!AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) || pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] != null))))
                                break;
                            else if (!isQueenTake && !take && pos[x + i * direction[0]][y + i * direction[1]] == null) {
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                            } else if (!isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null) {
                                isQueenMultitake = isQueenMultitake || takeQueen(x + i * direction[0], y + i * direction[1], direction, isTurnWhite);
                                if (isQueenMultitake)
                                    validMovesQueen.clear();
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});

                            } else if (isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null && takeQueen(x + i * direction[0], y + i * direction[1], direction, isTurnWhite)) {
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                            } else if (pos[x + i * direction[0]][y + i * direction[1]] != null && !isQueenTake && pieces[pos[x][y]].isWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null) {
                                isQueenTake = true;
                                if (!take)
                                    validMoves.clear();
                                take = true;
                                validMovesQueen.clear();
                            }
                        }
                        validMoves.addAll(0, validMovesQueen);
                    }
                }
            if (movePiece != null)
                break;
        }
    }

     boolean takeQueen(int x, int y, int[] direction2, boolean isTurnWhite) {
        int[][] directions = {{-direction2[0], direction2[1]}, {direction2[0], -direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1; AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                if (pos[x + i * direction[0]][y + i * direction[1]] != null) {
                    if (isTurnWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null)
                        return true;
                    else
                        break;
                }
        return false;
    }

    boolean take(int x, int y, int[][] directions) {
        if (pieces[pos[x][y]].isQueen) {
            for (int[] direction : directions)
                for (int i = 1; AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                    if (pos[x + i * direction[0]][y + i * direction[1]] != null) {
                        if (pieces[pos[x][y]].isWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null)
                            return true;
                        else
                            break;
                    }
        } else {
            for (int[] direction : directions)
                if (AiRun.isInBoard(x + direction[0], y + direction[1]))
                    if (pos[x + direction[0]][y + direction[1]] != null && AiRun.isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pos[x][y]].isWhite == !pieces[pos[x + direction[0]][y + direction[1]]].isWhite && pos[x + 2 * direction[0]][y + 2 * direction[1]] == null)
                        return true;
        }
        return false;
    }

    long getNumPos(boolean isTurnWhite) {
        long numPos = 0;
        for (int i = 0; i < Game.BOARD_SIZE; i++)
            for (int j = 0; j < Game.BOARD_SIZE; j++)
            if ((i+j) % 2 == 0){
                numPos *= 3L;
                if (pos[i][j] != null)
                        numPos += (pieces[pos[i][j]].isWhite) ? 1L : 2L;

            }

        if (movePiece!=null) {
            numPos *= 8;
            numPos += movePiece.x;
            numPos *= 8;
            numPos += movePiece.y;
        }

        numPos *= 2;
        numPos += (isTurnWhite) ? 0 : 1;

        return numPos;
    }

}
