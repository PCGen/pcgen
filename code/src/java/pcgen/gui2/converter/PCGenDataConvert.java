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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import pcgen.gui2.converter.panel.CampaignPanel;
import pcgen.gui2.converter.panel.ConvertSubPanel;
import pcgen.gui2.converter.panel.GameModePanel;
import pcgen.gui2.converter.panel.RunConvertPanel;
import pcgen.gui2.converter.panel.SourceSelectionPanel;
import pcgen.gui2.converter.panel.StartupPanel;
import pcgen.gui2.converter.panel.SummaryPanel;
import pcgen.gui2.converter.panel.WriteDirectoryPanel;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.Main;
import pcgen.system.PCGenPropBundle;
import pcgen.system.PropertyContextFactory;
import pcgen.util.Logging;

import org.apache.commons.lang3.SystemUtils;

public final class PCGenDataConvert extends JFrame
{
    private static final long serialVersionUID = 3921586726890440663L;

    private static PropertyContextFactory configFactory;

    private PCGenDataConvert()
    {
        super("PCGenDataConvert");
    }

    private static PCGenDataConvert getConverter()
    {
        PCGenDataConvert frame = new PCGenDataConvert();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        List<ConvertSubPanel> panels = new ArrayList<>();
        GameModeFileLoader gameModeFileLoader = new GameModeFileLoader();
        CampaignFileLoader campaignFileLoader = new CampaignFileLoader();
        panels.add(new StartupPanel(gameModeFileLoader, campaignFileLoader));

        final ConvertPanel installPanel = new ConvertPanel(panels);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent wEvent)
            {
                installPanel.checkExit();
            }
        });

        panels.add(new SourceSelectionPanel());
        panels.add(new GameModePanel(campaignFileLoader));
        panels.add(new CampaignPanel());

        panels.add(new WriteDirectoryPanel());

        panels.add(new SummaryPanel());

        panels.add(new RunConvertPanel(installPanel.getStatusField()));

        frame.getContentPane().add(installPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    public static void main(String[] args)
    {
        Logging.log(Level.INFO, "Starting PCGen Data Converter v" + PCGenPropBundle.getVersionNumber()); //$NON-NLS-1$
        configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
        configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance());
        Main.loadProperties(true);
        getConverter().setVisible(true);
    }

    static void savePrefs()
    {
        configFactory.savePropertyContexts();
        PropertyContextFactory.getDefaultFactory().savePropertyContexts();
    }
}
