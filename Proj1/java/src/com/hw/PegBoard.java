package com.hw;

/**
* Representation of a Triangle Peg Board.
*
* @Author Joonho Kim
*/
public class PegBoard {
  private int rows;
  private int numOfPieces;

  private boolean[][] board;

  public PegBoard(int rows) {
    this.rows = rows;
    this.numOfPieces = 0;
    board = new boolean[rows][];
    numOfPieces = 0;
    for (int i = 0; i < rows; i ++) {
      board[i] = new boolean[i + 1];
    }
  }
  
  public PegBoard(boolean[][] board) {
    this(board.length);
    numOfPieces = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < i + 1; j++) {
        if (board[i][j]) {
          this.board[i][j] = true;
          numOfPieces++;
        }
      }
    }
  }

  /**
  * Sets up the board with an empty spot at row,col
  */
  public void initializeBoard(int row, int col){
    if (!posOnBoard(row, col)) {
      throw new IndexOutOfBoundsException("Row/Col is not valid position on the board");
    }
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < i + 1; j++) {
        board[i][j] = true;
      }
    }
    board[row][col] = false;
    numOfPieces = ((this.rows * (this.rows + 1)) / 2) - 1;  // Summation from 1 to n
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
    
    numOfPieces--;
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
    return (row >= 0 && row < rows) && (col >= 0 && col <= row);
  }
    
  public boolean[][] getBoard() {
    return board;
  }
  
  public int getNumOfPieces() {
    return numOfPieces;
  }
  
  public int getRows() {
    return rows;
  }
  
  /**
  * Binary Representation of the pegBoard
  * From top to bottom, left to right, we create a string where
  * a piece is 1 and a empty space is 0.
  * This is used as a key to our HashMap of PegBoard to NextMove
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
      if (((PegBoard) o).getRows() == rows) {
        return getBinary().equals(((PegBoard) o).getBinary());
      }
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