package uk.co.pekim.ealing;


public interface DeviceListener {
	public void sessionStarted(long unitId);
	
    public void initialized();

    public void closed();
}
