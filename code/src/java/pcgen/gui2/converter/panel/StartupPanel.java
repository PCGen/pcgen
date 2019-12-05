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
package pcgen.gui2.converter.panel;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.UnstretchingGridLayout;
import pcgen.gui2.converter.event.ProgressEvent;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.system.Main;
import pcgen.system.PCGenPropBundle;
import pcgen.system.PCGenTask;

public class StartupPanel extends ConvertSubPanel
{

    private final JPanel message;
    private final JProgressBar progressBar;
    private final GameModeFileLoader gameModeFileLoader;
    private final CampaignFileLoader campaignFileLoader;

    /**
     * Create a new instance of StartupPanel
     *
     * @param gameModeFileLoader
     * @param campaignFileLoader
     */
    public StartupPanel(GameModeFileLoader gameModeFileLoader, CampaignFileLoader campaignFileLoader)
    {
        this.gameModeFileLoader = gameModeFileLoader;
        this.campaignFileLoader = campaignFileLoader;
        message = new JPanel();
        message.setLayout(new UnstretchingGridLayout(0, 1));
        message
                .add(new JLabel("Welcome to the PCGen " + PCGenPropBundle.getProdVersionSeries() + " Data Converter..."));
        message.add(new JLabel(" "));
        message.add(new JLabel("Loading Game Modes and Campaign Information."));
        message.add(new JLabel(" "));

        progressBar = new JProgressBar(0, 3);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        message.add(progressBar);
        message.add(new JLabel(" "));
    }

    @Override
    public boolean performAnalysis(CDOMObject pc)
    {
        new Thread(() -> {
            PCGenTask loadPluginTask = Main.createLoadPluginTask();
            loadPluginTask.run();
            progressBar.setValue(1);
            gameModeFileLoader.run();
            progressBar.setValue(2);
            campaignFileLoader.run();
            progressBar.setValue(3);

            message.add(new JLabel("Initialization complete, press next button to continue..."));
            message.revalidate();

            fireProgressEvent(ProgressEvent.AUTO_ADVANCE);
        }).start();
        return true;
    }

    @Override
    public boolean autoAdvance(CDOMObject pc)
    {
        return false;
    }

    @Override
    public void setupDisplay(JPanel panel, CDOMObject pc)
    {
        panel.add(message);
        panel.setPreferredSize(new Dimension(800, 500));
    }

    @Override
    public boolean isLast()
    {
        return false;
    }

}
