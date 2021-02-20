package zio.windows

import com.sun.jna.{IntegerType, Native}

class NativeSizeT(value: Long = 0) extends IntegerType(Native.SIZE_T_SIZE, value, true) {
  def this() = this(0)
}