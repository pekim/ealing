package uk.co.pekim.ealing.jna.win32;



public interface W32API {
//    @SuppressWarnings("serial")
//    public class WORD extends IntegerType {
//        public WORD() { this(0); }
//        public WORD(long value) { super(2, value); } 
//    }
//    
//    @SuppressWarnings("serial")
//    public class DWORD extends IntegerType {
//        public DWORD() { this(0); }
//        public DWORD(long value) { super(4, value); } 
//    }
//    
//    /** LPDWORD */
//    public class DWORDByReference extends ByReference {
//        public DWORDByReference() {
//            this(new DWORD(0));
//        }
//        public DWORDByReference(DWORD dword) {
//            super(Pointer.SIZE);
//            setValue(dword);
//        }
//        public void setValue(DWORD dword) {
//            getPointer().setInt(0, dword != null ? dword.intValue() : 0);
//        }
//        public DWORD getValue() {
//            return new DWORD(getPointer().getInt(0));
//        }
//    }
//
//    class HANDLE extends PointerType {
//        /** Override to the appropriate object for INVALID_HANDLE_VALUE. */
//        @Override
//        public Object fromNative(Object nativeValue, FromNativeContext context) {
//            Object o = super.fromNative(nativeValue, context);
//            if (INVALID_HANDLE_VALUE.equals(o))
//                return INVALID_HANDLE_VALUE;
//            return o;
//        }
//    }
//
//    class HWND extends HANDLE {/*No new functionality.*/}
//
//    /** Constant value representing an invalid HANDLE. */
//    HANDLE INVALID_HANDLE_VALUE = new HANDLE() { 
//        { super.setPointer(Pointer.createConstant(4294967295L)); }
//        @Override
//        public void setPointer(Pointer p) { 
//            throw new UnsupportedOperationException("Immutable reference");
//        }
//    };
    
    public static final int ERROR_ACCESS_DENIED = 5;
    public static final int ERROR_NO_MORE_ITEMS = 259;
    public static final int ERROR_INSUFFICIENT_BUFFER = 122;
    public static final int ERROR_IO_PENDING = 997;

    public static final int GENERIC_READ = 0x80000000;
    public static final int GENERIC_WRITE = 0x40000000;
    public static final int GENERIC_EXECUTE = 0x20000000;
    public static final int GENERIC_ALL = 0x10000000;

    public static final int CREATE_NEW = 1;
    public static final int CREATE_ALWAYS = 2;
    public static final int OPEN_EXISTING = 3;
    public static final int OPEN_ALWAYS = 4;
    public static final int TRUNCATE_EXISTING = 5;
    
    public static final int FILE_ATTRIBUTE_READONLY = 0x00000001;
    public static final int FILE_ATTRIBUTE_HIDDEN = 0x00000002;
    public static final int FILE_ATTRIBUTE_SYSTEM = 0x00000004;
    public static final int FILE_ATTRIBUTE_DIRECTORY = 0x00000010;
    public static final int FILE_ATTRIBUTE_ARCHIVE = 0x00000020;
    public static final int FILE_ATTRIBUTE_DEVICE = 0x00000040;
    public static final int FILE_ATTRIBUTE_NORMAL = 0x00000080;
    public static final int FILE_ATTRIBUTE_TEMPORARY = 0x00000100;

    public static final int FILE_FLAG_WRITE_THROUGH = 0x80000000;
    public static final int FILE_FLAG_OVERLAPPED = 1073741824;
    public static final int FILE_FLAG_NO_BUFFERING = 536870912;
    public static final int FILE_FLAG_RANDOM_ACCESS = 268435456;
    public static final int FILE_FLAG_SEQUENTIAL_SCAN = 134217728;
    public static final int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
    public static final int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
    public static final int FILE_FLAG_POSIX_SEMANTICS = 16777216;
    public static final int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
    public static final int FILE_FLAG_OPEN_NO_RECALL = 1048576;

    public static final int INVALID_HANDLE_VALUE = -1;

    public static final int INFINITE = 0xffffffff;

    public static final int WAIT_OBJECT_0 = 0;
    
    public static final int FILE_DEVICE_UNKNOWN = 34;
    public static final int METHOD_BUFFERED = 0;
    public static final int FILE_ANY_ACCESS = 0x00000000;
    
    public class CTL_CODE {
        private final int value;
        
        public CTL_CODE(int t, int f, int m, int a) {
            value = t << 16 | a << 14 | f << 2  | m;
        }
        
        public int value() {
            return value;
        }
    }
}