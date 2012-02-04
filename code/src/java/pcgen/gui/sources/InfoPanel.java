/*
 * InfoPanel.java
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
 * Created on 16/11/2008 3:34:16 PM
 *
 * $Id$
 */

package pcgen.gui.sources;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceManager;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;


/**
 * The Class <code>InfoPanel</code> provides a guide for first time 
 * users on what to do next and what sources are loaded.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class InfoPanel extends JPanel
{
	private JLabelPane infoPane = new JLabelPane();
	private JLabelPane gameMode = new JLabelPane();
	private JLabelPane sourceList = new JLabelPane();

	public InfoPanel()
	{
		setName(Tab.INFO.label());
		
		initComponents();
		refreshDisplay();
	}

	private void initComponents()
	{
		setLayout(new java.awt.GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(4, 4, 4, 4);

		JPanel sourcesPanel = new JPanel();
		sourcesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		sourcesPanel.setLayout(new java.awt.GridBagLayout());
		
		JLabel jLabel1 = new JLabel(LanguageBundle.getString("in_si_intro"));
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0.0, 0.0);
		sourcesPanel.add(jLabel1, gbc);

		jLabel1 = new JLabel(LanguageBundle.getString("in_si_gamemode"));
		Utility.buildRelativeConstraints(gbc, 1, 1,
			0.0, 0.0);
		sourcesPanel.add(jLabel1, gbc);
		gameMode.setOpaque(false);
		gameMode.setText(SettingsHandler.getGame().getDisplayName());
		gameMode.setEditable(false);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0.0, 0.0);
		sourcesPanel.add(gameMode, gbc);

		jLabel1 = new JLabel(LanguageBundle.getString("in_si_sources"));
		Utility.buildRelativeConstraints(gbc, 1, 1,
			0.0, 0.0);
		sourcesPanel.add(jLabel1, gbc);
		sourceList.setOpaque(false);
		sourceList.setEditable(false);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0.0, 0.0);
		sourcesPanel.add(sourceList, gbc);

		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1,
			0.0, 0.0);
		add(sourcesPanel, gbc);
		
		infoPane.setOpaque(false);
		infoPane.setContentType("text/html"); //$NON-NLS-1$
		infoPane.setText(LanguageBundle.getFormattedString("in_si_whatnext",
			IconUtilitities.class.getResource(IconUtilitities.RESOURCE_URL+"New16.gif"), 
			IconUtilitities.class.getResource(IconUtilitities.RESOURCE_URL+"NewNPC16.gif"), 
			IconUtilitities.class.getResource(IconUtilitities.RESOURCE_URL+"Open16.gif")));
		infoPane.setEditable(false);

		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER,
			0.0, 0.0);
		add(infoPane, gbc);
	}
	
	/**
	 * Refresh the info panel display to reflect the current source loaded status.
	 */
	public void refreshDisplay()
	{
		gameMode.setText(SettingsHandler.getGame().getDisplayName());

		List<String> selectedCampaigns = new ArrayList<String>();
		PersistenceManager pMan = PersistenceManager.getInstance();
		List<URI> campaigns = pMan.getChosenCampaignSourcefiles();  
		for (URI element : campaigns)
		{
			final Campaign aCampaign = Globals.getCampaignByURI(element);

			if (aCampaign != null)
			{
				String name = aCampaign.getDisplayName();
				if (!selectedCampaigns.contains(name) && pMan.isLoaded(aCampaign))
				{
					selectedCampaigns.add(name);
				}
			}
		}
		Collections.sort(selectedCampaigns);
		
		StringBuffer buff = new StringBuffer();
		for (String name : selectedCampaigns)
		{
			buff.append(name).append("<br>");
		}
		if (buff.length() > 0)
		{
			sourceList.setText(buff.toString());
		}
		else
		{
			sourceList.setText(LanguageBundle.getString("in_si_nosources"));
		}
	}
}
