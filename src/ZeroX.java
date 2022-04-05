import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;


class Field {
    char[][] vals = new char[3][3];

    char playerRole;
    char botRole;

    Scanner sc = new Scanner(System.in);

    byte playerX = 4;
    byte playerY = 4;

    volatile boolean inGame = true;

    private void drawHorizontalLine() {
        System.out.println("━╋━╋━");
    }

    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void redraw() {
        this.clear();

        for (byte i = 0; i <= 2; i++) {
            for (byte j = 0; j <= 2; j++) {
                if (vals[i][j] == 0)
                    System.out.print(" ");
                else
                    System.out.print(vals[i][j]);
                if (j <= 1)
                    System.out.print("┃");
                else
                    System.out.println();
            }
            if (i <= 1)
                this.drawHorizontalLine();
        }
    }

    private void selectPoint() {
        System.out.println();
        System.out.println("Select point (x y)");
        String inp = sc.nextLine();

        if (inp.charAt(1) != ' ') {
            System.out.println("error: invalid input");
            this.selectPoint();
        } else {
            if (Character.isDigit(inp.charAt(0)) && Character.isDigit(inp.charAt(2))) {
                if (((byte) Character.getNumericValue(inp.charAt(0)) >= 0) && ((byte) Character.getNumericValue(inp.charAt(0)) <= 2) && ((byte) Character.getNumericValue(inp.charAt(2)) >= 0) && ((byte) Character.getNumericValue(inp.charAt(2)) <= 2)) {
                    if (vals[Character.getNumericValue(inp.charAt(2))][Character.getNumericValue(inp.charAt(0))] == 0) {
                        playerX = (byte) Character.getNumericValue(inp.charAt(2));
                        playerY = (byte) Character.getNumericValue(inp.charAt(0));
                    } else {
                        System.out.println("error: invalid input");
                        this.selectPoint();
                    }
                } else {
                    System.out.println("error: invalid input");
                    this.selectPoint();
                }
            } else {
                System.out.println("error: invalid input");
                this.selectPoint();
            }

            vals[playerX][playerY] = String.valueOf(playerRole).toUpperCase().charAt(0);
        }
    }

    public boolean status(char[][] fd) {
        boolean flag = false;

        byte zerosCounter = 0;

        for (char[] i: fd) {
            for (char j: i) {
                if (j == 0)
                    zerosCounter++;
            }
        }

        if (zerosCounter == 0)
            flag = true;

        if ((fd[0][0] == fd[0][1] && fd[0][1] == fd[0][2] && fd[0][0] != 0) || (fd[1][0] == fd[1][1] && fd[1][1] == fd[1][2] && fd[1][0] != 0) || (fd[2][0] == fd[2][1] && fd[2][1] == fd[2][2]) && fd[2][0] != 0) {
            flag = true;
        }

        if ((fd[0][0] == fd[1][0] && fd[1][0] == fd[2][0] && fd[0][0] != 0) || (fd[0][1] == fd[1][1] && fd[1][1] == fd[2][1] && fd[0][1] != 0) || (fd[0][2] == fd[1][2] && fd[1][2] == fd[2][2]) && fd[0][2] != 0) {
            flag = true;
        }

        if ((fd[0][0] == fd[1][1] && fd[1][1] == fd[2][2]  && fd[0][0] != 0) || (fd[0][2] == fd[1][1] && fd[1][1] == fd[2][0]) && fd[0][2] != 0) {
            flag = true;
        }

        return flag;
    }

    public void selectRole() {
        System.out.println("Select x or o");

        char inp = sc.nextLine().charAt(0);

        if (inp != 'o' && inp != 'x') {
            System.out.println("error: invalid role");
            System.exit(0);
        }
        else
            playerRole = inp;

        if (playerRole == 'x')
            botRole = 'o';
        else
            botRole = 'x';

        this.clear();
    }

    Field() {
        this.clear();

        this.selectRole();

        for (byte i = 0; i <= 2; i++) {
            for (byte j = 0; j <= 2; j++) {
                if (vals[i][j] == 0)
                    System.out.print(" ");
                if (j <= 1)
                    System.out.print("┃");
                else
                    System.out.println();
            }
            if (i <= 1)
                this.drawHorizontalLine();
        }

        this.game();
    }

    void game() {
        Bot bot = new Bot(playerRole, botRole);

        while (inGame) {
            boolean check = this.status(vals);

            if (check) {
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println("===END===");
                break;
            }

            this.selectPoint();

            this.redraw();

            vals = bot.step(vals);
            this.redraw();
        }
    }
}


class Bot {
    private char playerRole;
    private char botRole;

    private Random random = new Random();

    private void pr(String text) {
        System.out.println(text);
    }

    Bot(char plRole, char btRole) {
        this.playerRole = plRole;
        this.botRole = btRole;
    }

    private char[][] selectRandPoint(char[][] fd) {
        byte xStep = 0;
        byte yStep = 0;

        while (true) {
            xStep = (byte) (random.nextInt(5) - 3);
            yStep = (byte) (random.nextInt(5) - 3);

            if (xStep <= 2 && xStep >= 0) {
                if (yStep <= 2 && yStep >= 0) {
                    if (fd[xStep][yStep] == 0) {
                        break;
                    }
                }
            }
        }

        fd[xStep][yStep] = Character.toUpperCase(botRole);

        return fd;
    }

    public char[][] step(char[][] field) {
        byte playerPointsCounter = 0;
        ArrayList<byte[]> coordsOfPlayerPoints = new ArrayList<byte[]>();

        for (byte i = 0; i <= 2; i ++) {
            for (byte j = 0; j <= 2; j ++) {
                if (Character.toLowerCase(field[i][j]) == this.playerRole) {
                    playerPointsCounter++;
                    byte[] coords = {i, j};
                    coordsOfPlayerPoints.add(coords);
                }
            }
        }

        byte botPointsCounter = 0;
        ArrayList<byte[]> coordsOfBotPoints = new ArrayList<byte[]>();

        for (byte i = 0; i <= 2; i ++) {
            for (byte j = 0; j <= 2; j ++) {
                if (Character.toLowerCase(field[i][j]) == this.botRole) {
                    botPointsCounter++;
                    byte[] coords = {i, j};
                    coordsOfBotPoints.add(coords);
                }
            }
        }

        if (playerPointsCounter == 1) {
            byte[] coords = coordsOfPlayerPoints.get(0);

            byte xStep = 0;
            byte yStep = 0;

            while (true) {
                xStep = (byte) (random.nextInt(5) - 2);
                yStep = (byte) (random.nextInt(5) - 2);

                if ((coords[0] + xStep <= 2) && (coords[0] + xStep >= 0) && (coords[1] + yStep <= 2) && (coords[1] + yStep >= 0) && (field[coords[0] + xStep][coords[1] + yStep] == 0))
                    break;
            }

            field[coords[0] + xStep][coords[1] + yStep] = Character.toUpperCase(botRole);
        }

        if (playerPointsCounter == 2) {
            byte[] coords1 = coordsOfPlayerPoints.get(0);
            byte[] coords2 = coordsOfPlayerPoints.get(1);

            boolean c1Flag = true;
            boolean c2Flag = true;
            boolean c3Flag = true;

            BACK:

            if (coords1[0] == coords2[0] && c1Flag) {
                for (byte i = 0; i <= 2; i++) {
                    if (i != coords1[1] && i != coords2[1]) {
                        if (field[coords1[0]][i] == 0)
                            field[coords1[0]][i] = Character.toUpperCase(botRole);
                        else {
                            c1Flag = false;
                            field = this.selectRandPoint(field);
                        }
                    }
                }
            } else {
                if (coords1[1] == coords2[1] && c2Flag) {
                    for (byte i = 0; i <= 2; i++) {
                        if (i != coords1[0] && i != coords2[0]) {
                            if (field[i][coords1[0]] == 0)
                                field[i][coords1[0]] = Character.toUpperCase(botRole);
                            else {
                                c2Flag = false;
                                field = this.selectRandPoint(field);
                            }
                        }
                    }
                } else {
                    byte xStep = 4;
                    byte yStep = 4;

                    for (byte i = 0; i <= 2; i++) {
                        if (i != coords1[0] && i != coords2[0]) {
                            xStep = i;
                        }
                    }
                    for (byte i = 0; i <= 2; i++) {
                        if (i != coords1[1] && i != coords2[1]) {
                            yStep = i;
                        }
                    }

                    if ((Math.abs(coords1[0] - coords2[0]) == 1) && (Math.abs(coords1[1] - coords2[1]) == 1) && c3Flag && field[xStep][yStep] == 0) {
                        if (field[xStep][yStep] == 0)
                            field[xStep][yStep] = Character.toUpperCase(botRole);
                        else {
                            c3Flag = false;
                            field = this.selectRandPoint(field);
                        }
                    } else {
                        xStep = 0;
                        yStep = 0;

                        while (true) {
                            xStep = (byte) (random.nextInt(5) - 3);
                            yStep = (byte) (random.nextInt(5) - 3);

                            if ((coords1[0] + xStep <= 2) && (coords1[0] + xStep >= 0)) {
                                if ((coords1[1] + yStep <= 2) && (coords1[1] + yStep >= 0)) {
                                    if (field[coords1[0] + xStep][coords1[1] + yStep] == 0) {
                                        break;
                                    }
                                }
                            }
                        }

                        field[coords1[0] + xStep][coords1[1] + yStep] = Character.toUpperCase(botRole);
                    }
                }
            }
        }

        if (playerPointsCounter == 3) {
            for (byte k = 0; k <= 1; k++) {
                byte[] coords1 = coordsOfPlayerPoints.get(k);
                byte[] coords2 = coordsOfPlayerPoints.get(k + 1);

                if (coords1[0] == coords2[0]) {
                    for (byte i = 0; i <= 2; i++) {
                        if (i != coords1[1] && i != coords2[1]) {
                            if (field[coords1[0]][i] == 0) {
                                field[coords1[0]][i] = Character.toUpperCase(botRole);
                                break;
                            }
                            else {
                                field = this.selectRandPoint(field);
                                break;
                            }
                        }
                    }
                } else {
                    if (coords1[1] == coords2[1]) {
                        for (byte i = 0; i <= 2; i++) {
                            if (i != coords1[0] && i != coords2[0]) {
                                if (field[i][coords1[0]] == 0) {
                                    field[i][coords1[0]] = Character.toUpperCase(botRole);
                                    break;
                                }
                                else {
                                    field = this.selectRandPoint(field);
                                    break;
                                }
                            }
                        }
                    } else if (Math.abs(coords1[0] - coords2[0]) == 1 && Math.abs(coords1[1] - coords2[1]) == 1) {
                        byte xStep = 0;
                        byte yStep = 0;

                        for (byte i = 0; i <= 2; i++) {
                            if (i != coords1[0] && i != coords2[0]) {
                                xStep = i;
                            }
                        }
                        for (byte i = 0; i <= 2; i++) {
                            if (i != coords1[1] && i != coords2[1]) {
                                yStep = i;
                            }
                        }
                        if (field[xStep][yStep] == 0) {
                            field[xStep][yStep] = Character.toUpperCase(botRole);
                            break;
                        }
                    }
                }
            }
        }

        if (playerPointsCounter == 4) {
            field = this.selectRandPoint(field);
        }
        return field;
    }
}


public class ZeroX {
    public static void main(String[] args) {
        Field game = new Field();

    }
}
