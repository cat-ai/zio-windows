package zio.windows.internal

import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder

@FieldOrder(Array("Internal", "InternalHigh", "Offset", "OffsetHigh", "hEvent"))
class OVERLAPPED(Internal: ULONG_PTR,
                 InternalHigh: ULONG_PTR,
                 Offset: Int,
                 OffsetHigh: Int,
                 hEvent: HANDLE) extends Structure

