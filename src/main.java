import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class main {

    static Position position;

    public static void main(String[] agrs){
        Piece[] pieces = {new Piece(true,true), new Piece(false,false), new Piece(false,false), new Piece(true,false)};
        Integer[][] coordinates = new Integer[8][8];
        List<Integer> livePieces = new ArrayList<>();
        coordinates[0][0]=0;
        coordinates[6][1]=1;
        coordinates[6][4]=2;
        coordinates[7][0]=3;
        livePieces.add(0,0);
        livePieces.add(1,1);
        livePieces.add(2,2);
        livePieces.add(3,3);
       position = new Position(pieces, coordinates,livePieces,new ArrayList<>(),new ArrayList<>(),false,null);
       position.update(true);
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

class Piece{
    boolean isWhite;
    boolean isQueen;
    Piece(boolean isWhite, boolean isQueen){
        this.isWhite=isWhite;
        this.isQueen=isQueen;
    }
}

class Position{
    private Piece[] pieces = new Piece[24];
    private Integer[][] coordinates = new Integer[8][8];
    private List<Integer> livePieces;
    private ArrayList<Point[]> validMovesWhite;
    private List<Point[]> validMovesBlack;
    private boolean take;
    private Point movePiece;
    Position(Piece[] pieces, Integer[][] coordinates, List<Integer> livePieces,List<Point[]> validMovesWhite,List<Point[]> validMovesBlack, boolean take,Point movePiece){
        this.pieces = pieces.clone();
        this.coordinates = coordinates.clone();
        this.livePieces =  new ArrayList<>(livePieces);
        this.validMovesWhite = new ArrayList<>(validMovesWhite);
        this.validMovesBlack = new ArrayList<>(validMovesBlack);
        this.take=take;
        this.movePiece=movePiece;
    }

    public void update(boolean isTurnWhite){
        boolean takeWhite = false, takeBlack = false;
        final int[][] directions = {
                {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
        };
        for (Integer pieceNumber:
             livePieces) {
            int x = -1,y = 0;
            label: for (int i = 0; i <= 7; i++)
                for (int j = 0; j <= 7; j++)
                    if (pieces[pieceNumber].isWhite==isTurnWhite && Objects.equals(coordinates[i][j], pieceNumber)) {
                        x = i;
                        y = j;
                        break label;
            }
            if (x!=-1)
            if (!pieces[pieceNumber].isQueen) {
                for (int[] direction : directions)
                    if (isInBoard(x + direction[0], y + direction[1]))
                        if (((!takeWhite && isTurnWhite) || ((!takeBlack && !isTurnWhite))) && coordinates[x + direction[0]][y + direction[1]] == null) {
                            if (isTurnWhite)
                                validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                            else
                                validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                        }
                        else if (coordinates[x + direction[0]][y + direction[1]]!=null && isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && pieces[pieceNumber].isWhite == !pieces[coordinates[x + direction[0]][y + direction[1]]].isWhite && coordinates[x + 2 * direction[0]][y + 2 * direction[1]] == null)
                            if (isTurnWhite) {
                                    if (!takeWhite)
                                validMovesWhite.clear();
                                takeWhite = true;
                                validMovesWhite.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                            } else {
                                    if (!takeBlack)
                                validMovesBlack.clear();
                                takeBlack = true;
                                validMovesBlack.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                            }
            }
            else{
                boolean isQueenTake = false, isQueenMultitake = false;
                        for (int[] direction : directions) {
                            ArrayList<Point[]> validMovesQueen = new ArrayList<>();
                            for (int i = 1; isInBoard(x + i * direction[0], y + i * direction[1]); i++) {
                                if (coordinates[x + i * direction[0]][y + i * direction[1]] != null && (isQueenTake || isTurnWhite == pieces[coordinates[x + i * direction[0]][y + i * direction[1]]].isWhite || (coordinates[x + i * direction[0]][y + i * direction[1]] != null
                                        && (!isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) || coordinates[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] != null))))
                                    break;
                                else if (!isQueenTake && ((!takeWhite && isTurnWhite) || ((!takeBlack && !isTurnWhite))) && coordinates[x + i * direction[0]][y + i * direction[1]] == null) {
                                    validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                                } else if (!isQueenMultitake && isQueenTake && coordinates[x + i * direction[0]][y + i * direction[1]] == null) {
                                    isQueenMultitake = isQueenMultitake || take(x + i * direction[0], y + i * direction[1], direction, isTurnWhite);
                                    if (isQueenMultitake)
                                    validMovesQueen.clear();
                                    validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});

                                } else if (isQueenMultitake && isQueenTake && coordinates[x + i * direction[0]][y + i * direction[1]] == null && take(x + i * direction[0], y + i * direction[1], direction, isTurnWhite)) {
                                    validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                                } else if (coordinates[x + i * direction[0]][y + i * direction[1]]!=null && !isQueenTake && pieces[pieceNumber].isWhite == !pieces[coordinates[x + i * direction[0]][y + i * direction[1]]].isWhite && isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) && coordinates[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null) {
                                    isQueenTake = true;
                                    if (isTurnWhite) {
                                        if (!takeWhite)
                                            validMovesWhite.clear();
                                        takeWhite = true;
                                    } else if (!takeBlack)
                                        validMovesBlack.clear();
                                    takeBlack = true;
                                    validMovesQueen.clear();
                                }
                            }
                                if (isTurnWhite)
                                    validMovesWhite.addAll(0,validMovesQueen);
                                else
                                    validMovesBlack.addAll(0,validMovesQueen);
                        }
            }
        }
    take = takeBlack || takeWhite;
    }

    private boolean isInBoard(int x,int y){
        return (x>=0 && x<=7 && y>=0 && y<=7);
    }

    private boolean take(int x,int y, int[] direction2,boolean isTurnWhite){
        int[][] directions = {{-direction2[0],direction2[1]},{direction2[0],-direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1;isInBoard(x+i*direction[0]+1,y+i*direction[1]+1);i++)
                if (coordinates[x + i*direction[0]][y + i*direction[1]] != null)
                    return isTurnWhite == !pieces[coordinates[x + i * direction[0]][y + i * direction[1]]].isWhite && coordinates[x + i * direction[0] + 1][y + i * direction[1] + 1] == null;
        return false;
    }

}
