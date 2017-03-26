
import java.util.*;


class AiRun {

    private static Map<NumPosWithDepth, Double> hashPos = new HashMap<>();
    static Map<NumPos, ArrayList<Integer>> hashValidMoves = new HashMap<>();
    static int timeToMove = 0;
    private static int CountPositions = 0;
    private static int MAX_DEEPING = 0;

    static void bfs() {
        Double[] movesscore = new Double[100];
        String out1 = "", out2;
        int k = 0, move;
        while (true) {
            Game.position.update(false,-1);
            if (Game.position.validMoves.size() == 1) {
                Game.position = Game.MakeMove(Game.position, Game.position.validMoves.get(0)[0].x, Game.position.validMoves.get(0)[0].y, Game.position.validMoves.get(0)[1].x, Game.position.validMoves.get(0)[1].y);
                move = 0;
            } else {

                label:
                for (int i = 0; System.currentTimeMillis() - Game.st <= timeToMove; i += 2) {
                    System.out.println("Идет анализ c глубиной: " + (i + 4));
                    Double[] anl = analyze(new Position(Game.position), 0, i + 4, 0, new int[100], i == 0);
                    CountPositions += hashPos.size();
                    hashPos.clear();
                    for (int j = 1; j < anl.length; j++)
                        if (anl[j] != null) {
                            movesscore[j - 1] = anl[j];
                            if (movesscore[j - 1] < -250)
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
                System.out.println("Максимальное заглубление " + MAX_DEEPING + " ходов");
                Game.position = Game.MakeMove(Game.position, Game.position.validMoves.get(resultMove)[0].x, Game.position.validMoves.get(resultMove)[0].y, Game.position.validMoves.get(resultMove)[1].x, Game.position.validMoves.get(resultMove)[1].y);
                move = resultMove;
            }
            if (k == 0)
                out1 = "Ход: " + (char) (Game.position.validMoves.get(move)[0].x + 'a') + (Game.position.validMoves.get(move)[0].y + 1);
            if (Game.position.movePiece == null)
                break;
            k++;
        }
        out2 = " " + (char) (Game.position.validMoves.get(move)[1].x + 'a') + (Game.position.validMoves.get(move)[1].y + 1) + ";";
        System.out.println(out1 + out2);
        CountPositions = 0;
        MAX_DEEPING = 0;
    }

    private static Double[] analyze(Position position, int depth, int maxDepth, double alpha, int[] lp, boolean first) {
        if (depth > MAX_DEEPING)
            MAX_DEEPING = depth;
        try {
            double m = hashPos.get(position.getNumPosWithDepth(depth % 2 == 1, depth));
            return new Double[]{m};
        } catch (NullPointerException ignored) {
        }
        if (depth != 0 && position.movePiece != null)
            depth--;

        if (depth == 0) {
            position.update(false,0);
            Double[] analyzedMoves = new Double[100];
            double min = 300 - 0.01 * depth;
            Deque<Integer> sequence = new ArrayDeque<>();
            int i = 0;
            for (Point[] move :
                    position.validMoves) {
                i++;
                int[] lpr = lp.clone();
                lpr[depth] = countLP(new Position(position));
                double result = analyze(Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), 1, maxDepth, min, lpr, first)[0];
                if (System.currentTimeMillis() - Game.st >= timeToMove && !first) {
                    analyzedMoves[0] = min;
                    return analyzedMoves;
                }
                if (result < min) {
                    min = result;
                    sequence.addFirst(i-1);
                }
                else
                    sequence.addLast(i-1);
                analyzedMoves[i] = result;
                if (min < -200)
                    break;
            }
            analyzedMoves[0] = min;
            hashingValidMoves(hashValidMoves.get(position.getNumPos(depth%2==1)), new ArrayList<>(sequence), position.getNumPos(depth % 2 == 1), position.validMoves.size());
            return analyzedMoves;
        }

        position.update(depth % 2 == 1,depth);

        if (!position.take) {
            if (depth % 2 == 1 && depth >= 3 && lp[depth] < lp[depth - 2])
                maxDepth -= 2;
            if (depth % 2 == 0 && depth >= 2 && lp[depth] > lp[depth - 2])
                maxDepth -= 2;
        }

        if (depth >= maxDepth)
            return new Double[]{Evaluate(new Position(position))};

        double min = 300 - 0.01 * depth, max = -300 + 0.01 * depth;
        int i = -1;
        Deque<Integer> sequence = new ArrayDeque<>();
        if ((position.take || position.validMoves.size() == 1) && position.movePiece == null)
            maxDepth++;

        for (Point[] move :
                position.validMoves) {
            i++;
            int[] lpr = lp.clone();
            lpr[depth] = countLP(new Position(position));
            double result = analyze(Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), depth + 1, maxDepth, (depth % 2 == 0) ? min : max, lpr, first)[0];
            if (depth % 2 == 0) {
                if (result < min) {
                    min = result;
                    sequence.addFirst(i);
                } else
                    sequence.addLast(i);
                if (alpha < 200 && alpha >= result)
                    break;
                if (min < -200)
                    break;
            } else {
                if (result > max) {
                    max = result;
                    sequence.addFirst(i);
                } else
                    sequence.addLast(i);
                if (alpha > -200 && alpha <= result)
                    break;
                if (max > 200)
                    break;
            }
            if (System.currentTimeMillis() - Game.st >= timeToMove && !first)
                break;
        }
        hashingValidMoves(hashValidMoves.get(position.getNumPos(depth%2==1)), new ArrayList<>(sequence), position.getNumPos(depth % 2 == 1), position.validMoves.size());
        hashPos.put(position.getNumPosWithDepth(depth % 2 == 1, depth), (depth % 2 == 0) ? min : max);
        return new Double[]{(depth % 2 == 0) ? min : max};
    }

    private static void hashingValidMoves(ArrayList<Integer> AfterSequence, ArrayList<Integer> sequence, NumPos numPos, int size) {
        if (AfterSequence!=null) {
            ArrayList<Integer> beforeSequence = new ArrayList<>();
            for (int i : sequence)
                beforeSequence.add(AfterSequence.get(i));
            for (int i : sequence)
                AfterSequence.remove(beforeSequence.get(i));
            beforeSequence.addAll(AfterSequence);
            hashValidMoves.put(new NumPos(numPos), beforeSequence);
        }
        else {
            for (int i = 0; i < size; i++)
                try {
                sequence.get(i);
                }
                catch (IndexOutOfBoundsException e) {
                    sequence.add(i, i);
                }
            hashValidMoves.put(new NumPos(numPos), new ArrayList<>(sequence));
        }
    }

    private static double Evaluate(Position position) {
        final double costPiece = 1;
        final double costQueen = 3 * costPiece;
        final double costOfCell = -0.002;
        final double passtoQueen = 0.5;
        final double flank = -0.01 * position.livePieces.size() / 24;

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

        final double[] cells = {19, 0, 13, 0, 7, 0, 1, 0, 0, 16, 0, 8, 0, 0, 0, 0, 15, 0, 9, 0, 1, 0, 0, 0, 0, 12, 0, 6, 0, 1, 0, 0, 12, 0, 8, 0, 1, 0, 0, 0, 0, 14, 0, 7, 0, 1, 0, 0, 17, 0, 11, 0, 3, 0, 0, 0, 0, 18, 0, 11, 0, 4, 0, 1};

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (position.pos[x][y] != null && !position.pieces[position.pos[x][y]].isQueen)
                    if (position.pieces[position.pos[x][y]].isWhite)
                        anl += cells[8 * x + y] * costOfCell;
                    else
                        anl -= cells[8 * (7 - x) + (7 - y)] * costOfCell;


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
    long[] numPos = new long[4];
    NumPos(long[] numPos){
        this.numPos = numPos.clone();
    }
    NumPos(NumPos numPos){this.numPos = numPos.numPos.clone();}
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof NumPos)) return false;

        NumPos other = (NumPos) obj;

        for (int i = 0; i <= 3; i++)
            if (numPos[i]!=other.numPos[i])
                return false;
        return true;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < numPos.length; ++i)
            hash += numPos[i];

        return hash;
    }
}

class NumPosWithDepth{
    long[] numPos = new long[4];
    int depth;
    NumPosWithDepth(long[] numPos, int depth){
        this.numPos = numPos.clone();
        this.depth = depth;
    }
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof NumPosWithDepth)) return false;

        NumPosWithDepth other = (NumPosWithDepth) obj;

        for (int i = 0; i <= 3; i++)
            if (numPos[i]!=other.numPos[i])
                return false;
        return other.depth == depth;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < numPos.length; ++i)
            hash += numPos[i];
        hash += 257*depth;
        return hash;
    }
}