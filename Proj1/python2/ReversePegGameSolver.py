from PegBoard import *
from PegBoardMove import *
from NextMove import *
import copy

class ReversePegGameSolver(object):
    def __init__(self):
        pass

    def solveReverseDP(self, rows):
        self.movesMap = {}
        self.piecesMap = {}
        
        board = PegBoard(rows)
        startRow = 0
        startCol = 0
        highestPiecesLeft = -1
        for i in range(0, rows/2+1):
            for j in range(0, i+1):
                board.initializeBoard(i, j)
                piecesLeft = self.solveReverseRecursive(board)
                if piecesLeft > highestPiecesLeft:
                    startRow = i
                    startCol = j
                    highestPiecesLeft = piecesLeft
                
        board.initializeBoard(startRow, startCol)
        initialBoardBinary = board.getBinary()
        initialPos = PegBoard.getPieceNumber(startRow, startCol) + 1
        self.printResults(initialPos, initialBoardBinary)

    def solveReverseRecursive(self, pegBoard):
        # print board
        if pegBoard.getBinary() in self.piecesMap:
            return self.piecesMap.get(pegBoard.getBinary())
        bool_ = pegBoard.board
        bestMove = None
        highestPiecesLeft = -1
        
        for i in range(0, pegBoard.rows):
            for j in range(0, i+1):
                # print str(i) + ' ' + str(j)
                if bool_[i][j]:
                    for move in PegBoardMove.LIST:
                        if pegBoard.canMakeMove(i, j, move):
                            copy1 = copy.deepcopy(pegBoard)
                            copy1.makeMove(i, j, move)
                            piecesLeft = self.solveReverseRecursive(copy1)
                            if piecesLeft > highestPiecesLeft:
                                highestPiecesLeft = piecesLeft
                                bestMove = NextMove(i, j, move, copy1.getBinary())
        #  Check all pieces on map
        #  Attempt to try all moves for pieces
        if bestMove == None:
            #  You cannot make any moves.
            highestPiecesLeft = pegBoard.numOfPieces
        self.piecesMap[pegBoard.getBinary()] = highestPiecesLeft
        self.movesMap[pegBoard.getBinary()] = bestMove
        return highestPiecesLeft




    def printResults(self, initialPos, binary):
        numOfMoves = 0
        builder = []
        currState = binary
        nextState = str()
        move = self.movesMap.get(currState)
        srcPos = int()
        destPos = int()
        while move != None:
            nextState = move.pegBoardBinary
            srcPos = PegBoard.getPieceNumber(move.row, move.col) + 1
            destPos = PegBoard.getPieceNumber(
                move.row + move.move.rowMove, 
                move.col + move.move.colMove) + 1
            builder.append("%d, %d" %(srcPos, destPos))
            numOfMoves += 1
            move = self.movesMap.get(nextState)
            currState = nextState
        firstLine = "%d, %d " %(initialPos, numOfMoves)
        builder.insert(0, firstLine)

        for line in builder:
            print line
            