import java.util.*;


class AiRun {

    ArrayList<Point[]> legalMoves;
    Map<NumPos, ArrayList<Integer>> hashValidMoves;
    Map<Integer,Map<NumPos, Double[]>> hashPos = new HashMap<>();
    long timeToMove;
    AiRun(long timeToMove){
        this.legalMoves = new ArrayList<>();
        this.hashValidMoves = new HashMap<>();
        this.timeToMove = timeToMove;
    }

    ArrayList<Point[]> bfs(Position position) {
        Double min =10000.0;
        ArrayList<Point[]> result = new ArrayList<>();
            Point[] resultMovePoint = new Point[2];
            Double[] movesscore = new Double[100];
            while (true) {
                position = update(position, false);
                legalMoves = new ArrayList<>(position.validMoves);
                if (legalMoves.size() == 1) {
                    int resultMove = 0;
                    resultMovePoint[0] = new Point(legalMoves.get(resultMove)[0].x, legalMoves.get(resultMove)[0].y);
                    resultMovePoint[1] = new Point(legalMoves.get(resultMove)[1].x, legalMoves.get(resultMove)[1].y);
                } else {

                    label:
                    for (int i = 0; (System.currentTimeMillis() - Game.st <= timeToMove); i += 2) {
                        if (Objects.equals(Thread.currentThread().getName(), "0 "))
                            System.out.println(i+4);
                        Double[] anl = analyze(new Position(position), 0, i + 4, 0, new int[100], i == 0);
                        if (!(Game.threadNumber==-1 || Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName()))) {
                            System.out.println("Завершен "+Thread.currentThread().getName());
                            return new ArrayList<>();
                        }
                        for (int j = 1; j < anl.length; j++)
                            if (anl[j] != null) {
                                movesscore[j - 1] = anl[j];
                                if (movesscore[j - 1] < -250)
                                    break label;
                            }
                            if (i>=100)
                                break;
                    }
                    min = 10000.0;
                    int resultMove = 0;
                    for (int i = 0; movesscore[i] != null; i++)
                        if (movesscore[i] < min) {
                            min = movesscore[i];
                            resultMove = i;
                        }
                    resultMovePoint[0] = new Point(legalMoves.get(resultMove)[0].x, legalMoves.get(resultMove)[0].y);
                    resultMovePoint[1] = new Point(legalMoves.get(resultMove)[1].x, legalMoves.get(resultMove)[1].y);
                }
                result.add(new Point[]{new Point(resultMovePoint[0].x, resultMovePoint[0].y), new Point(resultMovePoint[1].x, resultMovePoint[1].y)});
                position = Game.MakeMove(new Position(position),resultMovePoint[0].x,resultMovePoint[0].y,resultMovePoint[1].x,resultMovePoint[1].y);
                    if (position.movePiece == null)
                        break;
            }
            if (Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName())) {
                System.out.println("Не завершен "+Thread.currentThread().getName());
                Game.score = min;
                for (int i = 0; i < hashPos.size()+10; i++)
                    if (Game.hashPos.get(i)!=null && hashPos.get(i)!=null)
                        Game.hashPos.get(i).putAll(hashPos.get(i));
                return result;
            }
        while (Game.threadNumber==-1) {
            System.out.print("");
        }
        if (Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName()))
            Game.score = min;
            return new ArrayList<>(result);
    }



    private Double[] analyze(Position position, int depth, int maxDepth, double alpha, int[] lp, boolean first) {
        if (depth != 0 && position.movePiece != null)
            depth--;

        if (depth == 0) {
            position = update(position,false);
            Double[] analyzedMoves = new Double[1000];
            double min = 300 - 0.01 * depth;
            Deque<Integer> sequence = new ArrayDeque<>();
            int i = 0;
            for (Point[] move :
                    position.validMoves) {
                i++;
                int[] lpr = lp.clone();
                lpr[depth] = countLP(new Position(position));
                double result;
                Position newPosition = Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y);
                Double[] hash = new Double[3];
                try {
                    hash = Game.hashPos.get(Game.movesInGame + depth + 1).get(newPosition.getNumPos(depth % 2 == 1)).clone();
                }
                catch (NullPointerException e) {
                    try{
                        hash = hashPos.get(Game.movesInGame + depth + 1).get(newPosition.getNumPos(depth % 2 == 1)).clone();
                    }
                    catch (NullPointerException ignore) {}
                }
                if (hash[0]==null || (hash[0]!=null && hash[1]<(maxDepth-depth))) {
                    result = analyze(newPosition, 1, maxDepth, min, lpr, first)[0];
                    if (!(System.currentTimeMillis() - Game.st >= Game.timeToMove && !first)){
                        hashPos.computeIfAbsent(Game.movesInGame + depth + 1, k -> new HashMap<>());
                    hashPos.get((Game.movesInGame+depth+1)).put(newPosition.getNumPos(depth%2==1),new Double[]{result,(double)(maxDepth-depth)});
                    }
                }
                else
                    result = hash[0];
                if (System.currentTimeMillis() - Game.st >= Game.timeToMove && !first)
                    break;
                if (!(Game.threadNumber==-1 || Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName())))
                    return new Double[]{1.0};
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
            legalMoves = new ArrayList<>(position.validMoves);
            return analyzedMoves;
        }

        position = update(position, depth % 2 == 1);

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
            double result;
            Position newPosition = Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y);
            Double[] hash = new Double[3];
            try {
                hash = Game.hashPos.get(Game.movesInGame + depth + 1).get(newPosition.getNumPos(depth % 2 == 1)).clone();
            }
            catch (NullPointerException e) {
                try {
                    hash = hashPos.get(Game.movesInGame + depth + 1).get(newPosition.getNumPos(depth % 2 == 1)).clone();
                }
                catch (NullPointerException ignore) {}
            }
            if (hash[0]==null || (hash[0]!=null && hash[1]<(maxDepth-depth))) {
                result = analyze(newPosition, depth + 1, maxDepth, (depth % 2 == 0) ? min : max, lpr, first)[0];
                if (!(System.currentTimeMillis() - Game.st >= Game.timeToMove && !first)) {
                    hashPos.computeIfAbsent(Game.movesInGame + depth + 1, k -> new HashMap<>());
                    hashPos.get((Game.movesInGame + depth + 1)).put(newPosition.getNumPos(depth % 2 == 1), new Double[]{result, (double) (maxDepth - depth)});
                }
            }
            else
                result = hash[0];
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
            if (System.currentTimeMillis() - Game.st >= Game.timeToMove && !first)
                break;
            if (!(Game.threadNumber==-1 || Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName())))
                return new Double[]{1.0};
        }
        hashingValidMoves(hashValidMoves.get(position.getNumPos(depth%2==1)), new ArrayList<>(sequence), position.getNumPos(depth % 2 == 1), position.validMoves.size());
        return new Double[]{(depth % 2 == 0) ? min : max};
    }

    Position update(Position position, boolean isTurnWhite) {
        Piece[] pieces = new Piece[24];
        Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
        ArrayList<Integer> livePieces;
        ArrayList<Point[]> validMoves;
        Point movePiece;
        for (int i = 0; i < pieces.length; ++i)
            if (position.pieces[i] != null)
                pieces[i] = new Piece(position.pieces[i].isWhite, position.pieces[i].isQueen);
        for (int i = 0; i < Game.BOARD_SIZE; ++i) {
            pos[i] = position.pos[i].clone();
        }
        livePieces = new ArrayList<>(position.livePieces);
        validMoves = new ArrayList<>(position.validMoves);
        boolean take;
        if (position.movePiece != null)
            movePiece = new Point(position.movePiece.x, position.movePiece.y);
        else
            movePiece = null;

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
                        if (isInBoard(x + direction[0], y + direction[1]))
                            if ((isTurnWhite && direction[1] == 1 || !isTurnWhite && direction[1] == -1) && !take && pos[x + direction[0]][y + direction[1]] == null) {
                                validMoves.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            } else if (pos[x + direction[0]][y + direction[1]] != null && isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pos[x][y]].isWhite == !pieces[pos[x + direction[0]][y + direction[1]]].isWhite && pos[x + 2 * direction[0]][y + 2 * direction[1]] == null) {
                                if (!take)
                                    validMoves.clear();
                                take = true;
                                validMoves.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                            }
                } else {
                    for (int[] direction : directions) {
                        boolean isQueenTake = false, isQueenMultitake = false;
                        ArrayList<Point[]> validMovesQueen = new ArrayList<>();
                        for (int i = 1; isInBoard(x + i * direction[0], y + i * direction[1]); i++) {
                            if (pos[x + i * direction[0]][y + i * direction[1]] != null && (isQueenTake || isTurnWhite == pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite || (pos[x + i * direction[0]][y + i * direction[1]] != null
                                    && (!isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) || pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] != null))))
                                break;
                            else if (!isQueenTake && !take && pos[x + i * direction[0]][y + i * direction[1]] == null) {
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                            } else if (!isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null) {
                                isQueenMultitake = isQueenMultitake || takeQueen(new Position(position), x + i * direction[0], y + i * direction[1], direction, isTurnWhite);
                                if (isQueenMultitake)
                                    validMovesQueen.clear();
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});

                            } else if (isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null && takeQueen(new Position(position),x + i * direction[0], y + i * direction[1], direction, isTurnWhite)) {
                                validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                            } else if (pos[x + i * direction[0]][y + i * direction[1]] != null && !isQueenTake && pieces[pos[x][y]].isWhite == !pieces[pos[x + i * direction[0]][y + i * direction[1]]].isWhite && isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) && pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null) {
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
        ArrayList<Integer> sequence = hashValidMoves.get(position.getNumPos(isTurnWhite));
        if (sequence!=null) {
            ArrayList<Point[]> moves = new ArrayList<>();
            for (int i : sequence)
                moves.add(validMoves.get(i));
            validMoves = new ArrayList<>(moves);
        }
        return new Position(pieces,pos,livePieces,validMoves,take,movePiece);
    }

    static boolean isInBoard(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }

    static boolean takeQueen(Position position, int x, int y, int[] direction2, boolean isTurnWhite) {
        int[][] directions = {{-direction2[0], direction2[1]}, {direction2[0], -direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1; isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                if (position.pos[x + i * direction[0]][y + i * direction[1]] != null)
                    return isTurnWhite == !position.pieces[position.pos[x + i * direction[0]][y + i * direction[1]]].isWhite && position.pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null;
        return false;
    }

    private void hashingValidMoves(ArrayList<Integer> AfterSequence, ArrayList<Integer> sequence, NumPos numPos, int size) {
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

    private double Evaluate(Position position) {
        final double costPiece = 1;
        final double costQueen = 3 * costPiece;
        final double costOfCell = -0.002;
        final double passtoQueen = 0.5;
        final double flank = -0.01 * position.livePieces.size() / 24;
        final double bigWay = 0.07;

        int blackPieces = 0,whitePieces = 0,blackQueens = 0,whiteQueens = 0;
        boolean BigWayWhite = false,BigWayBlack = false;
        for (int n:
             position.livePieces)
            if (position.pieces[n].isWhite){
                if (position.pieces[n].isQueen)
                    whiteQueens++;

                else
                    whitePieces++;
            }
            else {
                if (position.pieces[n].isQueen)
                    blackQueens++;
                else
                     blackPieces++;
            }
            if (whiteQueens+blackQueens>0)
        for (int i = 0; i < 8; i++)
            if (position.pos[i][i]!=null && position.pieces[position.pos[i][i]].isQueen)
                if (position.pieces[position.pos[i][i]].isWhite)
                    BigWayWhite = true;
                else
                    BigWayBlack = true;


        //Draw
        if (whiteQueens==1 && whitePieces==0 && blackQueens>=1 && blackPieces+blackQueens<=3 && BigWayWhite || blackQueens==1 && blackPieces==0 && whiteQueens>=1 && whitePieces+whiteQueens<=3 && BigWayBlack)
            return 0;
        if (whiteQueens==1 && whitePieces==1 && blackQueens>=1 && blackPieces+blackQueens<=4 && blackPieces>=1 || blackQueens==1 && blackPieces==1 && whiteQueens>=1 && whitePieces+blackQueens<=4 && whitePieces>=1)
            return 0;


        double anl = 0;
        if (BigWayBlack)
            anl-=bigWay;
        if (BigWayWhite)
            anl+=bigWay;

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

    private boolean passToQueen(Position position, int x, int y) {
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

    private int countLP(Position position) {
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