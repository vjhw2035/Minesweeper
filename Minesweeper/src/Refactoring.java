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
    private GameState gameState;
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
        this.gameState = GameState.RUNNING;

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
        return (gameState != GameState.RUNNING);
    }

    public void applyCommand(Command command) {

    }

    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public int getMines() {
        return mines;
    }

    public Cell[][] getGrid() {
        return grid;
    }
    public GameState getGameState() {
        return this.gameState;
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

    public CellState getcellstate() {
        return this.state;
    }
}

// 보드 출력
class Renderer {
    public void render(Board board) {
        int opened_count = 0;
        System.out.print("   ");
        for (int c = 0; c < board.getCols(); c++) {
            System.out.printf("%-3d", c + 1);
        }
        System.out.println();
        Cell[][] rGrid = board.getGrid();
        for(int r = 0; r < board.getRows(); r++) {
            System.out.printf("%-3d", r + 1);
            for(int c = 0; c < board.getCols(); c++) {
                switch(rGrid[r][c].getcellstate()) {
                    case CLOSED:
                        System.out.printf("%-3s", "■");
                        break;
                    case FLAGGED:
                        System.out.printf("%-3s", "🚩");
                        break;
                    case OPENED:
                        int val = rGrid[r][c].getVal();
                        if(val == -1) System.out.printf("%-3s", "💥");
                        else if(val == 0) System.out.printf("%-3s", ".");
                        else System.out.printf("%-3d", val);
                        opened_count++;
                        break;
                }
            }
        }
        System.out.println("result square : " + (board.getCols() * board.getRows() - opened_count));
    }
}

// 입력 처리
class InputHandler {
    private BufferedReader br;
    
    InputHandler() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    public Command getCommand() {
        StringTokenizer st = new StringTokenizer(br.readLine());
        int r = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        String commandstr = st.nextToken();
        ActionType action;
        while(!commandstr.equals("o") && !commandstr.equals("f")) {
            System.out.println("Not correct command. Please enter \"o\" or \"f\"");
            commandstr = br.readLine();
        }
        if(commandstr.equals("o")) action =  ActionType.OPEN;
        else if(commandstr.equals("f")) action = ActionType.FLAG;

        return new Command(r, c, );
    }
}

// 입력의 동작
class Command {
    private int row, col;
    private ActionType action;

    Command(int row, int col, ActionType action) {
        this.row = row;
        this.col = col;
        this.action = action;
    }
}

enum ActionType {
    OPEN, FLAG;
}

public class Refactoring {
    

    
}
