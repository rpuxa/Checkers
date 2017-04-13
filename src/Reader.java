import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Objects;

public class Reader {

    static ArrayList<BaseGame> games = new ArrayList<>();

    public static void main(String[] args) {
        read();
    }

    static void read() {
        try (FileReader reader = new FileReader("f.txt")) {
            boolean error = false;
            Character bracket = null;
            Deque<Character> stack = new ArrayDeque<>();
            BaseGame game = new BaseGame();
            // читаем посимвольно
            int c;
            int countBracket = 0;
            label:
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                if (bracket!=null) {
                    if (character == '[' && bracket == ']')
                        countBracket++;
                    if (character == '{' && bracket == '}')
                        countBracket++;
                    if (character == '(' && bracket == ')')
                        countBracket++;
                }
                if (bracket == null) {
                    if (character == '[')
                        bracket = ']';
                    if (character == '{')
                        bracket = '}';
                    if (character == '(')
                        bracket = ')';
                    if (character == '$')
                        bracket = ' ';
                }
                if (bracket != null && character == bracket) {
                    countBracket--;
                    if (countBracket==-1) {
                        countBracket = 0;
                        bracket = null;
                    }
                    continue;
                }
                if (bracket == null) {
                    if (character != ' ' && character != '\n' && character != '\r') {
                        stack.addLast(character);
                    }
                    if (character == '.')
                        stack.clear();
                    else if (stack.size() != 0 && (character == ' ' || stack.getFirst() == '1' || stack.getFirst() == '0' || stack.getLast() == '2')) {
                        if ((stack.size() == 3 && stack.getLast() != '2') || stack.size() == 7) {
                            if (stack.size() == 7 || error)
                                game.result = 0.5;
                            else if (stack.getFirst() == '1')
                                game.result = 1.0;
                            else if (stack.getFirst() == '0')
                                game.result = 0.0;
                            if (error)
                                try {
                                    game.moves.remove(game.moves.size()-1);
                                } catch (Exception e) {
                                }
                            if (!error)
                            games.add(new BaseGame(game));
                           System.out.println(games.size());
                            game = new BaseGame();
                            stack.clear();
                            game.result = null;
                            error = false;
                           /* if (games.size()>=1000 && games.size()%1000==0) {
                                Coeff.counting();
                                games.clear();
                            }*/
                            if (games.size()>=1000)
                                break label;
                        } else if (character == ' ' && !error) {
                            try {
                                ArrayList<Character> stackList = new ArrayList(stack);
                                    stackList = new ArrayList<>(refactor(game.positions.get(game.positions.size() - 1), stackList.get(0) - 'a', stackList.get(1) - '1', stackList.get(3) - 'a', stackList.get(4) - '1', true));
                                for (int i = 0; i <= stackList.size() - 3; i += 3) {
                                    game.MakeMove(stackList.get(i), stackList.get(i + 1), stackList.get(i + 3), stackList.get(i + 4), i == stackList.size() - 5);
                                }
                                char[] arr = new char[stackList.size()];
                                for (int i = 0; i < stackList.size(); i++)
                                    arr[i] = stackList.get(i);
                                game.moves.add(String.copyValueOf(arr));
                                stack.clear();
                                game.result = null;
                            } catch (Exception e) {
                                System.out.println("Ошибка");
                                error = true;
                            }
                        }
                        else if (character == ' ' && error){
                            stack.clear();
                            game.result = null;
                        }
                    }
                }
            }
        } catch (IOException ignore) {
        }
       /* try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Base.dat"))) {
            oos.writeObject(games);
        } catch (Exception ignore) {
        }*/
        Coeff.counting();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Coeffs.dat"))) {
          //  oos.writeObject(Coeff.coeffs);
        } catch (Exception ignore) {}
    }

    static Deque<Character> refactor(CompressedPos position ,int x, int y,int tox, int toy, boolean first) {
        ArrayList<Point[]> validMoves = new ArrayList<>();
        boolean take = false;
        boolean isTurnWhite;
        isTurnWhite = position.pieces[position.pos[x][y]].isWhite;
        final int[][] directions = {
                {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
        };
        if (!position.pieces[position.pos[x][y]].isQueen) {
            for (int[] direction : directions)
                if (AiRun.isInBoard(x + direction[0], y + direction[1]))
                    if ((isTurnWhite && direction[1] == 1 || !isTurnWhite && direction[1] == -1) && !take && position.pos[x + direction[0]][y + direction[1]] == null) {
                        validMoves.add(new Point[]{new Point(x, y), new Point(x + direction[0], y + direction[1])});
                    } else if (position.pos[x + direction[0]][y + direction[1]] != null && AiRun.isInBoard(x + 2 * direction[0], y + 2 * direction[1]) && position.pieces[position.pos[x][y]].isWhite == !position.pieces[position.pos[x + direction[0]][y + direction[1]]].isWhite && position.pos[x + 2 * direction[0]][y + 2 * direction[1]] == null) {
                        if (!take)
                            validMoves.clear();
                        take = true;
                        validMoves.add(new Point[]{new Point(x, y), new Point(x + 2 * direction[0], y + 2 * direction[1])});
                    }
        } else {
            for (int[] direction : directions) {
                boolean isQueenTake = false, isQueenMultitake = false;
                ArrayList<Point[]> validMovesQueen = new ArrayList<>();
                for (int i = 1; AiRun.isInBoard(x + i * direction[0], y + i * direction[1]); i++) {
                    if (position.pos[x + i * direction[0]][y + i * direction[1]] != null && (isQueenTake || isTurnWhite == position.pieces[position.pos[x + i * direction[0]][y + i * direction[1]]].isWhite || (position.pos[x + i * direction[0]][y + i * direction[1]] != null
                            && (!AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) || position.pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] != null))))
                        break;
                    else if (!isQueenTake && !take && position.pos[x + i * direction[0]][y + i * direction[1]] == null) {
                        validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                    } else if (!isQueenMultitake && isQueenTake && position.pos[x + i * direction[0]][y + i * direction[1]] == null) {
                        isQueenMultitake = isQueenMultitake || takeQueen(position,x + i * direction[0], y + i * direction[1], direction, isTurnWhite);
                        if (isQueenMultitake)
                            validMovesQueen.clear();
                        validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});

                    } else if (isQueenMultitake && isQueenTake && position.pos[x + i * direction[0]][y + i * direction[1]] == null && takeQueen(position,x + i * direction[0], y + i * direction[1], direction, isTurnWhite)) {
                        validMovesQueen.add(new Point[]{new Point(x, y), new Point(x + i * direction[0], y + i * direction[1])});
                    } else if (position.pos[x + i * direction[0]][y + i * direction[1]] != null && !isQueenTake && position.pieces[position.pos[x][y]].isWhite == !position.pieces[position.pos[x + i * direction[0]][y + i * direction[1]]].isWhite && AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]) && position.pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null) {
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
        if (!first && !take)
            return null;

        Deque<Character> moves = new ArrayDeque<>();
        for (Point[] move:
             validMoves) {
            if (move[1].x == tox && move[1].y == toy) {
                moves.addLast((char)(x+'a'));
                moves.addLast((char)(y+'1'));
                moves.addLast('x');
                moves.addLast((char)(tox+'a'));
                moves.addLast((char)(toy+'1'));
                return moves;
            } else {
                Deque<Character> movesR = refactor(MakeMove(new CompressedPos(position),move[0].x,move[0].y,move[1].x,move[1].y),move[1].x,move[1].y,tox,toy,false);
                if (movesR!=null){
                    movesR.addFirst('x');
                    movesR.addFirst((char)(y+'1'));
                    movesR.addFirst((char)(x+'a'));
                    return movesR;
                }
            }
        }
        return null;
    }

    static boolean takeQueen(CompressedPos position,int x, int y, int[] direction2, boolean isTurnWhite) {
        int[][] directions = {{-direction2[0], direction2[1]}, {direction2[0], -direction2[1]}};
        for (int[] direction : directions)
            for (int i = 1; AiRun.isInBoard(x + (i + 1) * direction[0], y + (i + 1) * direction[1]); i++)
                if (position.pos[x + i * direction[0]][y + i * direction[1]] != null) {
                    if (isTurnWhite == !position.pieces[position.pos[x + i * direction[0]][y + i * direction[1]]].isWhite && position.pos[x + (i + 1) * direction[0]][y + (i + 1) * direction[1]] == null)
                        return true;
                    else
                        break;
                }
        return false;
    }

    static CompressedPos MakeMove(CompressedPos position, int x1, int y1, int x2, int y2){
        Piece[] pieces = new Piece[24];
        Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
        ArrayList<Integer> livePieces = new ArrayList<>(position.livePieces);

        for (int i = 0; i < position.pieces.length; ++i)
            if (position.pieces[i] != null)
                pieces[i] = new Piece(position.pieces[i].isWhite, position.pieces[i].isQueen);

        for (int i = 0; i <= 7; ++i)
            pos[i] = position.pos[i].clone();


        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (pos[i][j] != null && (y1 < y2 && (x1 < x2 && i > x1 && i < x2 && j > y1 && j < y2 && x1 - y1 == i - j || x1 > x2 && i < x1 && i > x2 && j > y1 && j < y2 && x1 + y1 == i + j)
                        || y1 > y2 && (x1 < x2 && i > x1 && i < x2 && j < y1 && j > y2 && x1 + y1 == i + j || x1 > x2 && i < x1 && i > x2 && j < y1 && j > y2 && x1 - y1 == i - j))) {
                    livePieces.remove(pos[i][j]);
                    pos[i][j] = null;
                }
            }

        pos[x2][y2] = pos[x1][y1];
        pos[x1][y1] = null;

        if ((pieces[pos[x2][y2]].isWhite && y2 == 7 || !pieces[pos[x2][y2]].isWhite && y2 == 0))
            pieces[pos[x2][y2]].isQueen = true;

        return new CompressedPos(pieces,pos,livePieces);
    }
}

class BaseGame implements Serializable{
    ArrayList<CompressedPos> positions;
    ArrayList<String> moves;
    Double result;
    BaseGame(){
        positions = new ArrayList<>();
        positions.add(new CompressedPos());
        moves = new ArrayList<>();
    }
    BaseGame(BaseGame game){
        this.positions = new ArrayList<>(game.positions);
        this.result = game.result;
        this.moves = new ArrayList<>(game.moves);
    }

    void MakeMove(char cx1, char cy1, char cx2, char cy2,boolean last){

        int x1 = cx1 - 'a', y1 = cy1 - '1', x2 = cx2 - 'a', y2 = cy2 - '1';

        CompressedPos position = positions.get(positions.size()-1);

        Piece[] pieces = new Piece[24];
        Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
        ArrayList<Integer> livePieces = new ArrayList<>(position.livePieces);

        for (int i = 0; i < position.pieces.length; ++i)
            if (position.pieces[i] != null)
                pieces[i] = new Piece(position.pieces[i].isWhite, position.pieces[i].isQueen);

        for (int i = 0; i <= 7; ++i)
            pos[i] = position.pos[i].clone();


        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (pos[i][j] != null && (y1 < y2 && (x1 < x2 && i > x1 && i < x2 && j > y1 && j < y2 && x1 - y1 == i - j || x1 > x2 && i < x1 && i > x2 && j > y1 && j < y2 && x1 + y1 == i + j)
                        || y1 > y2 && (x1 < x2 && i > x1 && i < x2 && j < y1 && j > y2 && x1 + y1 == i + j || x1 > x2 && i < x1 && i > x2 && j < y1 && j > y2 && x1 - y1 == i - j))) {
                    livePieces.remove(pos[i][j]);
                    pos[i][j] = null;
                }
            }

        pos[x2][y2] = pos[x1][y1];
        pos[x1][y1] = null;

        if ((pieces[pos[x2][y2]].isWhite && y2 == 7 || !pieces[pos[x2][y2]].isWhite && y2 == 0))
            pieces[pos[x2][y2]].isQueen = true;
        if (!last)
            positions.remove(positions.size()-1);
        positions.add(new CompressedPos(pieces, pos, livePieces));
    }

}

class CompressedPos implements Serializable{
    Piece[] pieces = new Piece[24];
    Integer[][] pos = new Integer[Game.BOARD_SIZE][Game.BOARD_SIZE];
    ArrayList<Integer> livePieces = new ArrayList<>();

    CompressedPos(){
        for (int i = 0; i <= 23; i++)
            livePieces.add(i, i);
        for (int i = 0; i <= 11; i++)
            pieces[i] = new Piece(true, false);
        for (int i = 12; i <= 23; i++)
            pieces[i] = new Piece(false, false);
        int i = 0;
        for (int x = 0; x <= 6; x += 2)
            for (int y = 0; y <= 2; y++) {
                if (y % 2 == 0)
                    pos[x][y] = i;
                else
                    pos[x + 1][y] = i;
                i++;
            }
        for (int x = 7; x >= 1; x -= 2)
            for (int y = 7; y >= 5; y--) {
                if (y % 2 == 1)
                    pos[x][y] = i;
                else
                    pos[x - 1][y] = i;
                i++;
            }
    }

    CompressedPos(Piece[] pieces, Integer[][] pos, ArrayList<Integer> livePieces) {
        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != null)
                this.pieces[i] = new Piece(pieces[i].isWhite, pieces[i].isQueen);
        for (int i = 0; i <= 7; ++i) {
            this.pos[i] = pos[i].clone();
        }
        this.livePieces = new ArrayList<>(livePieces);
    }

    CompressedPos(CompressedPos position) {
        for (int i = 0; i < pieces.length; ++i)
            if (position.pieces[i] != null)
                this.pieces[i] = new Piece(position.pieces[i].isWhite, position.pieces[i].isQueen);
        for (int i = 0; i < Game.BOARD_SIZE; ++i) {
            this.pos[i] = position.pos[i].clone();
        }
        this.livePieces = new ArrayList<>(position.livePieces);
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof CompressedPos)) return false;

        CompressedPos other = (CompressedPos) obj;

        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i].isQueen != other.pieces[i].isQueen)
                return false;

        for (int x = 0; x < 8 ; x++)
            for (int y = 0; y < 8; y++)
                if (!Objects.equals(pos[x][y], other.pos[x][y]))
                    return false;
        return true;
    }
    public int hashCode(){
        final int HASH_DIVIDER = 43046721;
        long hash = 1;
            for (int i = 0; i < Game.BOARD_SIZE; i++)
                for (int j = 0; j < Game.BOARD_SIZE; j++)
                    if ((i+j) % 2 == 0){
                        hash *= 3L;
                        if (pos[i][j] != null)
                            hash += (pieces[pos[i][j]].isWhite) ? 1L : 2L;

                    }
        int leftHash = (int) (hash / HASH_DIVIDER);
        int rightHash = (int) (hash % HASH_DIVIDER);

        return (leftHash ^ rightHash);
    }

}
