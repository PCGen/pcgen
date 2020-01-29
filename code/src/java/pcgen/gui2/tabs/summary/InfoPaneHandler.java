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

import javax.swing.JEditorPane;


import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.GameModeFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.tabs.models.HtmlSheetSupport;
import pcgen.system.LanguageBundle;

/**
 * Manages the information pane of the summary tab. This is an output sheet 
 * that is displayed in the summary tab to advise the user of important 
 * stats for their character. The output sheet to be displayed is specified in 
 * the game mode miscinfo.lst file using the INFOSHEET tag.
 *    
 */
public class InfoPaneHandler implements ReferenceListener<Object>, ListListener<CharacterLevelFacade>
{

	private HtmlSheetSupport support;
	private CharacterFacade character;

	/**
	 * Create a new info pane handler instance for a character.
	 * @param character The character the pane is to display information for.
	 * @param htmlPane the pane that displays the information
	 */
	public InfoPaneHandler(CharacterFacade character, JEditorPane htmlPane)
	{
		this.character = character;
		GameModeFacade game = character.getDataSet().getGameMode();
		support = new HtmlSheetSupport(character, htmlPane, game.getInfoSheet());
		support.setMissingSheetMsg(LanguageBundle.getFormattedString("in_sumNoInfoSheet", //$NON-NLS-1$
			character.getDataSet().getGameMode().getName()));
		registerListeners();
	}

	/**
	 * Initialise our display component. Any expected UI behaviour/
	 * configuration is enforced here. Note that this is a utility function for
	 * use by SummaryInfoTab. While there is a handler for each character 
	 * displayed, there is only a single instance of each display component. 
	 * 
	 * @param htmlPane The editor panel that will display the sheet.
	 */
	public static void initializeEditorPane(JEditorPane htmlPane)
	{
		htmlPane.setOpaque(false);
		htmlPane.setEditable(false);
		htmlPane.setFocusable(false);
		htmlPane.setContentType("text/html"); //$NON-NLS-1$
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
		if (character.getRaceRef()!=null)
		{
			character.getRaceRef().addReferenceListener(this);
		}
		if (character.getGenderRef()!=null)
		{
			character.getGenderRef().addReferenceListener(this);
		}
		if (character.getAlignmentRef()!=null)
		{
			character.getAlignmentRef().addReferenceListener(this);
		}
		if (character.getCharacterLevelsFacade()!=null)
		{
			character.getCharacterLevelsFacade().addListListener(this);
		}
		if (character.getDataSet()!=null && character.getDataSet().getStats()!=null)
		{
			for (StatFacade stat : character.getDataSet().getStats())
			{
				character.getScoreBaseRef(stat).addReferenceListener(this);
			}
		}
		if (character.getHandedRef()!=null)
		{
			character.getHandedRef().addReferenceListener(this);
		}
		if (character.getAgeRef()!=null)
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

	/**
	 * @see pcgen.facade.util.event.ReferenceListener#referenceChanged(ReferenceEvent)
	 */
	@Override
	public void referenceChanged(ReferenceEvent<Object> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.facade.util.event.ListListener#elementAdded(ListEvent)
	 */
	@Override
	public void elementAdded(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.facade.util.event.ListListener#elementRemoved(ListEvent)
	 */
	@Override
	public void elementRemoved(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.facade.util.event.ListListener#elementModified(ListEvent)
	 */
	@Override
	public void elementModified(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}

	/**
	 * @see pcgen.facade.util.event.ListListener#elementsChanged(ListEvent)
	 */
	@Override
	public void elementsChanged(ListEvent<CharacterLevelFacade> e)
	{
		scheduleRefresh();
	}
}
