package uk.co.pekim.ealing.jna.win32;

import java.nio.ByteBuffer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("KERNEL32", Kernel32.class);
    
    class OVERLAPPED extends Structure {
        public OVERLAPPED(int event) {
            this.event = event;
        }

        public volatile int internal = 0;
        public volatile int internalHigh = 0;
        public int offset = 0;
        public int offsetHigh = 0;
        public int event;
    }

    public abstract boolean CloseHandle(int handle);

    public abstract boolean ResetEvent(int eventHandle);

    public abstract boolean SetEvent(int eventHandle);

    public abstract boolean CancelIo(int file);

    public abstract int WaitForSingleObject(int handle, int milliseconds);
    
    public abstract int CreateFileA(String fileName, int desiredAccess, int shareMode, Pointer securityAttributes,
            int creationDisposition, int flagsAndAttributes, Pointer handleTemplateFile);
    
    public abstract int CreateEventA(Pointer eventAttributes, boolean manualReset, boolean initialState, String name);
    
    public abstract boolean DeviceIoControl(int hDevice, int ioControlCode, Pointer inBuffer, int inBufferSize,
            Pointer outBuffer, int outBufferSize, IntByReference bytesReturned, OVERLAPPED overlapped);
    public abstract boolean DeviceIoControl(int hDevice, int ioControlCode, Pointer inBufer, int inBufferSize,
            byte[] outBuffer, int outBufferSize, IntByReference bytesReturned, OVERLAPPED overlapped);
    public abstract boolean DeviceIoControl(int hDevice, int ioControlCode, Pointer inBuffer, int inBufferSize,
            ByteBuffer outBuffer, int outBufferSize, IntByReference bytesReturned, OVERLAPPED overlapped);
    public abstract boolean DeviceIoControl(int hDevice, int ioControlCode, Pointer inBuffer, int inBufferSize,
            byte[] outBuffer, int outBufferSize, IntByReference bytesReturned, Pointer overlapped);
    
    public abstract boolean WriteFile(int file, Pointer data, int numberOfBytesToWrite,
            IntByReference numberOfBytesWritten, OVERLAPPED overlapped);
    
    public abstract boolean GetOverlappedResult(int file, OVERLAPPED overlapped,
            IntByReference numberOfBytesTransferred, boolean wait);
    
    public abstract int WaitForMultipleObjects(int count,  int[] handles,  boolean waitAll, int milliseconds);
}