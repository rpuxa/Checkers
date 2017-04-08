import java.io.*;
import java.util.*;

class Debut {

    static ArrayList<BaseGame> games = Reader.games;
    static final Map<PosInfo, DebutPos> debut = new HashMap<>();

    static void filling() {

        for (BaseGame game : games){
            for (int i = 0; i < game.positions.size(); i++) {
                CompressedPos position = game.positions.get(i);
                DebutPos pos = debut.get(new PosInfo(position));
                try {
                    if (pos == null) {
                        DebutPos newPos = new DebutPos();
                        newPos.countGames++;
                        newPos.moves.add(new DebutMoves(strToByteArr(game.moves.get(i)), 1));
                        debut.put(new PosInfo(position), newPos);
                    } else
                        for (int j = 0; j < pos.moves.size(); j++) {
                            String move = byteArrToStr(pos.moves.get(j).move);
                            if (Objects.equals(move, game.moves.get(i))){
                                pos.moves.get(j).count++;
                                pos.countGames++;
                                break;
                            }
                            if (j == pos.moves.size()-1) {
                                pos.moves.add(new DebutMoves(strToByteArr(game.moves.get(i)), 1));
                                pos.countGames++;
                                break;
                            }
                        }
                        if (pos!=null)
                            debut.put(new PosInfo(position), pos);
                } catch (IndexOutOfBoundsException ignore) {
                }
            }
    }
    ArrayList<PosInfo> removed = new ArrayList<>();
        for (PosInfo posInfo : debut.keySet()) {
            if (debut.get(posInfo).countGames < 3) {
                removed.add(posInfo);
            }
        }
        for (PosInfo posInfo : removed)
            debut.remove(posInfo);

    }
   static byte[] strToByteArr(String stringMove){
        return new byte[]{(byte)(8*(stringMove.charAt(0)-'a')+(stringMove.charAt(1)-'1')),(byte)(8*(stringMove.charAt(3)-'a')+(stringMove.charAt(4)-'1'))};
    }
    static String byteArrToStr(byte[] byteMove){
       return new String(new char[]{(char)((int)byteMove[0]/8+'a'),(char)((int)byteMove[0]%8+'1'),'x',(char)((int)byteMove[1]/8+'a'),(char)((int)byteMove[1]%8+'1')});
    }


    private static final int COORD_BITS = 3,X_SHIFT = 0, Y_SHIFT = X_SHIFT + COORD_BITS, COLOR_SHIFT = Y_SHIFT + COORD_BITS, QUEEN_SHIFT = COLOR_SHIFT + 1;

    static short compressPiece(Piece piece, int x, int y) {
        short info = 0;
        info |= (x);
        info |= (y << Y_SHIFT);

        if (piece.isWhite) info |= (1 << COLOR_SHIFT);
        if (piece.isQueen) info |= (1 << QUEEN_SHIFT);

        return info;
    }

}

class PosInfo implements Serializable{
    public short[] pieces;
    PosInfo(){

    }

    PosInfo(short[] pieces){
        this.pieces = pieces.clone();
    }

    PosInfo(CompressedPos position){
        int count = 0;
        this.pieces = new short[position.livePieces.size()];
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (position.pos[x][y]!=null){
                    pieces[count] = Debut.compressPiece(position.pieces[position.pos[x][y]],x,y);
                    count++;
                }
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PosInfo)) return false;

        PosInfo other = (PosInfo) obj;

        if (pieces.length != other.pieces.length)
            return false;

        for (int i = 0; i < pieces.length; ++i)
            if (pieces[i] != other.pieces[i])
                return false;

        return true;
    }

    public int hashCode(){
        return Arrays.hashCode(pieces);
    }
}

class DebutMoves implements Serializable{
    byte[] move;
    int count;

    DebutMoves(byte[] move, int count){
        this.move = move.clone();
        this.count = count;
    }
}

class DebutPos implements Serializable{
    ArrayList<DebutMoves> moves;
    int countGames;
    DebutPos(){
        moves = new ArrayList<>();
    }
}

