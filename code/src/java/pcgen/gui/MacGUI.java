/*
 * MacGUI.java
 * Copyright 2006 (C) Tod Milam <twmilam@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on January 18, 2006
 */
package pcgen.gui;

import com.apple.eawt.*;


/**
 * <code>MacGUI</code> initializes Mac-specific GUI elements.
 *
 * @author Tod Milam <twmilam@yahoo.com>
 * @version $Revision$
 */
public class MacGUI extends ApplicationAdapter
{
	private static MacGUI myObj = null;
	private static com.apple.eawt.Application theApp = null;
	private static PCGen_Frame1 pcgenFrame = null;

	/**
	 * Initialize the Mac-specific properties.
	 * Create an ApplicationAdapter to listen for Help, Prefs, and Quit.
	 */
	public static void initialize() {
		if(myObj != null) {
			// we have already initialized.
			return;
		}

		// see what version of java we have
		String version = System.getProperty("java.version");
		String osVer = System.getProperty("os.version");
		if(osVer.startsWith("10.4") && version.startsWith("1.4")) {
			System.out.println("NOT using Mac screen menu bar as it causes a crash using Java 1.4 on Tiger.");
			System.setProperty("apple.laf.useScreenMenuBar", "false");
			System.setProperty("com.apple.macos.useScreenMenuBar", "false");
		/* Rely on the package setting these properties
		} else {
			//System.out.println("Using screen menu bar");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.macos.useScreenMenuBar", "true");
		*/
		}

		// set some Mac look and feel stuff
		/* Rely on the package setting these properties
		System.setProperty("com.apple.mrj.application.growbox.instrudes", "true");
		System.setProperty("com.apple.mrj.application.live-resize", "true");
		System.setProperty("com.apple.macos.smallTabs", "true");
		System.setProperty("apple.awt.showGrowBox", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PCGen");
		*/

		// set up the Application menu
		myObj = new MacGUI();
		theApp = new com.apple.eawt.Application();
		theApp.addApplicationListener(myObj);
		theApp.setEnabledPreferencesMenu(true);

		// see if the Quaqua look and feel is available
		java.io.File quaquaFile = new java.io.File("lib/quaqua.jar");
		if(quaquaFile.exists()) {
			// add the jar to the system classpath
// see http://www-128.ibm.com/developerworks/forums/dw_thread.jsp?message=13780590&cat=10&thread=103422&treeDisplayType=threadmodel&forum=171#13780590 for info on dynamically adding to classpath
			Class[] parameters = new Class[]{java.net.URL.class};
			java.net.URLClassLoader sysloader = (java.net.URLClassLoader)ClassLoader.getSystemClassLoader();
			Class sysclass = java.net.URLClassLoader.class;
			try {
				java.lang.reflect.Method method = sysclass.getDeclaredMethod("addURL",parameters);
				method.setAccessible(true);
				method.invoke(sysloader, new Object[]{quaquaFile.toURL()});
			} catch(Exception e) {
				System.out.println("Exception in MacGUI::initialize" + e.toString());
				System.out.println("Unable to add Quaqua Look and Feel as an option.");
			}
			javax.swing.UIManager.installLookAndFeel("Quaqua", "ch.randelshofer.quaqua.QuaquaLookAndFeel");
		} else {
			System.out.println("Quaqua Look and Feel not available.");
		}
	}  // end static initialize method

	/**
	 * Set the main frame
	 * @param frame
	 */
	public static void setPCGenFrame(PCGen_Frame1 frame) {
		pcgenFrame = frame;
	}  // end setPCGenFrame

	/**
	 * Called when user select "About" from the application menu.
	 */
	public void handleAbout(ApplicationEvent ae) {
		if(pcgenFrame != null) {
			pcgenFrame.aboutItem_actionPerformed();
		}
		ae.setHandled(true);
	}  // end handleAbout

	/**
	 * Called when user select "Preferences" from the application menu.
	 */
	public void handlePreferences(ApplicationEvent ae) {
		if(pcgenFrame != null) {
			pcgenFrame.preferencesItem_actionPerformed();
		}
		ae.setHandled(true);
	}  // end handlePreferences

	/**
	 * Called when user select "Quit" from the application menu.
	 */
	public void handleQuit(ApplicationEvent ae) {
		ae.setHandled(false);
		if(pcgenFrame != null) {
			pcgenFrame.exitItem_actionPerformed();
		}
	}  // end handleQuit
}  // end class MacGUI

