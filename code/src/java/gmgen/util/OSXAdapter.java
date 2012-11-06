/*
 * OSXAdapter.java
 *
 * This is a modified version of Apple's OSXAdapter.java developers example.
 *
 * This sample uses a single class with clear, static entry points for hooking existing preferences,
 * about, quit functionality from an existing Java app into handlers for the Mac OS X application
 * menu.  The class is loaded using reflection, so that it will only be referenced by platforms
 * that actually support the Apple EAWT.  The built product should run unmodified on any
 * Java implementations.  Useful for developers looking to support multiple platforms with
 * a single codebase, and support Mac OS X features with minimal impact.
 * Requirements: Mac OS X 10.2 or later; Java 1.4.1 or later
 */
package gmgen.util;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * An adaptor class to deal with Apple Macintosh OSX issues 
 */
public class OSXAdapter extends ApplicationAdapter
{
	// pseudo-singleton model; no point in making multiple instances
	// of the EAWT application or our adapter
	private static OSXAdapter theAdapter;
	private static com.apple.eawt.Application theApplication;

	// reference to the app where the existing quit, about, prefs code is
	private gmgen.GMGenSystem mainApp;

	private OSXAdapter(gmgen.GMGenSystem inApp)
	{
		mainApp = inApp;
	}

	/** 
	 * Another static entry point for EAWT functionality.  Enables the
	 * "Preferences..." menu item in the application menu.
	 * NOTE: called from GMGenSystem.java using reflection.
	 * 
	 * @param enabled
	 */
	public static void enablePrefs(boolean enabled)
	{
		if (theApplication == null)
		{
			theApplication = new com.apple.eawt.Application();
		}

		theApplication.setEnabledPreferencesMenu(enabled);
	}

    @Override
	public void handlePreferences(ApplicationEvent ae)
	{
		if (mainApp != null)
		{
			mainApp.mPreferencesActionPerformedMac();
			ae.setHandled(true);
		}
		else
		{
			throw new IllegalStateException("handlePreferences: GMGenSystem instance detached from listener");
		}
	}

    @Override
	public void handleQuit(ApplicationEvent ae)
	{
		if (mainApp != null)
		{
			/*
			   /    You MUST setHandled(false) if you want to delay or cancel the quit.
			   /    This is important for cross-platform development -- have a universal quit
			   /    routine that chooses whether or not to quit, so the functionality is identical
			   /    on all platforms.  This example simply cancels the AppleEvent-based quit and
			   /    defers to that universal method.
			 */
			ae.setHandled(false);
			mainApp.exitFormMac();
		}
		else
		{
			throw new IllegalStateException("handleQuit: GMGenSystem instance detached from listener");
		}
	}
	/**
	 * The main entry-point for this functionality.  This is the only method
	 * that needs to be called at runtime, and it can easily be done using
	 * reflection (see MyApp.java)
	 * NOTE: called from GMGenSystem.java using reflection.
	 * 
	 * @param inApp
	 */
	public static void registerMacOSXApplication(gmgen.GMGenSystem inApp)
	{
		if (theApplication == null)
		{
			theApplication = new com.apple.eawt.Application();
		}

		if (theAdapter == null)
		{
			theAdapter = new OSXAdapter(inApp);
		}

		theApplication.addApplicationListener(theAdapter);
	}

}
