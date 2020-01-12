/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.converter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.ObjectCache;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.converter.event.ProgressListener;
import pcgen.gui2.converter.event.TaskStrategyListener;
import pcgen.gui2.converter.event.TaskStrategyMessage;
import pcgen.gui2.converter.panel.ConvertSubPanel;
import pcgen.gui2.tools.CursorControlUtilities;
import pcgen.gui2.tools.Utility;

public class ConvertPanel extends JPanel
{
	private static final long serialVersionUID = 1686411319132380251L;

	private final JPanel basePanel = new JPanel();

	private final JButton finishButton;

	private final JButton prevButton;

	private final JButton nextButton;

	private final JButton cancelButton;

	private final CDOMObject properties;

	private final ProgressListener pl;

	private final List<ConvertSubPanel> queue;

	private int currentPanel = -1;

	private final JLabel statusLabel;

	public ConvertPanel(List<ConvertSubPanel> bq)
	{
		super(new BorderLayout());
		statusLabel = new JLabel();
		TaskStrategyListener tsl = (source, string) -> statusLabel.setText(string);
		TaskStrategyMessage.addTaskStrategyListener(tsl);

		properties = new ObjectCache();
		Box buttonBox = Box.createHorizontalBox();
		prevButton = new JButton("< Previous");
		prevButton.setMnemonic('P');
		prevButton.addActionListener(new PreviousButtonListener());
		prevButton.setEnabled(false); // FUTURE Need to reenable this
		buttonBox.add(prevButton);
		nextButton = new JButton("Next >");
		nextButton.setMnemonic('N');
		pl = new ProgressListener()
		{
			@Override
			public void progressAllowed(ProgressEvent pe)
			{
				if (pe.getID() == ProgressEvent.AUTO_ADVANCE)
				{
					proceedToNextPanel();
				}
				else
				{
					nextButton.setEnabled(true);
				}
			}

			@Override
			public void progressNotAllowed(ProgressEvent pe)
			{
				nextButton.setEnabled(false);
			}
		};
		nextButton.addActionListener(arg0 -> proceedToNextPanel());
		buttonBox.add(nextButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(arg0 -> checkExit());
		buttonBox.add(cancelButton);
		finishButton = new JButton("Finish");
		finishButton.addActionListener(arg0 -> {
            PCGenDataConvert.savePrefs();
            System.exit(0);
        });
		finishButton.setVisible(false);
		buttonBox.add(finishButton);
		basePanel.setPreferredSize(new Dimension(800, 500));
		JScrollPane jsp = new JScrollPane(basePanel);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(jsp);
		JPanel buttonLayout = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Utility.buildRelativeConstraints(gbc, 1, 1, 1.0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 10, 5, 10);
		buttonLayout.add(statusLabel, gbc);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.NONE,
			GridBagConstraints.EAST);
		buttonLayout.add(buttonBox, gbc);
		add(buttonLayout, BorderLayout.SOUTH);
		queue = bq;
		runNextPanel();
	}

	private void proceedToNextPanel()
	{
		Thread t = new Thread(() -> {
			CursorControlUtilities.startWaitCursor(basePanel);
			runNextPanel();
			CursorControlUtilities.stopWaitCursor(basePanel);
		});
		t.start();
	}

	public void prepare(ConvertSubPanel panel, boolean allowPrev)
	{
		setButtonVisibility(panel.isLast(), allowPrev);
		panel.addProgressListener(pl);
		panel.performAnalysis(properties);
	}

	private void setButtonVisibility(boolean displayingLast, boolean allowPrev)
	{
		nextButton.setEnabled(false);
		prevButton.setEnabled(allowPrev);
		//finishButton.setVisible(displayingLast);
		cancelButton.setVisible(true);
	}

	private void showFinishButton()
	{
		finishButton.setVisible(true);
		cancelButton.setVisible(false);
	}

	public void checkExit()
	{
		int response = JOptionPane.showConfirmDialog(this, "Are you sure you wish to cancel and exit?", "Confirm Exit",
			JOptionPane.OK_CANCEL_OPTION);
		if (response == JOptionPane.OK_OPTION)
		{
			PCGenDataConvert.savePrefs();
			System.exit(0);
		}
	}

	private void runNextPanel()
	{
		ConvertSubPanel nextpanel;
		do
		{
			boolean allowPrev = false;
			if (currentPanel >= 0 && currentPanel < queue.size())
			{
				allowPrev = queue.get(currentPanel).returnAllowed();
			}
			currentPanel++;
			if (currentPanel < queue.size())
			{
				nextpanel = queue.get(currentPanel);
				prepare(nextpanel, allowPrev);
				basePanel.removeAll();
				nextpanel.setupDisplay(basePanel, properties);
				basePanel.repaint();
			}
			else
			{
				nextpanel = null;
				showFinishButton();
			}
		}
		while (nextpanel != null && nextpanel.autoAdvance(properties));
	}

	/**
		 * The Class {@code PreviousButtonListener} ...
		 * 
		 * 
		 */
	public class PreviousButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			CursorControlUtilities.startWaitCursor(basePanel);
			ConvertSubPanel prevpanel;
			do
			{
				currentPanel--;
				if (currentPanel >= 0 && currentPanel < queue.size())
				{
					prevpanel = queue.get(currentPanel);
					boolean allowPrev = false;
					if (currentPanel > 0)
					{
						allowPrev = queue.get(currentPanel - 1).returnAllowed();
					}
					prepare(prevpanel, allowPrev);
					basePanel.removeAll();
					prevpanel.setupDisplay(basePanel, properties);
					basePanel.repaint();
				}
				else
				{
					prevpanel = null;
				}
			}
			while (prevpanel != null && prevpanel.autoAdvance(properties));
			CursorControlUtilities.stopWaitCursor(basePanel);
		}

	}

	/**
	 * @return The field which will be used for status display
	 */
	public Component getStatusField()
	{
		return statusLabel;
	}
}
