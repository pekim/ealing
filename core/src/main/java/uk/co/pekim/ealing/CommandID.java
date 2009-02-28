package uk.co.pekim.ealing;

public enum CommandID {
    CMND_ABORT_TRANSFER("abort current transfer"),
    CMND_TRANSFER_ALM("transfer almanac"),
    CMND_TRANSFER_POSN("transfer position"),
    CMND_TRANSFER_PRX("transfer proximity waypoints"),
    CMND_TRANSFER_RTE("transfer routes"),
    CMND_TRANSFER_TIME("transfer time"),
    CMND_TRANSFER_TRK("transfer track log"),
    CMND_TRANSFER_WPT("transfer waypoints"),
    CMND_TURN_OFF_PWR("turn off power"),
    CMND_START_PVT_DATA("start transmitting PVT data"),
    CMND_STOP_PVT_DATA("stop transmitting PVT data"),
    CMND_TRANSFER_FLIGHTBOOK("transfer flight records"),
    CMND_TRANSFER_LAPS("transfer fitness laps"),
    CMND_TRANSFER_WPT_CATS("transfer waypoint categories"),
    CMND_TRANSFER_RUNS("transfer fitness runs"),
    CMND_TRANSFER_WORKOUTS("transfer workouts"),
    CMND_TRANSFER_WORKOUT_OCCURENCES("transfer workout occurrences"),
    CMND_TRANSFER_FITNESS_USER_PROFILE("transfer fitness user profile"),
    CMND_TRANSFER_WORKOUT_LIMITS("transfer workout limits"),
    CMND_TRANSFER_COURSES("transfer fitness courses"),
    CMND_TRANSFER_COURSE_LAPS("transfer fitness course laps"),
    CMND_TRANSFER_COURSE_POINTS("transfer fitness course points"),
    CMND_TRANSFER_COURSE_TRACKS("transfer fitness course tracks"),
    CMND_TRANSFER_COURSE_LIMITS("transfer fitness course limits"),
    ;
    
    private String description;
    
    private CommandID(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
