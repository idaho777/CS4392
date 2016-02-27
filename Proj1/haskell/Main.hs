{-# LANGUAGE DeriveDataTypeable #-}
module Main where
  import MainAlgorithm
  import System.Console.CmdArgs

  data Sample = Sample {s :: Integer}
                deriving (Show, Data, Typeable)

  sample = Sample{s = def}

  main = do
    x <- cmdArgs sample
    runner (s x)
    
  runner x
    | x >= 5 && x <= 10 =
      (printAnswer . runAlgorithm) x
    | otherwise =
      print "Not in range 5-10"
