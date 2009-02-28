package uk.co.pekim.ealing.packet;

import java.util.Calendar;
import java.util.TimeZone;

import uk.co.pekim.ealing.Device;

public class DateTimeDataPacket extends Packet {

    private final Calendar calendar;

    DateTimeDataPacket(Device device, byte[] packet) {
        super(device, packet);

        // 7.4.37 D600_Date_Time_Type
        // Always uses UTC.
        calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.MONTH, getU8(0) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, getU8(1));
        calendar.set(Calendar.YEAR, getU16(2));
        calendar.set(Calendar.HOUR_OF_DAY, getU16(4));
        calendar.set(Calendar.MINUTE, getU8(6));
        calendar.set(Calendar.SECOND, getU8(7));
    }

    @Override
    public void notifyDeviceOfReceivedPacket() {
        getDevice().dateTimeData(this);
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
