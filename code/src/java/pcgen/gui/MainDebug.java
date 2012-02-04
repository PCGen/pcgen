/*
 * MainDebug.java
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
 * Created on May 14, 2001, 4:06 PM
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 * 
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
 */
public final class MainDebug extends JPanel
{
	private static final long serialVersionUID = 6997794954514089648L;
	private static final JTextArea txtAreaDebug = new JTextArea();
	private BorderLayout borderLayout4 = new BorderLayout();
	private FlowLayout flowLayout3 = new FlowLayout();
	private JButton btnDebugClear = new JButton();
	private JButton btnDebugGo = new JButton();
	private JButton btnMemory = new JButton();
	private JLabel lblDebugSouth = new JLabel();
	private JPanel debugSouth = new JPanel();
	private JScrollPane debugCenter = new JScrollPane();
	private JTextField txtDebugField = new JTextField();

	public MainDebug()
	{
		initComponents();
		DebugHandler ch = new DebugHandler();
		Logging.registerHandler(ch);
		System.setOut(new DebugStream(System.out));
		System.setErr(System.out);

		try
		{
			System.getProperties().list(System.out);
			Logging.memoryReport();

			System.getProperty("java.vm.version");

			if (Globals.javaVersionMajor <= 1 && Globals.javaVersionMinor < 4)
			{
				ShowMessageDelegate.showMessageDialog(
					"You do not have java 1.4.x installed. Go to"
						+ " http://www.java.com to get the latest java.",
					"PCGen", MessageType.ERROR);
			}

			try
			{
				ResourceBundle d_properties =
						ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
				Logging.debugPrint("PCGen version:", d_properties
					.getString("VersionNumber"));
			}
			catch (MissingResourceException mre)
			{
				// TODO: Should we really ignore this?
			}

			if (!(new File("options.ini").exists()))
			{
				ShowMessageDelegate
					.showMessageDialog(
						"If you experience any difficulties, look in the"
							+ " Debug tab and post its contents at http://groups.yahoo.com/group/pcgen",
						"PCGen", MessageType.INFORMATION);
			}
		}
		catch (Exception e)
		{
			// TODO: Should we really ignore this?
		}
	}

	/**
	 * This method clears the debug message area of all messages
	 */
	private static void btnDebugClear_actionPerformed()
	{
		txtAreaDebug.setText("");
	}

	private void btnMemory_actionPerformed()
	{
		txtAreaDebug.append(Logging.memoryReportStr());
		txtAreaDebug.append("\n");
		txtAreaDebug.append("Executing Garbage Collection\n");
		Runtime.getRuntime().gc();
		txtAreaDebug.append(Logging.memoryReportStr());
		txtAreaDebug.append("\n");
	}

	/**
	 * This method should take the keyword entered in the text box, find it and
	 * print its value out to the debug message area
	 */
	private void btnDebugGo_actionPerformed()
	{
		String keyWord = txtDebugField.getText();
		final PlayerCharacter aPC = PCGen_Frame1.getInst().getCurrentPC();

		if (aPC != null)
		{
			pcgen.core.VariableProcessor vp = aPC.getVariableProcessor();
			vp.pauseCache();
			txtAreaDebug.append(aPC.getDisplayName() + ":" + keyWord + " = "
				+ aPC.getVariable(keyWord, true));
			vp.restartCache();
		}
		else
		{
			txtAreaDebug.append("No character currently selected.");
		}

		txtAreaDebug.append("\n");
	}

	private void initComponents()
	{
		this.setLayout(borderLayout4);
		txtAreaDebug.setLineWrap(true);
		txtAreaDebug.setWrapStyleWord(true);
		txtAreaDebug.setDoubleBuffered(true);
		txtAreaDebug.setMinimumSize(new Dimension(426, 17));
		txtAreaDebug.setEditable(false);
		debugCenter
			.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		debugCenter
			.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		debugCenter.setDoubleBuffered(true);
		debugCenter.setPreferredSize(new Dimension(446, 37));
		txtDebugField.setPreferredSize(new Dimension(200, 21));
		debugSouth.setLayout(flowLayout3);
		debugSouth.setPreferredSize(new Dimension(200, 40));
		flowLayout3.setAlignment(FlowLayout.LEFT);
		lblDebugSouth.setText("Variable : ");
		btnDebugGo.setText("Get");
		btnDebugGo.addActionListener(new ActionListener()
		{
			/**
			 * Anonymous event handler
			 * 
			 * @param e
			 *            The ActionEvent
			 */
			public void actionPerformed(ActionEvent e)
			{
				btnDebugGo_actionPerformed();
			}
		});
		btnDebugClear.setText("Clear");
		btnDebugClear.addActionListener(new ActionListener()
		{
			/**
			 * Anonymous event handler
			 * 
			 * @param e
			 *            The ActionEvent
			 */
			public void actionPerformed(ActionEvent e)
			{
				btnDebugClear_actionPerformed();
			}
		});

		btnMemory.setText("Memory");
		btnMemory.addActionListener(new ActionListener()
		{
			/**
			 * Anonymous event handler
			 * 
			 * @param e
			 *            The ActionEvent
			 */
			public void actionPerformed(ActionEvent e)
			{
				btnMemory_actionPerformed();
			}
		});

		this.add(debugCenter, BorderLayout.CENTER);
		debugCenter.getViewport().add(txtAreaDebug, null);
		this.add(debugSouth, BorderLayout.SOUTH);
		debugSouth.add(lblDebugSouth, null);
		debugSouth.add(txtDebugField, null);
		debugSouth.add(btnDebugGo, null);
		debugSouth.add(btnDebugClear, null);
		debugSouth.add(btnMemory, null);
		btnDebugGo.setMnemonic('g');
		btnDebugClear.setMnemonic('l');

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				// run when the panel becomes visible
				requestFocus();
			}
		});
	}

	private final class DebugStream extends PrintStream
	{
		private char[] newline = {'\n'};

		private DebugStream(OutputStream os)
		{
			super(os);
		}

		public void println(String x)
		{
			txtAreaDebug.append(x + new String(newline));
		}

		public void println(Object x)
		{
			txtAreaDebug.append(x.toString() + new String(newline));
		}

		public void write(int x)
		{
			txtAreaDebug.append(Integer.toString(x));
		}
	}

	public final class DebugHandler extends Handler
	{

		DebugHandler()
		{
			super();
			setLevel(Level.FINER);
		}
		
		@Override
		public void close() throws SecurityException
		{
			// Nothing to do
		}

		@Override
		public void flush()
		{
			// Nothing to do
		}

		@Override
		public void publish(LogRecord arg0)
		{
			txtAreaDebug.append(arg0.getLevel() + " " + arg0.getLoggerName()
				+ " " + arg0.getMessage() + "\n");
		}

	}
}
