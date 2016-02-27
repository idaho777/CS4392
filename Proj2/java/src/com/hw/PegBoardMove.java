package com.hw;

/**
* Representation of a move allowed in the game.
* The rowMove and colMove are based on the representation of the PegBoard array.
*
* @Author Joonho Kim
*/
public enum PegBoardMove {
  LEFT(0, -2),
  RIGHT(0, 2),
  TOP_LEFT(-2, -2),
  TOP_RIGHT(-2, 0),
  BOTTOM_LEFT(2, 0),
  BOTTOM_RIGHT(2, 2);
  
  private int rowMove, colMove;
  
  PegBoardMove(int rowMove, int colMove) {
    this.rowMove = rowMove;
    this.colMove = colMove;
  }
  
  public int getRowMove() {
    return rowMove;
  }
  
  public int getColMove() {
    return colMove;
  }
}