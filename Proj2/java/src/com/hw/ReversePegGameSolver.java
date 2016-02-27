package com.hw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.*;

import java.util.Scanner;

/**
* Main algorithm:
* We start with a list of all initial board states.  We then make all possible
* moves for every board state in the list and add these new board states to a
* new list.
* Along the way, if we have a board state that has no possible moves, we
* interrupt and exit out of the search algorithm.
* Once all the board states in the old list are exhausted, we replace the old
* list with the new list and then repeat.  This is important because we are
* looking for unmovable states starting from the state with most pegs.  This
* ensures that we get the unmovable state with most pegs possible.
* 
* We have a Map reverseMovesMap of the binary representation of a board state
* and the board state it came from.
* 
* The parallel computing comes when looking for all possible moves of a board
* state.  The task of searching for all possible moves of a board state is 
* distributed among 10 threads in a thread pool.
* 
* @Author Joonho Kim
*/

public class ReversePegGameSolver {

  private ExecutorService executorService;

  private static Scanner scan = new Scanner(System.in);
  private static PegBoard board;

  public static Map<String, StateNextMove> reverseMovesMap;
  public static Set<String> nextStates;
  public static List<String> currentStates;
  public String endState;

  private static final Object nextLock = new Object();
  private static final Object mapLock = new Object();
  
  public ReversePegGameSolver() {
  }


  /*
  * Initial method that solves the reverse peg game.
  */
  public void solveReverseOP(int rows) {
    currentStates = new ArrayList<>();
    nextStates = new HashSet<>();
    reverseMovesMap = new HashMap<>();

    // Initializes the initial list with initial board states.
    for (int i = 0; i < rows / 2 + 1; i++) {
      for (int j = 0; j < i + 1; j++) {
        board = new PegBoard(rows);
        board.initializeBoard(i, j);
        currentStates.add(board.getBinary());
      }
    }

    // Main loop that exhausts through the list of current states.
    // At the end, the list of current states is replaced with the list of
    //  next computed states.
    while (endState == null) {
      try {
        // Set of new Threads.  Didn't know how to recycle old threads
        // after the executorService shutdown.
        executorService = Executors.newFixedThreadPool(10);

        // Assigns tasks to threads.
        for (String state : currentStates) {
          executorService.execute(new PegSolverTask(state));
        }

        executorService.shutdown();
        executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

        // replaces old list with new list of computed states.
        currentStates = new ArrayList<>();
        currentStates.addAll(nextStates);
        nextStates = new HashSet<>();
      } catch (Exception e) {
        // System.out.println("ThreadPool is over, no need for it.");
      }
    }

    printResults(endState);
  }

  /**
  * Task for the Peg Solver algorithm
  */
  public class PegSolverTask implements Runnable {
    private PegBoard board;
    private String binary;

    public PegSolverTask(String binary) {
      board = new PegBoard(binary);
      this.binary = binary;
    }

    @Override
    public void run() {
      List<String> currNextStates = solveReverseRecursiveOP(binary);
      // Adds all computed next states to nextStates set.
      synchronized (nextLock) {
        if (currNextStates != null) {
          nextStates.addAll(currNextStates);
        }
      }
    }

    // Returns all possible next states of a board state.
    private List<String> solveReverseRecursiveOP(String binary) {
      List<String> next = new ArrayList<>();

      board.setBoard(binary);
      boolean[][] bool = board.getBoard();
      boolean hasMadeMove = false;

      // Check through all pegs to make a move
      for (int i = 0; i < board.getRows(); i++) {
        for (int j = 0; j <= i; j++) {
          // If current position is a peg
          if (bool[i][j]){
            // Makes all moves possible at that position
            for (PegBoardMove move : PegBoardMove.values()) {
              if (board.canMakeMove(i, j, move)) {
                hasMadeMove = true;
                PegBoard copy = new PegBoard(binary);
                copy.makeMove(i, j, move);

                // adds the nextState : currentState pair to movesMap.
                synchronized (mapLock) {
                  next.add(copy.getBinary());
                  // Reverse Map of Dest : From
                  reverseMovesMap.put(copy.getBinary(), new StateNextMove(i, j, move, binary));
                }
              }
            }
          }
        }
      }

      // Found state that has no possible moves
      // Shutsdown threadpool
      if (!hasMadeMove) {
        endState = binary;
        executorService.shutdownNow();
        return null;
      }

      return next;
    }
  }

  
  /**
  * Prints the initial empty space as well as the sequence of moves.
  */
  public static void printResults(String endStateBinary) {
    int numOfMoves = 0;
    StringBuilder builder = new StringBuilder();
    
    String prevState = null;
    StateNextMove move = reverseMovesMap.get(endStateBinary);
    int srcPos;
    int destPos;
    
    // We traverse through our reverseMovesMap to get the sequence of moves.
    // A state with no valid moves will have a value of null in the moveMap.
    while (move != null) {
      prevState = move.getPegBoardBinary(); // Source of currState
      // getPieceNumber starts at 0
      srcPos = PegBoard.getPieceNumber(move.getRow(), move.getCol()) + 1;
      destPos = PegBoard.getPieceNumber(
          move.getRow() + move.getMove().getRowMove(),
          move.getCol() + move.getMove().getColMove()) + 1;
      builder.insert(0, String.format("\n%d, %d", srcPos, destPos));
      numOfMoves++;
      
      move = reverseMovesMap.get(prevState);
    }

    int startPos = prevState.indexOf('0') + 1;

    String firstLine = String.format("%d, %d", startPos, numOfMoves);
    builder.insert(0, firstLine);
    
    System.out.println(builder.toString());
  }
}


