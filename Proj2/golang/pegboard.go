package main

import (
  "fmt"
  "math"
  // "strconv"
)

type Move int
const (
  TOPLEFT Move = iota
  TOPRIGHT
  LEFT
  RIGHT
  BOTTOMLEFT
  BOTTOMRIGHT
)

type MoveDelta struct {
  dRow int
  dCol int
}
var moveDict = map[Move]MoveDelta {
  TOPLEFT     : MoveDelta{-2, -2},
  TOPRIGHT    : MoveDelta{-2, 0},
  LEFT        : MoveDelta{0, -2},
  RIGHT       : MoveDelta{0, 2},
  BOTTOMLEFT  : MoveDelta{2, 0},
  BOTTOMRIGHT : MoveDelta{2, 2},
}


type PegBoard struct {
  boardArray [][]bool
}

func initializeBoard(rows int, row int, col int) *PegBoard {
  pegBoard := new(PegBoard)
  pegBoard.boardArray = make([][]bool, rows)

  for i := 0; i < rows; i++ {
    pegBoard.boardArray[i] = make([]bool, i + 1)
    for j := 0; j <= i; j++ {
      pegBoard.boardArray[i][j] = true
    }
  }

  pegBoard.boardArray[row][col] = false

  return pegBoard
}

/*
 *  
 */
func (pegBoard *PegBoard) setBoard(binary string) {
  rows := int(math.Sqrt(float64(2 * len(binary))))

  board := make([][]bool, rows)

  currRow := 0
  stringPos := 0

  for stringPos < len(binary) {
    board[currRow] = make([]bool, currRow + 1)
    for currCol := 0; currCol <= currRow; currCol++ {
      board[currRow][currCol] = string(binary[stringPos]) == "1"
      stringPos++
    }
    currRow++
  }

  pegBoard.boardArray = board
}


func (pegBoard *PegBoard) canMakeMove(row int, col int, move Move) bool {
  board := pegBoard.boardArray

  // Checks if position is a peg
  if !board[row][col] {
    return false
  }

  destRow := row + moveDict[move].dRow
  destCol := col + moveDict[move].dCol

  // Checks if destination is a valid position on the board
  if !pegBoard.posOnBoard(destRow, destCol) {
    return false
  }

  midRow := (row + destRow) / 2
  midCol := (col + destCol) / 2

  return !board[destRow][destCol] && board[midRow][midCol]
}

func (pegBoard *PegBoard) makeMove(row int, col int, move Move) {
  board := pegBoard.boardArray

  destRow := row + moveDict[move].dRow
  destCol := col + moveDict[move].dCol

  midRow := (row + destRow) / 2
  midCol := (col + destCol) / 2

  board[row][col] = false;
  board[midRow][midCol] = false;
  board[destRow][destCol] = true;
}

func getPieceNumber(row int, col int) int {
  return ((row * (row + 1)) / 2) + col;
}

func (pegBoard *PegBoard) posOnBoard(row int, col int) bool {
  boardArray := pegBoard.boardArray
  return (row >= 0 && row < len(boardArray)) && (col >= 0 && col <= row);
}


func (pegBoard *PegBoard) getBinary() string {
  boardArray := pegBoard.boardArray
  binary := ""

  for i := 0; i < len(boardArray); i++ {
    for j := 0; j <= i; j++ {
      if boardArray[i][j] {
        binary += "1"
      } else {
        binary += "0"
      }
    }
  }

  return binary
}

func (pegBoard *PegBoard) printBoard() {
  boardArray := pegBoard.boardArray

  for i := 0; i < len(boardArray); i++ {
    for j := 0; j <= i; j++ {
      if boardArray[i][j] {
        fmt.Print("1 ")
      } else {
        fmt.Print("0 ")
      }
    }
    fmt.Println()
  }
}