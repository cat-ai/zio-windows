package zio.windows.internal

import com.sun.jna.IntegerType

class WORD(val value: Long) extends IntegerType(WORD.SIZE, value, true) with Comparable[WORD] {

  def this() {
    this(0)
  }

  override def compareTo(other: WORD): Int =
    IntegerType.compare(this, other)
}

object WORD {
  /** The Constant SIZE. */
  val SIZE = 2
}