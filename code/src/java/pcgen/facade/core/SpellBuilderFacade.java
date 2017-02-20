/*
 * SpellBuilderFacade.java
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
 *
 *
 */
package pcgen.facade.core;

import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.ListFacade;


/**
 * SpellBuilderFacade defines the interface between the UI and the core to be 
 * used for selection of spells. 
 * 
 * 
 */
public interface SpellBuilderFacade
{
	public void setClass(InfoFacade classFacade);

	public ReferenceFacade<InfoFacade> getClassRef();

	/**
	 * @return The list of possible casting classes and domains.
	 */
	public ListFacade<InfoFacade> getClasses();

	
	public void setSpellLevel(Integer spellLevel);

	public ReferenceFacade<Integer> getSpellLevelRef();

	/**
	 * @return The list of valid spell levels for this choice.
	 */
	public ListFacade<Integer> getLevels();

	
	public void setSpell(InfoFacade spell);

	public ReferenceFacade<InfoFacade> getSpellRef();

	/**
	 * @return The list of spells the user can select from.
	 */
	public ListFacade<InfoFacade> getSpells();

	
	public void setVariant(String variant);

	public ReferenceFacade<String> getVariantRef();

	/**
	 * @return The list of variants (if any) of the spell.
	 */
	public ListFacade<String> getVariants();

	
	public void setCasterLevel(Integer casterLevel);

	public ReferenceFacade<Integer> getCasterLevelRef();

	/**
	 * @return The list of valid caster levels for the spell and class 
	 * combination.
	 */
	public ListFacade<Integer> getCasterLevels();

	
	public void setSpellType(String spellType);

	public ReferenceFacade<String> getSpellTypeRef();

	/**
	 * @return The list of valid spell types for the current class or domain.
	 */
	public ListFacade<String> getSpellTypes();


	/**
	 * @return The metamagic feats that the user has selected.
	 */
	public ListFacade<AbilityFacade> getSelectedMetamagicFeats();

	/**
	 * Set a new set of metamagic feats.
	 * @param newFeats The metamagic feats that the user has selected.
	 */
	public void setSelectedMetamagicFeats(Object[] newFeats);

	/**
	 * @return The list of metamagic feats that can be applied to spells.
	 */
	public ListFacade<AbilityFacade> getAvailMetamagicFeats();

}
