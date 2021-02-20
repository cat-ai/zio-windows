package zio.windows.internal

import com.sun.jna.Pointer

class HWND(pointer: Pointer) extends HANDLE(pointer) {

  def this() {
    this(Pointer.NULL)
  }
}