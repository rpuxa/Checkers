
import java.util.*;


class AiRun {

    static Map<NumPos, Double> hashPos = new HashMap<>();
    static Map<NumPos, ArrayDeque> historyEuristic = new HashMap<>();
    final static int timeToMove = 40000;
    static int CountPositions = 0;

    static void bfs() {
        Double[] movesscore = new Double[100];
        String out1 = "", out2;
        int k = 0, move;
        while (true) {
            Game.position.update(false);
            if (Game.position.validMovesBlack.size() == 1) {
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(0)[0].x, Game.position.validMovesBlack.get(0)[0].y, Game.position.validMovesBlack.get(0)[1].x, Game.position.validMovesBlack.get(0)[1].y);
                move = 0;
            } else {

                label:
                for (int i = 0; System.currentTimeMillis() - Game.st <=timeToMove ; i += 2) {
                    System.out.println("Идет анализ c глубиной: " + (i + 8));
                    Double[] anl = analyze(new Position(Game.position), 0, i + 8, 0, new int[100], true);
                    CountPositions += hashPos.size();
                    System.out.println(hashPos.size());
                    hashPos.clear();
                    for (int j = 1; j < anl.length; j++)
                        if (anl[j] != null) {
                            movesscore[j-1] = anl[j];
                            if (movesscore[j-1] < -250)
                                break label;
                        }
                }
                Double min = 10000.0;
                int resultMove = 0;
                for (int i = 0; movesscore[i] != null; i++)
                    if (movesscore[i] < min) {
                        min = movesscore[i];
                        resultMove = i;
                    }
                System.out.println("Оценка: " + ((double) (Math.round(movesscore[resultMove] * 100000))) / 100000);
                System.out.println("Проанализировано " + CountPositions + " позиций");
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(resultMove)[0].x, Game.position.validMovesBlack.get(resultMove)[0].y, Game.position.validMovesBlack.get(resultMove)[1].x, Game.position.validMovesBlack.get(resultMove)[1].y);
                move = resultMove;
            }
            if (k == 0)
                out1 = "Ход: " + (char) (Game.position.validMovesBlack.get(move)[0].x + 'a') + (Game.position.validMovesBlack.get(move)[0].y + 1);
            if (Game.position.movePiece == null)
                break;
            k++;
        }
        out2 = " " + (char) (Game.position.validMovesBlack.get(move)[1].x + 'a') + (Game.position.validMovesBlack.get(move)[1].y + 1) + ";";
        System.out.println(out1 + out2);
    }

    private static Double[] analyze(Position position, int depth, int maxDepth, double alpha, int[] lp, boolean puring) {
        if (hashPos.get(position.getNumpos(depth % 2 == 1)) != null)
        return new Double[]{hashPos.get(position.getNumpos(depth % 2 == 1))};

        if (depth != 0 && position.movePiece != null)
            depth--;

        if (depth == 0) {
            position.update(false);
            Double[] analyzedMoves = new Double[100];
            double min = 300 - 0.01 * depth;
            int i = 0;
            for (Point[] move :
                    position.validMovesBlack) {
                i++;
                int[] lpr = lp.clone();
                lpr[depth] = countLP(new Position(position));
                double result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), 1, maxDepth, min, lpr, puring)[0];
                if (System.currentTimeMillis() - Game.st >= timeToMove && maxDepth!=8) {
                    analyzedMoves[0] = min;
                    return analyzedMoves;
                }
                if (result < min)
                    min = result;
                analyzedMoves[i] = result;
                if (min < -200)
                    break;
            }
            analyzedMoves[0] = min;
            return analyzedMoves;
        }
            position.update(depth % 2 == 1);

        if (puring && depth % 2 == 1 && depth >= 3 && lp[depth] < lp[depth - 2] && !position.takeWhite)
            maxDepth -= 2;
        if (puring && depth % 2 == 0 && depth >= 2 && lp[depth] > lp[depth - 2] && !position.takeBlack)
            maxDepth -= 2;

        if (depth >= maxDepth)
            return new Double[]{analyzeMaxDepth(new Position(position))};

        double min = 300 - 0.01 * depth, max = -300 + 0.01 * depth;
        int i = -1;
        Deque<Integer> sequence = new ArrayDeque<>();
        if ((position.validMovesBlack.size() == 1 || position.validMovesWhite.size() == 1) && position.movePiece == null)
            maxDepth++;

        for (Point[] move :
                (depth % 2 == 0) ? position.validMovesBlack : position.validMovesWhite) {
            i++;
            int[] lpr = lp.clone();
            lpr[depth] = countLP(new Position(position));
            double result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), depth + 1, maxDepth, (depth % 2 == 0) ? min : max, lpr, puring)[0];
            if (depth % 2 == 0) {
                if (result < min) {
                    min = result;
                    sequence.addFirst(i);
                }
                else
                   sequence.addLast(i);
                if (alpha<200 && alpha >= result)
                    break;
                if (min < -200)
                    break;
            } else {
                if (result > max){
                    max = result;
                   sequence.addFirst(i);
                }
                else
                   sequence.addLast(i);
                if (alpha>-200 && alpha <= result)
                    break;
                if (max > 200)
                    break;
            }
            if (System.currentTimeMillis() - Game.st >= timeToMove)
                break;
        }
        historyEuristic.put(position.getNumpos(depth % 2 == 1),new ArrayDeque(sequence));
        hashPos.put(position.getNumpos(depth % 2 == 1), (depth % 2 == 0) ? min : max);
        return new Double[]{(depth % 2 == 0) ? min : max};
    }

    private static double analyzeMaxDepth(Position position) {
        final double costPiece = 1;
        final double costQueen = 3 * costPiece;
        final double proximityToQueen = 0.01;
        final double captureCenter = 0.8 * position.livePieces.size() / 24;
        final double firstHorizontal = 0.04;
        final double cornerPiece = -0.05;
        final double passtoQueen = 1.5;
        final double flank = -0.1 * position.livePieces.size() / 24;

        double anl = 0;
        for (int i = 0; i < position.livePieces.size(); i++)
            if (position.pieces[position.livePieces.get(i)].isWhite) {
                if (position.pieces[position.livePieces.get(i)].isQueen)
                    anl += costQueen;
                else {
                    anl += costPiece;
                }
            } else {
                if (position.pieces[position.livePieces.get(i)].isQueen)
                    anl -= costQueen;
                else
                    anl -= costPiece;
            }

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++) {
                if (position.pos[x][y] != null && !position.pieces[position.pos[x][y]].isQueen)
                    if (position.pieces[position.pos[x][y]].isWhite) {
                        anl += y * proximityToQueen;
                    } else if (!position.pieces[position.pos[x][y]].isWhite) {
                        anl -= (7 - y) * proximityToQueen;
                    }
            }

        final int[][] center = {{2, 4}, {3, 3}, {4, 4}, {5, 3}};
        for (int coord[] :
                center) {
            int x = coord[0], y = coord[1];
            if (position.pos[x][y] != null)
                anl += (position.pieces[position.pos[x][y]].isWhite) ? captureCenter : -captureCenter;
        }
        final int[][] firstHorizontalPos = {{2, 0}, {4, 0}, {6, 0}};
        for (int coord[] :
                firstHorizontalPos) {
            int x = coord[0], y = coord[1];
            if (position.pos[x][y] != null)
                if (position.pieces[position.pos[x][y]].isWhite && !position.pieces[position.pos[x][y]].isQueen)
                    anl += firstHorizontal;
                else if (position.pieces[position.pos[x][y]].isWhite && !position.pieces[position.pos[x][y]].isQueen)
                    anl -= firstHorizontal;
        }

        if (position.pos[0][0] != null && position.pieces[position.pos[0][0]].isWhite && !position.pieces[position.pos[0][0]].isQueen)
            anl += cornerPiece;
        if (position.pos[7][7] != null && !position.pieces[position.pos[7][7]].isWhite && !position.pieces[position.pos[7][7]].isQueen)
            anl -= cornerPiece;

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (position.pos[x][y] != null && passToQueen(new Position(position), x, y))
                    anl += (position.pieces[position.pos[x][y]].isWhite) ? passtoQueen : -passtoQueen;

        int deltaw = 0, deltab = 0;
        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (position.pos[x][y] != null) {
                    if (x >= 3) {
                        if (position.pieces[position.pos[x][y]].isWhite)
                            deltaw++;
                        else
                            deltab++;
                    }
                    if (x <= 4) {
                        if (position.pieces[position.pos[x][y]].isWhite)
                            deltaw--;
                        else
                            deltab--;
                    }
                }
        anl += deltaw * flank;
        anl -= deltab * flank;


        return anl;
    }

    private static boolean passToQueen(Position position, int x, int y) {
        boolean pass = true;
        if (position.pieces[position.pos[x][y]].isWhite) {
            for (int i = 0; i <= 7; i++)
                for (int j = 0; j <= 7; j++)
                    if (j >= y && i <= x && (x + y - 2) - i <= j && position.pos[i][j] != null && !position.pieces[position.pos[i][j]].isWhite)
                        pass = false;

            if (!pass) {
                for (int i = 0; i <= 7; i++)
                    for (int j = 0; j <= 7; j++)
                        if (j >= y && i >= x && i - (x - y + 2) <= j && position.pos[i][j] != null && !position.pieces[position.pos[i][j]].isWhite)
                            return false;
            }

            return true;
        } else {
            for (int i = 0; i <= 7; i++)
                for (int j = 0; j <= 7; j++)
                    if (j <= y && i <= x && i - (x - y - 2) >= j && position.pos[i][j] != null && position.pieces[position.pos[i][j]].isWhite)
                        pass = false;

            if (!pass)
                for (int i = 0; i <= 7; i++)
                    for (int j = 0; j <= 7; j++)
                        if (j <= y && i >= x && (x + y + 2) - i >= j && position.pos[i][j] != null && position.pieces[position.pos[i][j]].isWhite)
                            return false;
            return true;
        }
    }

    private static int countLP(Position position) {
        int count = 0;
        for (int i = 0; i < position.livePieces.size(); i++) {
            count += (position.pieces[position.livePieces.get(i)].isWhite) ? 1 : -1;
        }
        return count;
    }

}

class NumPos{
    long[] numpos = new long[4];
    NumPos(long[] numpos){
        this.numpos = numpos.clone();
    }
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof NumPos)) return false;

        NumPos other = (NumPos) obj;

        for (int i = 0; i <= 3; i++)
            if (numpos[i]!=other.numpos[i])
                return false;
        return true;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < numpos.length; ++i)
            hash += numpos[i];

        return hash;
    }
}