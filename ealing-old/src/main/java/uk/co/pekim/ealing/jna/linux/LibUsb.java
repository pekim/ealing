package uk.co.pekim.ealing.jna.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface LibUsb extends Library {
	LibUsb INSTANCE = NativeLibrary.load(new String[] {"usb", "usb-0.1"}, LibUsb.class);
		
    public static final int PATH_MAX = 4096;
    
    public static class USBDeviceDescriptor extends Structure {
        public byte length;
        public byte descriptorType;
        public short bcdUSB;
        public byte deviceClass;
        public byte deviceSubClass;
        public byte deviceProtocol;
        public byte maxPacketSize0;
        public short idVendor;
        public short idProduct;
        public short bcdDevice;
        public byte manufacturer;
        public byte product;
        public byte serialNumber;
        public byte numberOfConfigurations;
    }
    
    public static class USBInterface extends Structure {
        public static class ByReference extends USBInterface implements Structure.ByReference { }
        
        public USBInterfaceDescriptor.ByReference altSetting;
        public int numberOfAltSetting;
    }
    
    public static class USBEndpointDescriptor extends Structure {
        public static class ByReference extends USBEndpointDescriptor implements Structure.ByReference { }

        public byte length;
        public byte descriptorType;
        public byte endpointAddress;
        public byte attributes;
        public short maxPacketSize;
        public byte interval;
        public byte refresh;
        public byte synchAddress;

        public String extra;   /* Extra descriptors */
        public int extralen;
        
        // Bits in endpointAddress field.
        public static final int ADDRESS_MASK = 0x0f;
        public static final int DIR_MASK = 0x80;

        // Bits in attributes field.
        public static final int TYPE_MASK = 0x03;
        public static final int TYPE_CONTROL = 0x00;
        public static final int TYPE_ISOCHRONOUS = 0x01;
        public static final int TYPE_BULK = 0x02;
        public static final int TYPE_INTERRUPT = 0x03;
    }
    
    public static class USBInterfaceDescriptor extends Structure {
        public static class ByReference extends USBInterfaceDescriptor implements Structure.ByReference { }
        
        public byte length;
        public byte descriptorType;
        public byte interfaceNumber;
        public byte alternateSetting;
        public byte numberOfEndpoints;
        public byte interfaceClass;
        public byte interfaceSubClass;
        public byte interfaceProtocol;
        public byte interface_; // Can't use 'interface' as it's a keyword.

        public USBEndpointDescriptor.ByReference endpoints;

        public String extra;   /* Extra descriptors */
        public int extralen;
    }
    
    public static class USBConfigDescriptor extends Structure {
        public static class ByReference extends USBConfigDescriptor implements Structure.ByReference { }

        public byte length;
        public byte descriptorType;
        public short totalLength;
        public byte numberOfInterfaces;
        public byte configurationValue;
        public byte configuration;
        public byte mAttributes;
        public byte maxPower;

        public USBInterface.ByReference usbInterface;

        public String extra;   /* Extra descriptors */
        public int extralen;
    }
    
    public static class USBDevice extends Structure {
        public static class ByReference extends USBDevice implements Structure.ByReference { }

        public USBDevice.ByReference next;
        public USBDevice.ByReference prev;
        public byte[] filename = new byte[PATH_MAX + 1];
//        public USBBus.ByReference bus;
        public Pointer bus;
        public USBDeviceDescriptor descriptor;
        public USBConfigDescriptor.ByReference config;
        public Pointer dev;            /* Darwin support */
        public byte deviceNumber;
        public byte numberOfChildren;
        public Pointer children; // struct usb_device **children;
        
        public String getFileName() {
            return Native.toString(filename);
        }
    }
        
    public static class USBBus extends Structure {
        public static class ByReference extends USBBus implements Structure.ByReference { }

        public USBBus.ByReference next;
  	  	public USBBus.ByReference prev;
    	public byte[] dirname = new byte[PATH_MAX + 1];
    	public USBDevice.ByReference firstDevice;
    	public int location;
    	public USBDevice.ByReference rootDevice;
        
        public String getDirName() {
            return Native.toString(dirname);
        }
    }
    
    public abstract void usb_init();
    public abstract void usb_find_busses();
    public abstract void usb_find_devices();
    public abstract USBBus usb_get_busses();
    
    public Pointer usb_open(USBDevice device);
    public Pointer usb_close(Pointer device);

    public int usb_claim_interface(Pointer device, int interface_);
    public int usb_release_interface(Pointer device, int interface_);
    
    public int usb_set_configuration(Pointer device, int configuration);
 
    public int usb_bulk_write(Pointer device, int endpoint, byte[] data, int size,
            int timeout);
    public int usb_bulk_read(Pointer device, int endpoint, byte[] data, int size,
            int timeout);
    public int usb_interrupt_write(Pointer device, int endpoint, byte[] data, int size,
            int timeout);
    public int usb_interrupt_read(Pointer device, int endpoint, byte[] data, int size,
            int timeout);

    public int usb_get_driver_np(Pointer device, int interface_, byte[] name, int nameLen);
    public String usb_strerror();
}