package main


import (
  "fmt"
  "strings"
  "sync"
)

/*
  Move State of current board and next move to make
*/
type MoveState struct {
  row int
  col int
  move Move
  srcState string
}

/*
  Map of destBoardState : srcMoveState
  This has a mutex in order to control concurrent read/write operations
*/
type MoveConMap struct {
  sync.RWMutex
  m map[string]MoveState
}

// Final end state
var endState string
var dsMoveMap *MoveConMap

/*
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
* We have a Map dsMoveMap of the binary representation of a board state
* and the board state it came from.
* 
* The parallel computing comes when looking for all possible moves of a board
* state.  The task of searching for all possible moves of a board state is 
* distributed among different go routines.  The go routines are fed a single
* channel that holds all next states.
*/
func solveRecursive(rows int) {
  var currentStates []string
  var nextStates []string
  dsMoveMap = new(MoveConMap)
  dsMoveMap.m = make(map[string]MoveState)

  // Initializes the currenStates list with starting positions
  for i := 0; i < rows / 2 + 1; i++ {
    for j := 0; j <= i; j++ {
      board := initializeBoard(rows, i, j)
      currentStates = append(currentStates, board.getBinary())
    }
  }

  // Single channel that will hold all nextStates as a blocking queue.
  var c chan []string = make(chan []string)

  // BFS main loop
  for endState == "" {
    i := -1
    // Calls getAllNextStates for each state in currentStates.
    // This is parallel by multiple go routines.
    for i = 0; i < len(currentStates) && endState == ""; i++ {
      go getAllNextStates(currentStates[i], c)
    }

    // Go routines return and adds states to nextStates.
    // This stops once all go routines are finishes.
    for i > 0 {
      nextStates = append(nextStates, <-c...)
      i--
    }
    currentStates = nextStates
    nextStates = []string{}
  }

  printResults()
}

func getAllNextStates(binary string, c chan []string) {
  var nextList []string

  board := new(PegBoard)
  board.setBoard(binary)

  boardArray := board.boardArray
  hasMadeMove := false

  rows := len(boardArray)

  // looks through all board positions
  for i := 0; i < rows; i++ {
    for j := 0; j <= i; j++ {
      // if position is a peg
      if boardArray[i][j] {
        // attemps to make all possible moves on current position
        for moveKey, _ := range moveDict {
          // if current move can be made
          if board.canMakeMove(i, j, moveKey) {
            hasMadeMove = true
            board.makeMove(i, j, moveKey)
            nextList = append(nextList, board.getBinary())

            // adds new MoveState from to dsMoveMap (dest: src)
            dsMoveMap.Lock()
            dsMoveMap.m[board.getBinary()] = MoveState{i, j, moveKey, binary}
            dsMoveMap.Unlock()

            board.setBoard(binary)
          }
        }
      }
    }
  }

  // If no moves were made
  if !hasMadeMove {
    endState = binary
    c <- nil
  }

  c <- nextList
}


/*
  Prints results as a sequence of moves.
*/
func printResults() {
  var stringBuilder string
  numOfMoves := 0
  moveMap := dsMoveMap.m

  var prevState string
  srcMove := moveMap[endState]

  srcPos, destPos := 0, 0

  // We traverse through our reverseMovesMap to get the sequence of moves.
  // A state with no valid moves will have a value of null in the moveMap.
  for srcMove.srcState != "" {
    prevState = srcMove.srcState

    moveDelta := moveDict[srcMove.move]
    srcPos = getPieceNumber(srcMove.row, srcMove.col) + 1
    destPos = getPieceNumber(
      srcMove.row + moveDelta.dRow,
      srcMove.col + moveDelta.dCol) + 1
    stringBuilder = fmt.Sprintf("\n%[1]d, %[2]d", srcPos, destPos) + stringBuilder
    numOfMoves++

    srcMove = moveMap[prevState]
  }

  startPos := strings.Index(prevState, "0") + 1
  firstLine := fmt.Sprintf("%[1]d, %[2]d", startPos, numOfMoves)
  finalString := firstLine + stringBuilder

  fmt.Println(finalString)
}