/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.gui2.converter.loader;

import java.io.Writer;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.gui2.converter.ConversionDecider;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.EditorLoadContext;

/**
 * AbilityLoader is an instance of BasicLoader that does extra processing to
 * properly process Ability files.
 */
public class AbilityLoader extends BasicLoader<Ability>
{
    private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

    private final EditorLoadContext context;

    /**
     * Create a new AbilityLoader instance.
     *
     * @param lc              The context being used to process the tokens.
     * @param cl              The class of the objects to be processed.
     * @param lk              The key under which the files to be processed are stored.
     * @param changeLogWriter The stream we will record any changes to.
     */
    public AbilityLoader(EditorLoadContext lc, Class<Ability> cl, ListKey<CampaignSourceEntry> lk,
            Writer changeLogWriter)
    {
        super(lc, cl, lk, changeLogWriter);

        context = lc;
    }

    @Override
    public List<CDOMObject> process(StringBuilder sb, int line, String lineString, ConversionDecider decider)
            throws PersistenceLayerException, InterruptedException
    {
        // We do a scan for the category first and ensure the ability category is defined.
        String[] tokens = lineString.split(FIELD_SEPARATOR);
        for (String tok : tokens)
        {
            if (tok.startsWith("CATEGORY:"))
            {
                String abilityCatName = tok.substring(9);
                final Category<Ability> cat = context.getReferenceContext()
                        .silentlyGetConstructedCDOMObject(ABILITY_CATEGORY_CLASS, abilityCatName);
                if (cat == null)
                {
                    //					Logging.log(Logging.INFO, "Found new cat " + abilityCatName
                    //						+ " at line " + line + ": " + lineString);
                    context.getReferenceContext().constructCDOMObject(ABILITY_CATEGORY_CLASS, abilityCatName);
                }

            }
        }

        return super.process(sb, line, lineString, decider);
    }

}
