package com.hw;

/**
* Represention of a state transition based on a peg move.
* @int row, col are the coordinates of the peg being moved
* @PegBoardMove move is the move picked.
* @String pegBoardBinary is the binary representation of the new PegBoard State
*     This is represented as a binary string to easily access a HashMap of 
*     current PegBoard states to the best StateNextMove.
*
* @Author Joonho Kim
*/
public class StateNextMove {

  private int row, col;
  private PegBoardMove move;
  private String pegBoardBinary;
  
  public StateNextMove(int row, int col, PegBoardMove move, String pegBoardBinary) {
    this.row = row;
    this.col = col;
    this.move = move;
    this.pegBoardBinary = pegBoardBinary;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public PegBoardMove getMove() {
    return move;
  }

  public void setMove(PegBoardMove move) {
    this.move = move;
  }

  public String getPegBoardBinary() {
    return pegBoardBinary;
  }

  public void setPegBoardBinary(String pegBoardBinary) {
    this.pegBoardBinary = pegBoardBinary;
  }
}
