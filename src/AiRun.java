import java.util.*;

class AiRun {

    ArrayList<Point[]> legalMoves;
    Map<Integer, Map<Long, Moves>> hashValidMoves;
    int threadNumber;
    long timeToMove;
    int realDepth = 0;

    AiRun(long timeToMove, int threadNumber) {
        this.legalMoves = new ArrayList<>();
        this.hashValidMoves = new TreeMap<>();
        this.timeToMove = timeToMove;
        this.threadNumber = threadNumber;
    }

    int generateIndex(double[] p) {
        double prob = Math.random();

        int index = Arrays.binarySearch(p,prob);
        if (index < 0) index = ~index;

        return index;
    }

    ArrayList<Point[]> bfs(Position position) throws InterruptedException {
        DebutPos pos = null;

        try {
            pos = Game.debut.get(new PosInfo(new CompressedPos(position.pieces, position.pos, position.livePieces)));
        }
        catch (NullPointerException ignore) {}

        if (pos != null){
            double[] p = new double[pos.moves.size()];
            System.out.println( pos.moves.get(0).count);
            System.out.println(pos.countGames);
            p[0] = (double) pos.moves.get(0).count / (double) pos.countGames;
            for (int i = 1; i < p.length; i++)
                p[i] = p[i-1] + (double)pos.moves.get(i).count / (double)pos.countGames;
            int index = generateIndex(p);
            String stringMove = Debut.byteArrToStr(pos.moves.get(index).move);
            ArrayList<Point[]> result = new ArrayList<>();
            for (int i = 0; i <= stringMove.length() - 3; i += 3) {
                result.add(new Point[]{new Point(stringMove.charAt(i)-'a',stringMove.charAt(i+1)-'1'),new Point(stringMove.charAt(i+3)-'a',stringMove.charAt(i+4)-'1')});
            }
            Game.score = -1000.0;
            while (Game.threadNumber == -1 && threadNumber != -9) {
                System.out.print("");
            }
            return result;
        }
        Double min = 10000.0;
        ArrayList<Point[]> result = new ArrayList<>();
        Point[] resultMovePoint = new Point[2];
        Double[] movesscore = new Double[100];
        while (true) {
            position = update(position, false, -1000, null);
            legalMoves = new ArrayList<>(position.validMoves);
            if (legalMoves.size() == 1) {
                int resultMove = 0;
                resultMovePoint[0] = new Point(legalMoves.get(resultMove)[0].x, legalMoves.get(resultMove)[0].y);
                resultMovePoint[1] = new Point(legalMoves.get(resultMove)[1].x, legalMoves.get(resultMove)[1].y);
            } else {
                label:
                for (int i = 0; (System.currentTimeMillis() - Game.st <= timeToMove); i += 2) {
                    realDepth = i+4;
                    //   if (Objects.equals(Thread.currentThread().getName(), "0 "))
                    System.out.print(i + 4 + " ");

                    Double[] anl = analyze(new Position(position), 0, i + 4, -1000, 1000, i <= 4, false,null, new Draw(Game.draw));
                    if (!(Game.threadNumber == -1 || Objects.equals(Game.threadNumber + " ", threadNumber + " "))) {
                        System.out.println("Завершен " + Thread.currentThread().getName());
                        return new ArrayList<>();
                    }
                    for (int j = 1; j < anl.length; j++)
                        if (anl[j] != null) {
                            movesscore[j - 1] = anl[j];
                            if (movesscore[j - 1] < -250)
                                break label;
                        }

                    if (i + 4 >= 6)
                        break;

                    if (System.currentTimeMillis() - Game.st >= Game.timeToMove)
                        break;
                }
                min = 10000.0;
                int resultMove = 0;
                for (int i = 0; i < movesscore.length; i++)
                    if (movesscore[i] != null && movesscore[i] < min) {
                        min = movesscore[i];
                        resultMove = i;
                    }
                resultMovePoint[0] = new Point(legalMoves.get(resultMove)[0].x, legalMoves.get(resultMove)[0].y);
                resultMovePoint[1] = new Point(legalMoves.get(resultMove)[1].x, legalMoves.get(resultMove)[1].y);
            }
            result.add(new Point[]{new Point(resultMovePoint[0].x, resultMovePoint[0].y), new Point(resultMovePoint[1].x, resultMovePoint[1].y)});
            position = Game.MakeMove(new Position(position), resultMovePoint[0].x, resultMovePoint[0].y, resultMovePoint[1].x, resultMovePoint[1].y);
            if (position.movePiece == null)
                break;
        }
        Thread.sleep(100);
        if (Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName())) {
            System.out.println("Не завершен " + Thread.currentThread().getName());
            Game.score = min;
            return result;
        }
        while (Game.threadNumber == -1 && threadNumber != -9) {
            System.out.print("");
        }
        if (Objects.equals(Game.threadNumber + " ", Thread.currentThread().getName()))
            Game.score = min;
        return new ArrayList<>(result);
    }

    private Double[] analyze(Position position, int depth, int maxDepth, double alpha, double beta, boolean first, boolean isNegaScoutOn, Point[] killerMove, Draw draw) throws InterruptedException {
        if (position.livePieces.size()<=3 && depth!=0 && position.movePiece==null)
        try {
            Position newPosition = new Position(position);
            if (depth % 2 == 0)
                newPosition.reverce();
            double d = (double)Game.endings.get(position.livePieces.size()).get(new PosInfo(new CompressedPos(newPosition.pieces,newPosition.pos,newPosition.livePieces)))/100;
            return new Double[]{(depth % 2 == 0) ? -d:d};
        } catch (NullPointerException ignore) {}

        try {
            Moves move = Game.hashValidMoves.get(Game.movesInGame + depth + 1).get(position.getNumPos(depth % 2 == 0));
      /*     if (move.depth >= realDepth - depth && depth!=0)
               try {
                   return new Double[]{move.score, (double) (move.validMoves.get(0)[0].x * 1000 + move.validMoves.get(0)[0].y * 100 + move.validMoves.get(0)[1].x * 10 + move.validMoves.get(0)[1].y)};
               }
               catch (IndexOutOfBoundsException e){
                   return new Double[]{move.score};
               }*/
            position.validMoves = new ArrayList<>(move.validMoves);
            position.take = move.take;
        } catch (NullPointerException ignore) {}

        if (depth != 0 && position.movePiece != null)
            depth--;

        if (depth == 0) {
            Deque<Point[]> sequence = new ArrayDeque<>();
            if (position.validMoves.size()==0)
                position = update(position, false, depth, null);
            ArrayList<Point[]> validMovesCopy = new ArrayList<>(position.validMoves);
            final Double[] analyzedMoves = new Double[1000];
            final double[] min = {300 - 0.01 * depth};
            int i = 0;
            for (Point[] move :
                    position.validMoves) {
                System.out.print((int)((double)(100*i+100)/position.validMoves.size()) +"% ");
                validMovesCopy.remove(0);
                i++;
                double result;
                Position newPosition = Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y);

                int whiteQueens = 0, blackQueens = 0;
                for (int c:
                        position.livePieces) {
                    if (position.pieces[c].isQueen && position.pieces[c].isWhite)
                        whiteQueens++;
                    if (position.pieces[c].isQueen && !position.pieces[c].isWhite)
                        blackQueens++;
                }

                Draw newDraw = new Draw(draw);

                newDraw.isDraw(position.take, position.pieces[position.pos[move[0].x][move[0].y]].isQueen, ((depth % 2 == 1) ? move[1].y == 7 : move[1].y == 0) && !position.pieces[position.pos[move[0].x][move[0].y]].isQueen, (blackQueens >= 3 && whiteQueens == 1 || whiteQueens >= 3 && blackQueens == 1) && position.livePieces.size() == whiteQueens + blackQueens, position.livePieces.size(), (whiteQueens > 0 && blackQueens > 0));

                result = analyze(newPosition, 1, maxDepth, alpha, beta, first, false, null,new Draw(draw))[0];

                if (System.currentTimeMillis() - Game.st >= Game.timeToMove && !first)
                    break;

                if (result < min[0]){
                    min[0] = result;
                    sequence.addFirst(new Point[]{new Point(move[0].x, move[0].y), new Point(move[1].x, move[1].y)});
                } else
                    sequence.addLast(new Point[]{new Point(move[0].x, move[0].y), new Point(move[1].x, move[1].y)});
                analyzedMoves[i] = result;

                if (min[0]<-200)
                    break;
            }
            sequence.addAll(validMovesCopy);
            Game.hashValidMoves.computeIfAbsent((Game.movesInGame + depth + 1), k -> new TreeMap<>());
                try {
                    Game.hashValidMoves.get((Game.movesInGame + depth + 1)).put(position.getNumPos(depth % 2 == 0),new Moves(new ArrayList<>(sequence),position.take,min[0],realDepth - depth));
                } catch (NullPointerException ignore) {}
            analyzedMoves[0] = min[0];
            legalMoves = new ArrayList<>(position.validMoves);
            System.out.println();
            return analyzedMoves;
        }

        if (position.validMoves.size()==0)
            position = update(position, depth % 2 == 1, depth, killerMove);

        if (depth >= maxDepth)
            return new Double[]{Evaluate(position)};

        double min = 300 - 0.01 * depth, max = -300 + 0.01 * depth;

        Deque<Point[]> sequence = new ArrayDeque<>();
        if ((position.take || position.validMoves.size() == 1) && position.movePiece == null)
            maxDepth++;

        ArrayList<Point[]> validMovesCopy = new ArrayList<>(position.validMoves);
        Point[] KillerMove = null;

        /*if (realDepth - 2 <= depth){
            double score = Evaluate(position);
            if (depth % 2 == 0) {
                if (score < beta)
                    beta = score;
            }
            else if (score > alpha)
                alpha = score;
        }*/

        for (Point[] move :
                position.validMoves) {
            validMovesCopy.remove(0);
            double result;
            Position newPosition = Game.MakeMove(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y);

             /*  if (depth % 2 == 0 && beta>300 || depth % 2 == 1 && alpha<-300)
                    result = analyze(newPosition, depth + 1, maxDepth, alpha,beta, first,false)[0];
                else{
                    if (depth % 2 == 0) {
                        result = analyze(newPosition, depth + 1, maxDepth, beta - 0.005, beta, first,true)[0];
                        if (result < beta && result > alpha)
                            result = analyze(newPosition, depth + 1, maxDepth, alpha,result, first,false)[0];
                    }
                    else {
                        result = analyze(newPosition, depth + 1, maxDepth, alpha, alpha + 0.005, first,true)[0];
                        if (result > alpha && result < beta)
                            result = analyze(newPosition, depth + 1, maxDepth, result, beta, first,false)[0];

                    }
                }*/

            int whiteQueens = 0, blackQueens = 0;
            for (int c:
                    position.livePieces) {
                if (position.pieces[c].isQueen && position.pieces[c].isWhite)
                    whiteQueens++;
                if (position.pieces[c].isQueen && !position.pieces[c].isWhite)
                    blackQueens++;
            }

            Draw newDraw = new Draw(draw);

            newDraw.isDraw(position.take, position.pieces[position.pos[move[0].x][move[0].y]].isQueen, ((depth % 2 == 1) ? move[1].y == 7 : move[1].y == 0) && !position.pieces[position.pos[move[0].x][move[0].y]].isQueen, (blackQueens >= 3 && whiteQueens == 1 || whiteQueens >= 3 && blackQueens == 1) && position.livePieces.size() == whiteQueens + blackQueens, position.livePieces.size(), (whiteQueens > 0 && blackQueens > 0));

            Double[] analyzed = analyze(newPosition, depth + 1, maxDepth, alpha, beta, first, false, KillerMove,new Draw(draw));
            result = analyzed[0];
                try {
                    KillerMove = new Point[]{new Point ((int)(double)analyzed[1]/1000,((int)(double)analyzed[1]/100) % 10), new Point(((int)(double)analyzed[1]/10) % 10,((int)(double)analyzed[1]) % 10)};
                } catch (Exception ignore) {}

            if (depth % 2 == 0) {
                if (result < min)
                    min = result;
                if (result < beta) {
                    beta = result;
                    sequence.addFirst(new Point[]{new Point(move[0].x, move[0].y), new Point(move[1].x, move[1].y)});
                } else
                    sequence.addLast(new Point[]{new Point(move[0].x, move[0].y), new Point(move[1].x, move[1].y)});
                if (alpha >= beta)
                    break;

                if (min < -200)
                    break;
            } else {
                if (result > max)
                    max = result;

                if (result > alpha) {
                    alpha = result;
                    sequence.addFirst(new Point[]{new Point(move[0].x, move[0].y), new Point(move[1].x, move[1].y)});
                } else
                    sequence.addLast(new Point[]{new Point(move[0].x, move[0].y), new Point(move[1].x, move[1].y)});
                if (alpha >= beta)
                    break;
                if (max > 200)
                    break;
            }
            if (System.currentTimeMillis() - Game.st >= Game.timeToMove && !first)
                break;
            if (!(Game.threadNumber == -1 || Objects.equals(Game.threadNumber + " ", threadNumber + " ")))
                return new Double[]{1.0};
        }
        sequence.addAll(validMovesCopy);
        if (!isNegaScoutOn) {
            Game.hashValidMoves.computeIfAbsent((Game.movesInGame + depth + 1), k -> new TreeMap<>());
            try {
                Game.hashValidMoves.get((Game.movesInGame + depth + 1)).put(position.getNumPos(depth % 2 == 0),new Moves(new ArrayList<>(sequence),position.take,((depth % 2 == 0) ? min : max), realDepth - depth));
            } catch (NullPointerException ignore) {}
        }
        Point[] move;
        Double doubleMove = null;
        try {
            move = sequence.getFirst();
            doubleMove = (double)(move[0].x*1000+move[0].y*100+move[1].x*10+move[1].y);
        } catch (Exception ignored) {}

        return new Double[]{(depth % 2 == 0) ? min : max,doubleMove};
    }

    Position update(Position position, boolean isTurnWhite, int depth, Point[] killerMove) {
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
        validMoves = new ArrayList<>();
        boolean take;
        if (position.movePiece != null)
            movePiece = new Point(position.movePiece.x, position.movePiece.y);
        else
            movePiece = null;

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
                            } else if (isQueenMultitake && isQueenTake && pos[x + i * direction[0]][y + i * direction[1]] == null && takeQueen(new Position(position), x + i * direction[0], y + i * direction[1], direction, isTurnWhite)) {
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
        if (killerMove!=null)
            for (int i = 0; i < validMoves.size(); i++) {
                Point[] move = validMoves.get(i);
                if (move[0].x == killerMove[0].x && move[0].y == killerMove[0].y && move[1].x == killerMove[1].x && move[1].y == killerMove[1].y) {
                    validMoves.remove(i);
                    validMoves.add(0, new Point[]{new Point(killerMove[0].x, killerMove[0].y), new Point(killerMove[1].x, killerMove[1].y)});
                    break;
                }
            }
        return new Position(pieces, pos, livePieces, validMoves, take, movePiece);
    }

    static boolean isInBoard(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }

    static boolean takeQueen(Position position, int x, int y, int[] direction2, boolean isTurnWhite) {
        int[][] directions = {{-direction2[0], direction2[1]}, {direction2[0], -direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1; isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                if (position.pos[x + i * direction[0]][y + i * direction[1]] != null) {
                    if (isTurnWhite == !position.pieces[position.pos[x + i * direction[0]][y + i * direction[1]]].isWhite && position.pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null)
                        return true;
                    else
                        break;
                }
        return false;
    }

    private double Evaluate(Position position) {
        final double costPiece = 1;
        final double costQueen = 3 * costPiece;
        final double costOfCell = -0.002;
        final double passtoQueen = 0.5;
        final double flank = -0.08;
        final double bigWay = 0.07;
        final double combination = 0.09;

        int blackPieces = 0, whitePieces = 0, blackQueens = 0, whiteQueens = 0;
        boolean BigWayWhite = false, BigWayBlack = false;
        for (int n :
                position.livePieces)
            if (position.pieces[n].isWhite) {
                if (position.pieces[n].isQueen)
                    whiteQueens++;

                else
                    whitePieces++;
            } else {
                if (position.pieces[n].isQueen)
                    blackQueens++;
                else
                    blackPieces++;
            }
        if (whiteQueens + blackQueens > 0)
            for (int i = 0; i < 8; i++)
                if (position.pos[i][i] != null && position.pieces[position.pos[i][i]].isQueen)
                    if (position.pieces[position.pos[i][i]].isWhite)
                        BigWayWhite = true;
                    else
                        BigWayBlack = true;


        //Draw
        if (whiteQueens == 1 && whitePieces == 0 && blackQueens >= 1 && blackPieces + blackQueens <= 2 || blackQueens == 1 && blackPieces == 0 && whiteQueens >= 1 && whitePieces + whiteQueens <= 2)
            return 0;
        if (whiteQueens == 1 && whitePieces == 0 && blackQueens >= 1 && blackPieces + blackQueens == 3 && BigWayWhite || blackQueens == 1 && blackPieces == 0 && whiteQueens >= 1 && whitePieces + whiteQueens == 3 && BigWayBlack)
            return 0;


        double anl = 0;
        if (BigWayBlack)
            anl -= bigWay;
        if (BigWayWhite)
            anl += bigWay;

        if (position.pos[2][4] != null && position.pos[7][5] != null && position.pieces[position.pos[2][4]].isWhite && !position.pieces[position.pos[2][4]].isQueen && position.pieces[position.pos[7][5]].isWhite && !position.pieces[position.pos[7][5]].isQueen)
            anl += combination;
        if (position.pos[5][3] != null && position.pos[0][2] != null && !position.pieces[position.pos[5][3]].isWhite && !position.pieces[position.pos[5][3]].isQueen && !position.pieces[position.pos[0][2]].isWhite && !position.pieces[position.pos[0][2]].isQueen)
            anl -= combination;
        if (position.pos[3][3] != null && position.pos[6][4] != null && position.pieces[position.pos[3][3]].isWhite && !position.pieces[position.pos[3][3]].isQueen && position.pieces[position.pos[6][4]].isWhite && !position.pieces[position.pos[6][4]].isQueen)
            anl += combination;
        if (position.pos[4][4] != null && position.pos[1][3] != null && position.pieces[position.pos[4][4]].isWhite && !position.pieces[position.pos[4][4]].isQueen && position.pieces[position.pos[1][3]].isWhite && !position.pieces[position.pos[1][3]].isQueen)
            anl += combination;

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
                if (position.pos[x][y] != null && passToQueen(position, x, y))
                    anl += (position.pieces[position.pos[x][y]].isWhite) ? passtoQueen : -passtoQueen;

        int rightFlankBlack = 0, leftFlankBlack = 0,rightFlankWhite = 0, leftFlankWhite = 0;
        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (position.pos[x][y] != null && !position.pieces[position.pos[x][y]].isQueen) {
                    if (x >= 4) {
                        if (position.pieces[position.pos[x][y]].isWhite)
                            rightFlankWhite++;
                        else
                            rightFlankBlack++;
                    }
                    else {
                        if (position.pieces[position.pos[x][y]].isWhite)
                            leftFlankWhite++;
                        else
                            leftFlankBlack++;
                    }
                }

        anl += Math.abs(rightFlankWhite - leftFlankWhite) * flank;
        anl -= Math.abs(rightFlankBlack - leftFlankBlack) * flank;

        anl *= 10000;
        double a = Math.round(anl);
        a /= 10000;
        return a;
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

    }

class Moves{
    ArrayList<Point[]> validMoves;
    boolean take;
    double score;
    int depth;
    Moves(ArrayList<Point[]> validMoves, boolean take, double score, int depth){
        this.validMoves = validMoves;
        this.take = take;
        this.score = score;
        this.depth = depth;
    }
}
