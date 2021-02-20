package zio.windows

import com.sun.jna.Native

import java.io.IOException

abstract class LinuxException(msg: String) extends IOException(msg)

case class PlatformException(msg: String) extends LinuxException(msg)

case class NativeLinuxException(errorNumber: Int, msg: String) extends LinuxException(msg) {
  def error: Int = errorNumber
}

object NativeLinuxException {

  def apply: NativeLinuxException =
    fromError(Native.getLastError)

  def fromError(error: Int): NativeLinuxException = new NativeLinuxException(error, s"Error number: $error")
}