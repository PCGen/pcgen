/**
 * ClassChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2008 Stefan Radermacher <zaister@users.sourceforge.net>
 */

package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.AbstractReferenceContext;

// Referenced classes of package pcgen.core.chooser:
//            AbstractBasicPObjectChoiceManager

public class ClassChoiceManager extends AbstractBasicPObjectChoiceManager<PCClass>
{

	/**
	 * Make a new PCClass chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
    public ClassChoiceManager(
    		PObject         aPObject, 
    		String          choiceString, 
    		PlayerCharacter aPC)
    {
        super(aPObject, choiceString, aPC);
        setTitle("Class Choice");
    }

   
	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
    public void getChoices(
    		final PlayerCharacter aPc, 
    		final List<PCClass>   availableList, 
    		final List<PCClass>   selectedList)
    {
		AbstractReferenceContext refContext = Globals.getContext().ref;
        for(String option : getChoiceList())
        {
            StringTokenizer tok1 = new StringTokenizer(option, ",");
            while(tok1.hasMoreTokens()) 
            {
                String choice = tok1.nextToken();
                if("ANY".equals(choice))
                {
                	for (PCClass aClass : refContext.getConstructedCDOMObjects(PCClass.class))
                    {
                        if(aClass.hasSubClass())
                        {
                        	for (PCClass aSubClass : aClass.getListFor(ListKey.SUB_CLASS))
                            {
                                availableList.add(aSubClass);
                            }
                        } else
                        {
                            availableList.add(aClass);
                        }
                    }
                } else
                if(choice.startsWith("TYPE="))
                {
                    StringTokenizer tok2 = new StringTokenizer(choice.substring(5), ".");
                    List<String> typeList = new ArrayList<String>();
                    while (tok2.hasMoreTokens())
                    {
                    	typeList.add(tok2.nextToken().toUpperCase());
                    }
                    
                    for (PCClass aClass : refContext.getConstructedCDOMObjects(PCClass.class))
                    {
                        boolean bMatch = true;
                        for (String type : typeList)
                        {
                            bMatch &= aClass.getTypeList(true).contains(type);
                        }

                        if(bMatch)
                        {
                            if(aClass.hasSubClass())
                            {
                            	for (PCClass aSubClass : aClass.getListFor(ListKey.SUB_CLASS))
                                {
                                    availableList.add(aSubClass);
                                }
                            } else
                            {
                                availableList.add(aClass);
                            }
                        }
                    }
                } else
                {
                    int dotLoc = choice.indexOf(".");
                    if(dotLoc == -1)
                    {
                        PCClass aClass = refContext.silentlyGetConstructedCDOMObject(PCClass.class, choice);
                        if(aClass != null)
                        {
                            availableList.add(aClass);
                            if(aClass.hasSubClass())
                            {
                            	for (PCClass aSubClass : aClass.getListFor(ListKey.SUB_CLASS))
                                {
                                    availableList.add(aSubClass);
                                }
                            }
                        }
                    } else
                    {
                        String substring = choice.substring(0, dotLoc);
                        PCClass aClass = refContext.silentlyGetConstructedCDOMObject(PCClass.class, substring);
                        if(aClass != null)
                        {
                            PCClass aSubClass = aClass.getSubClassKeyed(choice.substring(dotLoc + 1));
                            if(aSubClass != null)
                            {
                                availableList.add(aSubClass);
                            }
                        }
                    }
                }
            }
        }

		for (String key : aPc.getAssociationList(pobject))
        {
            PCClass aClass = refContext.silentlyGetConstructedCDOMObject(PCClass.class, key);
            if(aClass != null)
            {
                selectedList.add(aClass);
            }
        }
        setPreChooserChoices(selectedList.size());
    }
}
