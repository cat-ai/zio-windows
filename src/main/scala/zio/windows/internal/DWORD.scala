package zio.windows.internal

import com.sun.jna.IntegerType

class DWORD(val value: Long) extends IntegerType(DWORD.SIZE, value, true) with Comparable[DWORD] {

  def this() {
    this(0)
  }

  def getLow:  WORD = new WORD(longValue         & 0xFFFF)
  def getHigh: WORD = new WORD((longValue >> 16) & 0xFFFF)

  override def compareTo(other: DWORD): Int = IntegerType.compare(this, other)
}

object DWORD {
  val SIZE = 4
}
