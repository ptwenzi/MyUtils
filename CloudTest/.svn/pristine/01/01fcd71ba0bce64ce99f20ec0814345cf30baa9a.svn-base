package com.newland.appdriver;

import android.test.FlakyTest;
import android.test.suitebuilder.annotation.LargeTest;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * A working example of a ui automator test.
 * 
 * @author SNI
 */
public class AppDriverEntry extends UiAutomatorTestCase {
	private static final int TEST_TOLERANCE = 3;
	private DriverServer server = null;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
        try {
//            TestUseCase testCase = new TestUseCase();
//            testCase.GoRun();
            server = new DriverServer();
            server.startServer();
        }catch (Exception e){
            System.out.print("Setup" + e.getMessage());
        }
	}

	@Override
	protected void tearDown() throws Exception {
		server.stopRun();
		server = null;
		super.tearDown();
	}

    @LargeTest
    @FlakyTest(tolerance = TEST_TOLERANCE)
    public void testUIAutomatorStub() throws InterruptedException {
        while (server.isAlive())
            Thread.sleep(100);
    }

}