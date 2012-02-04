/*
 * LinkableHtmlMessage.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 30, 2003, 5:09 PM
 *
 * $Id$
 */
package pcgen.gui.utils;

import pcgen.system.LanguageBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class LinkableHtmlMessage extends JDialog implements
		ActionListener
{
	static final long serialVersionUID = -3678273627369325170L;
	private static final String HTML_START =
			"<html><body style=\"margin-left: 5px;margin-right: 5px;margin-top: 5px\">";
	private static final String HTML_END = "</body></html>";
	private JButton btnClose;

	// the pane to display the text
	private JLabelPane msgText;

	/** Creates new MessageWithUrl
	 * @param owner
	 * @param msg
	 * @param title
	 */
	public LinkableHtmlMessage(final Frame owner, final String msg,
		final String title)
	{
		super(owner, title, true);
		commonInit(msg);
		setLocationRelativeTo(owner); // centre on owner (Canadian spelling eh?)
	}

	/**
	 * Constructor
	 * @param owner
	 * @param msg
	 * @param title
	 */
	public LinkableHtmlMessage(final Dialog owner, final String msg,
		final String title)
	{
		super(owner, title, true);
		commonInit(msg);
		setLocationRelativeTo(owner); // centre on owner (Canadian spelling eh?)
	}

	public void actionPerformed(ActionEvent e)
	{
		quit();
	}

	public void setVisible(boolean b)
	{
		if (b)
		{
			btnClose.grabFocus();
		}
		super.setVisible(b);
	}

	private void commonInit(final String msg)
	{
		//		CoreUtility.maybeSetIcon(this, "PcgenIcon.gif");
		// initialize the interface
		initUI();

		//
		// replace newlines with <br>'s
		//
		String newMsg = msg;
		int idx = 0;

		for (;;)
		{
			idx = newMsg.indexOf('\n', idx);

			if (idx < 0)
			{
				break;
			}

			newMsg =
					newMsg.substring(0, idx) + "<br>"
						+ newMsg.substring(idx + 1);
		}

		msgText.setBackground(btnClose.getBackground());
		msgText.setText(HTML_START + newMsg + HTML_END);
		repaint();

		pack();
	}

	//
	// initialize the dialog
	//
	private void initUI()
	{
		final Container cont = getContentPane();
		GridBagLayout gl = new GridBagLayout();
		cont.setLayout(gl);

		Icon icon = UIManager.getIcon("OptionPane.warningIcon");
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.NORTHWEST;
		cont.add(new JLabel(icon), gc);

		msgText = new JLabelPane();
		msgText.setBorder(null);
		msgText.addHyperlinkListener(new Hyperactive());

		// If msgText is enabled, then you it can get focus by pressing the tab button, but
		// then you have to press ctrl-tab to get it to lose focus again.
		// If msgText is not enabled, then the mouse cursor doesn't change when it is over
		// a link
		//msgText.setEnabled(false);
		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		gc.weighty = 1;
		cont.add(msgText);

		btnClose = new JButton(LanguageBundle.getString("in_ok"));
		btnClose.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok"));
		btnClose.addActionListener(this);

		final JPanel buttons = new JPanel();
		buttons.add(btnClose);

		gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridwidth = 2;
		gc.weightx = 1;
		gc.insets = new Insets(5, 0, 5, 0);
		gc.anchor = GridBagConstraints.CENTER;
		cont.add(buttons, gc);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				quit();
			}
		});

		//
		// Allow enter and escape to close the window
		//
		btnClose.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if ((e.getKeyCode() == KeyEvent.VK_ESCAPE)
					|| (e.getKeyCode() == KeyEvent.VK_ENTER))
				{
					quit();
				}
			}
		});
	}

	// close the dialog
	private void quit()
	{
		setVisible(false);
		dispose();
	}
}
