package zio.windows.internal

import com.sun.jna.{Pointer, PointerType}

class LPVOID(pointer: Pointer) extends PointerType(pointer) {
  def this() = this(Pointer.NULL)
}