/*
 * SummaryPanel.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 18/01/2009 9:35:28 AM
 *
 * $Id$
 */

package pcgen.gui.converter.panel;

import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.gui.converter.event.ProgressEvent;

/**
 * The Class <code>SummaryPanel</code> presents a summary of the user's 
 * choices for confirmation before running a conversion process.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class SummaryPanel extends ConvertSubPanel
{

	private SpringLayout layout = new SpringLayout();

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#autoAdvance(pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#performAnalysis(pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		fireProgressEvent(ProgressEvent.ALLOWED);
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#setupDisplay(javax.swing.JPanel, pcgen.cdom.base.CDOMObject)
	 */
	@Override
	public void setupDisplay(JPanel panel, CDOMObject pc)
	{
		panel.setLayout(layout);
		JLabel introLabel =
				new JLabel("<html><b>Ready to convert.</b><br/>" +
						"Press Next to begin converting using the following settings:</html>");
		panel.add(introLabel);
		layout.putConstraint(SpringLayout.NORTH, introLabel, 20,
			SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, introLabel, 25,
			SpringLayout.WEST, panel);

		JLabel labels[] = new JLabel[4];
		JLabel values[] = new JLabel[4];
		labels[0] = new JLabel("Source Folder:");
		labels[1] = new JLabel("Destination Folder:");
		labels[2] = new JLabel("Game mode:");
		labels[3] = new JLabel("Sources:");
		
		values[0] = new JLabel(pc.get(ObjectKey.DIRECTORY).getAbsolutePath());
		values[1] = new JLabel(pc.get(ObjectKey.WRITE_DIRECTORY).getAbsolutePath());
		values[2] = new JLabel(pc.get(ObjectKey.GAME_MODE).getName());
		List<Campaign> campaigns = pc.getSafeListFor(ListKey.CAMPAIGN);
		StringBuffer campDisplay = new StringBuffer("<html>");
		for (int i = 0; i < campaigns.size(); i++)
		{
			campDisplay.append(campaigns.get(i).getDisplayName());
			campDisplay.append("<br>");
		}
		campDisplay.append("</html>");
		values[3] = new JLabel(campDisplay.toString());

		// Place the labels on the page and lay them out
		Font plainFont = panel.getFont().deriveFont(Font.PLAIN); 
		for (int i = 0; i < labels.length; i++)
		{
			panel.add(labels[i]);
			panel.add(values[i]);
			values[i].setFont(plainFont);
			if (i == 0)
			{
				layout.putConstraint(SpringLayout.NORTH, labels[i], 20,
					SpringLayout.SOUTH, introLabel);
				layout.putConstraint(SpringLayout.WEST, values[i], 20,
					SpringLayout.EAST, labels[1]);
			}
			else
			{
				layout.putConstraint(SpringLayout.NORTH, labels[i], 20,
					SpringLayout.SOUTH, values[i-1]);
				layout.putConstraint(SpringLayout.WEST, values[i], 0,
					SpringLayout.WEST, values[i-1]);
			}
			layout.putConstraint(SpringLayout.WEST, labels[i], 25,
				SpringLayout.WEST, panel);
			layout.putConstraint(SpringLayout.NORTH, values[i], 0,
				SpringLayout.NORTH, labels[i]);
			layout.putConstraint(SpringLayout.EAST, values[i], -25,
				SpringLayout.EAST, panel);
		}

	}
}
