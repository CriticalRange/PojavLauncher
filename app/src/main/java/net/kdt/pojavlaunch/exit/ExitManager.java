package net.kdt.pojavlaunch.exit;

import java.lang.reflect.*;

public class ExitManager {
	private static boolean stopLoop = false;
	
	private static ExitTrappedListener listener;
	private static Thread exitTrappedHook = new Thread(new Runnable(){
		
		@Override
		public void run()
		{
			if (listener != null) listener.onExitTrapped();
			// Pre-check
			// if (stopLoop) stopLoop = false;
			
			while (true) {
				if (stopLoop) {
					stopLoop = false;
					break;
				}
				
				// Make Thread hook never stop, then System.exit() never continue!
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
			
			stopLoop = false;
		}
	});
	
	public static void setExitTrappedListener(ExitTrappedListener l) {
		listener = l;
	}
	
	public static void stopExitLoop() {
		stopLoop = true;
	}
	
	public static void disableSystemExit() // throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		// changeRuntimeExitDisabled(true);
		
		Runtime.getRuntime().addShutdownHook(exitTrappedHook);
	}

	public static void enableSystemExit() // throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException
	{
		// changeRuntimeExitDisabled(false);
		
		try {
			Runtime.getRuntime().removeShutdownHook(exitTrappedHook);
		} catch (Throwable th) {
			stopExitLoop();
		}
	}
	
	// It's not safe. Add/Remove shutdown hooks will cause error.
	private static void changeRuntimeExitDisabled(boolean value) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		Runtime run = Runtime.getRuntime();
		Field shutdownField = run.getClass().getDeclaredField("shuttingDown");
		shutdownField.setAccessible(true);
		shutdownField.set(run, value);
	}

	
	public static interface ExitTrappedListener {
		public void onExitTrapped();
	}
}
