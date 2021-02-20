package zio.windows.internal

import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder

@FieldOrder(Array("LowPart", "HighPart"))
class LowHigh(LowPart: DWORD, HighPart: DWORD) extends Structure {

  def this(value: Long) {
    this(new DWORD(value & 0xFFFFFFFFL), new DWORD((value >> 32) & 0xFFFFFFFFL))
  }

  def this() {
    this(0)
  }

  def longValue: Long = {
    val loValue = LowPart.longValue
    val hiValue = HighPart.longValue
    ((hiValue << 32) & 0xFFFFFFFF00000000L) | (loValue & 0xFFFFFFFFL)
  }

  override def toString: String = longValue.toString
}