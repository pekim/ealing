package uk.co.pekim.ealing.datatype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDataType {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataType.class);

    private final int id;

    int getId() {
        return id;
    }

    BaseDataType(int id) {
        this.id = id;
    }

    BaseDataType(int id, byte[] data) {
        this(id);
    }
    
    @Override
    public String toString() {
        return "D" + id;
    }

    public void log(String prefix) {
        LOGGER.debug(prefix + toString());
        logDetail(LOGGER, prefix + "  ");
    }

    protected abstract void logDetail(Logger logger, String prefix);
}
