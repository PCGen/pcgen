/*
 * TipOfTheDay.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 17, 2001, 12:43 PM
 *
 * $Id$
 */
package pcgen.gui;

import pcgen.core.SettingsHandler;
import pcgen.gui.utils.Hyperactive;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class TipOfTheDay extends JFrame implements ActionListener
{
	static final long serialVersionUID = 6109389084434712217L;
	private static final String NEXT = "next";
	private static final String HTML_START = "<html><body style=\"margin-left: 5px;margin-right: 5px;margin-top: 5px\">";
	private static final String HTML_END = "</body></html>";
	private JCheckBox chkShowTips;

	// the pane to display the text
	private JLabelPane tipText;
	private List<String> tipList = null;
	private int lastNumber = -1;

	/** Creates new TipOfTheDay */
	TipOfTheDay()
	{
		super();

		IconUtilitities.maybeSetIcon(this, "TipOfTheDay16.gif");

		setTitle(LanguageBundle.getString("in_tod_title"));

		// initialize the interface
		initUI();

		// load tips
		loadTips();

		pack();

		Utility.centerFrame(this, false);
		lastNumber = SettingsHandler.getLastTipShown();
		showNextTip();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (NEXT.equals(e.getActionCommand()))
		{
			showNextTip();

			return;
		}

		quit();
	}

	private boolean hasTips()
	{
		return (tipList != null) && (tipList.size() > 0);
	}

	//
	// initialize the dialog
	//
	private void initUI()
	{
		final JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		JLabel iconLabel;
		final Icon icon = IconUtilitities.getImageIcon("TipOfTheDay24.gif");

		if (icon != null)
		{
			iconLabel = new JLabel(icon);
		}
		else
		{
			iconLabel = new JLabel("TipOfTheDay24.gif");
		}

		iconLabel.setOpaque(true);
		panel.add(iconLabel, BorderLayout.WEST);

		final JLabel lblDidYouKnow = new JLabel("    " + LanguageBundle.getString("in_tod_didyouknow"));
		final Font old = lblDidYouKnow.getFont();
		lblDidYouKnow.setFont(old.deriveFont(old.getStyle() | Font.ITALIC, 18f));
		lblDidYouKnow.setOpaque(true);

		tipText = new JLabelPane();
		tipText.setBorder(null);
		tipText.addHyperlinkListener(new Hyperactive());

		final JScrollPane pane = new JScrollPane(tipText);
		pane.setBorder(null);

		final JPanel content = new JPanel(new BorderLayout(0, 2));
		content.add(lblDidYouKnow, BorderLayout.NORTH);
		content.add(pane, BorderLayout.CENTER);
		content.setPreferredSize(new Dimension(585, 230));

		panel.add(content, BorderLayout.CENTER);

		chkShowTips = new JCheckBox(LanguageBundle.getString("in_tod_showTips"), SettingsHandler.getShowTipOfTheDay());

		final JButton btnClose = new JButton(LanguageBundle.getString("in_close"));
		btnClose.setMnemonic(LanguageBundle.getMnemonic("in_mn_close"));
		btnClose.addActionListener(this);

		final JButton btnNextTip = new JButton(LanguageBundle.getString("in_tod_nextTip"));
		btnNextTip.setMnemonic(LanguageBundle.getMnemonic("in_mn_tod_nextTip"));
		btnNextTip.addActionListener(this);
		btnNextTip.setActionCommand(NEXT);

		final JPanel actions = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
			    GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);
		actions.add(chkShowTips, c);

		final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(btnNextTip);
		buttons.add(btnClose);
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		actions.add(buttons, c);

		panel.add(actions, BorderLayout.SOUTH);
		setContentPane(panel);

		addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					quit();
				}
			});
		addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					{
						quit();
					}
				}
			});
	}

	private void loadTipFile(String tipsFilePath) throws FileNotFoundException, IOException
	{
		final File tipsFile = new File(tipsFilePath);

		//final BufferedReader tipsReader = new BufferedReader(new FileReader(tipsFile));
		final BufferedReader tipsReader = new BufferedReader(new InputStreamReader(new FileInputStream(tipsFile),
				"UTF-8"));
		final int length = (int) tipsFile.length();
		final char[] inputLine = new char[length];
		tipsReader.read(inputLine, 0, length);
		tipsReader.close();

		final StringTokenizer aTok = new StringTokenizer(new String(inputLine), "\r\n", false);

		while (aTok.hasMoreTokens())
		{
			tipList.add(aTok.nextToken());
		}
	}

	private void loadTips()
	{
		tipList = new ArrayList<String>(20);

		final String tipsFilePath = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" +
			File.separator + SettingsHandler.getGame().getName() + File.separator + "tips.lst";
		final String tipsDefaultPath = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" +
			File.separator + "default" + File.separator + "tips.lst";

		boolean tryDefault = false;

		try
		{
			loadTipFile(tipsFilePath);
		}
		catch (FileNotFoundException e)
		{
			tryDefault = true;
		}
		catch (IOException e)
		{
			tryDefault = true;
		}
		if (tryDefault)
		{
			try
			{
				loadTipFile(tipsDefaultPath);
			}
			catch (FileNotFoundException e1)
			{
				Logging.errorPrint("Warning: game mode " + SettingsHandler.getGame().getName() + " is missing file tips.lst");
			}
			catch (IOException e1)
			{
				Logging.errorPrint("Warning: game mode " + SettingsHandler.getGame().getName() + " is missing file tips.lst");
			}
		}
	}

	/**
	 * close the dialog and save the settings.
	 */
	private void quit()
	{
		setVisible(false);

		SettingsHandler.setLastTipShown(lastNumber);
		SettingsHandler.setShowTipOfTheDay(chkShowTips.isSelected());

		dispose();
	}

	private void showNextTip()
	{
		if (hasTips())
		{
			if (++lastNumber >= tipList.size())
			{
				lastNumber = 0;
			}

			final String tip = tipList.get(lastNumber);

			try
			{
				tipText.setText(HTML_START + "<b>Tip#" + Integer.toString(lastNumber + 1) + "</b><br>" + tip + HTML_END);
				repaint();
			}
			catch (Exception exc)
			{
				exc.printStackTrace(System.err);
			}
		}
	}
}
