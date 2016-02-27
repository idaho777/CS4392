package com.hw;

import java.util.HashMap;
import java.util.Map;

/**
* Main algorithm.
* We take a bruteforce approach by making every move possible at every state
*   of the board for every initial board position possible.  So basically
*   Brute force through everything.
* For a board state, we make every move possible for every piece and recursivly
*   call our recursive function on the new board state.
* If we reach a board state with no valid moves, record the number of pegs left
*   on the board.
* We have a Map movesMap of the binary representation of a PegBoard and the   
*   next best move it should take.  We also have a piecesMap of the binary
*   representation of a PegBoard and the number of pieces left at the end state
* 
* We use dynamic programming to cache states already visited and 
*
* @Author Joonho Kim
*/

public class ReversePegGameSolver {

  private static Map<String, NextMove> movesMap;
  private static Map<String, Integer> piecesMap;
  
  public ReversePegGameSolver() {
  }
  
  public static void solveReverseDP(int rows) {
    movesMap = new HashMap<>();
    piecesMap = new HashMap<>();
    
    PegBoard board = new PegBoard(rows);
    
    int startRow = 0;
    int startCol = 0;
    int highestPiecesLeft = -1;
        
    /*
    * Try every initial board state with the empty position above row/2 + 1.
    * This is because by the nature of the board states with empty
    *   positions under row/2 + 1 are just empty positions above row/2 + 1 with
    *   a rotation of the board.
    */
    for (int i = 0; i < board.getRows() / 2 + 1; i++) {
      for (int j = 0; j < i + 1; j++) {
        board.initializeBoard(i, j);
        
        int piecesLeft = solveReverseRecursive(board);

        // Replaces the current best initial position.
        if (piecesLeft > highestPiecesLeft) {
          startRow = i;
          startCol = j;
          highestPiecesLeft = piecesLeft;
        }
      }
    }
    
    board.initializeBoard(startRow, startCol);
    String initialBoardBinary = board.getBinary();
    int initialPos = PegBoard.getPieceNumber(startRow, startCol) + 1;
    
    printResults(initialPos, initialBoardBinary);
  }
  

  /**
  * Recursive algorithm function.
  */
  private static int solveReverseRecursive(PegBoard board) {
    // DP Case to not search through already visited pegboard states.
    if (piecesMap.containsKey(board.getBinary())) {
      return piecesMap.get(board.getBinary());
    }
    
    boolean[][] bool = board.getBoard(); 
    NextMove bestMove = null;
    int highestPiecesLeft = -1;

    // Check all pieces on map
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < i + 1; j++) {
        // If current position is a peg.
        if (bool[i][j]) {
          // Attempt to try all moves for the piece
          for (PegBoardMove move : PegBoardMove.values()) {
            if (board.canMakeMove(i, j, move)) {
              PegBoard copy = new PegBoard(board.getBoard());
              copy.makeMove(i, j, move);

              // Recursively call function on the new pegboard
              int piecesLeft = solveReverseRecursive(copy);
              
              // Replaces the current best move with the newly found one.
              if (piecesLeft > highestPiecesLeft) {
                highestPiecesLeft = piecesLeft;
                bestMove = new NextMove(i, j, move, copy.getBinary());
              }
            }
          }
        }
      }
    }
    
    // Reached an end state; cannot make any more moves.
    if (bestMove == null) {
      highestPiecesLeft = board.getNumOfPieces();
    }

    // Update maps.
    piecesMap.put(board.getBinary(), highestPiecesLeft);
    movesMap.put(board.getBinary(), bestMove);

    return highestPiecesLeft; 
  }
  
  /**
  * Prints the initial empty space as well as the sequence of moves.
  */
  public static void printResults(int initialPos, String binary) {
    int numOfMoves = 0;
    StringBuilder builder = new StringBuilder();
    
    String currState = binary;
    String nextState;
    NextMove move = movesMap.get(currState);
    int srcPos;
    int destPos;
    
    // We traverse through our movesMap to get the sequence of moves.
    // A state with no valid moves will have a value of null in the moveMap.
    while (move != null) {
      nextState = move.getPegBoardBinary();
      // getPieceNumber starts at 0
      srcPos = PegBoard.getPieceNumber(move.getRow(), move.getCol()) + 1;
      destPos = PegBoard.getPieceNumber(
          move.getRow() + move.getMove().getRowMove(),
          move.getCol() + move.getMove().getColMove()) + 1;
      builder.append(String.format("%d, %d\n", srcPos, destPos));
      numOfMoves++;
      
      move = movesMap.get(nextState);
      currState = nextState;
    }

    String firstLine = String.format("%d, %d\n", initialPos, numOfMoves);
    builder.insert(0, firstLine);
    
    System.out.println(builder.toString());
  }
}


