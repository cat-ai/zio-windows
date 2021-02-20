package zio.windows.internal

import com.sun.jna.{IntegerType, Native, Pointer}

class ULONG_PTR(val value: Long) extends IntegerType(Native.POINTER_SIZE, value, true) {

  def this() {
    this(0)
  }

  def asPointer: Pointer = Pointer.createConstant(longValue)
}