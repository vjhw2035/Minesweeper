package newversion;

import java.io.*;
import java.time.*;
import java.util.*;

class Game {
    private Board board;
    private InputHandler input;
    private Renderer renderer;
    private Timer timer;

    Game() {
        this.input = new InputHandler();
        this.renderer = new Renderer();
        this.timer = new Timer();
    }

    public void start() {
        Level level = input.getLevel();
        this.board = new Board(level.getRows(), level.getCols(), level.getMines());
        renderer.render(board);
        Command firstcmd = input.getCommand(board);
        board.applyCommand(firstcmd);
        timer.gameStart();

        while(!board.isGameOver()) {
            renderer.render(board);
            Command cmd = input.getCommand(board);
            board.applyCommand(cmd);
        }
        renderer.render(board);
        timer.gameEnd();
        System.out.println(timer.howMuchTime().getSeconds() + "seconds");
    }
}

//난이도 조절 easy, normal, hard, 사용자 설정은 추후
enum Level {
    TBA(0, 0, 0),
    EASY(9, 9, 10),
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

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//

enum GameState {
    RUNNING, WON, LOST;
}

class Timer {
    private LocalTime start;
    private LocalTime end;

    public void gameStart() {
        start = LocalTime.now();
    }
    public void gameEnd() {
        end = LocalTime.now();
    }
    public Duration howMuchTime() {
        Duration duration = Duration.between(start, end);
        return duration;
    }
}

// 보드
class Board {
    private Cell[][] grid;
    private int rows, cols, mines;
    private int totalCell;
    private int opened_count, flagged_count;
    private boolean initialized;
    private GameState gameState;
    public final int[][] Directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0,  -1},          {0,  1},
            {1,  -1}, {1,  0}, {1,  1}
        };
        public boolean isValid(int row, int col) {
            return row >= 0 && row < rows 
            && col >= 0 && col < cols;
        }
        
        Board(int rows, int cols, int mines) {
            this.rows = rows;
            this.cols = cols;
            this.mines = mines;
            this.totalCell = rows * cols;
            this.opened_count = 0;
            this.initialized = false;
            this.gameState = GameState.RUNNING;
    
            grid = new Cell[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    grid[r][c] = new Cell();
                }
            }
        }
    
        public void applyCommand(Command command) {
            int r = command.getRow();
            int c = command.getCol();
            ActionType action = command.getAction();
            Cell cell = grid[r][c];
    
            if (action == ActionType.OPEN && !initialized ) {
                BoardInitializer.initialize(this, r, c);
            }
    
            switch (action) {
                case OPEN:
                    openCell(r, c);
                    break;
            
                case FLAG:
                    if(!cell.isFlagged()) {
                        if(getFlaggedcnt() < getMines()) {
                            addFlaggedcnt();
                            cell.flag();
                        }
                        else {
                            System.out.println("No more Flag");
                            return;
                        }
                    }
                    else {
                        subFlaggedcnt();
                        cell.unflag();
                    }
                    break;
    
                case ARROUND:
                    arroundCell(r, c);
                    break;
            }
        }
        private void openCell(int r, int c) {
            Cell cell = grid[r][c];
            if(cell.isClose()) {
                int val = cell.getVal();
                cell.open();
                addOpenCnt();
    
                switch (val) {
                    case -1:
                        gameOver();
                        cell.setVal(-2);
                        return;
                    case 0:
                        search(r, c);
                        break;
                }
                if (getOpenedcnt() + getMines() == getTotalCell()) {
                    gameClear();
                }
            }
        }
    
        private void arroundCell(int r, int c) {
            if(grid[r][c].getcellstate() != CellState.OPENED) {
                System.out.println("You cannot command \"A\" on Closed or Flagged Cell");
                return;
            }
            int flagCnt = grid[r][c].getVal();
            for(int[] dir : Directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];
                if (isValid(nr, nc) && grid[nr][nc].isFlagged()) flagCnt--;
            }
            if(flagCnt <= 0) search(r, c);
            else return;
        }
    
        public void search(int r, int c) {
            for(int[] dir : Directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];
                if (isValid(nr, nc) && grid[nr][nc].isClose()) {
                    openCell(nr, nc);
                }
            }
        }

        public void allOpen() {
            for(Cell[] row : getGrid()) {
                for(Cell col : row) {
                    if(col.isFlagged()) {
                        if(col.getVal() != -1) {
                            col.setVal(-3);
                            col.open();
                        }
                    }
                    else col.open();
                }
            }
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
        public int getTotalCell() { 
            return totalCell;
        }
        public void addOpenCnt() {
            opened_count++;
        }
        public int getOpenedcnt() {
            return opened_count;
        }
        public void addFlaggedcnt() {
            flagged_count++;
        }
        public void subFlaggedcnt() {
            flagged_count--;
        }
        public int getFlaggedcnt() {
            return flagged_count;
        }
        public Cell[][] getGrid() {
            return grid;
        }
        public GameState getGameState() {
            return this.gameState;
        }
        public boolean isGameOver() {
            return (gameState != GameState.RUNNING);
        }
        public void gameOver() {
            this.gameState = GameState.LOST;
        }
        public void gameClear() {
            this.gameState = GameState.WON;
        }
        public void setInitialized(boolean bool) {
            this.initialized = bool;
        }
        public boolean getInitialized() {
            return this.initialized;
        }
}
class BoardInitializer {
        public static void initialize(Board board, int safeRow, int safeCol) {
            placeMines(board, safeRow, safeCol);
            calculateNumbers(board);
            board.setInitialized(true);
        }
    
        private static void placeMines(Board board, int safeRow, int safeCol) {
            Set<Integer> mine_location = new HashSet<>();
            while(mine_location.size() < board.getMines()) {
                int random = (int)(Math.random() * board.getRows() * board.getCols());
                if(random != (safeRow * board.getCols() + safeCol)) {
                    mine_location.add(random); 
                }
            }
    
            Iterator<Integer> it = mine_location.iterator();
            while(it.hasNext()) {
                int loc = (int)it.next();
                int row = loc / board.getCols();
                int col = loc % board.getCols();
                board.getGrid()[row][col].setBomb();
            }
        }
    
        private static void calculateNumbers(Board board) {
            for (int r = 0; r < board.getRows(); r++) { 
                for (int c = 0; c < board.getCols(); c++) { 
                    if (board.getGrid()[r][c].isBomb()) { 
                        for (int[] dir : board.Directions) { 
                        int nr = r + dir[0]; 
                        int nc = c + dir[1]; 
                        if (board.isValid(nr, nc)) { 
                            if(!board.getGrid()[nr][nc].isBomb()) { 
                                int cnt = board.getGrid()[nr][nc].getVal() + 1; 
                                board.getGrid()[nr][nc].setVal(cnt); 
                            }
                        }
                    }         
                }
            }
        }
    }
}



// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//


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

    public boolean isClose() { 
        return state == CellState.CLOSED;
    }
    
    public void open() {
        state = CellState.OPENED;
    }

    public boolean isFlagged() {
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

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//


// 보드 출력
class Renderer {
    public void render(Board board) {
        System.out.println("\nRemaining Mines : " + (board.getMines() - board.getFlaggedcnt()) + "\n");
        System.out.print("   ");
        for (int c = 0; c < board.getCols(); c++) {
            System.out.printf("%-3d", c + 1);
        }
        System.out.println();
        Cell[][] rGrid = board.getGrid();
        if(board.isGameOver()) board.allOpen();
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
                        switch(val) {
                            case -1:
                                System.out.printf("%-3s", "💣");
                                break;
                            case 0:
                                System.out.printf("%-3s", ".");
                                break;
                            case -2:
                                System.out.printf("%-3s", "💥");
                                break;
                            case -3:
                                System.out.printf("%-3s", "🎌");
                                break;
                            default: 
                                System.out.printf("%-3d", val);
                        }
                        break;
                }
            }
            System.out.println();
        }
        GameState state = board.getGameState();
        switch (state) {
            case WON:
                System.out.println("\nCongratulation for clear!");
                break;
            case LOST:
                System.out.println("\nOh... You failed... You want a retry?");
                break;
            case RUNNING: break;
        }
    }
}

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//

// 입력 처리
class InputHandler {
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    
    public Level getLevel() {
        while(true) {
            try {
                    System.out.println("What Level you want? please choose one and Enter, EASY : E, NORMAL : N, HARD : H");
                    Level level = Level.TBA;
                    while(level == Level.TBA) {
                        String lev = br.readLine().trim().toUpperCase();
                        switch (lev) {
                            case "E":
                            case "EASY": return Level.EASY;
                            case "N":
                            case "NORMAL": return Level.NORMAL;
                            case "H":
                            case "HARD": return Level.HARD;
                            default:
                                System.out.println("난이도를 다시 입력해주세요");
                        }
                    }
                }
            catch(IOException e) {
                System.out.println("LevelInput Error");
            }
        }
    }

    public Command getCommand(Board board) {
        while(true) {
            try {
                System.out.println("Enter row, col, (command \"O\" or \"F\" or \"A\")");
                String str = br.readLine();
                if(str == null) {
                    throw new NullPointerException();
                }

                StringTokenizer st = new StringTokenizer(str);
                if(st.countTokens() < 3) {
                    throw new NoSuchElementException();
                }
                int r = Integer.parseInt(st.nextToken()) - 1;
                int c = Integer.parseInt(st.nextToken()) - 1;
                String a = st.nextToken();

                if(r >= board.getRows() || r < 0 || c >= board.getCols() || c < 0) {
                    throw new IllegalArgumentException();
                }
                a = a.toUpperCase();
                ActionType action;
                switch (a) {
                    case "O": action = ActionType.OPEN;
                    break;
                    case "F": action = ActionType.FLAG;
                    break;
                    case "A": action = ActionType.ARROUND;
                    break;
                    default:
                        System.out.println("올바르지 않은 명령어입니다.");
                        continue;
                }
                return new Command(r, c, action);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                System.out.println("입력 형식이 NULL입니다.");
            }
            catch (NumberFormatException e) {
                System.out.println("좌표에는 숫자만 입력해주세요.");
            }
            catch (IllegalArgumentException e) {
                System.out.println("좌표가 보드를 벗어났습니다.");
            }
            catch (NoSuchElementException e) {
                System.out.println("row, col, command 순으로 전부 입력해주세요.");
            }
        }
    }
}

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//


// 입력의 동작
class Command {
    private int row, col;
    private ActionType action;

    Command(int row, int col, ActionType action) {
        this.row = row;
        this.col = col;
        this.action = action;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ActionType getAction() {
        return action;
    }
}

enum ActionType {
    OPEN, FLAG, ARROUND;
}

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//

public class Refactoring {
    public static void main(String[] args) throws IOException{
        
        Game g = new Game();
        g.start();
    }
}
