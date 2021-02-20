package zio.windows

import com.sun.jna.win32.W32APIOptions
import com.sun.jna.{Library, Native, Pointer}

protected[windows] trait Msvcrt extends Library {

  def memcmp(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): Int

  def memcpy(src: Pointer, dest: Pointer, n: NativeSizeT): Unit

  def memset(ptr: Pointer, b: Int, n: NativeSizeT): Unit
}

protected[windows] object Msvcrt {
  lazy val INSTANCE = Native.load("msvcrt", classOf[Kernel32IO], W32APIOptions.DEFAULT_OPTIONS)
}