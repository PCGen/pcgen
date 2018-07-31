/*
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
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pcgen.gui2.PCGenFrame;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Hyperactive;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.JLabelPane;
import pcgen.system.LanguageBundle;

public final class TipOfTheDay extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 6109389084434712217L;
	private static final UIPropertyContext PROPERTY_CONTEXT = UIPropertyContext.createContext("TipOfTheDay");
	private static final String NEXT = "next";
	private static final String PREV = "prev";
	private static final String HTML_START =
			"<html><body style=\"margin-left: 5px;margin-right: 5px;margin-top: 5px\">";
	private static final String HTML_END = "</body></html>";
	private JCheckBox chkShowTips;
	// the pane to display the text
	private JLabelPane tipText;
	private final TipOfTheDayHandler tipHandler;

	/** Creates new TipOfTheDay */
	public TipOfTheDay(PCGenFrame frame)
	{
		super(frame, true);

		setTitle(LanguageBundle.getString("in_tod_title")); //$NON-NLS-1$

		// initialize the interface
		initUI();

		tipHandler = TipOfTheDayHandler.getInstance();
		tipHandler.loadTips();

		pack();

		Utility.installEscapeCloseOperation(this);

		showNextTip();
	}

	public static boolean showTipOfTheDay()
	{
		return PROPERTY_CONTEXT.getBoolean("showTipOfTheDay", true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (NEXT.equals(e.getActionCommand()))
		{
			showNextTip();

			return;
		}
		else if (PREV.equals(e.getActionCommand()))
		{
			showPrevTip();

			return;
		}

		quit();
	}

	private boolean hasTips()
	{
		return tipHandler.hasTips();
	}

	//
	// initialize the dialog
	//
	private void initUI()
	{
		final JPanel panel = new JPanel(new BorderLayout(2, 2));
		panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		JLabel iconLabel;
		final Icon icon = Icons.TipOfTheDay24.getImageIcon();

		iconLabel = icon != null ? new JLabel(icon) : new JLabel("TipOfTheDay24.gif");

		iconLabel.setOpaque(true);
		panel.add(iconLabel, BorderLayout.WEST);
		final JLabel lblDidYouKnow = new JLabel("    " + LanguageBundle.getString("in_tod_didyouknow"));
		FontManipulation.xxlarge(lblDidYouKnow);
		lblDidYouKnow.setOpaque(true);

		tipText = new JLabelPane();
		tipText.setBorder(null);
		tipText.setFocusable(false);
		tipText.addHyperlinkListener(new Hyperactive());

		final JScrollPane pane = new JScrollPane(tipText);
		pane.setBorder(null);

		final JPanel content = new JPanel(new BorderLayout(0, 2));
		content.add(lblDidYouKnow, BorderLayout.NORTH);
		content.add(pane, BorderLayout.CENTER);
		content.setPreferredSize(new Dimension(585, 230));

		panel.add(content, BorderLayout.CENTER);

		chkShowTips = new JCheckBox(LanguageBundle.getString("in_tod_showTips"),
			PROPERTY_CONTEXT.initBoolean("showTipOfTheDay", true));

		final JButton btnClose = new JButton(LanguageBundle.getString("in_close"));
		btnClose.setMnemonic(LanguageBundle.getMnemonic("in_mn_close"));
		btnClose.addActionListener(this);
		// TODO give focus to close button

		final JButton btnPrevTip = new JButton(LanguageBundle.getString("in_tod_prevTip"));
		btnPrevTip.setMnemonic(LanguageBundle.getMnemonic("in_mn_tod_prevTip"));
		btnPrevTip.addActionListener(this);
		btnPrevTip.setActionCommand(PREV);

		final JButton btnNextTip = new JButton(LanguageBundle.getString("in_tod_nextTip"));
		btnNextTip.setMnemonic(LanguageBundle.getMnemonic("in_mn_tod_nextTip"));
		btnNextTip.addActionListener(this);
		btnNextTip.setActionCommand(NEXT);

		final JPanel actions = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);
		actions.add(chkShowTips, c);

		final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(btnPrevTip);
		buttons.add(btnNextTip);
		buttons.add(btnClose);
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		actions.add(buttons, c);

		panel.add(actions, BorderLayout.SOUTH);
		setContentPane(panel);
		getRootPane().setDefaultButton(btnClose);

		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				quit();
			}

		});
		addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					quit();
				}
			}

		});
	}

	/**
	 * close the dialog and save the settings.
	 */
	private void quit()
	{
		setVisible(false);

		PROPERTY_CONTEXT.setBoolean("showTipOfTheDay", chkShowTips.isSelected());

		dispose();
	}

	private void showNextTip()
	{
		if (hasTips())
		{
			showTip(tipHandler.getNextTip());
		}
	}

	private void showPrevTip()
	{
		if (hasTips())
		{
			showTip(tipHandler.getPrevTip());
		}
	}

	private void showTip(final String tip)
	{
		try
		{
			tipText.setText(HTML_START + LanguageBundle.getFormattedString("in_tod_tipDisplay", //$NON-NLS-1$
				Integer.toString(tipHandler.getLastNumber() + 1), tip) + HTML_END);
			repaint();
		}
		catch (Exception exc)
		{
			exc.printStackTrace(System.err);
		}
	}
}
