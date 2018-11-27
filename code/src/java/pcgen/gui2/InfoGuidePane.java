/*
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
 */
package pcgen.gui2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.UIResource;

import pcgen.cdom.base.Constants;
import pcgen.facade.core.CampaignFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.system.LanguageBundle;

/**
 * This class provides a guide for first time 
 * users on what to do next and what sources are loaded.
 * Note: this class extends UIResource so that the component can be added
 * as a child of a JTabbedPane without it becoming a tab
 */
class InfoGuidePane extends JComponent implements UIResource
{

	/**
	 * The context indicating what items are currently loaded/being processed in the UI
	 */
	private final UIContext uiContext;
	private final PCGenFrame frame;
	private final JEditorPane gameModeLabel;
	private final JEditorPane campaignList;
	private final JEditorPane tipPane;
	private JPanel mainPanel;

	InfoGuidePane(PCGenFrame frame, UIContext uiContext)
	{
		this.uiContext = Objects.requireNonNull(uiContext);
		this.frame = frame;
		this.gameModeLabel = createHtmlPane();
		this.campaignList = createHtmlPane();
		this.tipPane = createHtmlPane();
		TipOfTheDayHandler.getInstance().loadTips();
		initComponents();
		initListeners();
	}

	private static JEditorPane createHtmlPane()
	{
		JEditorPane htmlPane = new JEditorPane();
		htmlPane.setOpaque(false);
		htmlPane.setContentType("text/html");
		htmlPane.setEditable(false);
		htmlPane.setFocusable(true);
		return htmlPane;
	}

	private void initComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setBorder(
			BorderFactory.createTitledBorder(null, "", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null));

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setPreferredSize(new Dimension(650, 450));
		setOpaque(false);

		JPanel sourcesPanel = new JPanel(new GridBagLayout());

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
		guidePane.setText(LanguageBundle.getFormattedString("in_si_whatnext", Icons.New16.getImageIcon(),
			Icons.Open16.getImageIcon()));

		mainPanel.add(sourcesPanel);
		mainPanel.add(guidePane);
		mainPanel.add(tipPane);
		refreshDisplayedSources(null);

		JPanel outerPanel = new JPanel(new FlowLayout());
		outerPanel.add(mainPanel);
		setLayout(new BorderLayout());
		add(outerPanel, BorderLayout.CENTER);

		tipPane.setText(LanguageBundle.getFormattedString("in_si_tip", TipOfTheDayHandler.getInstance().getNextTip()));
	}

	private void initListeners()
	{
		frame.getSelectedCharacterRef().addReferenceListener(new ReferenceListener<CharacterFacade>()
		{

			@Override
			public void referenceChanged(ReferenceEvent<CharacterFacade> e)
			{
				boolean show = e.getNewReference() == null;
				if (show)
				{
					tipPane.setText(
						LanguageBundle.getFormattedString("in_si_tip", TipOfTheDayHandler.getInstance().getNextTip()));
				}
				setVisible(show);
			}

		});
		uiContext.getCurrentSourceSelectionRef()
			.addReferenceListener(new ReferenceListener<SourceSelectionFacade>()
			{

				@Override
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
			gameModeLabel.setText(sources.getGameMode().get().getDisplayName());
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
