/*
 * Copyright 2006 (C) Andriy Sen
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
package pcgen.io.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.CoreUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Delta;
import pcgen.util.Logging;

public class CharacterFilter implements OutputFilter
{
    private String outputFilterName = "";
    private Map<Integer, String> outputFilter = null;

    /**
     * Create a new CharacterFilter instance suitable for processing output to
     * files produced using the supplied template.
     *
     * @param templateFileName The file name of the output template file.
     */
    public CharacterFilter(String templateFileName)
    {

        final int idx = templateFileName.lastIndexOf('.');

        String filterName = templateFileName;
        if (idx >= 0)
        {
            filterName = filterName.substring(idx + 1);
        }

        filterName = filterName.toLowerCase();

        if (filterName.equals(outputFilterName))
        {
            return;
        }

        outputFilter = null;

        filterName = new File(ConfigurationSettings.getSystemsDir()) + File.separator + "outputFilters" + File.separator
                + filterName + Constants.EXTENSION_LIST_FILE;

        final File filterFile = new File(filterName);

        try
        {
            if (filterFile.canRead() && filterFile.isFile())
            {
                final BufferedReader br =
                        new BufferedReader(new InputStreamReader(new FileInputStream(filterFile),
                                StandardCharsets.UTF_8
                        ));

                outputFilterName = filterName;
                outputFilter = new HashMap<>();

                while (true)
                {
                    final String aLine = br.readLine();

                    if (aLine == null)
                    {
                        break;
                    }

                    final List<String> filterEntry = CoreUtility.split(aLine, '\t');

                    if (filterEntry.size() >= 2)
                    {
                        try
                        {
                            final Integer key = Delta.decode(filterEntry.get(0));
                            outputFilter.put(key, filterEntry.get(1));
                        } catch (NullPointerException | NumberFormatException e)
                        {
                            Logging.errorPrint("Exception in setCurrentOutputFilter", e);
                        }
                    }
                }

                br.close();
            }
        } catch (IOException e)
        {
            //Should this be ignored?
        }
    }

    @Override
    public String filterString(String aString)
    {
        if ((outputFilter != null) && (!outputFilter.isEmpty()) && aString != null)
        {
            final StringBuilder xlatedString = new StringBuilder(aString.length());

            for (int i = 0;i < aString.length();i++)
            {
                final char c = aString.charAt(i);
                final String xlation = outputFilter.get((int) c);

                if (xlation != null)
                {
                    xlatedString.append(xlation);
                } else
                {
                    xlatedString.append(c);
                }
            }

            aString = xlatedString.toString();
        }
        return aString;
    }

}
