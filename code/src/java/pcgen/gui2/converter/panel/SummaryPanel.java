/*
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
 */

package pcgen.gui2.converter.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FontManipulation;

/**
 * The Class {@code SummaryPanel} presents a summary of the user's
 * choices for confirmation before running a conversion process.
 */
public class SummaryPanel extends ConvertSubPanel
{

    @Override
    public boolean autoAdvance(CDOMObject pc)
    {
        return false;
    }

    @Override
    public boolean performAnalysis(CDOMObject pc)
    {
        fireProgressEvent(ProgressEvent.ALLOWED);
        return true;
    }

    @Override
    public void setupDisplay(JPanel panel, CDOMObject pc)
    {
        panel.setLayout(new GridBagLayout());

        JLabel introLabel = new JLabel("Ready to convert.");
        GridBagConstraints gbc = new GridBagConstraints();
        Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0, GridBagConstraints.HORIZONTAL,
                GridBagConstraints.NORTHWEST);
        gbc.insets = new Insets(50, 25, 10, 25);
        panel.add(introLabel, gbc);

        JLabel instructLabel = new JLabel("Press Next to begin converting using the following settings:");
        Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0, GridBagConstraints.HORIZONTAL,
                GridBagConstraints.NORTHWEST);
        gbc.insets = new Insets(10, 25, 20, 25);
        panel.add(instructLabel, gbc);

        JLabel[] labels = new JLabel[4];
        JComponent[] values = new JComponent[4];
        labels[0] = new JLabel("Source Folder:");
        labels[1] = new JLabel("Destination Folder:");
        labels[2] = new JLabel("Game mode:");
        labels[3] = new JLabel("Sources:");

        values[0] = new JLabel(pc.get(ObjectKey.DIRECTORY).getAbsolutePath());
        values[1] = new JLabel(pc.get(ObjectKey.WRITE_DIRECTORY).getAbsolutePath());
        values[2] = new JLabel(pc.get(ObjectKey.GAME_MODE).getDisplayName());
        List<Campaign> campaigns = pc.getSafeListFor(ListKey.CAMPAIGN);
        StringBuilder campDisplay = new StringBuilder();
        for (Campaign campaign : campaigns)
        {
            campDisplay.append(campaign.getDisplayName());
            campDisplay.append("\n");
        }
        JTextArea campText = new JTextArea(campDisplay.toString());
        campText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(campText);
        values[3] = scrollPane;

        // Place the labels on the page and lay them out
        Font plainFont = FontManipulation.plain(panel.getFont());
        for (int i = 0;i < labels.length;i++)
        {
            Utility.buildRelativeConstraints(gbc, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST);
            gbc.insets = new Insets(10, 25, 10, 10);
            panel.add(labels[i], gbc);
            if (i < labels.length - 1)
            {
                Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0,
                        GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHWEST);
            } else
            {
                Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1.0,
                        1.0, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST);
            }
            gbc.insets = new Insets(10, 10, 10, 25);
            panel.add(values[i], gbc);
            values[i].setFont(plainFont);
        }

    }
}
