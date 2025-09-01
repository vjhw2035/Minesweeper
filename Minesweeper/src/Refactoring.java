import java.io.*;
import java.util.*;

class Game {
    private Board board;
    private InputHandler input;
    private Show show;
    private Level level;


    public void start(Level level) {
        this.level = level;
        board = new Board(level.getRows(), level.getCols(), level.getMines());
        while(!board.isGameOver()) {
            Command cmd = input.getCommand();
            board.applyCommand(cmd);
            show.show(board);
        }
    }
}

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

class Board {
    private Square[][] grid;
    private int rows, cols, mines;

    Board(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    boolean isGameOver() {
        return true;
    }

    void applyCommand(Command command) {

    }
}

class Square {
    private boolean opened;
    private boolean flaged;
    private String value;

    Square(String value) {
        this.opened = false;
        this.flaged = false;
        this.value = value;
    }
}

class Show {
    public void show(Board board) {

    }
}

class InputHandler {
    public Command getCommand() {

    }
}

class Command {
    private int row, col;
    private String action;
}

public class Refactoring {
    
}
