

object Quicksort {
  def main(args: Array[String]): Unit = {
    val unsortedArray = Array(1, 3, 5, 2, 4);
    sort(unsortedArray)
    for (element <- unsortedArray)
      println(element)
  }

  def sort(unsortedArray: Array[Int]): Unit = {
    def swap(i: Int, j: Int): Unit = {
      val t = unsortedArray(i);
      unsortedArray(i) = unsortedArray(j);
      unsortedArray(j) = t;
    }

    def sort1(left: Int, right: Int): Unit = {
      val middle = unsortedArray((left + right) / 2)
      var i = left;
      var j = right;
      while (i <= j) {
        while (unsortedArray(i) < middle) i += 1;
        while (unsortedArray(j) > middle) j -= 1;
        if (i <= j) {
          swap(i, j)
          i += 1
          j -= 1
        }
      }
      if (left < j) sort1(left, j)
      if (j < right) sort1(i, right)
    }

    sort1(0, unsortedArray.length - 1)
  }
}
