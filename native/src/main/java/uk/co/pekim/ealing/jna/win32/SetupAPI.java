package uk.co.pekim.ealing.jna.win32;

//import uk.co.pekim.garmin.jna.win32.W32API.DWORD;
//import uk.co.pekim.garmin.jna.win32.W32API.DWORDByReference;
//import uk.co.pekim.garmin.jna.win32.W32API.HANDLE;
//import uk.co.pekim.garmin.jna.win32.W32API.HWND;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public interface SetupAPI extends Library {
    SetupAPI INSTANCE = (SetupAPI) Native.loadLibrary("SETUPAPI", SetupAPI.class);

    public static final int DIGCF_DEFAULT = 0x00000001;
    public static final int DIGCF_PRESENT = 0x00000002;
    public static final int DIGCF_ALLCLASSES = 0x00000004;
    public static final int DIGCF_PROFILE = 0x00000008;
    public static final int DIGCF_DEVICEINTERFACE = 0x00000010;
    public static final int DIGCF_INTERFACEDEVICE = 0x00000010;
    
    public static class SP_DEVICE_INTERFACE_DATA extends Structure {
        public int size = size();
        public GUID interfaceClassGuid;
        public int flags;
        public Pointer reserved;
    }

    public static class SP_DEVICE_INTERFACE_DETAIL_DATA extends Structure {
        public int size;
        public byte[] devicePath = new byte[1];
        
        public SP_DEVICE_INTERFACE_DETAIL_DATA(int devicePathSize) {
            size = 4 + 1;
            devicePath = new byte[devicePathSize];
        }
    }

    public static class SP_DEVINFO_DATA extends Structure {
        public int size = size();
        public GUID classGuid;
        public int devInst;
        public Pointer reserved;
    }

    public abstract int SetupDiGetClassDevsA(GUID classGuid, String enumerator, int hwndParent, int flags);

    public abstract boolean SetupDiEnumDeviceInterfaces(int DeviceInfoSet, Pointer DeviceInfoData, GUID classGuid,
            int memberIndex, SP_DEVICE_INTERFACE_DATA deviceInterfaceData);

    public abstract boolean SetupDiGetDeviceInterfaceDetailA(int hDeviceInfoSet, SP_DEVICE_INTERFACE_DATA deviceInterfaceData,
            SP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData, int deviceInterfaceDetailDataSize, IntByReference requiredSize,
            SP_DEVINFO_DATA DeviceInfoData);
}
