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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

/**
 * Deals with applying a Template via a Kit
 */
public class KitTemplate extends BaseKit
{
	private HashMapToList<CDOMSingleRef<PCTemplate>, CDOMSingleRef<PCTemplate>> templateList =
			new HashMapToList<CDOMSingleRef<PCTemplate>, CDOMSingleRef<PCTemplate>>();

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient HashMapToList<PCTemplate, PCTemplate> selectedMap =
			new HashMapToList<PCTemplate, PCTemplate>();

	/**
	 * Actually applies the templates to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	@Override
	public void apply(PlayerCharacter aPC)
	{
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);

		for (PCTemplate template : selectedMap.getKeySet())
		{
			List<PCTemplate> added = selectedMap.getListFor(template);
			if (added != null)
			{
				for (PCTemplate subtemplate : added)
				{
					aPC.setTemplatesAdded(template, subtemplate);
				}
			}
			aPC.addTemplate(template, added == null || added.size() == 0);
		}

		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
	}

	/**
	 * testApply
	 *
	 * @param aPC PlayerCharacter
	 * @param aKit Kit
	 * @param warnings List
	 */
	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC,
		List<String> warnings)
	{
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);

		for (CDOMSingleRef<PCTemplate> ref : templateList.getKeySet())
		{
			PCTemplate templateToAdd;
			try
			{
				templateToAdd = ref.resolvesTo().clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new UnreachableError("PCTemplate must support clone");
			}
			List<CDOMSingleRef<PCTemplate>> subList =
					templateList.getListFor(ref);
			List<PCTemplate> subAdded = new ArrayList<PCTemplate>();
			if (subList != null)
			{
				for (CDOMSingleRef<PCTemplate> subRef : subList)
				{
					PCTemplate ownedTemplate;
					try
					{
						ownedTemplate = subRef.resolvesTo().clone();
					}
					catch (CloneNotSupportedException e)
					{
						throw new UnreachableError(
							"PCTemplate must support clone");
					}
					subAdded.add(ownedTemplate);
					aPC.setTemplatesAdded(templateToAdd, ownedTemplate);
				}
			}

			Collection<PCTemplate> added = aPC.getTemplatesAdded(templateToAdd);
			aPC.addTemplate(templateToAdd, added == null || added.size() == 0);
			selectedMap.initializeListFor(templateToAdd);
			selectedMap.addAllToListFor(templateToAdd, subAdded);
		}

		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);

		if (selectedMap.size() > 0)
		{
			return true;
		}
		return false;
	}

	@Override
	public String getObjectName()
	{
		return "Templates";
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		boolean needsPipe = false;
		for (CDOMSingleRef<PCTemplate> ref : templateList.getKeySet())
		{
			if (needsPipe)
			{
				sb.append(Constants.PIPE);
			}
			needsPipe = true;
			sb.append(ref.getLSTformat());
			List<CDOMSingleRef<PCTemplate>> subList =
					templateList.getListFor(ref);
			if (subList != null)
			{
				for (CDOMSingleRef<PCTemplate> subref : subList)
				{
					sb.append("[TEMPLATE:");
					sb.append(subref.getLSTformat());
					sb.append(']');
				}
			}
		}
		return sb.toString();
	}

	public void addTemplate(CDOMSingleRef<PCTemplate> ref,
		List<CDOMSingleRef<PCTemplate>> subList)
	{
		templateList.initializeListFor(ref);
		templateList.addAllToListFor(ref, subList);
	}

	public Collection<CDOMSingleRef<PCTemplate>> getTemplates()
	{
		return templateList.getKeySet();
	}
}
