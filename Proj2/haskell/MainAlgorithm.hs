module MainAlgorithm where
  import Control.Parallel

  type Pos = (Int, Int)   -- Coordinate
  type Move = (Pos, Pos)  -- From, To
  type Board = [Pos]
  type Answer = (Pos, [Move]) -- (Formatted First Line, [List of Moves])

  -- Beginning of algorithm --
  -- Picks all initial position --
  runAlgorithm rows =
    solve rows [(row, col) | row <- [0 .. (rows `div` 2)], col <- [0 .. row]]

  -- Returns an Answer tuple --
  solve rows [] = (" ", [])
  solve rows (initialPos:tailList)
    | null bestMoves =
        currAnswer
    | null currMoves =
        bestAnswer
    | otherwise =
      currMoves `par` (bestAnswer `pseq` (answerLessMoves currAnswer bestAnswer))
    where
      bestAnswer = solve rows (tailList)
      bestMoves = snd bestAnswer
      currMoves = solveRecursive (initializeBoard initialPos rows) rows []
      currAnswer = (formatInitial initialPos (length currMoves), currMoves)

  -- Returns a list of moves to an end state --
  solveRecursive board rows moveList
    -- Solved the game --
    | length board == 1 =
        moveList
    -- Didn't solve the game, but no more valid moves
    | length availableMoves == 0 = 
        moveList
    | otherwise =
        goThroughMoves availableMoves
    where
      availableMoves = allPossibleMoves board rows
      goThroughMoves [] = []
      goThroughMoves (move:moves) 
        | null result =
            bestResult
        | null bestResult =
            result
        | otherwise =
          result `par` (bestResult `pseq` (listLessMoves result bestResult))
        where 
          result = solveRecursive (performMove board move) rows
              (moveList ++ [formatMove move])
          bestResult = goThroughMoves moves

  -- Returns the Answer with the shorter list of moves
  answerLessMoves currAnswer bestAnswer
    | length (currMoves) < length (bestMoves) =
      currAnswer
    | otherwise =
      bestAnswer
    where
      bestMoves = snd bestAnswer
      currMoves = snd currAnswer

  -- Returns the shorter list --
  listLessMoves listA listB
    | length (listA) < length (listB) =
      listA
    | otherwise =
      listB




  -- Initializes the board --
  initializeBoard (initialRow, initialCol) rows = [ (row, col) 
      | row <- [0 .. (rows - 1)], col <- [0 .. row], 
      not(row == initialRow && col == initialCol) ]

  -- All Possible moves for a certain peg --
  possibleMoves peg board rows = 
    -- List of moves from Peg to dest --
    [(peg, dest) | dest <- emptyDestinations, isPeg board (getOver peg dest)]
    where
      emptyDestinations = filter (isEmpty board) destinationsOnBoard -- gets destinations without a peg
      destinationsOnBoard = filter (isValidPos rows) allDestinations -- gets destinations on board
      allDestinations = map (addPositions peg) directionalMoves -- gets all destinations period

  -- List of all possible moves on the board --
  allPossibleMoves board rows = concat [possibleMoves (row, col) board rows
    | row <- [0 .. (rows - 1)], col <- [0 .. row], isPeg board (row, col)]

  performMove board (from, to) =
    filter (\pos -> pos /= from && pos /= (getOver from to)) (to:board)

  -- Checks whether the position is a peg --
  isPeg board peg = elem peg board
  isEmpty board peg = not (isPeg board peg)
  -- Checks whether peg is within the board --
  isValidPos rows (row, col) = elem row [0 .. (rows - 1)] && elem col [0 .. row]

  addPositions (row1, col1) (row2, col2) = (row1 + row2, col1 + col2)
  -- Gets piece in between From and To --
  getOver (row1, col1) (row2, col2) =
    ((row1 + row2) `div` 2, (col1 + col2) `div` 2)



  -- String formatting stuff --
  formatMove (from, to) =
    show (getPegNumber from) ++ ", " ++ show (getPegNumber to)
  formatInitial pos moves =
    show (getPegNumber pos) ++ ", " ++ show moves

  getPegNumber (row, col) =
    (row * (row + 1) `div` 2) + col + 1


  printAnswer (initial, moves) =
    putStrLn (initial ++ "\n" ++ (unlines $ moves))

  directionalMoves = [ 
    (0, -2),  --LEFT--
    (0, 2),   --RIGHT--
    (-2, -2), --TOP_LEFT--
    (-2, 0),  --TOP_RIGHT--
    (2, 0),   --BOTTOM_LEFT--
    (2, 2)]   --BOTTOM_RIGHT--
