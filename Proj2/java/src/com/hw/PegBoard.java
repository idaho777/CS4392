package com.hw;

/**
* Representation of a Triangle Peg Board.
*
* @Author Joonho Kim
*/
public class PegBoard {
  private boolean[][] board;

  public PegBoard(int rows) {
    board = new boolean[rows][];
    for (int i = 0; i < rows; i ++) {
      board[i] = new boolean[i + 1];
    }
  }
  
  public PegBoard(String binary) {
    char[] charArray = binary.toCharArray();
    int rows = (int) Math.sqrt(binary.length() * 2);

    board = new boolean[rows][];

    int currRow = 0;
    int charArrayPos = 0;
    while (charArrayPos < charArray.length) {
      board[currRow] = new boolean[currRow + 1];
      for (int currCol = 0; currCol <= currRow; currCol++) {
        board[currRow][currCol] = (charArray[charArrayPos] == '1');
        charArrayPos++;
      }
      currRow++;
    }
  }

  public void setBoard(String binary) {
    char[] charArray = binary.toCharArray();
    int rows = (int) Math.sqrt(binary.length() * 2);

    board = new boolean[rows][];

    int currRow = 0;
    int charArrayPos = 0;
    while (charArrayPos < charArray.length) {
      board[currRow] = new boolean[currRow + 1];
      for (int currCol = 0; currCol <= currRow; currCol++) {
        board[currRow][currCol] = (charArray[charArrayPos] == '1');
        charArrayPos++;
      }
      currRow++;
    }
  }


  /**
  * Sets up the board with an empty spot at row,col
  */
  public void initializeBoard(int row, int col){
    int rows = getRows();
    if (!posOnBoard(row, col)) {
      throw new IndexOutOfBoundsException("Row/Col is not valid position on the board");
    }
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < i + 1; j++) {
        board[i][j] = true;
      }
    }
    board[row][col] = false;
  }
  
  public static int getPieceNumber(int row, int col) {
    return ((row * (row + 1)) / 2) + col;
  }

  /**
  * Makes a move on piece row,col
  * Assumes that row,col can make the move.
  */
  public void makeMove(int row, int col, PegBoardMove move) {   
    int destRow = row + move.getRowMove();
    int destCol = col + move.getColMove();
    
    int midRow = (destRow + row) / 2;
    int midCol = (destCol + col) / 2;
    
    board[row][col] = false;
    board[midRow][midCol] = false;
    board[destRow][destCol] = true;
  }
  
  /**
  * Checks to see whether a peg at row,col can make the move
  */
  boolean canMakeMove(int row, int col, PegBoardMove move) {
    // piece at row,col is not a peg
    if (!board[row][col]) {
      return false;
    }

    int destRow = row + move.getRowMove();
    int destCol = col + move.getColMove();
    
    // Valid position on Board
    if (!posOnBoard(destRow, destCol)) {
      return false;
    }
    
    // Position in between dest and curr has a piece
    int midRow = (destRow + row) / 2;
    int midCol = (destCol + col) / 2;
    
    // Dest is empty and mid is a peg
    return !board[destRow][destCol] && board[midRow][midCol];
  }
  
  private boolean posOnBoard(int row, int col) {
    return (row >= 0 && row < board.length) && (col >= 0 && col <= row);
  }
    
  public boolean[][] getBoard() {
    return board;
  }
    
  public int getRows() {
    return board.length;
  }
  
  /**
  * Binary Representation of the pegBoard
  * From top to bottom, left to right, we create a string where
  * a piece is 1 and a empty space is 0.
  * This is used as a key to our HashMap of PegBoard to StateNextMove
  */
  public String getBinary() {
    String bin = "";
    for (boolean[] bArr : board) {
      for (boolean b : bArr) {
        bin += (b) ? "1" : "0";
      }
    }
    return bin;
  }
  
  public boolean equals(Object o) {
    if (o instanceof PegBoard) {
      return getBinary().equals(((PegBoard) o).getBinary());
    }
    return false;
  }
  
  public String toString() {
    String output = "";
    for (boolean[] b : board) {
      for (boolean bool : b) {
        output += (bool) ? "1" : "0";
      }
      output += "\n";
    }
    return output;
  }
  
  public void printBoard() {
    System.out.println(toString());
  }
}