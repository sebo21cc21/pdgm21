import scala.concurrent.{Await, Future}
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.util.Random


object L7 {

  // TIME COUNTER
  def time[A](fun: => A): Unit = {
    val startTime = System.nanoTime()
    fun
    val stopTime = System.nanoTime()
    println("Time equals: " + (stopTime - startTime) /1000000 + " ms")
  }

  // FIBONACCI

  def fib(n:BigInt) : BigInt =
    n match
    {
      case 0 => 0
      case 1 => 1
      case _ => if!(n <= 0) then return fib(n-2) + fib(n-1)
      else throw new Exception ("n less than zero")
    }

  def fibParallel(n:BigInt) : BigInt =
    n match
    {
      case 0 => 0
      case 1 => 1
      case _ => if!(n <= 0) then
        val f1 = Future(fib(n-1)) //rozdielamy procesy, uruchamiając potem równolegle dwa
        val f2 = Future(fib(n-2))
        return Await.result(f1, Inf) + Await.result(f2, Inf) //czekamy na ich wynik
      else throw new Exception ("n less than zero")
    }

  // QUICKSORT z wykładu

  def swap (tab:Array[Int], i:Int, j:Int):Unit= {
    val aux = tab(i)
    tab(i) = tab(j)
    tab(j) = aux
  }

  def choose_pivot(tab:Array[Int])(m:Int)(n:Int):Int= tab((m+n)/2)

  def partition(tab: Array[Int])(l: Int)(r: Int):(Int, Int) = {
    var i = l; var j = r; val pivot = choose_pivot(tab)(l)(r)
    while (i <= j) {
      while (tab(i) < pivot) i += 1
      while (pivot < tab(j)) j -= 1
      if (i <= j)
        swap(tab, i, j); i += 1; j -= 1
    }
    (i, j)
  }

  def quick(tab: Array[Int])(l: Int)(r: Int):Unit=
    if (l < r) {
      val (i, j) = partition(tab)(l)(r)
      if (j - l < r - i) {
        quick(tab)(l)(j)
        quick(tab)(i)(r)
      }
      else {
        quick(tab)(i)(r)
        quick(tab)(l)(j)
      }
    }
    else ()

  def quicksort(tab: Array[Int]):Unit = quick(tab)(0)(tab.length - 1)

  def quickParallel(tab: Array[Int])(l: Int)(r: Int):Unit=
    if (l < r) {
      val (i, j) = partition(tab)(l)(r)
      if (j - l < r - i) {
        val f1 = Future(quick(tab)(l)(j))
        val f2 = Future(quick(tab)(i)(r))
        Await.result(f1, Inf); Await.result(f2,Inf)
      }
      else {
        val f1 = Future(quick(tab)(i)(r))
        val f2 = Future(quick(tab)(l)(j))
        Await.result(f1, Inf); Await.result(f2,Inf)
      }
    }
    else ()

  def quicksortParallel(tab: Array[Int]):Unit = quickParallel(tab)(0)(tab.length - 1)

  def main(args: Array[String]): Unit = {

    //FIBONACCI TESTS
    println("Fibonacci sequence 20 without parallel: ")
    time(fib(20))
    println("Fibonacci sequence 20 parallel: ")
    time(fibParallel(20))

    println("Fibonacci sequence 25 without parallel: ")
    time(fib(25))
    println("Fibonacci sequence 25 parallel: ")
    time(fibParallel(25))

    println("Fibonacci sequence 30 without parallel: ")
    time(fib(30))
    println("Fibonacci sequence 30 parallel: ")
    time(fibParallel(30))

    println("Fibonacci sequence 35 without parallel: ")
    time(fib(35))
    println("Fibonacci sequence 35 parallel: ")
    time(fibParallel(35))

    println("Fibonacci sequence 40 without parallel: ")
    time(fib(40))
    println("Fibonacci sequence 40 parallel: ")
    time(fibParallel(40))

    println()
    //QUICKSORT TESTS

    val tab1 = Array.fill(10000)(Random.nextInt(100000))
    val tab2 = Array.fill(100000)(Random.nextInt(100000))
    val tab3 = Array.fill(1000000)(Random.nextInt(100000))
    println("Quick sort with 10 000 elem without parallel: ")
    time(quicksort(tab1))
    println("Quick sort with 10 000 elem parallel: ")
    time(quicksortParallel(tab1))

    println("Quick sort with 100 000 elem without parallel: ")
    time(quicksort(tab2))
    println("Quick sort with 100 000 elem parallel: ")
    time(quicksortParallel(tab2))

    println("Quick sort with 1 000 000 elem without parallel: ")
    time(quicksort(tab3))
    println("Quick sort with 1 000 000 elem parallel: ")
    time(quicksortParallel(tab3))
  }


}

//OUTPUT FIBBONACI

//Fibonacci sequence 20 without parallel:
//Time equals: 10 ms
//Fibonacci sequence 20 parallel:
//Time equals: 73 ms
//Fibonacci sequence 25 without parallel:
//Time equals: 2 ms
//Fibonacci sequence 25 parallel:
//Time equals: 2 ms
//Fibonacci sequence 30 without parallel:
//Time equals: 25 ms
//Fibonacci sequence 30 parallel:
//Time equals: 15 ms
//Fibonacci sequence 35 without parallel:
//Time equals: 297 ms
//Fibonacci sequence 35 parallel:
//Time equals: 171 ms
//Fibonacci sequence 40 without parallel:
//Time equals: 3208 ms
//Fibonacci sequence 40 parallel:
//Time equals: 1991 ms

//OUTPUT QUICKSORT

//Quick sort with 10 000 elem without parallel:
//Time equals: 2 ms
//Quick sort with 10 000 elem parallel:
//Time equals: 3 ms
//Quick sort with 100 000 elem without parallel:
//Time equals: 11 ms
//Quick sort with 100 000 elem parallel:
//Time equals: 11 ms
//Quick sort with 1 000 000 elem without parallel:
//Time equals: 87 ms
//Quick sort with 1 000 000 elem parallel:
//Time equals: 25 ms
