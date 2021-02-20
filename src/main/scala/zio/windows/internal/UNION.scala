package zio.windows.internal

import com.sun.jna.Union

class UNION(lh: LowHigh, value: Long = 0L) extends Union {

  def this(value: Long) {
    this(new LowHigh(value), value)
  }

  def longValue: Long = value

  override def read(): Unit = {
    readField("lh")
    readField("value")
  }
  override def toString: String = longValue.toString
}
