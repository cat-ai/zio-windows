package zio.windows

import com.sun.jna.{IntegerType, Native}

class NativeSignedSizeT(value: Long = 0) extends IntegerType(Native.SIZE_T_SIZE, value, false) {
  def this() = this(0)
}