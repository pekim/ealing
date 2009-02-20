package uk.co.pekim.ealing;

import java.util.ArrayList;
import java.util.List;

import uk.co.pekim.ealing.datatype.BaseDataType;
import uk.co.pekim.ealing.datatype.DataType;
import uk.co.pekim.ealing.packet.Packet;

public class Protocol extends ProtocolData {
    private final List<DataType> dataTypes;
    
    public Protocol(char tag, int data) {
        super(tag, data);
        dataTypes = new ArrayList<DataType>();
    }

    public List<DataType> getDataTypes() {
        return dataTypes;
    }

    public void addDataType(int tagData) {
        DataType dataType = DataType.valueOf(tagData);
        dataTypes.add(dataType);
    }

    public String getName() {
    	return toString();
    }

    /**
     * Create a data type from packet data.
     * 
     * <p>If data type <D1> is required, then call
     * <code>getDataType(1, packet)</code> .
     * 
     * @param dataTypeId the data type index.
     * @param packet the packet to create the DataType from.
     * @return
     */
    public BaseDataType createDataType(int dataTypeId, Packet packet) {
      if (dataTypes.size() < dataTypeId) {
            throw new DeviceException("No data type D" + dataTypeId
                    + " for protocol " + getName());
        }

        return dataTypes.get(dataTypeId - 1).createDataType(packet);
    }
}
