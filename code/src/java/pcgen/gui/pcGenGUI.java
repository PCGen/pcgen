/*
 * pcGenGUI.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui;

import gmgen.pluginmgr.PluginLoader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.party.Party;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.MessageWrapper;
import pcgen.core.utils.ShowMessageConsoleObserver;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.DialogInputInterface;
import pcgen.gui.utils.Hyperactive;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.ShowMessageGuiObserver;
import pcgen.gui.utils.SwingChooser;
import pcgen.gui.utils.SwingChooserRadio;
import pcgen.gui.utils.Utility;
import pcgen.io.ExportHandler;
import pcgen.io.PCGFile;
import pcgen.persistence.lst.SponsorLoader;
import pcgen.util.InputFactory;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

/**
 * <code>pcGenGUI</code> is the Main-Class for the application.
 * It creates an unreferenced copy of itself, basically so that
 * the constructor code is run.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public class pcGenGUI
{
	private static SplashScreen splash;
	private static String templateName = "";
	private static String inFileName = "";
	private static String outFileName = "";
	private static boolean partyMode = false;
	private static String[] startupArgs = {  };

	/** Instantiated popup frame {@link HPFrame}. */
	private static HPFrame hpFrame = null;
	private PCGen_Frame1 frame;

	/**
	 * Unknown. Doesn't appear to have a useful function. Kind of a
	 * debug argument, this decides whether the main frame is
	 * packed or validated. The value is only set to false, so
	 * this means the choice is always validate.
	 */
	private boolean packFrame = false;

	/**
	 * Initialises the application and loads the main
	 * screen. It uses some system properties for parameters, and calls
	 * {@link pcgen.persistence.PersistenceManager#initialize PersistenceManager.initialize} to load the
	 * required campaign and configuration files. Finally the main
	 * screen of the application is created,
	 * {@link pcgen.gui.PCGen_Frame1 PCGen_Frame1}.
	 * <p>
	 * Some of the logic of the program initialisation should probably
	 * be refactored into the core package.
	 */
	public pcGenGUI()
	{
		Dimension d = null;

		try
		{
			if (SettingsHandler.getFirstRun())
			{
				if (Globals.getUseGUI())
				{
					hideSplashScreen();
					askFileLocation();
				}
			}

			SettingsHandler.readOptionsProperties();
			d = SettingsHandler.getOptionsFromProperties(null);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);

			String message = e.getMessage();

			if ((message == null) || (message.length() == 0))
			{
				message = "Unknown error whilst reading options.ini";
			}

			message += "\n\nIt MAY be possible to fix this problem by deleting your options.ini file.";
			ShowMessageDelegate.showMessageDialog(new MessageWrapper(message, "PCGen - Error processing Options.ini", MessageType.ERROR));

			if (Globals.getUseGUI())
			{
				hideSplashScreen();
			}

			System.exit(0);
		}

		// Fixes for Mac OS X look-and-feel menu problems.
		// sk4p 12 Dec 2002
		// Moved into separate class
		// tmilam 21 Jan 2006
		if (System.getProperty("os.name").equals("Mac OS X"))
		{
			try {
				Class.forName("pcgen.gui.MacGUI").getDeclaredMethod("initialize", null).invoke(null, null);
			} catch (ClassNotFoundException e) {
				// don't do anything, just default to standard Java style
				System.out.println("This build of PCGen doesn't include Mac-enhanced features.  Try the Mac build for a more Mac-like interface.");
			} catch (NoSuchMethodException e) {
				// don't do anything, just default to standard Java style
			} catch (IllegalAccessException e) {
				// don't do anything, just default to standard Java style
			} catch (java.lang.reflect.InvocationTargetException e) {
				// don't do anything, just default to standard Java style
			}
		}


		// If we are not using the GUI then just load, export and exit
		if (!Globals.getUseGUI())
		{
			runNonGui();
			return;
		}

		// If we get here then we are running with a GUI
		frame = new PCGen_Frame1();
		frame.setMainClass(this);

		// if on Mac then set the Frame so the Application menu will work
		// tmilam 21 Jan 2006
		if (System.getProperty("os.name").equals("Mac OS X"))
		{
			try {
				Class[] cargs = new Class[]{PCGen_Frame1.class};
				Object[] oargs = new Object[]{frame};
				Class.forName("pcgen.gui.MacGUI").getDeclaredMethod("setPCGenFrame", cargs).invoke(null, oargs);
			} catch (ClassNotFoundException e) {
				// don't do anything, just default to standard Java style
			} catch (NoSuchMethodException e) {
				// don't do anything, just default to standard Java style
			} catch (IllegalAccessException e) {
				// don't do anything, just default to standard Java style
			} catch (java.lang.reflect.InvocationTargetException e) {
				// don't do anything, just default to standard Java style
			}
		}

		//Validate frames that have preset sizes
		//Pack frames that have useful preferred size info, e.g. from their layout
		if (packFrame)
		{
			frame.pack();
		}
		else
		{
			frame.validate();
		}

		int x = -11;
		int y = -11;

		if (SettingsHandler.getLeftUpperCorner() != null)
		{
			x = (int) SettingsHandler.getLeftUpperCorner().getX();
			y = (int) SettingsHandler.getLeftUpperCorner().getY();
		}

		if ((x < -10) || (y < -10) || (d.height == 0) || (d.width == 0))
		{
			Utility.centerFrame(frame, false);
		}
		else
		{
			frame.setLocation(x, y);
			frame.setSize(d);
		}

		//Read the maximized information from the options (1.4 ONLY!!!)
		int windowState = SettingsHandler.getWindowState();

		if (windowState != Frame.NORMAL)
		{
			frame.setExtendedState(windowState);
		}

		UIFactory.initLookAndFeel();
		frame.resetUI();

		processStartupArgs();

		hideSplashScreen();

		// These can't be handled before the main frame exists
		SettingsHandler.readGUIOptionsProperties();
		Utility.handleToolTipShownStateChange();

		PCGen_Frame1.enableDisableMenuItems();
		frame.setVisible(true);

		if (SettingsHandler.getShowTipOfTheDay())
		{
			showTipOfTheDay();
		}
	}

	/**
	 * Run PCGen in non GUI mode. This means we just load the PC and export
	 * it, then exit.
	 */
	private void runNonGui()
	{
		PlayerCharacter pc = runNonGuiLoad(new File(inFileName));
		if (pc != null && (templateName != null) && (outFileName != null))
		{
			runNonGuiExport(pc);
		}
	}

    /**
     * Perform the export part of a non-gui invocation.
     * This method will take the pre-loaded character(s)
     * and export them using the ouputsheet specified on the
     * command line.
     * @param pc
     */
    private void runNonGuiExport(PlayerCharacter pc) {
        try
        {
        	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileName), "UTF-8"));
        	final File template = new File(templateName);

        	if (partyMode)
        	{
        		SettingsHandler.setSelectedPartyHTMLOutputSheet(template.getAbsolutePath());
        		(new ExportHandler(template)).write(Globals.getPCList(), bw);
        	}
        	else
        	{
        		SettingsHandler.setSelectedCharacterHTMLOutputSheet(template.getAbsolutePath(), pc);
        		(new ExportHandler(template)).write(pc, bw);
        	}

        	bw.close();
        }
        catch (Exception ex)
        {
        	Logging.errorPrint("Exception in writing", ex);
        }
    	Globals.executePostExportCommandStandard(outFileName);
    }

    /**
     * Load the character or characters specified in the file.
     *
     * @param file
     *            The File object pointing to the file on disk that is to be
     *            loaded. This can either be a '*.pcg' file for a single
     *            character or a '*.pcp' for a party
     * @return True if the character or party loads sucessfully
     */
    private PlayerCharacter runNonGuiLoad(File file)
    {
        Party party;
        if (partyMode) {
            party = Party.makePartyFromFile(file);
        }
        else {
            party = Party.makeSingleCharacterParty(file);
        }
        PlayerCharacter pc = party.load(null);
        if (pc == null)
        {
            //todo: i18n these messages
            ShowMessageDelegate.showMessageDialog(new MessageWrapper("Problems occurred while loading the file:"
                    + file.getName(), "Error", MessageType.ERROR));
        }

        return pc;
    }

    /**
	 * Instantiates itself after setting look & feel, and
	 * opening splash screen.
	 *
	 * @param args "-j" If first command line parameter is -j then the cross
	 *             platform look and feel is used. Otherwise the current
	 *             system is used (i.e. native L&F). This is a hidden
	 *              option :-)
	 */
	public static void main(String[] args)
	{
		templateName = System.getProperty("pcgen.templatefile");
		inFileName = System.getProperty("pcgen.inputfile");
		outFileName = System.getProperty("pcgen.outputfile");

		startupArgs = args;

		if (inFileName != null)
		{
			partyMode = PCGFile.isPCGenPartyFile(new File(inFileName));
			Globals.setUseGUI(false);
		}


		Observer messageObserver = null;
		if (Globals.getUseGUI())
		{
			// need to set Mac GUI props before any other GUI stuff
			// tmilam 21 Jan 2006
			if (System.getProperty("os.name").equals("Mac OS X"))
			{
				try {
					Class.forName("pcgen.gui.MacGUI").getDeclaredMethod("initialize", null).invoke(null, null);
				} catch (ClassNotFoundException e) {
					// don't do anything, just default to standard Java style
				} catch (NoSuchMethodException e) {
					// don't do anything, just default to standard Java style
				} catch (IllegalAccessException e) {
					// don't do anything, just default to standard Java style
				} catch (java.lang.reflect.InvocationTargetException e) {
					// don't do anything, just default to standard Java style
				}
			}
			messageObserver = new ShowMessageGuiObserver();
			ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());
			ChooserFactory.setRadioInterfaceClassname(SwingChooserRadio.class.getName());
			InputFactory.setInterfaceClassname(DialogInputInterface.class.getName());
		}
		else
		{
			messageObserver = new ShowMessageConsoleObserver();
		}
		ShowMessageDelegate.getInstance().addObserver(messageObserver);



		//
		// Ensure we are using the correct version of the run-time environment.
		// If not, inform the user, but still allow him to use the program
		//
		// Might want to be able to turn this message off at some point.
		// i.e. Don't show this again checkbox
		//
		try
		{
			final String sVersion = System.getProperty("java.version");

			if (Double.valueOf(Globals.javaVersion.substring(0, 3)).doubleValue() < 1.3)
			{
				ShowMessageDelegate.showMessageDialog(new MessageWrapper(
				    "PCGen requires Java 2 v1.3.\nYour version of java is currently " + sVersion + ".\n"
				    + "To be able to run PCGen properly you will need:\n"
				    + " * The Java 2 v1.3 runtime environment available from\n"
				    + "   http://java.sun.com/j2se/1.3/jre/\n\n"
				    + "You'll need to pick the version of java appropriate for your\n"
				    + "OS (the choices are Solaris/SPARC, Linux and Windows).", "PCGen", MessageType.INFORMATION));
			}
		}
		catch (Exception e)
		{
			//Don't care?
		}

		try
		{
			if (Globals.getUseGUI()) // only set L&F if we're going to use a GUI
			{
				if ((args.length > 0) && args[0].equals("-j"))
				{
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				}
				else
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			}
		}
		catch (Exception e)
		{
			//Hardly a fatal error, and quite unlikely at that...
			Logging.errorPrint("Couldn't set look and feel", e);
		}

		if (Globals.getUseGUI() && !(args.length > 0 && args[0].equals("--nosplash")))
		{
			showSplashScreen();
		}

		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		new pcGenGUI();
	}

	public static void showHpFrame(PlayerCharacter aPC)
	{
		initHpFrame(aPC);
		hpFrame.setPSize();
		hpFrame.pack();
		hpFrame.setVisible(true);
	}

	public static void showLicense()
	{
		String aString = " ";
		aString += readTextFromFile(SettingsHandler.getPcgenSystemDir() + File.separator + "opengaminglicense.10a.txt");

		if (Globals.getSection15() != null)
		{
			aString += Globals.getSection15().toString();
		}

		showLicense("OGL License 1.0a", aString);
	}

	public static void showLicense(String title, List fileList)
	{
		for (Iterator i = fileList.iterator(); i.hasNext();)
		{
			String fileName = (String)i.next();
			String fileText = null;

			// TODO: This could be from a net URL as a RFE...
			if( fileName.startsWith("file:/"))
			{
				fileText = readTextFromFile(fileName.substring(6));
			}
			else
			{
				fileText = readTextFromFile(Globals.getDefaultPath() + File.separator + (String) i.next());
			}

			showLicense(title, fileText);
		}
	}

	public static void showLicense(String title, String text)
	{
		if (title == null)
		{
			title = "OGL License 1.0a";
		}

		if (text == null)
		{
			text = "No license information found";
		}

		final JFrame aFrame = new JFrame(title);
		final JButton jClose = new JButton("Close");
		final JPanel jPanel = new JPanel();
		final JCheckBox jCheckBox = new JCheckBox("Show on source load");
		jPanel.add(jCheckBox);
		jCheckBox.setSelected(SettingsHandler.showLicense());
		jCheckBox.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent evt)
				{
					SettingsHandler.setShowLicense(jCheckBox.isSelected());
				}
			});
		jPanel.add(jClose);
		jClose.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					aFrame.dispose();
				}
			});
		IconUtilitities.maybeSetIcon(aFrame, "PcgenIcon.gif");

		final JEditorPane a = new JEditorPane("text/html", text);
		a.setEditable(false);

		final JScrollPane aPane = new JScrollPane();
		aPane.setViewportView(a);
		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(aPane, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel, BorderLayout.SOUTH);
		aFrame.setSize(new Dimension(700, 500));
		Utility.centerFrame(aFrame, false);
		aFrame.setVisible(true);
	}

	public static void showSponsors()
	{
		String title = "PCGen's sponsors";

		final JFrame aFrame = new JFrame(title);
		final JButton jClose = new JButton("Close");
		final JPanel jPanel = new JPanel();
		final JCheckBox jCheckBox = new JCheckBox("Show on source load");
		jPanel.add(jCheckBox);
		jCheckBox.setSelected(SettingsHandler.showSponsors());
		jCheckBox.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent evt)
				{
					SettingsHandler.setShowSponsors(jCheckBox.isSelected());
				}
			});
		jPanel.add(jClose);
		jClose.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					aFrame.dispose();
				}
			});
		IconUtilitities.maybeSetIcon(aFrame, "PcgenIcon.gif");

		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<img src='")
			.append(SponsorLoader.getConvertedSponsorPath((String)Globals.getSponsor("PCGEN").get("IMAGEBANNER")))
			.append("'><br>");

		String s = "";
		if(Globals.getSponsors().size() > 2) {
			s = "s";
		}
		sb.append("<H2><CENTER>Would like to thank our official sponsor")
			.append(s)
			.append(":</CENTER></h2>");
		List sponsors = Globals.getSponsors();
		int size = 172;
		for(int i = 0; i < sponsors.size(); i++) {
			Map sponsor = (Map)sponsors.get(i);
			if(sponsor.get("SPONSOR").equals("PCGEN")) {
				continue;
			}
			
			size += 70;
			sb.append("<img src='")
				.append(SponsorLoader.getConvertedSponsorPath((String)sponsor.get("IMAGEBANNER")))
				.append("'><br>");
		}
		sb.append("</html>");
		final JEditorPane a = new JEditorPane("text/html", sb.toString());
		a.setEditable(false);

		final JScrollPane aPane = new JScrollPane();
		aPane.setViewportView(a);
		aFrame.getContentPane().setLayout(new BorderLayout());
		aFrame.getContentPane().add(aPane, BorderLayout.CENTER);
		aFrame.getContentPane().add(jPanel, BorderLayout.SOUTH);
		aFrame.setSize(new Dimension(505, size));
		Utility.centerFrame(aFrame, false);
		aFrame.setVisible(true);
	}

	public static void showMandatoryD20Info()
	{
		final ImageIcon imgIcon = IconUtilitities.getImageIcon("D20_logo_RGB.jpg");

		if (imgIcon != null)
		{
			final JFrame aFrame = new JFrame("D20 Required Information");
			IconUtilitities.maybeSetIcon(aFrame, "PcgenIcon.gif");

			final JPanel jPanel1 = new JPanel();
			final JPanel jPanel2 = new JPanel();
			final JPanel jPanel3 = new JPanel();
			final JLabel jLabel1 = new JLabel(imgIcon);
			final JCheckBox jCheckBox1 = new JCheckBox("Show on source load");
			final JButton jClose = new JButton("Close");

			jPanel1.add(jLabel1);

			jPanel2.setLayout(new BorderLayout());

			aFrame.getContentPane().add(jPanel1, BorderLayout.NORTH);
			aFrame.getContentPane().add(jPanel2, BorderLayout.CENTER);
			aFrame.getContentPane().add(jPanel3, BorderLayout.SOUTH);

			String d20License = readTextFromFile(SettingsHandler.getPcgenSystemDir() + File.separator + "D20System.htm");
			final JEditorPane a = new JEditorPane("text/html", d20License);
			a.setEditable(false);
			a.addHyperlinkListener(new Hyperactive());

			final JScrollPane aPane = new JScrollPane();
			aPane.setViewportView(a);
			jPanel2.add(aPane, BorderLayout.CENTER);

			jPanel3.add(jCheckBox1);
			jPanel3.add(jClose);
			jCheckBox1.setSelected(SettingsHandler.showD20Info());

			jClose.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						aFrame.dispose();
					}
				});

			jCheckBox1.addItemListener(new ItemListener()
				{
					public void itemStateChanged(ItemEvent evt)
					{
						SettingsHandler.setShowD20Info(jCheckBox1.isSelected());
					}
				});

			aFrame.setSize(new Dimension(456, 352));
			Utility.centerFrame(aFrame, false);
			aFrame.setVisible(true);
		}
	}

	public static void showTipOfTheDay()
	{
		new TipOfTheDay().setVisible(true);
	}

	private void askFileLocation()
	{
		final Object[] oOk = { "OK" };
		final JLabel aLabel = new JLabel(
			    "<html>Select a directory to store PCGen options in:<hr><b>PCGen Dir</b>: This is the directory that PCGen is installed into (default)<br><b>Home Dir</b>: This is your home directory<br><b>Select</b>: Select a directory to use <br>If you have an existing options.ini file, then select the directory containing that file<hr>");
		final JPanel aPanel = new JPanel();
		final JPanel bPanel = new JPanel();
		final JPanel cPanel = new JPanel(new BorderLayout());
		final JPanel allPanel = new JPanel(new BorderLayout());

		ButtonGroup rGroup = new ButtonGroup();
		JRadioButton rPButton = new JRadioButton("PCGen Dir", true);
		JRadioButton rUButton = new JRadioButton("Home Dir");
		JRadioButton rSButton = new JRadioButton("Select a directory");
		final JTextField textField = new JTextField(String.valueOf(SettingsHandler.getPcgenFilesDir()));
		textField.setEditable(false);
		textField.setMinimumSize(new Dimension(90, 25));

		final JButton dirButton = new JButton("...");
		dirButton.setEnabled(false);
		rGroup.add(rPButton);
		rGroup.add(rUButton);
		rGroup.add(rSButton);

		aPanel.add(aLabel);
		bPanel.add(rPButton);
		bPanel.add(rUButton);
		cPanel.add(rSButton, BorderLayout.NORTH);
		rPButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					SettingsHandler.setFilePaths("pcgen");
					textField.setText(System.getProperty("user.dir"));
				}
			});
		rUButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					SettingsHandler.setFilePaths("user");
					textField.setText(System.getProperty("user.home") + File.separator + ".pcgen");
				}
			});
		rSButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					SettingsHandler.setFilePaths("select");
					dirButton.setEnabled(true);
				}
			});
		dirButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					File returnFile = SettingsHandler.getPcgenFilesDir();
					JFileChooser fc;

					if (returnFile == null)
					{
						fc = new JFileChooser();
					}
					else
					{
						fc = new JFileChooser(returnFile);
					}

					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

					final int rVal = fc.showOpenDialog(null);

					if (rVal == JFileChooser.APPROVE_OPTION)
					{
						returnFile = fc.getSelectedFile();
					}

					textField.setText(String.valueOf(returnFile));
					SettingsHandler.setPcgenFilesDir(returnFile);
				}
			});
		cPanel.add(textField, BorderLayout.CENTER);
		cPanel.add(dirButton, BorderLayout.EAST);

		allPanel.setSize(new Dimension(400, 200));
		allPanel.add(aPanel, BorderLayout.NORTH);
		allPanel.add(bPanel, BorderLayout.CENTER);
		allPanel.add(cPanel, BorderLayout.SOUTH);

		final Object[] message = new Object[1];
		message[0] = allPanel;

		JOptionPane.showOptionDialog(null, message, "Directory for options.ini location", JOptionPane.DEFAULT_OPTION,
		    JOptionPane.INFORMATION_MESSAGE, null, oOk, oOk[0]);
	}

	/**
	 * Ensures that the splash screen is not visible. This should be
	 * called before displaying any dialog boxes or windows at
	 * startup.
	 */
	private static void hideSplashScreen()
	{
		if (splash != null)
		{
			splash.dispose();
			splash = null;
		}
	}

	private static void initHpFrame(PlayerCharacter aPC)
	{
		if (hpFrame == null)
		{
			hpFrame = new HPFrame();
		}
	    hpFrame.setCharacter(aPC);
	}

	private boolean processStartupArgs()
	{
		boolean status = true;

		/* Load through the frame instead of this class so that
		   the frame has a chance to update the menubars, etc.
		 */
		for (int i = 0; i < startupArgs.length; ++i)
		{
			final File file = new File(startupArgs[i]);

			if (PCGFile.isPCGenCharacterFile(file))
			{
				if (frame.loadPCFromFile(file) == null)
				{
					Logging.errorPrint("No such PC file: " + startupArgs[i]);
					status = false;
				}
			}
			else if (PCGFile.isPCGenPartyFile(file))
			{
				if (!frame.loadPartyFromFile(file))
				{
					Logging.errorPrint("No such Party file: " + startupArgs[i]);
					status = false;
				}
			}
		}

		return status;
	}

	private static String readTextFromFile(String fileName)
	{
		String aString;
		final File aFile = new File(fileName);

		if (!aFile.exists())
		{
			Logging.errorPrint("Could not find license at " + fileName);
			aString = "No license information found";

			return aString;
		}

		try
		{
			BufferedReader theReader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), "UTF-8"));
			final int length = (int) aFile.length();
			final char[] inputLine = new char[length];
			theReader.read(inputLine, 0, length);
			theReader.close();
			aString = new String(inputLine);
		}
		catch (IOException e)
		{
			Logging.errorPrint("Could not read license at " + fileName, e);
			aString = "No license information found";
		}

		return aString;
	}

	private static void showSplashScreen()
	{
		splash = new SplashScreen();
	}
}
