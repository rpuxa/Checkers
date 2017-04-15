
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Coeff {

    static double[] y = new double[36];
    static double[][] x = new double[53000][36];
    static double[] p = new double[53000];
    static double[] n = new double[53000];
    static int count;

    public static void counting() {
        int c = 0;
        for (BaseGame game : Reader.games) {
            double result;
            double endGame = 0;
            try {
                result = game.result;
            } catch (NullPointerException e) {
                continue;
            }
            for (CompressedPos position : game.positions) {
                coeff(position, result, c,game.positions.size()-endGame);
                c++;
                endGame++;
            }
        }
        count = c;
        System.out.println(c);
        search(33,-3,3);

        /*
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Coeffs.dat"))) {
            oos.writeObject(y);
        } catch (Exception ignore) {
        }*/
    }

    static void search(int j,double a, double b) {
        if (Math.abs(a-b)<0.00001)
            System.out.println(a);
        else {
            if (funtion(a, j) > 0 != funtion((a + b) / 2, j) > 0)
                search(j, a, (a + b) / 2);
            if (funtion(b, j) > 0 != funtion((a + b) / 2, j) > 0)
                search(j, (a + b) / 2, b);
        }
    }


    static double funtion(double a, int j) {
        double answer = 0;
        for (int i = 0; i < count; i++)
            answer += (1 / (1 + Math.exp(-a * x[i][j])) - p[i]) * (Math.exp(-a * x[i][j] - n[i] / 40)) / Math.pow(1 + Math.exp(-a * x[i][j]),2);
        return answer;
    }


    static void coeff(CompressedPos position, double result, int number, double endGame) {
        int FlankWhite = 0;
        boolean passW = false;
        boolean queenBlack = false;
        for (int n :
                position.livePieces) {
            if (position.pieces[n].isQueen) {
                if (!position.pieces[n].isWhite)
                    queenBlack = true;
                break;
            }
        }
        int count = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if ((i + j) % 2 == 0) {
                    if (position.pos[i][j] != null && !position.pieces[position.pos[i][j]].isQueen)
                        if (position.pieces[position.pos[i][j]].isWhite)
                            x[number][count] = 1;


                        if (position.pos[i][j] != null && !position.pieces[position.pos[i][j]].isQueen) {
                            if (i >= 4) {
                                if (position.pieces[position.pos[i][j]].isWhite)
                                    FlankWhite++;
                            } else {
                                if (position.pieces[position.pos[i][j]].isWhite)
                                    FlankWhite--;
                            }
                        }

                        if (position.pos[i][j] != null && passToQueen(position, i, j))
                            if ((position.pieces[position.pos[i][j]].isWhite)) {
                                if (!queenBlack)
                                    passW = true;
                            }

                    count++;
                }

        for (int in = 0; in < 8; in++)
            if (position.pos[in][in] != null && position.pieces[position.pos[in][in]].isQueen)
                if (position.pieces[position.pos[in][in]].isWhite)
                    x[number][34] = 1;


        if (passW)
            x[number][32] = 1;

        x[number][33] = Math.abs(FlankWhite);

        if (position.pos[2][4] != null && position.pos[7][5] != null && position.pieces[position.pos[2][4]].isWhite && !position.pieces[position.pos[2][4]].isQueen && position.pieces[position.pos[7][5]].isWhite && !position.pieces[position.pos[7][5]].isQueen)
            x[number][35]++;
        if (position.pos[3][3] != null && position.pos[6][4] != null && position.pieces[position.pos[3][3]].isWhite && !position.pieces[position.pos[3][3]].isQueen && position.pieces[position.pos[6][4]].isWhite && !position.pieces[position.pos[6][4]].isQueen)
            x[number][35]++;
        p[number] = result;
        n[number] = endGame;
    }

    private static boolean passToQueen(CompressedPos position, int x, int y) {
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
class Statistic implements Serializable{
    int wins;
    int loses;
    int all;
    Statistic(){}
    Statistic(Statistic statistic){
        this.wins = statistic.wins;
        this.loses = statistic.loses;
        this.all = statistic.all;
    }
}

class Chance implements Serializable{
    Statistic statistic;
    double win;
    double lose;
    double expectancy;

    Chance(){
        statistic = new Statistic();
        expectancy = -9;
    }

    Chance(double win, double lose, Statistic statistic){
        this.win = win;
        this.lose = lose;
        this.statistic = new Statistic(statistic);
    }

    void count(double result){
        if (result == 1)
            statistic.wins++;
        if (result == 0)
            statistic.loses++;
        calculate();
    }

    void calculate(){
        statistic.all++;
        win = (double) statistic.wins / (double)statistic.all;
        lose = (double)statistic.loses / (double)statistic.all;
        expectancy = win - lose;
    }

    void changeW(Chance chance){
        if (expectancy!=-9) {
            win = win + chance.win - win * chance.win;
            lose = lose * chance.lose;
        } else {
            win = chance.win;
            lose = chance.lose;
            expectancy = 0;
        }
    }

    void changeB(Chance chance){
        if (expectancy != -9){
        win = win + chance.lose - win*chance.lose;
        lose = lose*chance.win;
    } else {
        win = chance.lose;
        lose = chance.win;
        expectancy = 0;
    }
    }
}

class Coeffs implements Serializable {
    Map<Integer, Chance[][]> cells;
    Chance passToQueen;
    Map<Integer, Chance> flank;
    Chance bigWay;
    Chance combination;
    Map<Integer, Chance[][]> UNcells;
    Chance piece;

    Coeffs() {
        cells = new HashMap<>();
        flank = new HashMap<>();
        passToQueen = new Chance();
        combination = new Chance();
        bigWay = new Chance();
        UNcells = new HashMap<>();
        piece = new Chance();
    }



}
