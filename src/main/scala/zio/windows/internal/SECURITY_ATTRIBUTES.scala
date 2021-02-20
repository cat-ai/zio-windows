package zio.windows.internal

import com.sun.jna.Structure.FieldOrder
import com.sun.jna.{Pointer, Structure}

@FieldOrder(Array("dwLength", "lpSecurityDescriptor", "bInheritHandle"))
class SECURITY_ATTRIBUTES(dwLengthOpt: Option[DWORD] = None,
                          lpSecurityDescriptor: Pointer,
                          bInheritHandle: Boolean) extends Structure {

  val dwLength = dwLengthOpt getOrElse new DWORD(super.size)
}
