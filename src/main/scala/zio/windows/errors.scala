package zio.windows

import com.sun.jna.Native

import java.io.IOException

abstract class WindowsException(msg: String) extends IOException(msg)

case class PlatformException(msg: String) extends WindowsException(msg)

case class NativeWindowsException(errorNumber: Int, msg: String) extends LinuxException(msg) {
  def error: Int = errorNumber
}

object NativeWindowsException {

  def apply: WindowsException =
    fromError(Native.getLastError)

  def fromError(error: Int): WindowsException = new WindowsException(error, s"Error number: $error")
}
