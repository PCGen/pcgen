/*
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 *
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

/**
 * This class is a LstFileLoader used to load character traits.
 *
 * <p>
 */
public class TraitLoader extends LstLineFileLoader
{
    private int traitType = -1;

    @Override
    public void loadLstFile(LoadContext context, URI fileName) throws PersistenceLayerException
    {
        traitType = -1;
        super.loadLstFile(context, fileName);
    }

    @Override
    public void parseLine(LoadContext context, String lstLine, URI sourceURI)
    {
        if (lstLine.charAt(0) != '[')
        {
            switch (traitType)
            {
                case 0:
                    SystemCollections.addToTraitList(lstLine.intern(), gameMode);

                    break;

                case 1:
                    SystemCollections.addToSpeechList(lstLine.intern(), gameMode);

                    break;

                case 2:
                    SystemCollections.addToPhraseList(lstLine.intern(), gameMode);

                    break;

                case 3:
                    SystemCollections.addToPhobiaList(lstLine.intern(), gameMode);

                    break;

                case 4:
                    SystemCollections.addToInterestsList(lstLine.intern(), gameMode);

                    break;

                case 5:
                    SystemCollections.addToHairStyleList(lstLine.intern(), gameMode);

                    break;

                //case 6:
                //	break;
                default:
                    break;
            }
        } else
        {
            if (lstLine.startsWith("[TRAIT]"))
            {
                traitType = 0;
            } else if (lstLine.startsWith("[SPEECH]"))
            {
                traitType = 1;
            } else if (lstLine.startsWith("[PHRASE]"))
            {
                traitType = 2;
            } else if (lstLine.startsWith("[PHOBIA]"))
            {
                traitType = 3;
            } else if (lstLine.startsWith("[INTERESTS]"))
            {
                traitType = 4;
            } else if (lstLine.startsWith("[HAIRSTYLE]"))
            {
                traitType = 5;
            }

            //else if (lstLine.startsWith("[HAIRLENGTH]"))
            //{
            //	traitType = 6;
            //}
            else
            {
                traitType = -1;
            }
        }
    }
}
