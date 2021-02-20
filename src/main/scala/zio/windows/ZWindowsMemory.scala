package zio.windows

import com.sun.jna.{Memory, Platform, Pointer, Native => JnaNative}

import zio.blocking._
import zio.{ZIO, ZManaged}

import java.io.IOException

class ZWindowsMemory[N <: JnaNative] {

  lazy val load = JnaNative.register("msvcrt")

  @native
  private def memcmp(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): Int

  @native
  private def memcpy(src: Pointer, dest: Pointer, n: NativeSizeT): Unit

  @native
  private def memset(ptr: Pointer, b: Int, n: NativeSizeT): Unit

  def memcmpZIO(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): ZIO[Blocking, IOException, Int] =
    effectBlockingIO(memcmp(ptr1, ptr2, n))

  def memcpyZIO(src: Memory, dest: Pointer): ZIO[Blocking, IOException, Pointer] =
    effectBlockingIO(memcpy(src, dest, new NativeSizeT(src.size))) *> ZIO.succeedNow(dest)

  def memsetZIO(ptr: Pointer, b: Int, n: NativeSizeT): ZIO[Blocking, IOException, Pointer] =
    effectBlockingIO(memset(ptr, b, n)) *> ZIO.succeedNow(ptr)

  def memsetZIO(ptr: Memory, b: Int): ZIO[Blocking, IOException, Pointer] =
    memsetZIO(ptr, b, new NativeSizeT(ptr.size))

  def equalZIO(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): ZIO[Blocking, IOException, Boolean] =
    memcmpZIO(ptr1, ptr2, n) map(_ == 0)

  def equalZIO(ptr1: Pointer, ptr2: Memory): ZIO[Blocking, IOException, Boolean] = equalZIO(ptr1, ptr2, new NativeSizeT(ptr2.size))

  def equalZIO(ptr1: Memory, ptr2: Pointer): ZIO[Blocking, IOException, Boolean] = equalZIO(ptr1, ptr2, new NativeSizeT(ptr1.size))

  def equalZIO(ptr1: Memory, ptr2: Memory): ZIO[Blocking, IOException, Boolean] = equalZIO(ptr1, ptr2.asInstanceOf[Pointer])

  def unsignedByteZIO(ptr: Pointer, offset: Long): ZIO[Blocking, IOException, Int] =
    effectBlockingIO(ptr.getByte(offset)).map(_ & 0x000000ff)

  def setUnsignedByteZIO(ptr: Pointer, offset: Long, b: Int): ZIO[Blocking, IOException, Pointer] =
    effectBlockingIO(ptr.setByte(offset, b.toByte)) *> ZIO.succeedNow(ptr)

  def unsignedShortZIO(ptr: Pointer, offset: Long): ZIO[Blocking, IOException, Int] =
    effectBlockingIO(ptr.getShort(offset)).map(_ & 0x0000ffff)

  def setUnsignedShortZIO(ptr: Pointer, offset: Long, s: Int): ZIO[Blocking, IOException, Pointer] =
    effectBlockingIO(ptr.setShort(offset, s.toShort)) *> ZIO.succeedNow(ptr)

  def unsignedIntZIO(ptr: Pointer, offset: Long): ZIO[Blocking, IOException, Long] =
    effectBlockingIO(ptr.getInt(offset)).map(_ & 0xffffffffL)

  def setUnsignedIntZIO(ptr: Pointer, offset: Long, i: Long): ZIO[Blocking, IOException, Pointer] =
    effectBlockingIO(ptr.setInt(offset, i.toInt)) *> ZIO.succeedNow(ptr)

  protected def failZIO: ZIO[Blocking, IOException, IOException] = effectBlockingIO(new IOException("Error"))
}

object ZWindowsMemory {

  type Native = JnaNative

  def make: ZManaged[Blocking, IOException, ZWindowsMemory[Native]] =
    ZManaged.make(
      effectBlocking {
        val zLinuxMemory = new ZWindowsMemory[Native]
        zLinuxMemory.load
        zLinuxMemory
      } mapError( ex =>
        if (!Platform.isLinux)
          PlatformException("Not WINDOWS")
        else
          new IOException(ex)
        )
    ) {
      _.failZIO.ignore
    }
}