/*
 * InfoGuidePane.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Nov 7, 2011, 6:32:32 PM
 */
package pcgen.gui2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.UIResource;
import pcgen.cdom.base.Constants;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.system.LanguageBundle;

/**
 * This class provides a guide for first time 
 * users on what to do next and what sources are loaded.
 * 
 * Note: this class extends UIResource so that the component can be added
 * as a child of a JTabbedPane without it becoming a tab
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class InfoGuidePane extends JComponent implements UIResource
{

	private final PCGenFrame frame;
	private final JEditorPane gameModeLabel;
	private final JEditorPane campaignList;

	public InfoGuidePane(PCGenFrame frame)
	{
		this.frame = frame;
		this.gameModeLabel = createHtmlPane();
		this.campaignList = createHtmlPane();
		initComponents();
		initListeners();
	}

	private static JEditorPane createHtmlPane()
	{
		JEditorPane htmlPane = new JEditorPane();
		htmlPane.setOpaque(false);
		htmlPane.setContentType("text/html");
		htmlPane.setEditable(false);
		htmlPane.setFocusable(false);
		return htmlPane;
	}

	private void initComponents()
	{
		setLayout(new GridBagLayout());
		setOpaque(false);

		JPanel sourcesPanel = new JPanel(new GridBagLayout());
		sourcesPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
																  BorderFactory.createEmptyBorder(4, 4, 4, 4)));

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.anchor = GridBagConstraints.EAST;
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridwidth = GridBagConstraints.REMAINDER;
		gbc2.fill = GridBagConstraints.BOTH;
		sourcesPanel.add(new JLabel(LanguageBundle.getString("in_si_intro")), gbc2);
		sourcesPanel.add(new JLabel(LanguageBundle.getString("in_si_gamemode")), gbc1);
		sourcesPanel.add(gameModeLabel, gbc2);
		sourcesPanel.add(new JLabel(LanguageBundle.getString("in_si_sources")), gbc1);
		sourcesPanel.add(campaignList, gbc2);


		JEditorPane guidePane = createHtmlPane();
		guidePane.setText(LanguageBundle.getFormattedString("in_si_whatnext",
															Icons.New16.getImageIcon(),
															Icons.Open16.getImageIcon()));

		add(sourcesPanel, gbc2);
		add(guidePane, gbc2);
		refreshDisplayedSources(null);
	}

	private void initListeners()
	{
		frame.getSelectedCharacterRef().addReferenceListener(new ReferenceListener()
		{

			public void referenceChanged(ReferenceEvent e)
			{
				setVisible(e.getNewReference() == null);
			}

		});
		frame.getCurrentSourceSelectionRef().addReferenceListener(
				new ReferenceListener<SourceSelectionFacade>()
				{

					public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
					{
						refreshDisplayedSources(e.getNewReference());
					}

				});
	}

	private void refreshDisplayedSources(SourceSelectionFacade sources)
	{
		if (sources == null)
		{
			gameModeLabel.setText(Constants.WRAPPED_NONE_SELECTED);
		}
		else
		{
			gameModeLabel.setText(sources.getGameMode().getReference().getDisplayName());
		}
		if (sources == null || sources.getCampaigns().isEmpty())
		{
			campaignList.setText(LanguageBundle.getString("in_si_nosources"));
		}
		else
		{
			HtmlInfoBuilder builder = new HtmlInfoBuilder();
			for (CampaignFacade campaign : sources.getCampaigns())
			{
				builder.append(campaign.getName()).appendLineBreak();
			}
			campaignList.setText(builder.toString());
		}
	}

}
