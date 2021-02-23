package zio.windows

import com.sun.jna.ptr.IntByReference
import com.sun.jna.{Platform, Pointer}
import zio.ZEnv._
import zio.windows.internal.{HANDLE, OVERLAPPED, SECURITY_ATTRIBUTES}
import zio.{ZEnv, ZIO, ZManaged}

import java.io.IOException

class ZWindowsIO[K <: Kernel32IO](kernel32: K) {

  def CreateFileZIO(lpFileName: String,
                    dwDesiredAccess:Int,
                    dwShareMode: Int,
                    lpSecurityAttributes: SECURITY_ATTRIBUTES,
                    dwCreationDisposition: Int,
                    dwFlagsAndAttributes: Int,
                    hTemplateFile: Int): ZIO[ZEnv, IOException, HANDLE]  =
    ZIO.effect {
      kernel32
        .CreateFile(
          lpFileName,
          dwDesiredAccess,
          dwShareMode,
          lpSecurityAttributes,
          dwCreationDisposition,
          dwFlagsAndAttributes,
          hTemplateFile
        )
    }.refineToOrDie[IOException]

  def SetFilePointerZIO(hFile: HANDLE,
                        lDistanceToMove: Int,
                        lpDistanceToMoveHigh: Pointer,
                        dwMoveMethod: Int): ZIO[ZEnv, IOException, Int] =
    ZIO.effect {
      kernel32.SetFilePointer(hFile, lDistanceToMove, lpDistanceToMoveHigh, dwMoveMethod)
    }.refineToOrDie[IOException]

  def CloseHandleZIO(hObject: HANDLE): ZIO[ZEnv, IOException, Boolean] =
    ZIO.effect(kernel32.CloseHandle(hObject)).refineToOrDie[IOException]

  def DeviceIoControlZIO(hDevice: HANDLE,
                         dwIoControlCode: Int,
                         lpInBuffer: Pointer,
                         nInBufferSize: Int,
                         lpOutBuffer: Pointer,
                         nOutBufferSize: Int,
                         lpBytesReturned: IntByReference,
                         lpOverlapped: Pointer): ZIO[ZEnv, IOException, Boolean] =
    ZIO.effect {
      kernel32
        .DeviceIoControl(
          hDevice,
          dwIoControlCode,
          lpInBuffer,
          nInBufferSize,
          lpOutBuffer,
          nOutBufferSize,
          lpBytesReturned,
          lpOverlapped
        )
    }.refineToOrDie[IOException]

  def ReadFileZIO(hFile: HANDLE,
                  lpBuffer: Array[Byte],
                  nNumberOfBytesToRead: Int,
                  lpNumberOfBytesRead: IntByReference,
                  lpOverlapped: OVERLAPPED): ZIO[ZEnv, IOException, Boolean] =
    ZIO.effect {
      kernel32.ReadFile(hFile, lpBuffer, nNumberOfBytesToRead, lpNumberOfBytesRead, lpOverlapped)
    }.refineToOrDie[IOException]


  def WriteFileZIO(hFile: HANDLE,
                   lpBuffer: Array[Byte],
                   nNumberOfBytesToWrite: Int,
                   lpNumberOfBytesWritten: IntByReference,
                   lpOverlapped: OVERLAPPED): ZIO[ZEnv, IOException, Boolean] =
    ZIO.effect {
      kernel32.WriteFile(hFile, lpBuffer, nNumberOfBytesToWrite, lpNumberOfBytesWritten, lpOverlapped)
    }.refineToOrDie[IOException]

  protected def FailZIO: ZIO[ZEnv, IOException, IOException] = ZIO.effectTotal(new IOException("Error"))
}

object ZWindowsIO {

  type WinIO = Kernel32IO

  def make: ZManaged[ZEnv, IOException, ZWindowsIO[WinIO]] =
    ZManaged.make(
      ZIO.effect {
        new ZWindowsIO[WinIO](Kernel32IO.INSTANCE)
      } mapError(ex =>
        if (!Platform.isWindows)
          PlatformException("Not Windows")
        else
          new IOException(ex)
        )
    ) {
      _.FailZIO.ignore
    }
}