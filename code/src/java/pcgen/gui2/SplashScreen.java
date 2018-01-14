/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import pcgen.gui2.tools.Icons;
import pcgen.system.PCGenTask;
import pcgen.system.PCGenTaskEvent;
import pcgen.system.PCGenTaskListener;

/**
 * PCGen's splash screen which is shown upon startup.
 *
 * @see pcgen.system.Main
 */
public class SplashScreen extends JWindow implements PCGenTaskListener
{

	private final JProgressBar loadProgress;
	private final JLabel loadingLabel;

	public SplashScreen()
	{
		this.loadProgress = new JProgressBar();
		this.loadingLabel = new JLabel();
		initComponents();
	}

	private void initComponents()
	{
		JComponent pane = new JPanel(new BorderLayout());
		pane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		Component splashLabel = new JLabel(Icons.SplashPcgen_Ennie.getImageIcon());
		pane.add(splashLabel, BorderLayout.NORTH);
		loadingLabel.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 10));
		
		Font curFont = pane.getFont();
		FontMetrics ftMetrics = pane.getFontMetrics(curFont);
		int ftHeight = ftMetrics.getHeight();
		loadingLabel.setPreferredSize(new Dimension(splashLabel.getWidth(), ftHeight));
		pane.add(loadingLabel, BorderLayout.CENTER);

		loadProgress.setStringPainted(true);
		pane.add(loadProgress, BorderLayout.SOUTH);

		Container cont = getContentPane();
		cont.setLayout(new GridLayout(1, 1));
		cont.add(pane);
		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Sets the message to display
	 *
	 * @param text the message to display
	 */
	public void setMessage(String text)
	{
		loadingLabel.setText(text);
	}

	private boolean dirty = false;

	/**
	 * This updates the progress bar with the latest task progress information.
	 * It is not assumed that this method will be called on the Event Dispatch
	 * thread so UI updates are added to the Event Dispatch queue so that they
	 * are handled appropriately. To make sure that update requests do not
	 * overwhelm the UI thread a {@code dirty} flag is used to make sure
	 * that the multiple UI update requests are not queued at the same time.
	 *
	 * @param event a PCGenTaskEvent
	 */
	@Override
	public void progressChanged(final PCGenTaskEvent event)
	{
		if (!dirty)
		{
			dirty = true;
			SwingUtilities.invokeLater(() ->
			{
				PCGenTask task = event.getSource();
				loadProgress.getModel().setRangeProperties(task.getProgress(), 1, 0, task.getMaximum(), true);
				loadingLabel.setText(task.getMessage());
				dirty = false;
			});
		}

	}

	@Override
	public void errorOccurred(PCGenTaskEvent event)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
