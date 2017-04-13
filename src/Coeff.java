
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Coeff {

    static double[] y = new double[36], c = new double[36];
    static double[][] x = new double[53000][36], b = new double[36][36];
    static double[] p = new double[53000];

    public static void counting() {
        int c = 0;
        for (BaseGame game : Reader.games) {
            double result;
            try {
                result = game.result;
            } catch (NullPointerException e) {
                continue;
            }
            for (CompressedPos position : game.positions) {
                coeff(position, result, c);
                c++;
            }
        }
        method(c);
    }

    static void method(int count) {
        for (int j = 0; j < 36; j++)
            for (int k = 0; k < 36; k++)
                for (int i = 0; i < count; i++)
                    b[j][k] += x[i][j] * x[i][k];

        for (int k = 0; k < 36; k++)
            for (int i = 0; i < count; i++)
                c[k] = p[i] * x[i][k];

        gauss(b, c);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Coeffs.dat"))) {
            oos.writeObject(y);
        } catch (Exception ignore) {
        }

    }

    static void gauss(double[][] a, double[] b) {
        int n = a.length;
        for (int baseColumn = 0; baseColumn < n; ++baseColumn) {
            int baseRow = baseColumn;
            for (int row = baseRow; row < n; ++row) {
                if (Math.abs(a[row][baseColumn]) > Math.abs(a[baseRow][baseColumn])) {
                    baseRow = row;
                }
            }
            if (a[baseRow][baseColumn] == 0) continue;

            double[] tmp = a[baseColumn];
            a[baseColumn] = a[baseRow];
            a[baseRow] = tmp;
            double tmpD = b[baseColumn];
            b[baseColumn] = b[baseRow];
            b[baseRow] = tmpD;
            baseRow = baseColumn;

            double baseElement = a[baseRow][baseColumn];
            for (int row = 0; row < n; ++row) {
                if (row == baseRow) continue;
                double coeff = a[row][baseColumn] / baseElement;
                for (int column = baseColumn; column < n; ++column) {
                    a[row][column] -= a[baseRow][column] * coeff;
                }
                b[row] -= b[baseRow] * coeff;
            }
        }
        for (int i = 0; i < 36; i++) {
            if (a[i][i] == 0) y[i] = 0;
            else y[i] = b[i] / a[i][i];
        }
    }


    static void coeff(CompressedPos position, double result, int number) {
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
