/*
 * SpellSupportFacade.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Sep 22, 2011, 12:12:43 AM
 */
package pcgen.facade.core;

import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;

/**
 *
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public interface SpellSupportFacade
{

	public ListFacade<? extends SuperNode> getAvailableSpellNodes();

	/**
	 * The returned list will be used to build the tree for all known spells. This includes
	 * known spells, innate spells, etc...
	 * @return a list containing the nodes for all known spells
	 */
	public ListFacade<? extends SuperNode> getAllKnownSpellNodes();

	public ListFacade<? extends SuperNode> getKnownSpellNodes();

	public ListFacade<? extends SuperNode> getPreparedSpellNodes();

	public ListFacade<? extends SuperNode> getBookSpellNodes();

	/**
	 * Adds the spell identified by the {@code SpellNode} to the list of known spells for
	 * the character associated with this {@code SpellSupportFacade}. The nodes that are used
	 * as a parameter will originate from the available spell nodes list and implementors should
	 * not reuse this node in other lists. The node serves as an identifier for the spell not as a 
	 * literal object to add to other lists. Thus, this method should make a copy or otherwise make
	 * a new node based upon the parameter node and add that new node to other lists.
	 * @param spell the spell to add
	 */
	public void addKnownSpell(SpellNode spell);

	/**
	 * Removes the spell identified by the {@code SpellNode} from the list of known spells/
	 * all known spells.
	 * @param spell the spell to remove
	 */
	public void removeKnownSpell(SpellNode spell);

	/**
	 * Add a spell to a list of prepared spells.
	 * @param spell The spell to be added.
	 * @param spellList The list add the spell to.
	 * @param useMetamagic Should the user be asked for metamagic feats to add to the spell.
	 */
	public void addPreparedSpell(SpellNode spell, String spellList, boolean useMetamagic);

	public void removePreparedSpell(SpellNode spell, String spellList);

	/**
	 * Creates a new spell list and updates the prepared spell node list
	 * @param spellList the name of the new spell list
	 */
	public void addSpellList(String spellList);

	/**
	 * Deletes a spell list and all spells associated with that spell list from the prepared spell
	 * node list.
	 * @param spellList the name of the spell list to delete
	 */
	public void removeSpellList(String spellList);

	public void addToSpellBook(SpellNode node, String spellBook);

	public void removeFromSpellBook(SpellNode node, String spellBook);

	/**
	 * Returns an html info string containing the spell caster info for a given class.
	 * @param spellcaster a spell caster class
	 * @return an HTML string
	 */
	public String getClassInfo(ClassFacade spellcaster);

	/** 
	 * Refresh the available and known spells list in response to an action 
	 * such as levelling up. 
	 */
	public void refreshAvailableKnownSpells();

	/**
	 * Whether we should add auto known spells at level up.
	 * @return true if auto known spells should be added, false for manual management.
	 */
	public boolean isAutoSpells();

	/**
	 * Whether we should add auto known spells at level up.
	 * 
	 * @param autoSpells The new value for auto known spells.
	 */
	public void setAutoSpells(boolean autoSpells);
	
	/**
	 * Determine whether higher level known spell slots can be used for lower
	 * level spells, or if known spells are restricted to their own level only.
	 * 
	 * @return Returns the useHigherKnownSlots.
	 */
	public boolean isUseHigherKnownSlots();

	
	/**
	 * Set whether higher level known spell slots can be used for lower
	 * level spells, or if known spells are restricted to their own level only.
	 * 
	 * @param useHigher The new useHigherKnownSlots value.
	 */
	public void setUseHigherKnownSlots(boolean useHigher);

	/**
	 * Determine whether higher level prepared spell slots can be used for lower
	 * level spells, or if prepared spells are restricted to their own level only.
	 * 
	 * @return Returns the useHigherPreppedSlots.
	 */
	public boolean isUseHigherPreppedSlots();

	/**
	 * Set whether higher level prepared spell slots can be used for lower
	 * level spells, or if prepared spells are restricted to their own level only.
	 * 
	 * @param useHigher The new useHigherPreppedSlots value.
	 */
	public void setUseHigherPreppedSlots(boolean useHigher);

	
	// -------------------------- Interfaces ----------------------------------------
	
	public static interface SuperNode
	{
	}

	public static interface RootNode extends SuperNode
	{
		public String getName();
	}

	public static interface SpellListNode extends RootNode
	{
	}

	public static interface SpellBookNode extends RootNode
	{
	}

	public static interface SpellNode extends SuperNode
	{

		public ClassFacade getSpellcastingClass();

		public String getSpellLevel();

		public SpellFacade getSpell();

		/**
		 * Returns the name of the root of this node's tree. The returned string may be null
		 * such as for available spells which use the spellcasting class as the root node instead.
		 * In the case of prepared spells this root node may be the spell list name and for spellbooks
		 * the root is the spell book name etc...
		 * @return the root node string
		 */
		public RootNode getRootNode();
		
		/**
		 * @return The number of occurrences of the spell that are held.
		 */
		public int getCount();
		
		/**
		 * Adjust the number of occurrences held of the spell.
		 * @param num The number of occurrences to add.
		 */
		public void addCount(int num);

	}

	/**
	 * Show the current character's spells in the browser based on the selected spell sheet.
	 */
	public void previewSpells();

	/**
	 * Export the current character's spells to a file based on the selected spell sheet.
	 */
	public void exportSpells();

	/**
	 * @return the list of spell books
	 */
	public ListFacade<String> getSpellbooks();

	/**
	 * @return the defaultSpellBook The name of the spell book to hold any new known spells.
	 */
	public DefaultReferenceFacade<String> getDefaultSpellBookRef();

	/**
	 * Set the spell book to hold any new known spells.
	 * @param bookName The name of the new default spell book.
	 */
	public void setDefaultSpellBook(String bookName);

}
