package zio

import com.sun.jna._
import com.sun.jna.ptr.IntByReference
import zio.ZEnv._
import zio.windows.internal.{HANDLE, OVERLAPPED, SECURITY_ATTRIBUTES}

import java.io.IOException

package object windows {

  type WindowsMemory = Has[ZWindowsMemory[ZWindowsMemory.Native]]
  type WindowsIO     = Has[ZWindowsIO[ZWindowsIO.WinIO]]

  def lastError: ZIO[ZEnv, IOException, Int] =
    ZIO.effect(Kernel32IO.INSTANCE.GetLastError).refineToOrDie[IOException]

  def setLastError(number: Int): ZIO[ZEnv, IOException, Unit] =
    ZIO.effect(Kernel32IO.INSTANCE.SetLastError(number)).refineToOrDie[IOException]

  def alloc(size: Int): ZIO[ZEnv, IOException, Memory] =
    ZIO.effect(new Memory(size)).refineToOrDie[IOException]

  def free(pointer: Pointer): ZIO[ZEnv, IOException, Unit] =
    ZIO.effect(Native.free(Pointer.nativeValue(pointer))).refineToOrDie[IOException]

  object WindowsMemory {

    def memcmp(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): ZIO[WindowsMemory with ZEnv, IOException, Int] =
      ZIO.accessM(_.get.memcmpZIO(ptr1, ptr2, n))

    def memcpy(src: Memory, dest: Pointer): ZIO[WindowsMemory with ZEnv, IOException, Pointer] =
      ZIO.accessM(_.get.memcpyZIO(src, dest))

    def memset(ptr: Pointer, b: Int, n: NativeSizeT): ZIO[WindowsMemory with ZEnv, IOException, Pointer] =
      ZIO.accessM(_.get.memsetZIO(ptr, b, n))

    def memset(ptr: Memory, b: Int): ZIO[WindowsMemory with ZEnv, IOException, Pointer] =
      ZIO.accessM(_.get.memsetZIO(ptr, b))

    def equal(ptr: Pointer, ptr2: Pointer, n: NativeSizeT): ZIO[WindowsMemory with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.equalZIO(ptr, ptr2, n))

    def equal(ptr: Pointer, memory: Memory): ZIO[WindowsMemory with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.equalZIO(ptr, memory))

    def equal(memory: Memory, ptr: Pointer): ZIO[WindowsMemory with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.equalZIO(memory, ptr))

    def equal(mem1: Memory, mem2: Memory): ZIO[WindowsMemory with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.equalZIO(mem1, mem2))

    def unsignedByte(ptr: Pointer, offset: Long): ZIO[WindowsMemory with ZEnv, IOException, Int] =
      ZIO.accessM(_.get.unsignedByteZIO(ptr, offset))

    def setUnsignedByte(ptr: Pointer, offset: Long, b: Int): ZIO[WindowsMemory with ZEnv, IOException, Pointer] =
      ZIO.accessM(_.get.setUnsignedByteZIO(ptr, offset, b))

    def unsignedShort(ptr: Pointer, offset: Long): ZIO[WindowsMemory with ZEnv, IOException, Int] =
      ZIO.accessM(_.get.unsignedShortZIO(ptr, offset))

    def setUnsignedShort(ptr: Pointer, offset: Long, s: Int): ZIO[WindowsMemory with ZEnv, IOException, Pointer] =
      ZIO.accessM(_.get.setUnsignedShortZIO(ptr, offset, s))

    def unsignedInt(ptr: Pointer, offset: Long): ZIO[WindowsMemory with ZEnv, IOException, Long] =
      ZIO.accessM(_.get.unsignedIntZIO(ptr, offset))

    def setUnsignedInt(ptr: Pointer, offset: Long, i: Long): ZIO[WindowsMemory with ZEnv, IOException, Pointer] =
      ZIO.accessM(_.get.setUnsignedIntZIO(ptr, offset, i))
  }

  object WindowsIO {

    def CreateFile(lpFileName: String,
                   dwDesiredAccess:Int,
                   dwShareMode: Int,
                   lpSecurityAttributes: SECURITY_ATTRIBUTES,
                   dwCreationDisposition: Int,
                   dwFlagsAndAttributes: Int,
                   hTemplateFile: Int): ZIO[WindowsIO with ZEnv, IOException, HANDLE] =
      ZIO.accessM(_.get.CreateFileZIO(lpFileName, dwDesiredAccess, dwShareMode, lpSecurityAttributes, dwCreationDisposition, dwFlagsAndAttributes, hTemplateFile))

    def SetFilePointer(hFile: HANDLE,
                       lDistanceToMove: Int,
                       lpDistanceToMoveHigh: Pointer,
                       dwMoveMethod: Int): ZIO[WindowsIO with ZEnv, IOException, Int] =
      ZIO.accessM(_.get.SetFilePointerZIO(hFile, lDistanceToMove, lpDistanceToMoveHigh, dwMoveMethod))

    def CloseHandle(hObject: HANDLE): ZIO[WindowsIO with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.CloseHandleZIO(hObject))

    def DeviceIoControl(hDevice: HANDLE,
                        dwIoControlCode: Int,
                        lpInBuffer: Pointer,
                        nInBufferSize: Int,
                        lpOutBuffer: Pointer,
                        nOutBufferSize: Int,
                        lpBytesReturned: IntByReference,
                        lpOverlapped: Pointer): ZIO[WindowsIO with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.DeviceIoControlZIO(hDevice, dwIoControlCode, lpInBuffer, nInBufferSize, lpOutBuffer, nOutBufferSize, lpBytesReturned, lpOverlapped))

    def ReadFile(hFile: HANDLE,
                 lpBuffer: Array[Byte],
                 nNumberOfBytesToRead: Int,
                 lpNumberOfBytesRead: IntByReference,
                 lpOverlapped: OVERLAPPED): ZIO[WindowsIO with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.ReadFileZIO(hFile, lpBuffer, nNumberOfBytesToRead, lpNumberOfBytesRead, lpOverlapped))

    def WriteFileZIO(hFile: HANDLE,
                     lpBuffer: Array[Byte],
                     nNumberOfBytesToWrite: Int,
                     lpNumberOfBytesWritten: IntByReference,
                     lpOverlapped: OVERLAPPED): ZIO[WindowsIO with ZEnv, IOException, Boolean] =
      ZIO.accessM(_.get.WriteFileZIO(hFile, lpBuffer, nNumberOfBytesToWrite, lpNumberOfBytesWritten, lpOverlapped))
  }

  def memory: ZLayer[ZEnv, IOException, WindowsMemory] =
    ZLayer.fromManaged(ZWindowsMemory.make)

  def io: ZLayer[ZEnv, IOException, WindowsIO] =
    ZLayer.fromManaged(ZWindowsIO.make)
}
