import java.util.ArrayList;
import java.util.List;

public class main {
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

class Pos{
    Piece[] pieces = new Piece[24];
    Integer[][] coordinates = new Integer[8][8];
    List<Integer> livePieces;
    List<Point[]> validMovesWhite;
    List<Point[]> validMovesBlack;
    Pos(Piece[] pieces, Integer[][] coordinates, List<Integer> livePieces,List<Point[]> validMovesWhite,List<Point[]> validMovesBlack){
        this.pieces = pieces.clone();
        this.coordinates = coordinates.clone();
        this.livePieces =  new ArrayList(livePieces);
        this.validMovesWhite = new ArrayList(validMovesWhite);
        this.validMovesBlack = new ArrayList(validMovesBlack);
    }

    void update(boolean isTurnWhite){
        boolean takeWhite=false,takeBlack=false;
        int x=0,y=0;
        final int[][] directions = {
                {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
        };
        for (int pieceNumber:
             livePieces) {
            label: for (int i = 0; i <= 8; i+=2)
                for (int j = 0; j <= 8; j+=2)
                    if (coordinates[i][j]==pieceNumber) {
                        x = i;
                        y = j;
                        break label;
                    }
            if (!pieces[pieceNumber].isQueen) {
                for (int[] direction : directions)
                    if (isInBoard(x + direction[0], y + direction[1]))
                        if (((!takeWhite && isTurnWhite) || ((!takeBlack && !isTurnWhite))) && coordinates[x + direction[0]][y + direction[1]] == null) {
                            if (isTurnWhite)
                                validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            else
                                validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                        }
                        else if (isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pieceNumber].isWhite == !pieces[coordinates[x + direction[0]][y + direction[1]]].isWhite && coordinates[x + 2 * direction[0]][y + 2 * direction[1]] == null)
                            if (isTurnWhite) {
                                validMovesWhite.clear();
                                takeWhite = true;
                                validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            } else {
                                validMovesBlack.clear();
                                takeBlack = true;
                                validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            }
            }
            else{
                boolean isQueenTake = false, isQueenMultitake = false;
                        for (int[] direction : directions)
                            for (int i = 1;isInBoard(x+i*direction[0],y+i*direction[1]);i++)
                                if (!isQueenMultitake && ((!takeWhite && isTurnWhite) || ((!takeBlack && !isTurnWhite))) && coordinates[x + i*direction[0]][y + i*direction[1]] == null) {
                                    if (isTurnWhite)
                                        validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                                    else
                                        validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                                }
                                else if (isQueenTake && coordinates[x + i*direction[0]][y + i*direction[1]] == null){

                                }
                                else if (!isQueenTake && pieces[pieceNumber].isWhite == !pieces[coordinates[x + i*direction[0]][y + i*direction[1]]].isWhite)
                                    isQueenTake=true;



            }




        }

    }

    boolean isInBoard(int x,int y){
        return (x>0 && x<9 && y>0 && y<9);
    }

    boolean take(int x,int y, int[] direction2){
        int[][] directions = {{-direction2[0],direction2[1]},{direction2[0],-direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1;isInBoard(x+i*direction[0],y+i*direction[1]);i++)
                if ()
    }

}
