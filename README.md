# CS4392 Peg Board game

The Peg Board Game is found here: http://www.joenord.com/puzzles/peggame/ .  The purpose of the game is to make a series of moves that leaves one peg on the board.

We had to create a reverse peg board game solver that would leave the most number of pegs possible.


Project 1 consisted of a brute force algorithm.
- Perform a DFS for all the current states of the board.
- I included dynamic programming that mapped states to next states.
- This takes a while and was very inefficient because it would search the same states.

The prolog implementation performed a dumb BFS.


Project 2 performed concurrent programming.
- Algorithm changes were made to make them more optimized. (See Java)
