import java.io.*;
import java.util.*;

class Game {
    private Board board;
    private InputHandler input;
    private Renderer renderer;
    private Level level;


    public void start(Level level) {
        this.level = level;
        board = new Board(level.getRows(), level.getCols(), level.getMines());
        while(!board.isGameOver()) {
            renderer.render(board);
            Command cmd = input.getCommand();
            board.applyCommand(cmd);
        }
    }
}

//난이도 조절 easy, normal, hard, 사용자 설정은 추후
enum Level {
    EASY(9, 9, 16),
    NORMAL(16, 16, 40),
    HARD(16, 30, 99);

    private final int rows;
    private final int cols;
    private final int mines;

    Level(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMines() { return mines; }
}

enum GameState {
    RUNNING, WON, LOST;
}

// 보드
class Board {
    private Cell[][] grid;
    private int rows, cols, mines;
    private static final int[][] Directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };
    private boolean isValid(int row, int col) {
        return row >= 0 && row < rows 
        && col >= 0 && col < cols;
    }
    

    Board(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;

        grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell();
            }
        }
        
        Set<Integer> mine_location = new HashSet<>();
        while(mine_location.size() < mines) {
            int random = (int)(Math.random() * rows * cols);
            mine_location.add(random);
        }

        Iterator<Integer> it = mine_location.iterator();
        while(it.hasNext()) {
            int loc = (int)it.next();
            int row = loc / cols;
            int col = loc % cols;
            grid[row][col].setBomb();
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].isBomb()) {
                    for (int[] dir : Directions) {
                        int nr = r + dir[0];
                        int nc = c + dir[1];
                        if (isValid(nr, nc)) {
                            if(!grid[nr][nc].isBomb()) {
                                int cnt = grid[nr][nc].getVal() + 1;
                                grid[nr][nc].setVal(cnt);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
        return true;
    }

    public void applyCommand(Command command) {

    }
}

enum CellState {
    CLOSED, OPENED, FLAGGED;
}

// 각각의 칸
class Cell {
    private CellState state;
    private int value;

    Cell() {
        this.state = CellState.CLOSED;
        this.value = 0;
    }

    public void setVal(int val) {
        this.value = val;
    }

    public int getVal() {
        return this.value;
    }

    public void setBomb() {
        this.value = -1;
    }

    public boolean isBomb() {
        return this.value == -1;
    }

    public boolean isOpen() {
        return state == CellState.OPENED;
    }
    
    public void openCell() {
        state = CellState.OPENED;
    }

    public boolean isFlaged() {
        return state == CellState.FLAGGED;
    }

    public void flag() {
        state = CellState.FLAGGED;
    }

    public void unflag() {
        state = CellState.CLOSED;
    }
}

// 보드 출력
class Renderer {
    public void render(Board board) {
        
    }
}

// 입력 처리
class InputHandler {
    public Command getCommand(String str) {
        return new Command();
    }
}

// 입력의 동작
class Command {
    private int row, col;
    private ActionType action;
}

enum ActionType {
    OPEN, FLAG, 
}

public class Refactoring {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    
}
