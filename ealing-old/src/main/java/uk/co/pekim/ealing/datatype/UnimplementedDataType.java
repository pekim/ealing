package uk.co.pekim.ealing.datatype;

import org.slf4j.Logger;


public class UnimplementedDataType extends BaseDataType{
    public UnimplementedDataType() {
        super(0);
        throw new UnsupportedOperationException("Unsupported Datatype");
    }

    @Override
    protected void logDetail(Logger logger, String prefix) {
        // Nothing to add.
    }
}
