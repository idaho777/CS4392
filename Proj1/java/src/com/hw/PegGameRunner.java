package com.hw;

/**
* Main Method that runs the program.
*
* @Author Joonho Kim
*/
public class PegGameRunner {

  public static void main(String[] args) {
    int rows = -1;

    if (args.length != 2 || !args[0].equals("-s")) {
      System.out.println(invalidArgumentMessage());
    }

    try {
      rows = Integer.parseInt(args[1]);
    } catch(NumberFormatException e) {
      System.out.println(invalidArgumentMessage());
    }

    if (rows < 5 || rows > 10) {
      System.out.println(invalidArgumentMessage());
    } else {
      ReversePegGameSolver.solveReverseDP(rows);
    }
  }

  public static String invalidArgumentMessage() {
    return "Proper arguments are \"-s (# of rows from 5-10)\"";
  }
}
