
class PegBoardMove:
    rowMove = int()
    colMove = int()

    def __init__(self, rowMove, colMove):
        self.rowMove = rowMove
        self.colMove = colMove
    

PegBoardMove.LEFT = PegBoardMove(0, -2)
PegBoardMove.RIGHT = PegBoardMove(0, 2)
PegBoardMove.TOP_LEFT = PegBoardMove(-2, -2)
PegBoardMove.TOP_RIGHT = PegBoardMove(-2, 0)
PegBoardMove.BOTTOM_LEFT = PegBoardMove(2, 0)
PegBoardMove.BOTTOM_RIGHT = PegBoardMove(2, 2)
PegBoardMove.LIST = [
    PegBoardMove.LEFT, 
    PegBoardMove.RIGHT,
    PegBoardMove.TOP_LEFT,
    PegBoardMove.TOP_RIGHT,
    PegBoardMove.BOTTOM_LEFT,
    PegBoardMove.BOTTOM_RIGHT]