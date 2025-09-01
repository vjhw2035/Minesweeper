import java.io.*;
import java.util.*;

class Square {
    boolean opened;
    boolean flaged;
    String value;

    Square(String value) {
        this.opened = false;
        this.flaged = false;
        this.value = value;
    }
}

class Board {
    static int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
    static int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};

    Square[][] board = new Square[9][9];
    Set<Integer> bomb = new HashSet<>();
    int opened_count = 0;

    Board() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new Square(".");
            }
        }

        while(bomb.size() < 10) {
            int random = (int)(Math.random() * 81); // 0~80
            bomb.add(random);
        }

        for(int val : bomb) {
            int y = val / 9;
            int x = val % 9;
            board[y][x].value = "💣";
        }    

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                if(!board[i][j].value.equals("💣")) {
                    int cnt_bomb = 0;
                    for(int dir = 0; dir < 8; dir++) {
                        int ny = i + dy[dir];
                        int nx = j + dx[dir];
                        if(nx >= 0 && nx < 9 && ny >= 0 && ny < 9) {
                            if(board[ny][nx].value.equals("💣")) cnt_bomb++;
                        }
                    }
                    if(cnt_bomb != 0) {
                        board[i][j].value = Integer.toString(cnt_bomb);
                    }
                }
            }
        }
    }
    String getvalue(int y, int x) {
        return board[y][x].value;
    }

    void show() {
        System.out.println("result square : " + (81 - opened_count));
        System.out.print("   ");
        for (int j = 0; j < 9; j++) {
            System.out.printf("%-3d", j + 1);
        }
        System.out.println();

        for (int i = 0; i < 9; i++) {
            System.out.printf("%-3d", i + 1);
            for (int j = 0; j < 9; j++) {
                if(board[i][j].flaged) System.out.printf("%-3s", "🚩");
                else if(!board[i][j].opened) System.out.printf("%-3s", "■ ");
                else System.out.printf("%-3s", (board[i][j].value + " "));
            }
            System.out.println();
        }
    }

    void search(int y, int x) {
        board[y][x].opened = true;
        opened_count++;
        for(int dir = 0; dir < 8; dir++) {
            int ny = y + dy[dir];
            int nx = x + dx[dir];
            if(nx >= 0 && nx < 9 && ny >= 0 && ny < 9) {
                if(!board[ny][nx].opened) {
                    if(board[ny][nx].value == ".") search(ny, nx);
                    else {
                        board[ny][nx].opened = true;
                        opened_count++;
                    }
                }
            }
        }
    }

    void Command(int y, int x, String command) {
        y -= 1;
        x -= 1;

        switch (command) {
            case "O":
                if(board[y][x].flaged) {
                    System.out.println("You cannot open square flaged!");
                }
                else if(board[y][x].opened) System.out.println("You aleady open this square");
                else if(board[y][x].value.equals(".")) search(y, x);
                else if(board[y][x].value.equals("💣")) fail(y, x);
                else {
                    board[y][x].opened = true;
                    opened_count++;
                }
                break;
        
            case "F":
                board[y][x].flaged = !board[y][x].flaged;
                break;
            default:
                System.out.println("Please give me correct command");
        }
        show();
    }

    boolean clear() {
        return (opened_count == 9 * 9 - 10);
    }
    boolean bombed = false;

    void fail(int y, int x) {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                board[i][j].opened = true;
            }
        }
        board[y][x].value = "💥";
        bombed = true;
    }
    boolean failed() {
        return bombed;
    }
}

public class App {

    public static void main(String[] args) throws IOException {
        Board board = new Board();
        board.show();

        while(true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringTokenizer st = new StringTokenizer(br.readLine());
            int y, x;
            String command;
            y = Integer.parseInt(st.nextToken());
            x = Integer.parseInt(st.nextToken());
            command = st.nextToken();
            if(x > 9 || x < 1 || y > 9 || y < 1) {
                System.out.println("wrong number!");
                continue;
            }

            board.Command(y, x, command);
            if(board.clear()) {
                System.out.println("Congratulation for clear!!");
                break;
            }
            if(board.failed()) {
                System.out.println("Oh... You failed...");
                break;
            }

        }
        

    }
}
