/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steven
 */
public class GameState{

    private char[][] state;
    private int width;
    private int height;
    private int playerRow;
    private int playerCol;
    private List<Position> goalPositions;

    public GameState(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = in.readLine();
        List<char[]> l = new ArrayList<char[]>();
        while (line != null) {
            l.add(line.toCharArray());
            line = in.readLine();
        }
        state = l.toArray(new char[0][0]);
        height = state.length;
        width = state[0].length;
        for (int i = 1; i < state.length; i++) {
            if (state[i].length != width) {
                throw new Exception();
            }
        }
        in.close();
        findPlayer();
        findGoalPositions();
    }

    public GameState(char[][] state, int playerRow, int playerCol, List<Position> goalPositions) {
        this.state = state;
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        this.goalPositions = goalPositions;
        height = state.length;
        width = state[0].length;
    }

    private void findPlayer() {
        boolean found = false;
        for (int i = 0; i < height && !found; i++) {
            for (int j = 0; j < width && !found; j++) {
                if (state[i][j] == 'p' || state[i][j] == 'P') {
                    playerRow = i;
                    playerCol = j;
                    found = true;
                }
            }
        }
    }

    private void findGoalPositions() {
        goalPositions = new ArrayList<Position>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (state[i][j] == 'g' || state[i][j] == 'P' || state[i][j] == 'B') {
                    goalPositions.add(new Position(i, j));
                }
            }
        }
    }

    private List<Position> findBlockPositions() {
        List<Position> res = new ArrayList<Position>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (state[i][j] == 'b' || state[i][j] == 'B') {
                    res.add(new Position(i, j));
                }
            }
        }
        return res;
    }

     public boolean isGoalState() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (state[i][j] == 'g' || state[i][j] == 'P') {
                    return false;
                }
            }
        }
        return true;
    }
    
    public GameState moveLeft() {
        return move(playerRow, playerCol, playerRow, playerCol - 1, playerRow, playerCol - 2);
    }

    public GameState moveRight() {
        return move(playerRow, playerCol, playerRow, playerCol + 1, playerRow, playerCol + 2);
    }

    public GameState moveUp() {
        return move(playerRow, playerCol, playerRow - 1, playerCol, playerRow - 2, playerCol);
    }

    public GameState moveDown() {
        return move(playerRow, playerCol, playerRow + 1, playerCol, playerRow + 2, playerCol);
    }

    public GameState move(int row, int col, int row1, int col1, int row2, int col2) {
        if (row1 < 0 || col1 < 0 || row1 >= height || col1 >= width || state[row1][col1] == 'w') {
            return null;
        }
        else {
            char[][] newState = new char[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    newState[i][j] = state[i][j];
                }
            }
            if (state[row][col] == 'p') {
                newState[row][col] = '.';
            }
            else if (state[row][col] == 'P') {
                newState[row][col] = 'g';
            }
            if (state[row1][col1] == '.' || state[row1][col1] == 'g') {

                if (state[row1][col1] == '.') {
                    newState[row1][col1] = 'p';
                }
                else if (state[row1][col1] == 'g') {
                    newState[row1][col1] = 'P';
                }
            }
            else if (state[row1][col1] == 'b' || state[row1][col1] == 'B') {
                if (row2 < 0 || col2 < 0 || row2 >= height || col2 >= width || state[row2][col2] == 'w'
                        || state[row2][col2] == 'b' || state[row2][col2] == 'B') {
                    return null;
                }
                if (state[row1][col1] == 'B') {
                    newState[row1][col1] = 'P';
                }
                else if (state[row1][col1] == 'b') {
                    newState[row1][col1] = 'p';
                }
                if (state[row2][col2] == 'g') {
                    newState[row2][col2] = 'B';
                }
                else if (state[row2][col2] == '.') {
                    newState[row2][col2] = 'b';
                }
            }
            return new GameState(newState, row1, col1, goalPositions);
        }
    }

    public boolean moveLeftLegal() {
        return moveLeftLegal(playerRow, playerCol);
    }

    public boolean moveRightLegal() {
        return moveRightLegal(playerRow, playerCol);
    }

    public boolean moveDownLegal() {
        return moveDownLegal(playerRow, playerCol);
    }

    public boolean moveUpLegal() {
        return moveUpLegal(playerRow, playerCol);
    }

    public boolean moveLeftLegal(int row, int col) {
        return moveLegal(row, col, row, col - 1, row, col - 2);
    }

    public boolean moveRightLegal(int row, int col) {
        return moveLegal(row, col, row, col + 1, row, col + 2);
    }

    public boolean moveUpLegal(int row, int col) {
        return moveLegal(row, col, row - 1, col, row - 2, col);
    }

    public boolean moveDownLegal(int row, int col) {
        return moveLegal(row, col, row + 1, col, row + 2, col);
    }

    public boolean moveLegal(int row, int col, int row1, int col1, int row2, int col2) {
        if (row1 < 0 || col1 < 0 || row1 >= height || col1 >= width || state[row1][col1] == 'w') {
            return false;
        }
        else if ((state[row1][col1] == 'b' || state[row1][col1] == 'B')
                && (row2 < 0 || col2 < 0 || row2 >= height || col2 >= width || state[row2][col2] == 'w'
                || state[row2][col2] == 'b' || state[row2][col2] == 'B')) {
            return false;
        }
        return true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char getType(int x, int y) {
        return state[x][y];
    }

    public void printState() {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
    }

    private boolean isGoalPosition(Position pos) {
        return state[pos.row][pos.col] == 'g' || state[pos.row][pos.col] == 'B' || state[pos.row][pos.col] == 'P';
    }

    private boolean isBlockPosition(Position pos) {
        return state[pos.row][pos.col] == 'b' || state[pos.row][pos.col] == 'B';
    }

    public boolean equals(Object o) {
        if (!(o instanceof GameState)) {
            return false;
        }
        GameState gs = (GameState) o;
        if (gs.height != height || gs.width != width || playerRow != gs.playerRow || playerCol != gs.playerCol) {
            return false;
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (state[row][col] != gs.state[row][col]) {
                    return false;
                }
            }
        }
        return true;
        
    }

    public int hashCode() {
        List<Position> blockPositions = findBlockPositions();
        int res = playerRow + 17 * playerCol;
        for (Position pos : blockPositions) {
            res += 29 * pos.row + 47 * pos.col;
        }
        return res;
    }

    public static class Position {

        public int row;
        public int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int distance(Position pos) {
            return Math.abs(row - pos.row) + Math.abs(col - pos.col);
        }
        
       public boolean equals(Object o){
    	   if (!(o instanceof Position)) {
               return false;
           }
    	   Position toCompare = (Position) o;
    	   if(toCompare.row == this.row && toCompare.col == this.col){
        		return true;
        	}
        	return false;
        }
        
    }
    
    public List<Position> getGoalPositions(){
    	return this.goalPositions;
    }
    
    public List<Position> getBlockPositions(){
    	return this.findBlockPositions();
    }
    
    public char[][] getMap(){
    	return this.state;
    }


}
