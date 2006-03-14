/*
 * SourceLoadProgressPanel.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import javax.swing.*;
import java.awt.*;

public class SourceLoadProgressPanel extends JPanel {
	private JProgressBar progressBar;
	private JLabel filenameArea;
	private JTextArea messageArea;
	private JScrollPane messageAreaContainer;

	private boolean errorState = false;

	/**
	 * Constructs a new, default <code>SourceLoadProgressPanel</code>.
	 */
	public SourceLoadProgressPanel()
	{
		super();

		initialize();
	}

	public void setMaxProgress(int max)
	{
		getProgressBar().setMaximum(max);
	}

	public void setCutrrentProgress(int curr)
	{
		getProgressBar().setValue(curr);
	}

	public void setCurrentFilename(String filename)
	{
		Graphics g = getFilenameArea().getGraphics();
		FontMetrics fm = g.getFontMetrics();
		int width = fm.stringWidth(filename);

		if (width < getFilenameArea().getWidth())
		{
			getFilenameArea().setText(filename);
		}
		else
		{
			getFilenameArea().setText(
					shortenString(fm, filename, getFilenameArea().getWidth()));
		}
	}

	public void addMessage(String message)
	{
		getMessageArea().append(message + "\n");
	}

	/**
	 * Keeps track if there has been set an error message.
	 *
	 * @param errorState <code>true</code> if there was an error message
	 */
	public void setErrorState(boolean errorState)
	{
		this.errorState = errorState;
	}

	public boolean getErrorState()
	{
		return errorState;
	}

	/**
	 * This method initializes this
	 */
	private void initialize()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints progressConstraints = new GridBagConstraints();
		progressConstraints.gridx = 0;
		progressConstraints.gridy = 0;
		progressConstraints.ipadx = 0;
		progressConstraints.ipady = 0;
		progressConstraints.weightx = 1.0;
		progressConstraints.fill = GridBagConstraints.HORIZONTAL;
		progressConstraints.insets = new Insets(0, 10, 5, 10);
		add(getProgressBar(), progressConstraints);

		GridBagConstraints filenameConstraints = new GridBagConstraints();
		filenameConstraints.gridx = 0;
		filenameConstraints.gridy = 1;
		filenameConstraints.ipadx = 0;
		filenameConstraints.ipady = 0;
		filenameConstraints.weightx = 1.0;
		filenameConstraints.fill = GridBagConstraints.HORIZONTAL;
		filenameConstraints.insets = new Insets(5, 10, 5, 10);
		add(getFilenameArea(), filenameConstraints);

		addMessageAreaContainer();

		setSize(594, 193);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void addMessageAreaContainer()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 2.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.ipadx = 340;
		constraints.ipady = 40;
		constraints.insets = new Insets(5, 0, 0, 0);
		add(messageAreaContainer(), constraints);
	}

	/**
	 * This method initializes progressBar
	 *
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getProgressBar()
	{
		if (progressBar == null)
		{
			progressBar = new JProgressBar();
			progressBar.setValue(17);
			progressBar.setStringPainted(true);
		}

		return progressBar;
	}

	/**
	 * This method initializes filenameArea
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getFilenameArea()
	{
		if (filenameArea == null)
		{
			filenameArea = new JLabel();
			filenameArea.setText("Current File Name");
			filenameArea.setFont(new Font("Dialog", java.awt.Font.PLAIN, 12));
			filenameArea.setName("labelCurrentFileName");
		}

		return filenameArea;
	}

	/**
	 * This method initializes messageArea
	 *
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getMessageArea()
	{
		if (messageArea == null)
		{
			messageArea = new JTextArea();
			messageArea.setName("errorMessageBox");
			messageArea.setEditable(false);
			messageArea.setTabSize(8);
		}

		return messageArea;
	}

	/**
	 * Lazily constructs and returns the scroll pane container.
	 *
	 * @return the scroll pane container
	 */
	private JScrollPane messageAreaContainer()
	{
		if (messageAreaContainer == null)
		{
			messageAreaContainer = new JScrollPane(getMessageArea());
		}

		return messageAreaContainer;
	}

	/**
	 * @param fm
	 * @param string
	 * @param maxWidth
	 * @return String
	 */
	private String shortenString(FontMetrics fm, String string, int maxWidth)
	{
		for (int i=string.length() ; i>0 ; i-=5)
		{
			String foo = "..." + string.substring( string.length()-i);

			int width = fm.stringWidth(foo);
			//System.out.println("testing '"+foo+"' = "+width);
			if (width < maxWidth)
			{
				return foo;
			}
		}
		return "";
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
