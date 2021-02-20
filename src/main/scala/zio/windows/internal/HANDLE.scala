package zio.windows.internal

import com.sun.jna.{FromNativeContext, Native, Pointer, PointerType}
import zio.windows.internal.HANDLE.invalidHandleValue

class HANDLE(pointer: Pointer, val mutable: Boolean) extends PointerType(pointer) {

  def this() {
    this(Pointer.NULL, false)
  }

  def this(pointer: Pointer) {
    this(pointer, true)
  }

  override def fromNative(nativeValue: Object, context: FromNativeContext): Object = {
    val o = super.fromNative(nativeValue, context)
    if (invalidHandleValue == o)
      invalidHandleValue
    else o
  }

  override def setPointer(pointer: Pointer): Unit = {
    if (mutable)
      throw new UnsupportedOperationException("Immutable reference")
    else
      super.setPointer(pointer)
  }

  override def toString: String = String.valueOf(getPointer)
}

object HANDLE {
  val invalidHandleValue =
    new HANDLE(Pointer.createConstant(if (Native.POINTER_SIZE == 8) -1 else 0xFFFFFFFFL))
}