/*
 * NoteToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

//NOTE
public class NoteToken extends Token
{
    public static final String TOKENNAME = "NOTE";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        StringTokenizer tok = new StringTokenizer(tokenSource, ".");
        tok.nextToken();
        StringBuilder sb = new StringBuilder();

        String name = tok.nextToken();
        List<NoteItem> noteList = getNoteList(pc, name);

        String beforeHeader = "<b>";
        String afterHeader = "</b><br/>";
        String beforeValue = "";
        String afterValue = "<br/>";
        String token = "ALL";

        if (tok.hasMoreTokens())
        {
            beforeHeader = tok.nextToken();
            if ("NAME".equals(beforeHeader))
            {
                token = "NAME";
                beforeHeader = afterHeader = beforeValue = afterValue = "";
                if (tok.hasMoreTokens() && !"ALL".equals(token))
                {
                    beforeHeader = tok.nextToken();
                }
                if (tok.hasMoreTokens())
                {
                    afterHeader = tok.nextToken();
                }
            } else if ("VALUE".equals(beforeHeader))
            {
                token = "VALUE";
                beforeHeader = afterHeader = beforeValue = afterValue = "";
                if (tok.hasMoreTokens())
                {
                    beforeValue = tok.nextToken();
                }
                if (tok.hasMoreTokens())
                {
                    afterValue = tok.nextToken();
                }
            } else if ("ALL".equals(beforeHeader))
            {
                token = "ALL";
                if (tok.hasMoreTokens())
                {
                    beforeHeader = tok.nextToken();
                }
                if (tok.hasMoreTokens())
                {
                    afterHeader = tok.nextToken();
                }
                if (tok.hasMoreTokens())
                {
                    beforeValue = tok.nextToken();
                }
                if (tok.hasMoreTokens())
                {
                    afterValue = tok.nextToken();
                }
            }
        }

        for (NoteItem ni : noteList)
        {
            switch (token)
            {
                case "ALL":
                    // TODO - Why doesn't this handle value the same as the VALUE token
                    sb.append(ni.getExportString(beforeHeader, afterHeader, beforeValue, afterValue));
                    break;
                case "NAME":
                    sb.append(ni.getName());
                    break;
                case "VALUE":
                    String internal = beforeValue + afterValue;
                    if ("".equals(internal))
                    {
                        internal = "$1";
                    }
                    sb.append(beforeValue);
                    sb.append(ni.getValue().replaceAll("(\n)", internal));
                    sb.append(afterValue);
                    break;
            }
        }

        return sb.toString().trim();
    }

    public static List<NoteItem> getNoteList(PlayerCharacter pc, String name)
    {
        List<NoteItem> noteList = new ArrayList<>();
        List<NoteItem> resultList;

        buildSubTree(noteList, pc.getDisplay().getNotesList(), -1);

        if ("ALL".equals(name))
        {
            resultList = noteList;
        } else
        {
            resultList = new ArrayList<>();
            try
            {
                int i = Integer.parseInt(name);

                if ((i >= 0) || (i < noteList.size()))
                {
                    resultList.add(noteList.get(i));
                }
            } catch (NumberFormatException e)
            {
                resultList = new ArrayList<>(noteList);

                for (int i = resultList.size() - 1;i >= 0;--i)
                {
                    final NoteItem ni = resultList.get(i);

                    if (!ni.getName().equalsIgnoreCase(name))
                    {
                        resultList.remove(i);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * Populate the target list with the children of the specified node.
     * This will recursively build up a list of the nodes in the base
     * list in breadth-first order. <br>
     * The initial call should have a parentNode of -1. This will add all
     * children of the hard-coded base nodes.
     *
     * @param targetList The list to be populated.
     * @param baseList   The source list for notes
     * @param parentNode The id of the node to be processed.
     */
    private static void buildSubTree(List<NoteItem> targetList, Collection<NoteItem> baseList, int parentNode)
    {
        for (NoteItem note : baseList)
        {
            if (note.getParentId() == parentNode || (parentNode == -1 && note.getParentId() < 0))
            {
                targetList.add(note);
                buildSubTree(targetList, baseList, note.getId());
            }
        }
    }
}
