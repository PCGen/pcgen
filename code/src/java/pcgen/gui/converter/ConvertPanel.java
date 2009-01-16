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
package pcgen.gui.converter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.ObjectCache;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.gui.converter.event.ProgressListener;
import pcgen.gui.converter.event.TaskStrategyListener;
import pcgen.gui.converter.event.TaskStrategyMessage;
import pcgen.gui.converter.panel.ConvertSubPanel;
import pcgen.gui.utils.CursorControlUtilities;

public class ConvertPanel extends JPanel
{

	private final JPanel basePanel = new JPanel();

	private final JButton finishButton;

	private final JButton nextButton;

	private final JButton cancelButton;

	private final CDOMObject properties;

	private final ProgressListener pl;

	private final BlockingQueue<ConvertSubPanel> queue;

	public ConvertPanel(BlockingQueue<ConvertSubPanel> bq)
	{
		super(new BorderLayout());
		final JLabel statusLabel = new JLabel();
		TaskStrategyListener tsl = new TaskStrategyListener()
		{
			private String status;

			private long time;

			public void processMessage(Object owner, String string)
			{
				JOptionPane.showMessageDialog(null, string);
			}

			public void processStatus(Object source, String string)
			{
				status = string;
				statusLabel.setText(string);
			}

			public void processActiveItem(Object source, String string)
			{
				long currentTime = System.currentTimeMillis();
				if ((currentTime - time) > 100)
				{
					statusLabel.setText(status + " [" + string + "]");
					time = currentTime;
				}
			}
		};
		TaskStrategyMessage.addTaskStrategyListener(tsl);

		properties = new ObjectCache();
		Box buttonBox = Box.createHorizontalBox();
		// prevButton = new JButton("< Previous");
		// prevButton.setMnemonic('P');
		// prevButton.addActionListener(new PreviousButtonListener());
		// prevButton.setEnabled(false); // FUTURE Need to reenable this
		// buttonBox.add(prevButton);
		nextButton = new JButton("Next >");
		nextButton.setMnemonic('N');
		pl = new ProgressListener()
		{
			public void progressAllowed(ProgressEvent pe)
			{
				nextButton.setEnabled(true);
			}

			public void progressNotAllowed(ProgressEvent pe)
			{
				nextButton.setEnabled(false);
			}
		};
		nextButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Thread t = new Thread(new Runnable()
				{
					public void run()
					{
						CursorControlUtilities.startWaitCursor(basePanel);
						runNextPanel();
						CursorControlUtilities.stopWaitCursor(basePanel);
					}
				});
				t.start();
			}
		});
		buttonBox.add(nextButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				checkExit();
			}
		});
		buttonBox.add(cancelButton);
		finishButton = new JButton("Finish");
		finishButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});
		finishButton.setVisible(false);
		buttonBox.add(finishButton);
		basePanel.setPreferredSize(new Dimension(800, 500));
		JScrollPane jsp = new JScrollPane(basePanel);
		jsp
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(jsp);
		JPanel buttonLayout = new JPanel(new BorderLayout());
		buttonLayout.add(buttonBox, BorderLayout.EAST);
		buttonLayout.add(statusLabel, BorderLayout.WEST);
		add(buttonLayout, BorderLayout.SOUTH);
		queue = bq;
		runNextPanel();
	}

	public void prepare(ConvertSubPanel panel)
	{
		setButtonVisibility(panel.isLast());
		panel.addProgressListener(pl);
		panel.performAnalysis(properties);
	}

	private void setButtonVisibility(boolean displayingLast)
	{
		nextButton.setEnabled(false);
		finishButton.setVisible(displayingLast);
		cancelButton.setVisible(!displayingLast);
	}

	public void checkExit()
	{
		// FINALCLEAN this is temporary, so it doesn't annoy me
		System.exit(0);

		int response = JOptionPane.showConfirmDialog(this,
				"Are you sure you wish to cancel and exit?", "Confirm Exit",
				JOptionPane.OK_CANCEL_OPTION);
		if (response == JOptionPane.OK_OPTION)
		{
			System.exit(0);
		}
	}

	private void runNextPanel()
	{
		ConvertSubPanel nextpanel = null;
		do
		{
			try
			{
				nextpanel = queue.take();
				prepare(nextpanel);
				basePanel.removeAll();
				nextpanel.setupDisplay(basePanel, properties);
				basePanel.repaint();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (nextpanel != null && nextpanel.autoAdvance(properties));
	}
}
