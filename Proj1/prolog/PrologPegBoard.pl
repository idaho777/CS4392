
main :-
    %% Parse Flags
    current_prolog_flag(argv, Argv),
    (   
        nth0(0, Argv, '-s'),
        nth0(1, Argv, Id)
    ),
    atom_number(Id, Rows),
    runReversePeg(Rows),
    halt.
    
main :-
    print(["Invalid Arguments: -s (5-10)"]),
    halt.

runReversePeg(Rows) :-
    board(Rows, Board),
    solveInitial(Board, Rows, 0, OutputList),
    print(OutputList).

%% We give the algorithm a number of moves to finish in.
%% The algorithm will bruteforce through all states with the given number of moves.
%% If the algorithm cannot reach an end state, we increase MovesAvailable and try again.
solveInitial(Board, Rows, MovesAvailable, OutputList) :-
    setup(Board, Rows, InitialBoard, InitialPos),
    solveRecursive(InitialBoard, Rows, MovesAvailable, MovesList),
    length(MovesList, NumberOfMoves),
    formatInitialPosition(InitialPos, NumberOfMoves, InitialString),
    OutputList = [InitialString|MovesList],
    !.
%% Increases the number of Moves available and starts over.
solveInitial(Board, Rows, MovesAvailable, MovesList) :-
    MoreMoves is MovesAvailable + 1,
    solveInitial(Board, Rows, MoreMoves, MovesList).


%% This part recursively makes and backtracks moves.
%% No valid moves and no moves available
solveRecursive(Board, Rows, 0, _) :-
    \+validMove(_, Board, Rows),
    !.
%% Valid Moves and Moves Available
solveRecursive(Board, Rows, MovesAvailable, MovesList) :-
    MovesAvailable > 0,
    validMove(Move, Board, Rows),
    makeMove(Move, Board, NewBoard),
    LessMoves is MovesAvailable - 1,
    solveRecursive(NewBoard, Rows, LessMoves, NextMoves),
    formatMove(Move, FormattedMove),
    MovesList = [FormattedMove|NextMoves],
    !.

%% Setups the board by picking the empty position.
setup(Board, Rows, InitialBoard, InitialPos) :-
    InitialRows is truncate(Rows / 2) + 1,
    empty(O),
    validPos(InitialPos, InitialRows),
    setPos(InitialPos, Board, O, InitialBoard).

%%==========================================================================
%%==========================================================================
%% Moves the algorithm can make
moveTopLeft([FromRow, FromCol], [ToRow, ToCol])     :- ToRow is FromRow - 2, ToCol is FromCol - 2.
moveTopRight([FromRow, FromCol], [ToRow, ToCol])    :- ToRow is FromRow - 2, ToCol is FromCol.
moveLeft([FromRow, FromCol], [ToRow, ToCol])        :- ToRow is FromRow    , ToCol is FromCol - 2.
moveRight([FromRow, FromCol], [ToRow, ToCol])       :- ToRow is FromRow    , ToCol is FromCol + 2.
moveBottomLeft([FromRow, FromCol], [ToRow, ToCol])  :- ToRow is FromRow + 2, ToCol is FromCol.
moveBottomRight([FromRow, FromCol], [ToRow, ToCol]) :- ToRow is FromRow + 2, ToCol is FromCol + 2.

move(From, To) :- moveTopLeft(From, To).
move(From, To) :- moveTopRight(From, To).
move(From, To) :- moveLeft(From, To).
move(From, To) :- moveRight(From, To).
move(From, To) :- moveBottomLeft(From, To).
move(From, To) :- moveBottomRight(From, To).

%% 1) Checks whether From and To's Rows and Columns are within the board.
%% 2) Checks whether From and To are valid spots to perform a move.
%% 3) Checks whether From, To, and Over are the correct pieces to perform a move.
validMove([From, To], Board, Rows) :-
    validPos(From, Rows),
    validPos(To, Rows),
    move(From, To),
    getOver(From, Over, To),        %% Gets Peg in between From and To positions
    validPos(Over, Rows),
    isPeg(From, Board),
    isPeg(Over, Board),
    isEmpty(To, Board).

validPos([Row, Col], Rows) :-
    X is Rows - 1,
    between(0, X, Row),
    between(0, X, Col).

isPeg([Row, Col], Board) :-
    peg(P),
    valueAt([Row, Col], Board, P).

isEmpty([Row, Col], Board) :-
    empty(P),
    valueAt([Row, Col], Board, P).

valueAt([Row, Col], Board, Value) :-
    nth0(Row, Board, RowList),  %% Get Row Value which is a list
    nth0(Col, RowList, Value).  %% Get element at col index

%% Gets the piece in between from and To.
%% Should assume From and To are atleast one peg away
getOver([FromRow, FromCol], [OverRow, OverCol], [ToRow, ToCol]) :-
    OverRow is (FromRow + ToRow) / 2,
    OverCol is (FromCol + ToCol) / 2.

%%==========================================================================
%%==========================================================================
%% Performs a move and returns a NewBoard reflecting the move.
makeMove([From, To], Board, NewBoard) :-
    peg(X),
    empty(O),
    getOver(From, Over, To),
    setPos(From, Board, O, First),
    setPos(Over, First, O, Second),
    setPos(To, Second, X, NewBoard).

setPos([Row, Col], Board, Val, NewBoard) :-
    setVal(Row, Col, Board, Val, NewBoard).

%% Set Val assuming Row and Col are valid
%% Returns NewBoard
setVal(Row, Col, Board, Val, NewBoard) :-
    %% set_value2(Row, Col, Board, Val, NewBoard).
    setValRow(Row, Col, Board, Val, NewBoard).

%% BaseCase: Stopped at correct column,
%% return [Val|Tail] List with Val and rest of the tail
setValCol(0, [_|Tail], Val, [Val|Tail]).
%% Recursive Case: Keeps checking through column values.
setValCol(Col, [Head|Tail], Val,[Head|NewList]) :-
    Count is Col - 1,
    setValCol(Count, Tail, Val, NewList).

%% Base Case: Stopped at correct row
setValRow(0, Col, [Row|Tail], Val, [NewRow|Tail]) :-
    setValCol(Col, Row, Val, NewRow).
%% Recursive Case: Keeps checking through list of rows.
setValRow(Row, Col, [Head|TailList], Val, [Head|NewList]) :-
    Count is Row - 1,
    setValRow(Count, Col, TailList, Val, NewList).

%%==========================================================================
%%==========================================================================

%% Gets a Peg Number based on the row and column
getPegNumber([Row, Col], PegNumber) :-
    PegNumber is (Row * (Row + 1) / 2) + Col + 1.

%% Formats a move ie. '1, 4'
formatMove([[FromRow, FromCol], [ToRow, ToCol]], Move) :-
    getPegNumber([FromRow, FromCol], FromPegNumber),
    getPegNumber([ToRow, ToCol], ToPegNumber),
    format(atom(Move), '~D, ~D', [FromPegNumber, ToPegNumber]).

%% Formats the first line of the output: EmptyPosition, Number of Moves
formatInitialPosition([FromRow, FromCol], NumberOfMoves, String) :-
    getPegNumber([FromRow, FromCol], PegNumber),
    format(atom(String), '~D, ~D', [PegNumber, NumberOfMoves]).

%% Prints a list of atoms.
print([]).
print([X|Y]) :- 
    write(X),
    nl,
    print( Y ).

%%==========================================================================
%%==========================================================================
peg(x).
empty(o).

board(5,
[[x],
[x,x],
[x,x,x],
[x,x,x,x],
[x,x,x,x,x]]).

board(6,
[[x],
[x,x],
[x,x,x],
[x,x,x,x],
[x,x,x,x,x],
[x,x,x,x,x,x]]).

board(7,
[[x],
[x,x],
[x,x,x],
[x,x,x,x],
[x,x,x,x,x],
[x,x,x,x,x,x],
[x,x,x,x,x,x,x]]).

board(8,
[[x],
[x,x],
[x,x,x],
[x,x,x,x],
[x,x,x,x,x],
[x,x,x,x,x,x],
[x,x,x,x,x,x,x],
[x,x,x,x,x,x,x,x]]).

board(9,
[[x],
[x,x],
[x,x,x],
[x,x,x,x],
[x,x,x,x,x],
[x,x,x,x,x,x],
[x,x,x,x,x,x,x],
[x,x,x,x,x,x,x,x],
[x,x,x,x,x,x,x,x,x]]).

board(10,
[[x],
[x,x],
[x,x,x],
[x,x,x,x],
[x,x,x,x,x],
[x,x,x,x,x,x],
[x,x,x,x,x,x,x],
[x,x,x,x,x,x,x,x],
[x,x,x,x,x,x,x,x,x],
[x,x,x,x,x,x,x,x,x,x]]).