/*
 * KitTemplate.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on October 15, 2005, 10:00 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Kit;
import pcgen.core.SettingsHandler;
import pcgen.core.Globals;

/**
 * Deals with applying a Template via a Kit
 */
public class KitTemplate extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String templateStr = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient List theTemplates = new ArrayList();

	/**
	 * Constructor
	 * @param aTemplate
	 */
	public KitTemplate(final String aTemplate)
	{
		templateStr = aTemplate;
	}

	/**
	 * Actually applies the templates to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	public void apply(PlayerCharacter aPC)
	{
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);

		for (Iterator i = theTemplates.iterator(); i.hasNext(); )
		{
			PCTemplate template = (PCTemplate)i.next();
			aPC.addTemplate(template, template.templatesAdded().size() == 0);
		}

		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
	}

	/**
	 * testApply
	 *
	 * @param aPC PlayerCharacter
	 * @param aKit Kit
	 * @param warnings List
	 * TODO Implement this pcgen.core.kit.BaseKit method
	 */
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		if (templateStr == null)
		{
			return false;
		}

		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);


		final StringTokenizer aTok = new StringTokenizer(templateStr, "|");
		while (aTok.hasMoreTokens())
		{
			final String template = aTok.nextToken();

			final StringTokenizer subTok = new StringTokenizer(template, "[]");
			PCTemplate templateToAdd = null;
			while (subTok.hasMoreTokens())
			{
				String subStr = subTok.nextToken();
//				final PCTemplate ret = aPC.addTemplateNamed(template);
				if (subStr.startsWith("TEMPLATE:"))
				{
					final String ownedTemplateName = subStr.substring(9);
					PCTemplate ownedTemplate = Globals.getTemplateNamed(ownedTemplateName);
					if (ownedTemplate != null)
					{
						templateToAdd.addTemplateName(ownedTemplateName);
					}
					else
					{
						if (warnings != null)
						{
							warnings.add(
								"TEMPLATE: Could not add owned template \""
								+ ownedTemplateName + "\"");
						}
					}
				}
				else
				{
					PCTemplate potentialTemplate = Globals.getTemplateNamed(subStr);
					if (potentialTemplate != null)
					{
						try
						{
							templateToAdd = (PCTemplate) potentialTemplate.
								clone();
						}
						catch (CloneNotSupportedException notUsed)
						{
							// Should never happen
						}
					}
					else
					{
						if (warnings != null)
						{
							warnings.add("TEMPLATE: Could not add template \""
										 + template + "\"");
						}
					}
				}

			}
			aPC.addTemplate(templateToAdd, templateToAdd.templatesAdded().size() == 0);
			theTemplates.add(templateToAdd);
		}

		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);

		if (theTemplates.size() > 0)
		{
			return true;
		}
		return false;
	}

	public Object clone()
	{
		KitTemplate aClone = (KitTemplate)super.clone();
		aClone.templateStr = templateStr;
		return aClone;
	}

	public String getObjectName()
	{
		return "Templates";
	}

	public String toString()
	{
		return templateStr.replaceAll("\\|", ", ");
	}
}
