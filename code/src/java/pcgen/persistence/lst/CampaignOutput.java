/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.persistence.lst;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.io.FileAccess;
import pcgen.rules.context.LoadContext;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

/**
 * {@code CampaignOutput} writes out data sets to PCC files.
 */
public final class CampaignOutput
{
    /**
     * Private constructor added to inhibit instance creation for this utility class.
     */
    private CampaignOutput()
    {
        // Empty Constructor
    }

    /**
     * @param campaign
     */
    public static void output(LoadContext context, Campaign campaign)
    {
        final File outFile = new File(
                ConfigurationSettings.getPccFilesDir() + File.separator + campaign.getSafe(StringKey.DESTINATION));
        BufferedWriter out = null;

        try
        {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8));

            List<String> commentList = campaign.getListFor(ListKey.COMMENT);
            if (commentList != null)
            {
                for (String s : commentList)
                {
                    FileAccess.write(out, "#" + s);
                    FileAccess.newLine(out);
                }
            }

            Collection<String> lines = context.unparse(campaign);
            if (lines != null)
            {
                for (String line : lines)
                {
                    FileAccess.write(out, line);
                    FileAccess.newLine(out);
                }
            }
        } catch (FileNotFoundException exc)
        {
            Logging.errorPrint("Error while writing to " + outFile.toString(), exc);

            //TODO: Is this ok? Shouldn't something be done if writing a campaign fails?
        } finally
        {
            try
            {
                if (out != null)
                {
                    out.flush();
                    out.close();
                }
            } catch (IOException ex)
            {
                Logging.errorPrint("Can't close " + outFile.toString(), ex); //Not much more to do really...
            }
        }
    }
}
