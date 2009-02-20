package uk.co.pekim.ealing.datatype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import uk.co.pekim.ealing.DeviceException;
import uk.co.pekim.ealing.SportType;
import uk.co.pekim.ealing.packet.DataUtils;

/**
 * A run.
 * 
 * @author Mike D Pilsbury
 */
public class D1009RunType extends BaseDataType {
    private static final int NO_TRACK = 0xffff;
    
    private final int trackIndex;
    private final int firstLapIndex;
    private final int lastLapIndex;
    private final SportType sportType;
    private final ProgramType programType;
    private final MultiSport multiSport;
    private final long quickWorkoutTime;
    private final float quickWorkoutDistance;
    private final D1008WorkoutType workout;

    public D1009RunType(byte[] data) {
        super(1009);
        
        trackIndex = DataUtils.getU16(data, 0);
        firstLapIndex = DataUtils.getU16(data, 2);
        lastLapIndex = DataUtils.getU16(data, 4);
        sportType = SportType.valueOf(DataUtils.getU8(data, 6));
        programType = new ProgramType(DataUtils.getU8(data, 7));
        multiSport = MultiSport.valueOf(DataUtils.getU8(data, 8));
        //  9 - U8  reserved
        // 10 - U16 reserved
        quickWorkoutTime = DataUtils.getU32(data, 12);
        quickWorkoutDistance = DataUtils.getF32(data, 16);
        
        byte[] workoutTypeData = Arrays.copyOfRange(data, 20, data.length);
        workout = new D1008WorkoutType(workoutTypeData);
    }
    
    public int getTrackIndex() {
        return trackIndex;
    }

    public int getFirstLapIndex() {
        return firstLapIndex;
    }

    public int getLastLapIndex() {
        return lastLapIndex;
    }

    public SportType getSportType() {
        return sportType;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public MultiSport getMultiSport() {
        return multiSport;
    }

    public D1008WorkoutType getWorkout() {
        return workout;
    }

    public long getQuickWorkoutTime() {
        if (!programType.isWorkout()) {
            throw new DeviceException("Run is not a quick workout");
        }
        return quickWorkoutTime;
    }
    
    public float getQuickWorkoutDistance() {
        if (!programType.isWorkout()) {
            throw new DeviceException("Run is not a quick workout");
        }
        return quickWorkoutDistance;
    }

    @Override
    protected void logDetail(Logger logger, String prefix) {
        logger.debug(prefix + "track index     : " + (trackIndex != NO_TRACK ? Integer.toString(trackIndex) : "no track"));
        logger.debug(prefix + "first lap index : " + firstLapIndex);
        logger.debug(prefix + "last lap index  : " + lastLapIndex);
        logger.debug(prefix + "sport type      : " + sportType);
        logger.debug(prefix + "program type    : " + programType);
        logger.debug(prefix + "multi sport     : " + multiSport);
        if (programType.isQuickWorkout()) {
            logger.debug(prefix + "quickworkout");
            logger.debug(prefix + "  time          : " + quickWorkoutTime);
            logger.debug(prefix + "  distance      : " + quickWorkoutDistance);
        }
        if (programType.isWorkout()) {
            logger.debug(prefix + "workout");
            workout.logDetail(logger, prefix + "  ");
        }
    }
    
    private class ProgramType {
        private final boolean virtualPartnerRun;
        private final boolean workout;
        private final boolean quickWorkout;
        private final boolean course;
        private final boolean intervalWorkout;
        private final boolean autoMultiSportSession;
        
        ProgramType(int flags) {
            int bit = 0;
            virtualPartnerRun = ((flags >> bit++) & 0x1) == 1;
            workout = ((flags >> bit++) & 0x1) == 1;
            quickWorkout = ((flags >> bit++) & 0x1) == 1;
            course = ((flags >> bit++) & 0x1) == 1;
            intervalWorkout = ((flags >> bit++) & 0x1) == 1;
            autoMultiSportSession = ((flags >> bit++) & 0x1) == 1;
        }

        public boolean isVirtualPartnerRun() {
            return virtualPartnerRun;
        }

        public boolean isWorkout() {
            return workout;
        }

        public boolean isQuickWorkout() {
            return quickWorkout;
        }

        public boolean isCourse() {
            return course;
        }

        public boolean isIntervalWorkout() {
            return intervalWorkout;
        }

        public boolean isAutoMultiSportSession() {
            return autoMultiSportSession;
        }

        @Override
        public String toString() {
            StringBuilder string = new StringBuilder();
            string.append(virtualPartnerRun ? "virtual partner run, " : "");
            string.append(workout ? "workout, " : "");
            string.append(quickWorkout ? "quick workout, " : "");
            string.append(course ? "course, " : "");
            string.append(intervalWorkout ? "interval workout, " : "");
            string.append(autoMultiSportSession ? "auto-MultiSport session, " : "");
            if (string.length() > 2) {
                // Remove trailing ", ".
                string.setLength(string.length() - 2);
            }
            
            return string.toString();
        }
    }
    
    private enum MultiSport {
        NO(0, "No"),
        YES(1, "Yes"),
        YES_LAST_IN_GROUP(2, "Yes, last in group"),
        ;
        
        static Map<Integer, MultiSport> idMap;
        
        static {
            idMap = new HashMap<Integer, MultiSport>();
            for (MultiSport multiSport: MultiSport.values()) {
                idMap.put(multiSport.id, multiSport);
            }
        }
        
        private final Integer id;
        private final String description;

        private MultiSport(int id, String description) {
            this.id = new Integer(id);
            this.description = description;
        }

        public int getId() {
            return id.intValue();
        }

        public String getDescription() {
            return description;
        }
        
        public static MultiSport valueOf(int id) {
            return idMap.get(new Integer(id));
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}
