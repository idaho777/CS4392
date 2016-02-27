package main

import (
  "fmt"
  "os"
  "strconv"
)

func main() {
  args := os.Args[1:]
  
  // Checks number of arguments
  if len(args) != 2 {
    fmt.Println("./main -s (5-9)")
    os.Exit(1)
  } 
  
  // Checks flag string
  if args[0] != "-s" {
    fmt.Println("./main -s (5-9)")
    os.Exit(1)
  }

  // Checks number of rows
  rows, _ := strconv.Atoi(args[1])
  if rows < 5 || rows > 9 {
    fmt.Println("./main -s (5-9)")
    os.Exit(1)
  }

  solveRecursive(rows)
}