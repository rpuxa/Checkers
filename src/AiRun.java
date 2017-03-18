
public class AiRun {

   public double[] analyze(Position position,int depth, int maxDepth){
       if (position.movePiece!=null)
           depth--;

       if (depth==0){
           double[] analyzedMoves = new double[100];
           double max = -10000;
           int i=0;
           for (Point[] move:
                   position.validMovesWhite) {
               i++;
               double result = analyze(new Position(Game.replace(position,move[0].x,move[0].y,move[1].x,move[1].y)),1,maxDepth)[0];
               if (result>max)
                   max=result;
               analyzedMoves[i]=result;
           }
           analyzedMoves[0]=max;
           return analyzedMoves;
       }

       if (depth==maxDepth)
           return new double[]{analyzeMaxDepth(position)};

        if (depth%2==0){
           double min = 10000;
           for (Point[] move:
                   position.validMovesBlack) {
               double result = analyze(new Position(Game.replace(position,move[0].x,move[0].y,move[1].x,move[1].y)),depth+1,maxDepth)[0];
               if (result<min)
                   min=result;
           }
           return new double[]{min};
       }

       if (depth%2==1){
           double max = -10000;
           for (Point[] move:
                   position.validMovesWhite) {
               double result = analyze(new Position(Game.replace(position,move[0].x,move[0].y,move[1].x,move[1].y)),depth+1,maxDepth)[0];
               if (result>max)
                   max=result;
           }
           return new double[]{max};
       }
       return new double[]{0};
    }

    private double analyzeMaxDepth(Position position){
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
