package uk.co.pekim.ealing.datatype;

import java.util.HashMap;
import java.util.Map;

import uk.co.pekim.ealing.packet.Packet;

public enum DataType {
    D110(110, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
        }
    }),
    D202(202, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
        }
    }),
    D210(210, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
        }
    }),
    D304(304, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
        }
    }),
    D311(311, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
        }
    }),
    D501(501, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D600(600, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D601(601, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D700(700, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D800(800, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D801(801, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D907(907, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D908(908, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D909(909, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D910(910, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D918(918, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1003(1003, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1004(1004, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1005(1005, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1006(1006, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1007(1007, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1008(1008, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1009(1009, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new D1009RunType(data);
    }}),
    D1012(1012, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1013(1013, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1014(1014, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    D1015(1015, new Marshaller() {
        @Override
        public BaseDataType createDataType(byte[] data) {
            return new UnimplementedDataType();
    }}),
    ;
    
    private static final Map<Integer, DataType> dataTypesMap;
    
    static {
        dataTypesMap = new HashMap<Integer, DataType>();
        for (DataType dataTypes : DataType.values()) {
            dataTypesMap.put(dataTypes.typeId, dataTypes);
        }
    }
    
    private final Integer typeId;
    private final Marshaller marshaller;

    private DataType(int id, Marshaller marshaller) {
        this.typeId = new Integer(id);
        this.marshaller = marshaller;
    }

    public BaseDataType createDataType(Packet packet) {
        return marshaller.createDataType(packet.getData());
    }

    public static DataType valueOf(int id) {
        if (!dataTypesMap.containsKey(new Integer(id))) {
            throw new IllegalArgumentException("No data type with id " + id);
        }
        return dataTypesMap.get(new Integer(id));
    }
    
    @Override
    public String toString() {
        return "D" + typeId;
    }
}
