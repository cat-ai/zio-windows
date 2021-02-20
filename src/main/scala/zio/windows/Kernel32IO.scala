package zio.windows

import com.sun.jna.{Native, Pointer}
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.{StdCallLibrary, W32APIOptions}

import zio.windows.internal.{HANDLE, OVERLAPPED, SECURITY_ATTRIBUTES}

protected[windows] trait Kernel32IO extends StdCallLibrary {

  def CreateFile(lpFileName: String,
                 dwDesiredAccess: Int,
                 dwShareMode: Int,
                 lpSecurityAttributes: SECURITY_ATTRIBUTES,
                 dwCreationDisposition: Int,
                 dwFlagsAndAttributes: Int,
                 hTemplateFile: Int): HANDLE

  def SetFilePointer(hFile: HANDLE,
                     lDistanceToMove: Int,
                     lpDistanceToMoveHigh: Pointer,
                     dwMoveMethod: Int): Int

  def CloseHandle(hObject: HANDLE): Boolean

  def DeviceIoControl(hDevice: HANDLE,
                      dwIoControlCode: Int,
                      lpInBuffer: Pointer,
                      nInBufferSize: Int,
                      lpOutBuffer: Pointer,
                      nOutBufferSize: Int,
                      lpBytesReturned: IntByReference,
                      lpOverlapped: Pointer): Boolean

  def ReadFile(hFile: HANDLE,
               lpBuffer: Array[Byte],
               nNumberOfBytesToRead: Int,
               lpNumberOfBytesRead: IntByReference,
               lpOverlapped: OVERLAPPED): Boolean

  def WriteFile(hFile: HANDLE,
                lpBuffer: Array[Byte],
                nNumberOfBytesToWrite: Int,
                lpNumberOfBytesWritten: IntByReference,
                lpOverlapped: OVERLAPPED): Boolean

  def GetLastError: Int

  def SetLastError(dwErrorCode:Int): Unit
}
protected[windows] object Kernel32IO {
  lazy val INSTANCE = Native.load("kernel32", classOf[Kernel32IO], W32APIOptions.DEFAULT_OPTIONS)
}
