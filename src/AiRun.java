
public class AiRun {

    public static void bfs(int depth) {
        System.out.print("Ход: ");
        while (true) {
            Game.position.update(false);
            int move = 0;
            if (Game.position.validMovesBlack.size() == 1) {
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(0)[0].x, Game.position.validMovesBlack.get(0)[0].y, Game.position.validMovesBlack.get(0)[1].x, Game.position.validMovesBlack.get(0)[1].y);
                move = 0;
            } else {
                Double[] anl = analyze(new Position(Game.position),0,depth);
                Double min = 10000.0;
                int resultMove = 0;
                for (int i = 1; anl[i]!=null ; i++)
                    if (anl[i]<min) {
                        min = anl[i];
                        resultMove = i;
                    }
                Game.position = Game.replace(Game.position, Game.position.validMovesBlack.get(resultMove)[0].x, Game.position.validMovesBlack.get(resultMove)[0].y, Game.position.validMovesBlack.get(resultMove)[1].x, Game.position.validMovesBlack.get(resultMove)[1].y);
                move = resultMove;
            }
            System.out.println(Game.position.validMovesBlack.get(move)[0].x + "," + Game.position.validMovesBlack.get(move)[0].y +" : "+ Game.position.validMovesBlack.get(move)[1].x + "," + Game.position.validMovesBlack.get(move)[1].y);
            if (Game.position.movePiece==null)
                break;
        }

    }

   public static Double[] analyze(Position position,int depth, int maxDepth){
       if (depth!=0 && position.movePiece!=null)
           depth--;

       if (depth==0){
           position.update(false);
           Double[] analyzedMoves = new Double[100];
           double min = 300-0.01*depth;
           int i=0;
           for (Point[] move:
                   position.validMovesBlack) {
               i++;
               double result = analyze(Game.replace(new Position(position),move[0].x,move[0].y,move[1].x,move[1].y),1,maxDepth)[0];
               if (result<min)
                   min=result;
               analyzedMoves[i]=result;
           }
           analyzedMoves[0]=min;
           return analyzedMoves;
       }

       if (depth==maxDepth)
           return new Double[]{analyzeMaxDepth(position)};

        if (depth%2==0){
            position.update(false);
           double min = 300-0.01*depth;
           for (Point[] move:
                   position.validMovesBlack) {
               double result = analyze(Game.replace(new Position(position),move[0].x,move[0].y,move[1].x,move[1].y),depth+1,maxDepth)[0];
               if (result<min)
                   min=result;
           }
           return new Double[]{min};
       }

       if (depth%2==1){
           double max = -300+0.01*depth;
           position.update(true);
           for (Point[] move:
                   position.validMovesWhite) {
               double result = analyze(Game.replace(new Position(position),move[0].x,move[0].y,move[1].x,move[1].y),depth+1,maxDepth)[0];
               if (result>max)
                   max=result;
           }
           return new Double[]{max};
       }
       return new Double[]{0.0};
    }

    private static double analyzeMaxDepth(Position position){
       double anl = 0;
        for (int i = 0; i < position.livePieces.size(); i++)
            if (position.pieces[position.livePieces.get(i)].isWhite) {
                if (position.pieces[position.livePieces.get(i)].isQueen)
                    anl += 3.5;
                else
                    anl += 1;
            }
            else{
                    if (position.pieces[position.livePieces.get(i)].isQueen)
                        anl -= 3.5;
                    else
                        anl -= 1;
            }
        return anl;
    }


}
