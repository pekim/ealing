package uk.co.pekim.ealing.datatype;


public class NotImplementedMarshaller implements Marshaller {
    @Override
    public BaseDataType createDataType(byte[] data) {
        throw new UnsupportedOperationException("Unsupported Datatype");
    }
}
