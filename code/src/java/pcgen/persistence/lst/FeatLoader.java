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
 *
 *
 */
package pcgen.persistence.lst;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class FeatLoader extends AbilityLoader
{
    private boolean defaultFeatsLoaded = false;

    @Override
    public Ability parseLine(LoadContext context, Ability aFeat, String lstLine, SourceEntry source)
            throws PersistenceLayerException
    {
        Ability feat = aFeat;

        AbstractReferenceContext referenceContext = context.getReferenceContext();
        AbilityCategory featCategory = referenceContext.get(AbilityCategory.class, "FEAT");
        if (feat == null)
        {
            feat = new Ability();
            int tabLoc = lstLine.indexOf(SystemLoader.TAB_DELIM);
            String name = tabLoc == -1 ? lstLine : lstLine.substring(0, tabLoc);
            feat.setName(name.intern());
            feat.setCDOMCategory(featCategory);
            context.addStatefulInformation(feat);
            context.getReferenceContext().importObject(feat);
        } else
        {
            feat.setCDOMCategory(AbilityCategory.FEAT);
        }

        return super.parseLine(context, feat, lstLine, source);
    }

    @Override
    protected void loadLstFile(LoadContext context, CampaignSourceEntry sourceEntry)
    {
        super.loadLstFile(context, sourceEntry);

        if (!defaultFeatsLoaded)
        {
            loadDefaultFeats(context, sourceEntry);
        }
    }

    /**
     * This method loads the default feats with the first feat source.
     *
     * @param context
     * @param firstSource CampaignSourceEntry first loaded by this loader
     */
    private void loadDefaultFeats(LoadContext context, CampaignSourceEntry firstSource)
    {
        AbstractReferenceContext referenceContext = context.getReferenceContext();
        AbilityCategory featCategory = referenceContext.get(AbilityCategory.class, "FEAT");
        Ability wpFeat =
                referenceContext.getManufacturerId(featCategory).getActiveObject(Constants.INTERNAL_WEAPON_PROF);
        if (wpFeat == null)
        {

            /* Add catch-all feat for weapon proficiencies that cannot be granted as part
             * of a Feat eg. Simple weapons should normally be applied to the Simple
             * Weapon Proficiency feat, but it does not allow multiples (either all or
             * nothing).  So monk class weapons will get dumped into this bucket.  */

            String aLine = Constants.INTERNAL_WEAPON_PROF + "\tOUTPUTNAME:Weapon Proficiency\tTYPE:General"
                    + "\tVISIBLE:NO\tMULT:YES\tSTACK:YES\tCHOOSE:NOCHOICE"
                    + "\tDESC:You attack with this specific weapon normally,"
                    + " non-proficiency incurs a -4 to hit penalty." + "\tSOURCELONG:PCGen Internal";
            try
            {
                parseLine(context, null, aLine, firstSource);
            } catch (PersistenceLayerException ple)
            {
                Logging.errorPrint("Unable to parse the internal default feats '" + aLine + "': " + ple.getMessage());
            }
            defaultFeatsLoaded = true;
        }
    }

    @Override
    protected Ability getObjectKeyed(LoadContext context, final String aKey)
    {
        AbstractReferenceContext referenceContext = context.getReferenceContext();
        AbilityCategory featCategory = referenceContext.get(AbilityCategory.class, "FEAT");
        return referenceContext.getManufacturerId(featCategory).getActiveObject(aKey);
    }

    @Override
    protected Ability getMatchingObject(LoadContext context, CDOMObject key)
    {
        return getObjectKeyed(context, key.getKeyName());
    }

}
