/*
 * Copyright 2007-14 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.context;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

public final class ReferenceContextUtilities
{

    private ReferenceContextUtilities()
    {
        //Do not instantiate utility class
    }

    /**
     * Check the associations now that all the data is loaded.
     *
     * @param validator The helper object to track things such as FORWARDREF
     *                  instances.
     */
    public static void validateAssociations(AbstractReferenceContext refContext, LoadValidator validator)
    {
        for (ReferenceManufacturer<?> rm : refContext.getAllManufacturers())
        {
            for (CDOMSingleRef<?> singleRef : rm.getReferenced())
            {
                String choice = singleRef.getChoice();
                if (choice != null)
                {
                    CDOMObject cdo = (CDOMObject) singleRef.get();
                    ChooseInformation<?> ci = cdo.get(ObjectKey.CHOOSE_INFO);
                    if (ci == null)
                    {
                        Logging.errorPrint("Found " + rm.getReferenceDescription() + " " + cdo.getKeyName() + " "
                                + " that had association: " + choice + " but was not an object with CHOOSE");
                        rm.fireUnconstuctedEvent(singleRef);
                        continue;
                    }
                    if (choice.indexOf('%') > -1)
                    {
                        //patterns or %LIST are OK
                        //See CollectionToAbilitySelection.ExpandingConverter
                        continue;
                    }
                    Class<?> cl = ci.getReferenceClass();
                    if (Loadable.class.isAssignableFrom(cl))
                    {
                        @SuppressWarnings("unchecked")
                        ReferenceManufacturer<? extends Loadable> mfg = refContext
                                .getManufacturerByFormatName(ci.getPersistentFormat(), (Class<? extends Loadable>) cl);
                        if (!mfg.containsObjectKeyed(choice) && (TokenLibrary.getPrimitive(cl, choice) == null)
                                && !report(validator, mfg.getReferenceIdentity(), choice))
                        {
                            Logging.errorPrint("Found " + rm.getReferenceDescription() + " " + cdo.getKeyName() + " "
                                    + " that had association: " + choice + " but no such " + mfg.getReferenceDescription()
                                    + " was ever defined");
                            rm.fireUnconstuctedEvent(singleRef);
                            continue;
                        }
                    }
                }
            }
        }
    }

    private static boolean report(UnconstructedValidator validator, ClassIdentity<?> cl, String key)
    {
        return validator != null && validator.allowUnconstructed(cl, key);
    }

    public static <T extends Categorized<T>> ReferenceManufacturer<? extends Loadable> getManufacturer(
            AbstractReferenceContext refContext, String firstToken)
    {
        int equalLoc = firstToken.indexOf('=');
        String className;
        String categoryName;
        if (equalLoc != firstToken.lastIndexOf('='))
        {
            Logging.log(Logging.LST_ERROR, "  Error encountered: Found second = in ObjectType=Category");
            Logging.log(Logging.LST_ERROR, "  Format is: ObjectType[=Category]|Key[|Key] value was: " + firstToken);
            Logging.log(Logging.LST_ERROR, "  Valid ObjectTypes are: " + StringPClassUtil.getValidStrings());
            return null;
        } else if ("FEAT".equals(firstToken))
        {
            className = "ABILITY";
            categoryName = "FEAT";
        } else if (equalLoc == -1)
        {
            className = firstToken;
            categoryName = null;
        } else
        {
            className = firstToken.substring(0, equalLoc);
            categoryName = firstToken.substring(equalLoc + 1);
        }
        //CONSIDER Dynamic fails here
        Class<? extends Loadable> c = StringPClassUtil.getClassFor(className);
        if (c == null)
        {
            Logging.log(Logging.LST_ERROR, "Unrecognized ObjectType: " + className);
            return null;
        }
        ReferenceManufacturer<? extends Loadable> rm;
        if (Categorized.class.isAssignableFrom(c))
        {
            if (categoryName == null)
            {
                Logging.log(Logging.LST_ERROR, "  Error encountered: Found Categorized Type without =Category");
                Logging.log(Logging.LST_ERROR, "  Format is: ObjectType[=Category]|Key[|Key] value was: " + firstToken);
                Logging.log(Logging.LST_ERROR, "  Valid ObjectTypes are: " + StringPClassUtil.getValidStrings());
                return null;
            }

            rm = refContext.getManufacturerByFormatName(firstToken, c);
            if (rm == null)
            {
                Logging.log(Logging.LST_ERROR,
                        "  Error encountered: " + className + " Category: " + categoryName + " not found");
                return null;
            }
        } else
        {
            if (categoryName != null)
            {
                Logging.log(Logging.LST_ERROR, "  Error encountered: Found Non-Categorized Type with =Category");
                Logging.log(Logging.LST_ERROR, "  Format is: ObjectType[=Category]|Key[|Key] value was: " + firstToken);
                Logging.log(Logging.LST_ERROR, "  Valid ObjectTypes are: " + StringPClassUtil.getValidStrings());
                return null;
            }
            rm = refContext.getManufacturer(c);
        }
        return rm;
    }

}
