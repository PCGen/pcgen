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
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.UIResource;

import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.SimpleHtmlPanelController;
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
    private final JFXPanelFromResource<SimpleHtmlPanelController> gameModeLabel;
    private final JFXPanelFromResource<SimpleHtmlPanelController> campaignList;
    private final JFXPanelFromResource<SimpleHtmlPanelController> tipPane;

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

    private static JFXPanelFromResource<SimpleHtmlPanelController> createHtmlPane()
    {
        return new JFXPanelFromResource<>(
                SimpleHtmlPanelController.class,
                "SimpleHtmlPanel.fxml"
        );
    }

    private void initComponents()
    {
        /*
         * The layout here is kind of wonky and forces us into a fixed size.
         * As we convert to JavaFX strongly consider replacing fixed constants with layout that respect their parents.
         */
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(
                BorderFactory.createTitledBorder(null, "", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, null));

        mainPanel.setLayout(new GridLayout(0, 1));
        mainPanel.setPreferredSize(new Dimension(650, 650));
        setOpaque(false);

        JPanel sourcesPanel = new JPanel(new GridLayout(0, 1));

        sourcesPanel.setPreferredSize(new Dimension(650, 250));
        sourcesPanel.add(new JLabel(LanguageBundle.getString("in_si_intro")));
        sourcesPanel.add(new JLabel(LanguageBundle.getString("in_si_gamemode")));
        sourcesPanel.add(gameModeLabel);
        sourcesPanel.add(new JLabel(LanguageBundle.getString("in_si_sources")));
        sourcesPanel.add(campaignList);

        var guidePane = createHtmlPane();
        guidePane.getController().setHtml(LanguageBundle.getFormattedString("in_si_whatnext",
                Icons.New16.getImageIcon(),
                Icons.Open16.getImageIcon()));

        mainPanel.add(sourcesPanel);
        mainPanel.add(guidePane);
        mainPanel.add(tipPane);
        refreshDisplayedSources(null);

        setLayout(new FlowLayout());
        add(mainPanel, BorderLayout.CENTER);

        tipPane.getController().setHtml(LanguageBundle.getFormattedString("in_si_tip", TipOfTheDayHandler.getInstance().getNextTip()));
    }

    private void initListeners()
    {
        frame.getSelectedCharacterRef().addReferenceListener(e -> {
            if (e.getNewReference() == null)
            {
                this.setVisible(true);
                tipPane.getController().setHtml(
                        LanguageBundle.getFormattedString(
                                "in_si_tip",
                                TipOfTheDayHandler.getInstance().getNextTip()
                        ));
            } else
            {
                this.setVisible(false);
            }
        });

        uiContext.getCurrentSourceSelectionRef()
                .addReferenceListener(e -> refreshDisplayedSources(e.getNewReference()));
    }

    private void refreshDisplayedSources(SourceSelectionFacade sources)
    {
        if (sources == null)
        {
            gameModeLabel.getController().setHtml(Constants.WRAPPED_NONE_SELECTED);
        } else
        {
            gameModeLabel.getController().setHtml(sources.getGameMode().get().getDisplayName());
        }
        if (sources == null || sources.getCampaigns().isEmpty())
        {
            campaignList.getController().setHtml(LanguageBundle.getString("in_si_nosources"));
        } else
        {
            HtmlInfoBuilder builder = new HtmlInfoBuilder();
            for (Campaign campaign : sources.getCampaigns())
            {
                builder.append(campaign.getKeyName()).appendLineBreak();
            }
            campaignList.getController().setHtml(builder.toString());
        }
    }

}
