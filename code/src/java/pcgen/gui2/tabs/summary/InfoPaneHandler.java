/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.summary;

import pcgen.core.GameMode;
import pcgen.core.PCStat;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tabs.models.HtmlSheetSupport;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.SimpleHtmlPanelController;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * Manages the information pane of the summary tab. This is an output sheet 
 * that is displayed in the summary tab to advise the user of important 
 * stats for their character. The output sheet to be displayed is specified in 
 * the game mode miscinfo.lst file using the INFOSHEET tag.
 *    
 */
public class InfoPaneHandler implements ReferenceListener<Object>, ListListener<CharacterLevelFacade>
{

	private final HtmlSheetSupport support;
	private final CharacterFacade character;

	/**
	 * Create a new info pane handler instance for a character.
	 * @param character The character the pane is to display information for.
	 * @param htmlPane the pane that displays the information
	 */
	public InfoPaneHandler(CharacterFacade character, JFXPanelFromResource<SimpleHtmlPanelController> htmlPane)
	{
		this.character = character;
		GameMode game = character.getDataSet().getGameMode();
		support = new HtmlSheetSupport(character, htmlPane, game.getInfoSheet());
		support.setMissingSheetMsg(LanguageBundle.getFormattedString("in_sumNoInfoSheet", //$NON-NLS-1$
			character.getDataSet().getGameMode().getName()));
		registerListeners();
	}

	/**
	 * Link this handler with our display component and schedule a refresh of 
	 * the contents for the character. 
	 */
	public void install()
	{
		support.install();
		scheduleRefresh();
	}

	/**
	 * Register with the things we want to be notified of changes about. 
	 */
	private void registerListeners()
	{
		if (character.getRaceRef()==null) 
		{
			Logging.debugPrint("ERROR:getRaceRef is null");
		} 
		else
		{
		character.getRaceRef().addReferenceListener(this);
		}
		if (character.getGenderRef()==null) 
		{
			Logging.debugPrint("ERROR:getGenderRef is null");
		} 
		else
		{
			character.getGenderRef().addReferenceListener(this);
		}
		if (character.getDataSet()==null || character.getDataSet().getAlignments()==null) 
		{
			Logging.debugPrint("ERROR:getDataSet or getDataSet.getAlignments is null");
		} 
		else
		{
			if (!character.getDataSet().getAlignments().isEmpty())
			{
				character.getAlignmentRef().addReferenceListener(this);
			}
		}
		for (PCStat stat : character.getDataSet().getStats())
		{
			if (character.getScoreBaseRef(stat)==null) 
			{
				Logging.debugPrint("getScoreBaseRef is null");
			} 
			else
			{
				character.getScoreBaseRef(stat).addReferenceListener(this);
			}
		}
		if (character.getCharacterLevelsFacade()==null)
		{
			Logging.debugPrint("getCharacterLevelsFacade is null");
		} 
		else
		{
			character.getCharacterLevelsFacade().addListListener(this);
		}
		if (character.getHandedRef()==null)
		{
			Logging.debugPrint("getHandedRef is null");
		} 
		else
		{
			character.getHandedRef().addReferenceListener(this);
		}
		if (character.getAgeRef()==null)
		{
			Logging.debugPrint("getAgeRef is null");
		} 
		else
		{
			character.getAgeRef().addReferenceListener(this);
		}
	}
	/**
	 * Start an update of the contents of the info pane for this character. The
	 * update will happen in a new thread and will not be started if one is 
	 * already running.  
	 */
	public void scheduleRefresh()
	{
		support.refresh();
	}

	/**
	 * Register that we are no longer the active character. 
	 */
	public void uninstall()
	{
		support.uninstall();
	}

	@Override
	public void referenceChanged(ReferenceEvent<Object> e)
	{
		scheduleRefresh();
	}

	@Override
	public void elementAdded(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	@Override
	public void elementRemoved(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	@Override
	public void elementModified(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	@Override
	public void elementsChanged(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}
}
