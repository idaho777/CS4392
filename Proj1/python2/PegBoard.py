
class PegBoard(object):

    def __init__(self, rows):
        self.rows = rows
        self.numOfPieces = 0
        self.board = [[] for _ in range(rows)]
        self.numOfPieces = 0

        for i in range (0, self.rows):
            for j in range(0, i+1):
                self.board[i].append(0)

    def initFromBoard(self, board):
        self(len(board))

        for i in range(0, self.rows):
            for j in range(0, i+1):
                if (board[i][j]):
                    self.board[i][j] = True
                    self.numOfPieces = self.numOfPieces + 1

    def initializeBoard(self, row, col):
        if not self.posOnBoard(row, col):
            raise IndexError("Row/Col is not valid position on the board")
        i = 0
        for i in range(0, self.rows):
           for j in range(0, i+1):
                self.board[i][j] = True
         
        self.board[row][col] = False
        self.numOfPieces = ((self.rows * (self.rows + 1)) / 2) - 1
        
    @classmethod
    def getPieceNumber(cls, row, col):
        return ((row * (row + 1)) / 2) + col

    def makeMove(self, row, col, move):
        destRow = row + move.rowMove
        destCol = col + move.colMove
        midRow = (destRow + row) / 2
        midCol = (destCol + col) / 2
        self.board[row][col] = False
        self.board[midRow][midCol] = False
        self.board[destRow][destCol] = True
        self.numOfPieces -= 1

    def canMakeMove(self, row, col, move):
        destRow = row + move.rowMove
        destCol = col + move.colMove
        if not self.posOnBoard(destRow, destCol):
            return False
        midRow = (destRow + row) / 2
        midCol = (destCol + col) / 2
        return not self.board[destRow][destCol] and self.board[midRow][midCol]

    def posOnBoard(self, row, col):
        return (row >= 0 and row < self.rows) and (col >= 0 and col <= row)

    def getBinary(self):
        bin = ""
        for bArr in self.board:
            for b in bArr:
                bin += "1" if (b) else "0"
        return bin

    def equals(self, o):
        if isinstance(o, (PegBoard, )):
            if (PegBoard(o)).getRows() == rows:
                return self.getBinary() == (PegBoard(o).getBinary())
        return False

    def __str__(self):
        output = ""
        for b in self.board:
            for bool_ in b:
                output += "1" if (bool_) else "0"
            output += "\n"
        return output

    def printBoard(self):
        print self.__str__()
