
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class AiRun {

    static Map<HashPos,Double> hashPos = new HashMap<>();

    static void bfs() {
        Double[] movesscore = new Double[100];
        String out1="",out2;
        int k=0,move;
        while (true) {
            Game.position.update(false);
            if (Game.position.validMovesBlack.size() == 1) {
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(0)[0].x, Game.position.validMovesBlack.get(0)[0].y, Game.position.validMovesBlack.get(0)[1].x, Game.position.validMovesBlack.get(0)[1].y);
                move = 0;
            } else {
                label: for (int i = 0; System.currentTimeMillis() - Game.st <= 30000; i++) {
                    System.out.println("Идет анализ c глубиной: " + (2*(i/2)) + ((i%2==0) ? " с отсечениями":" без отсечений"));
                    Double[] anl = analyze(new Position(Game.position), 0, (2*(i/2)), 0, new int[100], (i%2==0));
                    for (int j = 0; j < anl.length; j++)
                        if (anl[j]!=null) {
                            movesscore[j] = anl[j];
                            if (movesscore[j]<-250)
                            break label;
                        }
                }
                Double min = 10000.0;
                System.out.println("Оценка: " + ((double)(Math.round(movesscore[0]*100000)))/100000);
                int resultMove = 0;
                for (int i = 1; movesscore[i]!=null ; i++)
                    if (movesscore[i]<min) {
                        min = movesscore[i];
                        resultMove = i-1;
                    }
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(resultMove)[0].x, Game.position.validMovesBlack.get(resultMove)[0].y, Game.position.validMovesBlack.get(resultMove)[1].x, Game.position.validMovesBlack.get(resultMove)[1].y);
                move = resultMove;
            }
            if (k==0)
            out1 = "Ход: " + (char)(Game.position.validMovesBlack.get(move)[0].x+97) + (Game.position.validMovesBlack.get(move)[0].y+1);
            if (Game.position.movePiece==null)
                break;
            k++;
        }
        out2 = " " + (char)(Game.position.validMovesBlack.get(move)[1].x+97) + (Game.position.validMovesBlack.get(move)[1].y+1) + ";";
        System.out.println(out1+out2);
    }

    private static Double[] analyze(Position position, int depth, int maxDepth, double alpha,int[] lp, boolean puring){
       if (depth!=0 && position.movePiece!=null)
           depth--;
        double result;
       if (depth==0){
           position.update(false);
           Double[] analyzedMoves = new Double[100];
           double min = 300-0.01*depth;
           int i=0;
           for (Point[] move:
                   position.validMovesBlack) {
               i++;
               int[] lpr = lp.clone();
                   lpr[depth] = countLP(new Position(position));
                   result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), 1, maxDepth, min,lpr,puring)[0];
               if (result<min)
                   min=result;
               analyzedMoves[i]=result;
               if (System.currentTimeMillis() - Game.st >= 30000)
                   break;
               if (min<-200)
                   break;
           }
           analyzedMoves[0]=min;
           return analyzedMoves;
       }
           if (depth % 2 ==0)
               position.update(false);
               else
               position.update(true);

        if (puring && depth%2==1 && depth>=3 && lp[depth]<lp[depth-2] && !position.takeWhite)
            maxDepth-=2;
       if (puring && depth%2==0 && depth>=2 && lp[depth]>lp[depth-2] && !position.takeBlack)
            maxDepth-=2;

       if (depth>=maxDepth)
           return new Double[]{analyzeMaxDepth(new Position(position))};

        if (depth%2==0){
           double min = 300-0.01*depth;
            if (position.validMovesBlack.size()==1 && position.movePiece==null)
               maxDepth++;
           for (Point[] move:
                   position.validMovesBlack) {
               int[] lpr = lp.clone();
               lpr[depth] = countLP(new Position(position));
                   result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), depth + 1, maxDepth, min,lpr,puring)[0];
               if (result<min)
                   min=result;
               if (alpha >= result)
                   return new Double[]{min};
               if (min<-200)
                   return new Double[]{min};
           }
           return new Double[]{min};
       }

       if (depth%2==1){
           double max = -300+0.01*depth;
           if (position.validMovesWhite.size()==1 && position.movePiece==null)
              maxDepth++;
           for (Point[] move:
                   position.validMovesWhite) {
               int[] lpr = lp.clone();
               lpr[depth] = countLP(new Position(position));
                   result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), depth + 1, maxDepth, max,lpr,puring)[0];
               if (result>max)
                   max=result;
              if ( alpha <= result)
                  return new Double[]{max};
              if(max>200)
               return new Double[]{max};
           }
           return new Double[]{max};
       }
       return new Double[]{0.0};
    }

    private static double analyzeMaxDepth(Position position){
       final double costPiece = 1;
       final double costQueen = 3*costPiece;
       final double proximityToQueen = 0.01;
       final double captureCenter = 0.8*position.livePieces.size()/24;
       final double firstHorizontal = 0.04;
       final double cornerPiece = -0.05;
       final double passtoQueen = 1.5;

       double anl = 0;
        for (int i = 0; i < position.livePieces.size(); i++)
            if (position.pieces[position.livePieces.get(i)].isWhite) {
                if (position.pieces[position.livePieces.get(i)].isQueen)
                    anl += costQueen;
                else {
                    anl += costPiece;
                }
            }
            else{
                    if (position.pieces[position.livePieces.get(i)].isQueen)
                        anl -= costQueen;
                    else
                        anl -= costPiece;
            }

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++) {
            if (position.pos[x][y]!=null && !position.pieces[position.pos[x][y]].isQueen)
                if (position.pieces[position.pos[x][y]].isWhite) {
                    anl += y * proximityToQueen;
                }
                else if (!position.pieces[position.pos[x][y]].isWhite) {
                    anl -= (7 - y) * proximityToQueen;
                }
            }

        final int[][] center = {{2,4},{3,3},{4,4},{5,3}};
        for (int coord[]:
             center) {
            int x = coord[0],y = coord[1];
            if (position.pos[x][y] != null)
                anl += (position.pieces[position.pos[x][y]].isWhite) ? captureCenter:-captureCenter;
        }
        final int[][] firstHorizontalPos = {{2,0},{4,0},{6,0}};
        for (int coord[]:
                firstHorizontalPos) {
            int x = coord[0], y = coord[1];
            if (position.pos[x][y] != null)
                if (position.pieces[position.pos[x][y]].isWhite && !position.pieces[position.pos[x][y]].isQueen)
                    anl += firstHorizontal;
                else if (position.pieces[position.pos[x][y]].isWhite && !position.pieces[position.pos[x][y]].isQueen)
                    anl -= firstHorizontal;
        }

        if (position.pos[0][0] != null && position.pieces[position.pos[0][0]].isWhite && !position.pieces[position.pos[0][0]].isQueen)
            anl+=cornerPiece;
        if (position.pos[7][7] != null && !position.pieces[position.pos[7][7]].isWhite && !position.pieces[position.pos[7][7]].isQueen)
            anl-=cornerPiece;

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (position.pos[x][y]!=null && passToQueen(new Position(position),x,y))
                    anl+=(position.pieces[position.pos[x][y]].isWhite) ? passtoQueen:-passtoQueen;

        return anl;
    }

    private static boolean passToQueen(Position position,int x, int y) {
        boolean pass = true;
        if (position.pieces[position.pos[x][y]].isWhite) {
            for (int i = 0; i <= 7; i++)
                for (int j = 0; j <= 7; j++)
                    if (j >= y && i <= x && (x + y - 2) - i <= j && position.pos[i][j]!=null && !position.pieces[position.pos[i][j]].isWhite)
                        pass = false;

            if (!pass) {
                for (int i = 0; i <= 7; i++)
                    for (int j = 0; j <= 7; j++)
                        if (j >= y && i >= x && i - (x - y + 2) <= j && position.pos[i][j]!=null && !position.pieces[position.pos[i][j]].isWhite)
                            return false;
            }

            return true;
        }
        else{
            for (int i = 0; i <= 7; i++)
                for (int j = 0; j <= 7; j++)
                    if (j <= y && i <= x && i - (x - y - 2) >= j && position.pos[i][j]!=null && position.pieces[position.pos[i][j]].isWhite)
                        pass = false;

            if (!pass)
                for (int i = 0; i <= 7; i++)
                    for (int j = 0; j <= 7; j++)
                        if (j <= y && i >= x && (x + y + 2) - i >= j && position.pos[i][j]!=null && position.pieces[position.pos[i][j]].isWhite)
                            return false;
            return true;
        }
    }

    private static Double equals(Position position,int depth){
        for (int i = depth; i <= depth; i+=2)
            if (hashPos.get(new HashPos(position,i))!=null)
                return hashPos.get(new HashPos(position,i));
        return null;
    }

    private static int countLP(Position position){
        int count = 0;
        for (int i = 0; i < position.livePieces.size(); i++) {
           count += (position.pieces[position.livePieces.get(i)].isWhite) ? 1:-1;
        }
        return count;
    }

}

class HashPos {
    boolean[] pieces = new boolean[24];
    Integer[][] pos = new Integer[8][8];
    int depth;

    HashPos(Position position, int depth) {
        for (int i = 0; i <= 23; i++)
            this.pieces[i] = position.pieces[i].isQueen;
        for (int i = 0; i <= 7; ++i) {
            this.pos[i] = position.pos[i].clone();
        }
        this.depth = depth;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof HashPos)) return false;

        HashPos other = (HashPos) obj;

        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != other.pieces[i]) return false;

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (!Objects.equals(pos[x][y], other.pos[x][y])) return false;


        return depth == other.depth;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < pieces.length; ++i) {
            hash += (pieces[i]) ? 1:2;
            hash *= 5;
        }

        for (int x = 0; x <= 7; x++)
            for (int y = 0; y <= 7; y++)
                if (pos[x][y]!=null)
                hash+=x*7+y*11;

        hash += depth*257;
        return hash;
    }

}

