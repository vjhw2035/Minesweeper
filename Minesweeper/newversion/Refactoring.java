import java.io.*;
import java.util.*;

class Game {
    private Board board;
    private InputHandler input;
    private Renderer renderer;

    Game(Level level) {
        this.board = new Board(level.getRows(), level.getCols(), level.getMines());
        this.input = new InputHandler();
        this.renderer = new Renderer();
    }

    public void start() {
        while(!board.isGameOver()) {
            renderer.render(board);
            Command cmd = input.getCommand(board);
            board.applyCommand(cmd);
        }
        renderer.render(board);
    }
}

//난이도 조절 easy, normal, hard, 사용자 설정은 추후
enum Level {
    TBA(0, 0, 0),
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

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//

enum GameState {
    RUNNING, WON, LOST;
}

// 보드
class Board {
    private Cell[][] grid;
    private int rows, cols, mines;
    private int totalCell;
    private int opened_count;
    private GameState gameState;
    private static final int[][] Directions = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0,  -1},          {0,  1},
        {1,  -1}, {1,  0}, {1,  1}
    };
    private boolean isValid(int row, int col) {
        return row >= 0 && row < rows 
        && col >= 0 && col < cols;
    }
    

    Board(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.totalCell = rows * cols;
        this.opened_count = 0;
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
    
    public void gameOver() {
        this.gameState = GameState.LOST;
    }

    public void gameClear() {
        this.gameState = GameState.WON;
    }

    public void applyCommand(Command command) {
        int r = command.getRow();
        int c = command.getCol();
        ActionType action = command.getAction();

        Cell cell = grid[r][c];

        switch (action) {
            case OPEN:
                openCell(r, c);
                break;
        
            case FLAG:
                if(cell.isFlaged()) cell.unflag();
                else cell.flag();
                break;

            case ARROUND:
                arroundCell(r, c);
                break;
        }
    }
    public void openCell(int r, int c) {
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

    public void arroundCell(int r, int c) {
        int flagCnt = grid[r][c].getVal();
        for(int[] dir : Directions) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            if (isValid(nr, nc) && grid[nr][nc].isFlaged()) flagCnt--;
        }
        if(flagCnt == 0) search(r, c);
        else return;
    }

    public void search(int r, int c) {
        for(int[] dir : Directions) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            if (isValid(nr, nc)) {
                openCell(nr, nc);
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

    public Cell[][] getGrid() {
        return grid;
    }
    public GameState getGameState() {
        return this.gameState;
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
        if(isClose()) state = CellState.OPENED;
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

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//


// 보드 출력
class Renderer {
    public void render(Board board) {
        System.out.println("\nresult square : " + (board.getTotalCell() - board.getOpenedcnt()) + "\n");
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
                            default: 
                                System.out.printf("%-3d", val);
                        }
                        break;
                }
            }
            System.out.println();
        }
    }
}

// ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ//

// 입력 처리
class InputHandler {
    private BufferedReader br;
    
    InputHandler() {
        br = new BufferedReader(new InputStreamReader(System.in));
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
                    throw new NoSuchElementException("row, col, command 순으로 전부 입력해주세요.");
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
        System.out.println("What Level you want? please choose one and Enter, EASY : E, NORMAL : N, HARD : H");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Level level = Level.TBA;
        while(level == Level.TBA) {
            String lev = br.readLine();
            lev = lev.toUpperCase();
            switch (lev) {
                case "E": 
                case "EASY": 
                    level = Level.EASY;
                    break;
                case "N":
                case "NORMAL":
                    level = Level.NORMAL;
                    break;
                case "H":
                case "HARD":
                    level = Level.HARD;
                    break;
                default:
                    System.out.println("난이도를 다시 입력해주세요");
                    continue;
            }
        }
        Game g = new Game(level);
        g.start();
    }
}
