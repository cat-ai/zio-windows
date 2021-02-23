package zio.windows

import com.sun.jna.{Memory, Platform, Pointer, Native => JnaNative}
import zio.ZEnv._
import zio.{ZEnv, ZIO, ZManaged}

import java.io.IOException

class ZWindowsMemory[N <: JnaNative] {

  lazy val load = JnaNative.register("msvcrt")

  @native
  private def memcmp(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): Int

  @native
  private def memcpy(src: Pointer, dest: Pointer, n: NativeSizeT): Unit

  @native
  private def memset(ptr: Pointer, b: Int, n: NativeSizeT): Unit

  def memcmpZIO(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): ZIO[ZEnv, IOException, Int] =
    ZIO.effect(memcmp(ptr1, ptr2, n)).refineToOrDie[IOException]

  def memcpyZIO(src: Memory, dest: Pointer): ZIO[ZEnv, IOException, Pointer] =
    (ZIO.effect(memcpy(src, dest, new NativeSizeT(src.size))) *> ZIO.succeedNow(dest)).refineToOrDie[IOException]

  def memsetZIO(ptr: Pointer, b: Int, n: NativeSizeT): ZIO[ZEnv, IOException, Pointer] =
    (ZIO.effect(memset(ptr, b, n)) *> ZIO.succeedNow(ptr)).refineToOrDie[IOException]

  def memsetZIO(ptr: Memory, b: Int): ZIO[ZEnv, IOException, Pointer] =
    memsetZIO(ptr, b, new NativeSizeT(ptr.size)).refineToOrDie[IOException]

  def equalZIO(ptr1: Pointer, ptr2: Pointer, n: NativeSizeT): ZIO[ZEnv, IOException, Boolean] =
    memcmpZIO(ptr1, ptr2, n).map(_ == 0).refineToOrDie[IOException]

  def equalZIO(ptr1: Pointer, ptr2: Memory): ZIO[ZEnv, IOException, Boolean] = equalZIO(ptr1, ptr2, new NativeSizeT(ptr2.size))

  def equalZIO(ptr1: Memory, ptr2: Pointer): ZIO[ZEnv, IOException, Boolean] = equalZIO(ptr1, ptr2, new NativeSizeT(ptr1.size))

  def equalZIO(ptr1: Memory, ptr2: Memory): ZIO[ZEnv, IOException, Boolean] = equalZIO(ptr1, ptr2.asInstanceOf[Pointer])

  def unsignedByteZIO(ptr: Pointer, offset: Long): ZIO[ZEnv, IOException, Int] =
    ZIO.effect(ptr.getByte(offset)).map(_ & 0x000000ff).refineToOrDie[IOException]

  def setUnsignedByteZIO(ptr: Pointer, offset: Long, b: Int): ZIO[ZEnv, IOException, Pointer] =
    (ZIO.effect(ptr.setByte(offset, b.toByte)) *> ZIO.succeedNow(ptr)) .refineToOrDie[IOException]

  def unsignedShortZIO(ptr: Pointer, offset: Long): ZIO[ZEnv, IOException, Int] =
    ZIO.effect(ptr.getShort(offset)).map(_ & 0x0000ffff).refineToOrDie[IOException]

  def setUnsignedShortZIO(ptr: Pointer, offset: Long, s: Int): ZIO[ZEnv, IOException, Pointer] =
    (ZIO.effect(ptr.setShort(offset, s.toShort)) *> ZIO.succeedNow(ptr)).refineToOrDie[IOException]

  def unsignedIntZIO(ptr: Pointer, offset: Long): ZIO[ZEnv, IOException, Long] =
    ZIO.effect(ptr.getInt(offset)).map(_ & 0xffffffffL).refineToOrDie[IOException]

  def setUnsignedIntZIO(ptr: Pointer, offset: Long, i: Long): ZIO[ZEnv, IOException, Pointer] =
    (ZIO.effect(ptr.setInt(offset, i.toInt)) *> ZIO.succeedNow(ptr)).refineToOrDie[IOException]

  protected def failZIO: ZIO[ZEnv, IOException, Unit] = ZIO.fail(new IOException("Error"))
}

object ZWindowsMemory {

  type Native = JnaNative

  def make: ZManaged[ZEnv, IOException, ZWindowsMemory[Native]] =
    ZManaged.make(
      ZIO.effect {
        val zWindowsMemory = new ZWindowsMemory[Native]
        zWindowsMemory.load
        zWindowsMemory
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