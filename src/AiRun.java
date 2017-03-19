import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AiRun {

    static Map<HashPos,Double> hashPos = new HashMap<>();

    static void bfs(int depth) {
        String out1="",out2;
        Point[][] moves = new Point[100][2];
        int k=0,move;
        while (true) {
            Game.position.update(false);
            if (Game.position.validMovesBlack.size() == 1) {
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(0)[0].x, Game.position.validMovesBlack.get(0)[0].y, Game.position.validMovesBlack.get(0)[1].x, Game.position.validMovesBlack.get(0)[1].y);
                move = 0;
            } else {
                System.out.println("Идет анализ:");
                Double[] anl = analyze(new Position(Game.position),0,depth,0);
                Double min = 10000.0;
                System.out.println("Оценка: " + ((double)(Math.round(anl[0]*100000)))/100000);
                int resultMove = 0;
                for (int i = 1; anl[i]!=null ; i++)
                    if (anl[i]<min) {
                        min = anl[i];
                        resultMove = i-1;
                    }
               // Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(resultMove)[0].x, Game.position.validMovesBlack.get(resultMove)[0].y, Game.position.validMovesBlack.get(resultMove)[1].x, Game.position.validMovesBlack.get(resultMove)[1].y);
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

    private static Double[] analyze(Position position, int depth, int maxDepth, double alpha){
       if (depth!=0 && position.movePiece!=null)
           depth--;
        double result;
       if (depth==0){
           position.update(false);
           Double[] analyzedMoves = new Double[100];
           double min = 300-0.01*depth;
           int i=0;
           if (position.validMovesBlack.size()==1)
               maxDepth++;
           for (Point[] move:
                   position.validMovesBlack) {
               i++;
               System.out.println(100*i/position.validMovesBlack.size()+"% ");
               try {
                   result = equels(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y),depth);
               }
               catch (NullPointerException e) {
                   result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), 1, maxDepth, min)[0];
                   hashPos.put(new HashPos(position,depth),result);
               }
               if (result<min)
                   min=result;
               analyzedMoves[i]=result;
           }
           analyzedMoves[0]=min;
           return analyzedMoves;
       }

       if (depth==maxDepth)
           return new Double[]{analyzeMaxDepth(new Position(position))};

        if (depth%2==0){
            position.update(false);
           double min = 300-0.01*depth;
            if (position.validMovesBlack.size()==1)
                maxDepth++;
           for (Point[] move:
                   position.validMovesBlack) {
               try {
                   result = equels(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y),depth);
               }
               catch (NullPointerException e) {
                   result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), depth + 1, maxDepth, min)[0];
                   hashPos.put(new HashPos(position,depth),result);
               }
               if (result<min)
                   min=result;
               if (alpha > min)
                   return new Double[]{min};
           }
           return new Double[]{min};
       }

       if (depth%2==1){
           double max = -300+0.01*depth;
           position.update(true);
           if (position.validMovesWhite.size()==1)
               maxDepth++;
           for (Point[] move:
                   position.validMovesWhite) {
               try {
                   result = equels(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y),depth);
               }
               catch (NullPointerException e) {
                   result = analyze(Game.replace(new Position(position), move[0].x, move[0].y, move[1].x, move[1].y), depth + 1, maxDepth, max)[0];
                   hashPos.put(new HashPos(position,depth),result);
               }
               if (result>max)
                   max=result;
               if (alpha < max)
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
       final double captureCenter = 0.15*position.livePieces.size()/24;

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
        return anl;
    }

    private static Double equels(Position position,int depth){
        for (int i = 0; i <= depth; i++)
            if (hashPos.get(new HashPos(position,i))!=null)
                return hashPos.get(position);
        return null;
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

