package uk.co.pekim.ealing.datatype;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import uk.co.pekim.ealing.SportType;
import uk.co.pekim.ealing.packet.DataUtils;

/**
 * A workout.
 * 
 * @author Mike D Pilsbury
 */
public class WorkoutType extends BaseDataType {
    
    
//    private enum DurationType {
//        TIME(0, "time"),
//        DISTANCE(1, "distance"),
//        ;
//
//        private static final Map<Integer, DurationType> lookup = new HashMap<Integer, DurationType>();
//
//        static {
//            for(PacketType pt : EnumSet.allOf(PacketType.class))
//                 lookup.put(new Integer(pt.getType()), pt);
//        }
//
//        private final int type;
//        private final String description;
//        
//        PacketType(int type, String description) {
//            this.type = type;
//            this.description = description;
//        }
//
//        int getType() {
//            return type;
//        }
//
//        String getDescription() {
//            return description;
//        }
//
//        public static PacketType get(int type) { 
//            PacketType packetType = lookup.get(new Integer(type));
//            if (packetType == null) {
//                throw new DeviceException("Unknown packet type " + type);
//            }
//            return packetType;
//        }
//
//        @Override
//        public String toString() {
//            return description + " (" + type + ")";
//        }
//        
//    }
    
    private final long numberOfSteps;
    private final String name;
    private final SportType sportType;
    private final List<Step> steps;

    private class Step {
        private final String name;
        private final int durationValue;
        private final int durationType;
        
        Step(byte[] data, int offset) {
            name = DataUtils.getNullTerminatedString(data, offset + 0);
            //target_custom_zone_low
            //target_custom_zone_high
            durationValue = DataUtils.getU16(data, offset + 24);
            //intensity
            durationType = DataUtils.getU8(data, offset + 27);
        }

        void logDetail(Logger logger, String prefix) {
            logger.debug(prefix + "name          : " + name);
            logger.debug(prefix + "duration type : " + durationType);
            logger.debug(prefix + "duration      : " + durationValue);
        }
    }
    
    public WorkoutType(byte[] data) {
        super(1008);

        steps = new ArrayList<Step>();
        numberOfSteps = DataUtils.getU32(data, 0);
        
        int offset = 4;
        for (int stepNumber = 0; stepNumber < numberOfSteps; stepNumber++) {
            Step step = new Step(data, offset);
            steps.add(step);
            offset += 32;
        }
        
        offset = 4 + (20 * 32);
        name = DataUtils.getNullTerminatedString(data, offset + 0);
        sportType = SportType.valueOf(DataUtils.getU8(data, offset + 16));
    }

    @Override
    protected void logDetail(Logger logger, String prefix) {
        logger.debug(prefix + "name       : " + name);
        logger.debug(prefix + "sport type : " + sportType);
        logger.debug(prefix + "# of steps : " + numberOfSteps);
        
        int stepNumber = 1;
        for (Step step : steps) {
            logger.debug(prefix + "  step " + stepNumber);
            step.logDetail(logger, prefix + "    ");
            stepNumber++;
        }
    }
}
