from ReversePegGameSolver import *
import sys, argparse

class PegGameRunner(object):
    rows = int()
      
    @classmethod
    def main(cls):
        parser = argparse.ArgumentParser(description='Reverse Peg Game Solver')
        parser.add_argument('-s', nargs=1)
        args = parser.parse_args(sys.argv[1:])
        
        rows = int(args.s.pop())
        if rows < 5 or rows > 10:
            print 'Invalid arguments: must have -s (5-10)'
        else:
            game = ReversePegGameSolver()
            game.solveReverseDP(rows)

    
if __name__ == '__main__':
    PegGameRunner.main()
