package uk.co.pekim.ealing;


import static org.junit.Assert.fail;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.pekim.ealing.Device;
import uk.co.pekim.ealing.DeviceListener;

public class TestDevice {
    private Device device;
    private Semaphore initialized;
    private boolean setupSuccessful;

    @Before
    public void setUp() throws Exception {
        setupSuccessful = false;

        initialized = new Semaphore(0);
        
        device = new Device();
        device.addListener(new DeviceListener() {
            @Override
            public void closed() {
                //
            }

            @Override
            public void sessionStarted(long unitId) {
                //
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public void initialized() {
                initialized.release();
            }
            
        });

        device.initialise();

        // Wait for initialization to complete.
        if (!initialized.tryAcquire(2, TimeUnit.SECONDS)) {
            fail("Waited too long for initialisation to complete.");
        }

        setupSuccessful = true;
    }

    @After
    public void tearDown() {
        if (setupSuccessful) {
            device.close();
        }
    }

    @Test
    public void initialisation() {
        // Simply test that the initialisation completes.
        // (All handled in the setup() method.)
    }

    @Test
    public void requestRuns() throws InterruptedException {
        device.requestRuns();
    	Thread.sleep(1400);
    }
}
