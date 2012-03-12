/**
 * SpellSupportFacadeImpl.java
 * Copyright James Dempsey, 2011
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
 * Created on 15/10/2011 4:20:41 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SpellProhibitor;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.EquipmentListFacade.EquipmentListEvent;
import pcgen.core.facade.EquipmentListFacade.EquipmentListListener;
import pcgen.core.facade.TodoFacade.CharacterTab;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.SpellFacade;
import pcgen.core.facade.SpellSupportFacade;
import pcgen.core.facade.UIDelegate;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.Utility;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.FOPHandler;
import pcgen.util.Logging;

/**
 * The Class <code>SpellSupportFacadeImpl</code> marshals the spell data for a 
 * character for display in the user interface. It also responds to any actions 
 * by the UI layer on the character's spells.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class SpellSupportFacadeImpl implements SpellSupportFacade,
		EquipmentListListener, ListListener<EquipmentFacade>
{
	private final PlayerCharacter pc;
	private UIDelegate delegate;

	private DefaultListFacade<SpellNode> availableSpellNodes;
	private DefaultListFacade<SpellNode> allKnownSpellNodes;
	private DefaultListFacade<SpellNode> knownSpellNodes;
	private DefaultListFacade<SpellNode> preparedSpellNodes;
	private DefaultListFacade<SpellNode> bookSpellNodes;
	private List<SpellNode> preparedSpellLists;
	private List<SpellNode> spellBooks;
	private Map<String, RootNodeImpl> rootNodeMap;
	private final DataSetFacade dataSet;
	
	private DefaultListFacade<String> spellBookNames;
	private DefaultReferenceFacade<String> defaultSpellBook;
	private TodoManager todoManager;
	

	/**
	 * Create a new instance of SpellSupportFacadeImpl to manage the display and update of a 
	 * character's spells.
	 *  
	 * @param pc The character we are managing.
	 * @param delegate The delegate class for UI display.
	 * @param dataSet The current data being used.
	 * @param todoManager The user tasks tracker.
	 */
	public SpellSupportFacadeImpl(PlayerCharacter pc, UIDelegate delegate, DataSetFacade dataSet, TodoManager todoManager)
	{
		this.pc = pc;
		this.delegate = delegate;
		this.dataSet = dataSet;
		this.todoManager = todoManager;
		rootNodeMap = new HashMap<String, RootNodeImpl>();
		
		spellBookNames = new DefaultListFacade<String>();
		defaultSpellBook = new DefaultReferenceFacade<String>(pc.getSpellBookNameToAutoAddKnown());
		
		availableSpellNodes = new DefaultListFacade<SpellSupportFacade.SpellNode>();
		buildAvailableNodes();
		allKnownSpellNodes = new DefaultListFacade<SpellSupportFacade.SpellNode>();
		knownSpellNodes = new DefaultListFacade<SpellSupportFacade.SpellNode>();
		preparedSpellNodes = new DefaultListFacade<SpellSupportFacade.SpellNode>();
		bookSpellNodes = new DefaultListFacade<SpellSupportFacade.SpellNode>();
		preparedSpellLists = new ArrayList<SpellSupportFacade.SpellNode>();
		spellBooks = new ArrayList<SpellSupportFacade.SpellNode>();
		buildKnownPreparedNodes();
		
		updateSpellsTodo();
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#getAvailableSpellNodes()
	 */
	@Override
	public ListFacade<SpellNode> getAvailableSpellNodes()
	{
		return availableSpellNodes;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#getAllKnownSpellNodes()
	 */
	@Override
	public ListFacade<SpellNode> getAllKnownSpellNodes()
	{
		return allKnownSpellNodes;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#getKnownSpellNodes()
	 */
	@Override
	public ListFacade<SpellNode> getKnownSpellNodes()
	{
		return knownSpellNodes;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#getPreparedSpellNodes()
	 */
	@Override
	public ListFacade<SpellNode> getPreparedSpellNodes()
	{
		return preparedSpellNodes;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#getBookSpellNodes()
	 */
	@Override
	public ListFacade<SpellNode> getBookSpellNodes()
	{
		return bookSpellNodes;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#addKnownSpell(pcgen.core.facade.SpellSupportFacade.SpellNode)
	 */
	@Override
	public void addKnownSpell(SpellNode spell)
	{
		SpellNode node =
				addSpellToCharacter(spell, Globals.getDefaultSpellBook(), new ArrayList<Ability>());
		if (node != null)
		{
			allKnownSpellNodes.addElement(node);
			knownSpellNodes.addElement(node);
			if (!StringUtils.isEmpty(pc.getSpellBookNameToAutoAddKnown()))
			{
				addToSpellBook(node, pc.getSpellBookNameToAutoAddKnown());
			}

		}
		updateSpellsTodo();
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#removeKnownSpell(pcgen.core.facade.SpellSupportFacade.SpellNode)
	 */
	@Override
	public void removeKnownSpell(SpellNode spell)
	{
		//TODO: This should also remove the spell from books and lists
		if (removeSpellFromCharacter(spell, Globals.getDefaultSpellBook()))
		{
			allKnownSpellNodes.removeElement(spell);
			knownSpellNodes.removeElement(spell);
		}
		updateSpellsTodo();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#addPreparedSpell(pcgen.core.facade.SpellSupportFacade.SpellNode, java.lang.String)
	 */
	@Override
	public void addPreparedSpell(SpellNode spell, String spellList, boolean useMetamagic)
	{
		List<Ability> metamagicFeats = new ArrayList<Ability>();
		if (useMetamagic)
		{
			metamagicFeats = queryUserForMetamagic(spell);
			if (metamagicFeats == null)
			{
				return;
			}
		}
		SpellNode node =
			addSpellToCharacter(spell, spellList, metamagicFeats);
		if (node != null)
		{
			if (preparedSpellNodes.containsElement(node))
			{
				SpellNode spellNode = preparedSpellNodes.getElementAt(preparedSpellNodes.getIndexOfElement(node));
				spellNode.addCount(1);
				// Remove and readd to ensure the display is updated
				preparedSpellNodes.removeElement(spellNode);
				preparedSpellNodes.addElement(spellNode);
			}
			else
			{
				preparedSpellNodes.addElement(node);
			}
			// Remove dummy spelllist node from the list
			for (Iterator<SpellNode> iterator = preparedSpellNodes.iterator(); iterator.hasNext();)
			{
				SpellNode sn = iterator.next();
				if (sn.getSpell() == null && spellList.equals(sn.getRootNode().getName()))
				{
					iterator.remove();
				}
				
			}
		}
	}

	private void updateSpellsTodo()
	{
		boolean hasFree = false;
		for (PCClass aClass : pc.getClassSet())
		{
			if (pc.getSpellSupport(aClass).hasKnownList() || pc.getSpellSupport(aClass).hasKnownSpells(pc))
			{
				int highestSpellLevel = pc.getSpellSupport(aClass).getHighestLevelSpell(pc);
				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					if (pc.availableSpells(i, aClass, Globals
						.getDefaultSpellBook(), true, true))
					{
						hasFree = true;
						break;
					}
				}
			}
		}

		if (hasFree)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SpellsTab, "Known",
				"in_splTodoRemain", 120));
		}
		else
		{
			todoManager.removeTodo("in_splTodoRemain");
			
		}
	}

	/**
	 * Request the metamagic feats to be applied to a spell from the user via
	 *  a chooser.
	 *  
	 * @param spellNode The spell to have metamagic applied  
	 * @return The list of metamagic feats to be applied.
	 */
	private List<Ability> queryUserForMetamagic(SpellNode spellNode)
	{
		// get the list of metamagic feats for the PC
		List<InfoFacade> availableList =
				buildAvailableMetamagicFeatList(spellNode);
		if (availableList.isEmpty())
		{
			return Collections.emptyList();
		}
		
		String label = dataSet.getGameMode().getAddWithMetamagicMessage();
		if (StringUtils.isEmpty(label))
		{
			label = LanguageBundle.getString("InfoSpells.add.with.metamagic");
		}
		
		final ArrayList<Ability> selectedList = new ArrayList<Ability>();
		GeneralChooserFacadeBase chooserFacade = new GeneralChooserFacadeBase(label, availableList, new ArrayList<InfoFacade>(), 99)
		{
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void commit()
			{
				for (InfoFacade item : getSelectedList())
				{
					selectedList.add((Ability) item);
				}
			}
		};
		
		boolean result = delegate.showGeneralChooser(chooserFacade);
		return result ? selectedList : null;
	}

	/**
	 * Get the list of metatmagic feats that the character can use.
	 * @param spellNode The spell the feats would be applied to.
	 * @return The list of metamagic feats.
	 */
	private List<InfoFacade> buildAvailableMetamagicFeatList(SpellNode spellNode)
	{
		List<Ability> characterMetaMagicFeats = new ArrayList<Ability>();;
		List<Ability> feats =
				AbilityUtilities.getAggregateAbilitiesListForKey(
					AbilityCategory.FEAT.getKeyName(), pc);
		Globals.sortPObjectListByName(feats);

		for (Ability aFeat : feats)
		{
			if (aFeat.isType("Metamagic")) //$NON-NLS-1$
			{
				characterMetaMagicFeats.add(aFeat);
			}
		}

		if (!(spellNode.getSpell() instanceof SpellFacadeImplem))
		{
			return Collections.emptyList();
		}
		SpellFacadeImplem spell = (SpellFacadeImplem) spellNode.getSpell();
		
		List<InfoFacade> availableList = new ArrayList<InfoFacade>();
		if (Globals.hasSpellPPCost())
		{
			//
			// Does feat apply a BONUS:PPCOST to this spell, all spells, or all spells of
			// one of this spell's types? If it does, then we can possibly apply it to
			// this spell.
			//

			final String aKey = spell.getKeyName();
			List<Ability> metamagicFeats = new ArrayList<Ability>();
			for (Ability anAbility : characterMetaMagicFeats)
			{
				boolean canAdd = false;
				List<BonusObj> bonusList =
						BonusUtilities.getBonusFromList(anAbility
							.getRawBonusList(pc), "PPCOST"); //$NON-NLS-1$
				if (bonusList.size() == 0)
				{
					canAdd = true; // if doesn't modify PP COST, then allow it
				}
				else
				{
					for (BonusObj aBonus : bonusList)
					{
						final java.util.StringTokenizer aTok =
								new java.util.StringTokenizer(aBonus
									.getBonusInfo(), ","); //$NON-NLS-1$
						while (aTok.hasMoreTokens())
						{
							final String aBI = aTok.nextToken();

							if (aBI.equalsIgnoreCase(aKey)
								|| aBI.equalsIgnoreCase("ALL")) //$NON-NLS-1$
							{
								canAdd = true;
								break;
							}
							else if (aBI.startsWith("TYPE=") || aBI.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
							{
								if (spell.getSpell().isType(aBI.substring(5)))
								{
									canAdd = true;
									break;
								}
							}
						}
					}
				}
				if (!canAdd)
				{
					continue;
				}
				metamagicFeats.add(anAbility);
			}
			availableList.addAll(metamagicFeats);
		}
		else
		{
			availableList.addAll(characterMetaMagicFeats);
		}
		return availableList;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#removePreparedSpell(pcgen.core.facade.SpellSupportFacade.SpellNode, java.lang.String)
	 */
	@Override
	public void removePreparedSpell(SpellNode spell, String spellList)
	{
		if (removeSpellFromCharacter(spell, spellList))
		{
			if (spell.getCount() > 1)
			{
				spell.addCount(-1);
				// Remove and readd to ensure the display is updated
				preparedSpellNodes.removeElement(spell);
				preparedSpellNodes.addElement(spell);
			}
			else
			{
				preparedSpellNodes.removeElement(spell);
			}

			addDummyNodeIfSpellListEmpty(spellList);
		}
	}

	/**
	 * If there are no spells in the spell list, add in the spell list 
	 * placeholder node so that the list shows in the UI.
	 * 
	 * @param spellList The list to be checked.
	 */
	private void addDummyNodeIfSpellListEmpty(String spellList)
	{
		boolean spellListEmpty = true;
		for (SpellNode node : preparedSpellNodes)
		{
			if (spellList.equals(node.getRootNode().getName()))
			{
				spellListEmpty = false;
				break;
			}
		}
		if (spellListEmpty)
		{
			for (SpellNode listNode : preparedSpellLists)
			{
				if (spellList.equals(listNode.getRootNode().getName()))
				{
					preparedSpellNodes.addElement(listNode);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#addSpellList(java.lang.String)
	 */
	@Override
	public void addSpellList(String spellList)
	{
		if (StringUtils.isEmpty(spellList))
		{
			return;
		}
		
		// Prevent spellbooks being given the same name as a class
		for (PCClass current : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
		{
			if ((spellList.equals(current.getKeyName())))
			{
				JOptionPane.showMessageDialog(null, LanguageBundle
					.getString("in_spellbook_name_error"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

				return;
			}
		}

		if (pc.addSpellBook(spellList))
		{
			pc.setDirty(true);

			DummySpellNodeImpl spellListNode =
					new DummySpellNodeImpl(getRootNode(spellList));
			preparedSpellLists.add(spellListNode);
			addDummyNodeIfSpellListEmpty(spellList);
		}
		else
		{
			JOptionPane.showMessageDialog(null, LanguageBundle
				.getFormattedString(
					"InfoPreparedSpells.add.list.fail", new Object[]{spellList}), //$NON-NLS-1$
				Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

			return;
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#removeSpellList(java.lang.String)
	 */
	@Override
	public void removeSpellList(String spellList)
	{
		if (spellList.equalsIgnoreCase(Globals.getDefaultSpellBook()))
		{
			Logging.errorPrint(LanguageBundle
				.getString("InfoSpells.can.not.delete.default.spellbook")); //$NON-NLS-1$

			return;
		}

		if (pc.delSpellBook(spellList))
		{
			pc.setDirty(true);
			for (Iterator<SpellNode> iterator = preparedSpellLists.iterator(); iterator.hasNext();)
			{
				SpellNode listNode = iterator.next();
				if (spellList.equals(listNode.getRootNode().getName()))
				{
					iterator.remove();
				}
			}

			for (Iterator<SpellNode> iterator = preparedSpellNodes.iterator(); iterator.hasNext();)
			{
				SpellNode spell = iterator.next();
				if (spellList.equals(spell.getRootNode().getName()))
				{
					iterator.remove();
				}
			}
		}
		else
		{
			Logging.errorPrint("delBookButton:failed "); //$NON-NLS-1$

			return;
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#addToSpellBook(pcgen.core.facade.SpellSupportFacade.SpellNode, java.lang.String)
	 */
	@Override
	public void addToSpellBook(SpellNode spell, String spellBook)
	{
		String bookName = spellBook;
		if (bookName.endsWith("]") && bookName.contains(" ["))
		{
			bookName = bookName.substring(0, bookName.lastIndexOf(" ["));
		}
		SpellNode node =
			addSpellToCharacter(spell, bookName, new ArrayList<Ability>());
		if (node != null)
		{
			if (bookSpellNodes.containsElement(node))
			{
				SpellNode spellNode = bookSpellNodes.getElementAt(bookSpellNodes.getIndexOfElement(node));
				spellNode.addCount(1);
				// Remove and readd to ensure the display is updated
				bookSpellNodes.removeElement(spellNode);
				bookSpellNodes.addElement(spellNode);
			}
			else
			{
				bookSpellNodes.addElement(node);
			}
			// Remove dummy spellbook node from the list
			for (Iterator<SpellNode> iterator = bookSpellNodes.iterator(); iterator.hasNext();)
			{
				SpellNode sn = iterator.next();
				if (sn.getSpell() == null && bookName.equals(sn.getRootNode().getName()))
				{
					iterator.remove();
				}
				
			}
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#removeFromSpellBook(pcgen.core.facade.SpellSupportFacade.SpellNode, java.lang.String)
	 */
	@Override
	public void removeFromSpellBook(SpellNode spell, String spellBook)
	{
		if (removeSpellFromCharacter(spell, spellBook))
		{
			if (spell.getCount() > 1)
			{
				spell.addCount(-1);
				// Remove and readd to ensure the display is updated
				bookSpellNodes.removeElement(spell);
				bookSpellNodes.addElement(spell);
			}
			else
			{
				bookSpellNodes.removeElement(spell);
			}

			addDummyNodeIfSpellBookEmpty(spellBook);
		}
	}

	/**
	 * If there are no spells in the spell book, add in the spell book 
	 * placeholder node so that the book shows in the UI.
	 * 
	 * @param spellBook The book to be checked.
	 */
	private void addDummyNodeIfSpellBookEmpty(String spellBook)
	{
		boolean spellListEmpty = true;
		for (SpellNode node : bookSpellNodes)
		{
			if (spellBook.equals(node.getRootNode().getName()))
			{
				spellListEmpty = false;
				break;
			}
		}
		if (spellListEmpty)
		{
			for (SpellNode listNode : spellBooks)
			{
				if (spellBook.equals(listNode.getRootNode().getName()))
				{
					bookSpellNodes.addElement(listNode);
				}
			}
		}
	}

	@Override
	public void refreshAvailableKnownSpells() 
	{
		buildAvailableNodes();
		buildKnownPreparedNodes();
		updateSpellsTodo();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SpellSupportFacade#getClassInfo(pcgen.core.facade.ClassFacade)
	 */
	@Override
	public String getClassInfo(ClassFacade spellcaster)
	{
		if (!(spellcaster instanceof PCClass))
		{
			return "";
		}
		PCClass aClass = (PCClass) spellcaster;
		int highestSpellLevel = pc.getSpellSupport(aClass).getHighestLevelSpell(pc);

		final HtmlInfoBuilder b = new HtmlInfoBuilder();
		b.append("<table border=1><tr><td><font size=-2><b>"); //$NON-NLS-1$
		b.append(OutputNameFormatting.piString(aClass, false)).append(" ["); //$NON-NLS-1$
		b.append(String.valueOf(pc.getLevel(aClass)
			+ (int) pc.getTotalBonusTo("PCLEVEL", aClass.getKeyName()))); //$NON-NLS-1$
		b.append("]</b></font></td>"); //$NON-NLS-1$

		for (int i = 0; i <= highestSpellLevel; ++i)
		{
			b.append("<td><font size=-2><b><center>&nbsp;"); //$NON-NLS-1$
			b.append(String.valueOf(i));
			b.append("&nbsp;</b></center></font></td>"); //$NON-NLS-1$
		}

		b.append("</tr>"); //$NON-NLS-1$
		b.append("<tr><td><font size=-1><b>Cast</b></font></td>"); //$NON-NLS-1$

		for (int i = 0; i <= highestSpellLevel; ++i)
		{
			b.append("<td><font size=-1><center>"); //$NON-NLS-1$
			b.append(getNumCast(aClass, i, pc));
			b.append("</center></font></td>"); //$NON-NLS-1$
		}
		b.append("</tr>"); //$NON-NLS-1$

		// Making sure KnownList can be handled safely and produces the correct behaviour
		if (pc.getSpellSupport(aClass).hasKnownList() || pc.getSpellSupport(aClass).hasKnownSpells(pc))
		{
			b.append("<tr><td><font size=-1><b>Known</b></font></td>"); //$NON-NLS-1$

			for (int i = 0; i <= highestSpellLevel; ++i)
			{
				final int a = pc.getSpellSupport(aClass).getKnownForLevel(i, "null", pc);
				final int bonus = pc.getSpellSupport(aClass).getSpecialtyKnownForLevel(i, pc);
				StringBuffer bString = new StringBuffer();

				if (bonus > 0)
				{
					bString.append('+').append(bonus);
				}

				b.append("<td><font size=-1><center>"); //$NON-NLS-1$
				b.append(String.valueOf(a)).append(bString.toString());
				b.append("</center></font></td>"); //$NON-NLS-1$
			}
			b.append("</tr>"); //$NON-NLS-1$
		}

		b.append("<tr><td><font size=-1><b>DC</b></font></td>"); //$NON-NLS-1$

		for (int i = 0; i <= highestSpellLevel; ++i)
		{
			b.append("<td><font size=-1><center>"); //$NON-NLS-1$
			b.append(String.valueOf(getDC(aClass, i, pc)));
			b.append("</center></font></td>"); //$NON-NLS-1$
		}

		b.append("</tr></table>"); //$NON-NLS-1$

		b.appendI18nElement("InfoSpells.caster.type", aClass.getSpellType()); //$NON-NLS-1$
		b.appendLineBreak();
		b.appendI18nElement("InfoSpells.stat.bonus", aClass.getSpellBaseStat()); //$NON-NLS-1$ 

		if (pc.hasAssocs(aClass, AssociationKey.SPECIALTY) || pc.hasDomains())
		{
			boolean needComma = false;
			StringBuffer schoolInfo = new StringBuffer(); 
			String spec = pc.getAssoc(aClass, AssociationKey.SPECIALTY);
			if (spec != null)
			{
				schoolInfo.append(spec);
				needComma = true;
			}

			for (Domain d : pc.getSortedDomainSet())
			{
				if (needComma)
				{
					schoolInfo.append(',');
				}
				needComma = true;
				schoolInfo.append(d.getKeyName());
			}
			b.appendLineBreak();
			b.appendI18nElement("InfoSpells.school", schoolInfo.toString()); //$NON-NLS-1$ 
		}

		Set<String> set = new TreeSet<String>();
		for (SpellProhibitor sp : aClass
			.getSafeListFor(ListKey.PROHIBITED_SPELLS))
		{
			set.addAll(sp.getValueList());
		}

		Collection<? extends SpellProhibitor> prohibList = pc
				.getProhibitedSchools(aClass);
		if (prohibList != null)
		{
			for (SpellProhibitor sp : prohibList)
			{
				set.addAll(sp.getValueList());
			}
		}
		if (!set.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("InfoSpells.prohibited.school", //$NON-NLS-1$ 
				StringUtil.join(set, ",")); //$NON-NLS-1$ 
		}

		String bString = SourceFormat.getFormattedString(aClass,
		Globals.getSourceDisplay(), true);
		if (bString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_source", bString); //$NON-NLS-1$ 
		}

		return b.toString();
	}

	private static final String getNumCast(PCClass aClass, int level,
		PlayerCharacter pc)
	{
		String sbook = Globals.getDefaultSpellBook();
		final String cast =
				pc.getSpellSupport(aClass).getCastForLevel(level, sbook, true, false, pc)
					+ pc.getSpellSupport(aClass).getBonusCastForLevelString(level, sbook, pc);

		return cast;
	}

	private static int getDC(PCClass aClass, int level, PlayerCharacter pc)
	{
		return pc.getDC(new Spell(), aClass, level, 0, aClass);
	}
	
	/**
	 * Construct the list of available spells for the character. 
	 */
	private void buildAvailableNodes() 
	{
		// Scan character classes for spell classes
		List<PCClass> classList = getCharactersSpellcastingClasses();

		// Look at each spell on each spellcasting class
		for (PCClass pcClass : classList)
		{
			DoubleKeyMapToList<SpellFacade, String, SpellNode> existingSpells =
					buildExistingSpellMap(availableSpellNodes, pcClass);
			
			for (Spell spell : Globals.getSpellsIn(-1,
				pc.getSpellLists(pcClass), pc))
			{
				// Create SpellNodeImpl for each spell
				CharacterSpell charSpell = new CharacterSpell(pcClass, spell);
				SpellFacadeImplem spellImplem =
						new SpellFacadeImplem(pc, spell, charSpell, null);

				HashMapToList<CDOMList<Spell>, Integer> masterLevelInfo =
						pc.getMasterLevelInfo(spell);
				
				for (CDOMList<Spell> spellList : pc.getSpellLists(pcClass))
				{
					List<Integer> levels =  masterLevelInfo.getListFor(spellList);
					if (levels != null)
					{
						for (Integer level : levels)
						{
							SpellNodeImpl node =
									new SpellNodeImpl(spellImplem, pcClass,
										String.valueOf(level), null);
							if (!existingSpells.containsInList(spellImplem,
								node.getSpellLevel(), node))
							{
								// Add to list
								availableSpellNodes.addElement(node);
							}
						}
					}
				}
			}
		}	
	}

	/**
	 * Create a map of the spell nodes for a class in the supplied list. This
	 * is intended to allow quick checking of the presence of a spell in the 
	 * list.
	 * 
	 * @param spellNodeList The list of spell nodes 
	 * @param pcClass The class to filter the map by
	 * @return A double map to the class' spells from the list. 
	 */
	private DoubleKeyMapToList<SpellFacade, String, SpellNode> buildExistingSpellMap(
		DefaultListFacade<SpellNode> spellNodeList, PCClass pcClass)
	{
		DoubleKeyMapToList<SpellFacade, String, SpellNode> spellMap =
				new DoubleKeyMapToList<SpellFacade, String, SpellSupportFacade.SpellNode>();

		for (SpellNode spellNode : spellNodeList)
		{
			if (pcClass.equals(spellNode.getSpellcastingClass()))
			{
				spellMap.addToListFor(spellNode.getSpell(),
					spellNode.getSpellLevel(), spellNode);
			}
		}

		return spellMap;
	}

	/**
	 * Construct the list of spells the character knows, has prepared or has in 
	 * a spell book. 
	 */
	private void buildKnownPreparedNodes() 
	{
		// Ensure spell information is up to date
		pc.getSpellList();

		// Scan character classes for spell classes
		List<PCClass> classList = getCharactersSpellcastingClasses();
		List<PObject> pobjList = new ArrayList<PObject>(classList);
		
		// Include spells from race etc
		pobjList.add(pc.getRace());

		
		// Look at each spell on each spellcasting class
		for (PObject pcClass : pobjList)
		{
			buildKnownPreparedSpellsForCDOMObject(pcClass);
		}
		
		spellBooks.clear();
		spellBookNames.clearContents();
		for (SpellBook spellBook : pc.getSpellBooks())
		{
			if (spellBook.getType() == SpellBook.TYPE_PREPARED_LIST)
			{
				DummySpellNodeImpl spellListNode =
						new DummySpellNodeImpl(getRootNode(spellBook.getName()));
				preparedSpellLists.add(spellListNode);
				addDummyNodeIfSpellListEmpty(spellBook.getName());
			}
			else if (spellBook.getType() == SpellBook.TYPE_SPELL_BOOK)
			{
				DummySpellNodeImpl spellListNode =
						new DummySpellNodeImpl(getRootNode(spellBook.getName()));
				spellBooks.add(spellListNode);
				addDummyNodeIfSpellBookEmpty(spellBook.getName());
				spellBookNames.addElement(spellBook.getName());
			} 
		}
	}

	private void buildKnownPreparedSpellsForCDOMObject(CDOMObject pObject)
	{
		Collection<? extends CharacterSpell> sp = pc.getCharacterSpells(pObject);
		List<CharacterSpell> cSpells = new ArrayList<CharacterSpell>(sp);

		// Add in the spells granted by objects
		pc.addBonusKnownSpellsToList(pObject, cSpells);
		PCClass pcClass = (PCClass) (pObject instanceof PCClass ? pObject : null);
		
		for (CharacterSpell charSpell : cSpells)
		{
			for (SpellInfo spellInfo : charSpell.getInfoList())
			{
				// Create SpellNodeImpl for each spell
				String book = spellInfo.getBook();
				boolean isKnown = Globals.getDefaultSpellBook().equals(book);
				SpellFacadeImplem spellImplem =
						new SpellFacadeImplem(pc, charSpell.getSpell(),
							charSpell, spellInfo);
				SpellNodeImpl node;
				if (pcClass != null)
				{
					node =
							new SpellNodeImpl(spellImplem, pcClass,
								String.valueOf(spellInfo.getActualLevel()),
								getRootNode(book));
				}
				else
				{
					node =
							new SpellNodeImpl(spellImplem,
								String.valueOf(spellInfo.getActualLevel()),
								getRootNode(book));
				}
				if (spellInfo.getTimes() > 1)
				{
					node.addCount(spellInfo.getTimes() -1);
				}
				boolean isSpellBook =
						pc.getDisplay().getSpellBookByName(book).getType() == SpellBook.TYPE_SPELL_BOOK;
				// Add to list
				if (isKnown)
				{
					allKnownSpellNodes.addElement(node);
					knownSpellNodes.addElement(node);
				}
				else if (isSpellBook)
				{
					bookSpellNodes.addElement(node);
				}
				else if (pObject instanceof Race)
				{
					allKnownSpellNodes.addElement(node);
				}
				else
				{
					preparedSpellNodes.addElement(node);
				}
			}
		}
	}

	/**
	 * Add a spell to the named book for the character. The request will be 
	 * validated and any errors shown to the user by the UIDelegate.
	 * 
	 * @param spell The spell to be added.
	 * @param bookName The book to add the spell to.
	 * @param metamagicFeats List of the metamagic feats that should be applied to this spell.
	 * @return The new SpellNode, or null if the selection was invalid.
	 */
	private SpellNode addSpellToCharacter(SpellNode spell, String bookName, List<Ability> metamagicFeats)
	{
		if (!(spell.getSpell() instanceof SpellFacadeImplem))
		{
			return null;
		}
		if (spell.getSpellcastingClass() == null)
		{
			return null;
		}
		CharacterSpell charSpell =
				((SpellFacadeImplem) spell.getSpell()).getCharSpell();
		if (charSpell == null)
		{
			return null;
		}
		int level = Integer.parseInt(spell.getSpellLevel());
		for (Ability ability : metamagicFeats)
		{
			level += ability.getSafe(IntegerKey.ADD_SPELL_LEVEL);
		}

		String errorMsg =
				pc.addSpell(charSpell, metamagicFeats, spell.getSpellcastingClass()
					.getKeyName(), bookName, level, level);
		if (!StringUtils.isEmpty(errorMsg))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, errorMsg);
			return null;
		}

		SpellInfo spellInfo =
				charSpell.getSpellInfoFor(bookName, level, metamagicFeats);
		boolean isKnown = Globals.getDefaultSpellBook().equals(bookName);
		SpellFacadeImplem spellImplem =
				new SpellFacadeImplem(pc, charSpell.getSpell(), charSpell,
					spellInfo);
		SpellNodeImpl node =
				new SpellNodeImpl(spellImplem, spell.getSpellcastingClass(),
					String.valueOf(spellInfo.getActualLevel()), getRootNode(bookName));
		return node;
	}

	private RootNodeImpl getRootNode(String bookName)
	{
		if (Globals.getDefaultSpellBook().equals(bookName))
		{
			return null;
		}
		
		RootNodeImpl rootNode = rootNodeMap.get(bookName);
		if (rootNode == null)
		{
			SpellBook book = pc.getDisplay().getSpellBookByName(bookName);
			if (book == null)
			{
				return null;
			}

			rootNode = new RootNodeImpl(book);
			rootNodeMap.put(bookName, rootNode);
		}
		
		return rootNode;
	}
	/**
	 * Remove a spell from the named book for the character. The request will be 
	 * validated and any errors shown to the user by the UIDelegate.
	 * 
	 * @param spell The spell to be removed.
	 * @param bookName The book to remove the spell from.
	 * @return True if the removal worked, false if the selection was invalid.
	 */
	private boolean removeSpellFromCharacter(SpellNode spell, String bookName)
	{
		if (!(spell.getSpell() instanceof SpellFacadeImplem))
		{
			return false;
		}
		SpellFacadeImplem sfi = (SpellFacadeImplem) spell.getSpell();
		CharacterSpell charSpell = sfi.getCharSpell();
		SpellInfo spellInfo = sfi.getSpellInfo();
		if (charSpell == null || spellInfo == null)
		{
			return false;
		}

		final String errorMsg =
				pc.delSpell(spellInfo, (PCClass) spell.getSpellcastingClass(),
					bookName);

		if (errorMsg.length() > 0)
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, errorMsg);
			ShowMessageDelegate.showMessageDialog(errorMsg,
				Constants.APPLICATION_NAME, MessageType.ERROR);
			return false;
		}

		return true;
	}	
	
	private List<PCClass> getCharactersSpellcastingClasses()
	{
		List<PCClass> castingClasses = new ArrayList<PCClass>();
		Collection<PCClass> classes = pc.getClassSet();
		for (PCClass pcClass : classes)
		{
			if (pcClass.get(StringKey.SPELLTYPE) != null)
			{
				if (!pc.getSpellSupport(pcClass).zeroCastSpells() || pc.getSpellSupport(pcClass).hasKnownList())
				{
					castingClasses.add(pcClass);
				}
			}
			
		}

		return castingClasses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoSpells()
	{
		return pc.getAutoSpells();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoSpells(boolean autoSpells)
	{
		pc.setAutoSpells(autoSpells);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUseHigherKnownSlots()
	{
		return pc.getUseHigherKnownSlots();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUseHigherPreppedSlots(boolean useHigher)
	{
		pc.setUseHigherPreppedSlots(useHigher);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUseHigherPreppedSlots()
	{
		return pc.getUseHigherPreppedSlots();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUseHigherKnownSlots(boolean useHigher)
	{
		pc.setUseHigherKnownSlots(useHigher);
	}

	/**
	 * @return the list of spell books
	 */
	@Override
	public ListFacade<String> getSpellbooks()
	{
		return spellBookNames;
	}

	/**
	 * @return the defaultSpellBook The name of the spell book to hold any new known spells.
	 */
	@Override
	public DefaultReferenceFacade<String> getDefaultSpellBookRef()
	{
		return defaultSpellBook;
	}

	/**
	 * Set the spell book to hold any new known spells.
	 * @param bookName The name of the new default spell book.
	 */
	@Override
	public void setDefaultSpellBook(String bookName)
	{
		SpellBook book = pc.getDisplay().getSpellBookByName(bookName);
		if (book == null || book.getType() != SpellBook.TYPE_SPELL_BOOK)
		{
			return;
		}
		
		pc.setSpellBookNameToAutoAddKnown(bookName);
		defaultSpellBook.setReference(bookName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementAdded(ListEvent<EquipmentFacade> e)
	{
		updateSpellBooks((Equipment) e.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementRemoved(ListEvent<EquipmentFacade> e)
	{
		updateSpellBooks((Equipment) e.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementsChanged(ListEvent<EquipmentFacade> e)
	{
		updateSpellBooks((Equipment) e.getElement());
	}

	@Override
	public void elementModified(ListEvent<EquipmentFacade> e)
	{
		updateSpellBooks((Equipment) e.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void quantityChanged(EquipmentListEvent e)
	{
		updateSpellBooks((Equipment) e.getEquipment());
	}

	/**
	 * Update the stored spellbook details in response to a spellbook item of 
	 * equipment changing. Changes to equipment other than spellbooks will be 
	 * ignored. 
	 * @param equip The equipment item that changed.
	 */
	private void updateSpellBooks(Equipment equip)
	{
		if (equip == null || equip.isType("SPELLBOOK"))
		{
			buildKnownPreparedNodes();
			for (Iterator<SpellNode> iterator = bookSpellNodes.iterator(); iterator.hasNext();)
			{
				SpellNode spell = iterator.next();
				if (!spellBookNames.containsElement(spell.getRootNode().getName()))
				{
					iterator.remove();
				}
			}
		}
	}

	public class RootNodeImpl implements RootNode
	{

		private final String name;
		private final SpellBook book;

		public RootNodeImpl(String text)
		{
			this.name = text;
			this.book = null;
		}

		public RootNodeImpl(SpellBook book)
		{
			this.book = book;
			this.name = book.getName();
		}

		@Override
		public String toString()
		{
			if (book != null)
			{
				return book.toString();
			}
			return name;
		}

		@Override
		public String getName()
		{
			return name;
		}
		
		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
			return hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final RootNodeImpl other = (RootNodeImpl) obj;
			if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
			{
				return false;
			}
			return true;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void previewSpells()
	{
		boolean aBool = SettingsHandler.getPrintSpellsWithPC();
		SettingsHandler.setPrintSpellsWithPC(true);
		Utility.previewInBrowser(
			PCGenSettings.getInstance().getProperty(
				PCGenSettings.SELECTED_SPELL_SHEET_PATH), pc);
		SettingsHandler.setPrintSpellsWithPC(aBool);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportSpells()
	{
		final String template = PCGenSettings.getInstance().getProperty(
			PCGenSettings.SELECTED_SPELL_SHEET_PATH);
		if (StringUtils.isEmpty(template))
		{
			delegate.showErrorMessage("PCGen", "No spell sheet selected.");
			return;
		}
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getPcgPath());

		fcExport.setDialogTitle(LanguageBundle
			.getString("InfoSpells.export.spells.for") + pc.getDisplayName()); //$NON-NLS-1$

		if (fcExport.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final String aFileName = fcExport.getSelectedFile().getAbsolutePath();

		if (aFileName.length() < 1)
		{
			delegate.showErrorMessage("PCGen", //$NON-NLS-1$ 
				LanguageBundle.getString("InfoSpells.must.set.filename")); //$NON-NLS-1$ 
			return;
		}

		try
		{
			final File outFile = new File(aFileName);

			if (outFile.isDirectory())
			{
				delegate.showErrorMessage("PCGen", //$NON-NLS-1$ 
					LanguageBundle.getString("InfoSpells.can.not.overwrite.directory")); //$NON-NLS-1$ 
				return;
			}

			if (outFile.exists())
			{
				int reallyClose =
						JOptionPane
							.showConfirmDialog(
								null,
								LanguageBundle
									.getFormattedString(
										"InfoSpells.confirm.overwrite", outFile.getName()), //$NON-NLS-1$
								LanguageBundle
									.getFormattedString(
										"InfoSpells.overwriting", outFile.getName()), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			if (ext.equalsIgnoreCase(".htm") || ext.equalsIgnoreCase(".html")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				BufferedWriter w =
						new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(outFile), "UTF-8")); //$NON-NLS-1$
				Utility.printToWriter(w, template, pc);
			}
			else if (ext.equalsIgnoreCase(".fo") || ext.equalsIgnoreCase(".pdf")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				File tmpFile = File.createTempFile("tempSpells_", ".fo"); //$NON-NLS-1$ //$NON-NLS-2$
				BufferedWriter w =
						new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(tmpFile), "UTF-8")); //$NON-NLS-1$
				Utility.printToWriter(w, template, pc);

				pdfExport(outFile, tmpFile, null);
			}
			else if (ext.equalsIgnoreCase(".xslt") || ext.equalsIgnoreCase(".xsl")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				Logging.debugPrint("Printing using XML/XSLT");
				File tmpFile = File.createTempFile("tempSpells_", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
				BufferedWriter w =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpFile), "UTF-8")); //$NON-NLS-1$
				File baseTemplate = new File(SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator + SettingsHandler.getGame().getName() + File.separator + "base.xml");
				if(!baseTemplate.exists()) {
					baseTemplate = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "base.xml");
				}
				Utility.printToWriter(w, baseTemplate.getAbsolutePath(), pc);

				File xsltFile = new File(template);
				pdfExport(outFile, tmpFile, xsltFile);
			}
		}
		catch (Exception ex)
		{
			Logging.errorPrint(LanguageBundle.getFormattedString(
				"InfoSpells.export.failed", pc.getDisplayName()), ex); //$NON-NLS-1$
			delegate.showErrorMessage("PCGen", //$NON-NLS-1$ 
				LanguageBundle.getFormattedString(
					"InfoSpells.export.failed.retry", pc.getDisplayName())); //$NON-NLS-1$ 
		}
	}

	/**
	 * Export to PDF using the FOP PDF generator. 
	 * 
	 * @param outFile The file to place the output in.
	 * @param tmpFile The file containing the definition of the character data. May be FO or XML. 
	 * @param xsltFile An optional XSLT file for use when the tmpFile is in XML.
	 */
	private void pdfExport(final File outFile, File tmpFile, File xsltFile)
	{
		FOPHandler fh = new FOPHandler();

		// setting up pdf renderer
		fh.setMode(FOPHandler.PDF_MODE);
		if (xsltFile != null)
		{
			fh.setInputFile(tmpFile, xsltFile);
		}
		else
		{
			fh.setInputFile(tmpFile);
		}
		fh.setOutputFile(outFile);

		// render to awt
		fh.run();

		tmpFile.deleteOnExit();

		String errMessage = fh.getErrorMessage();

		if (errMessage.length() > 0)
		{
			delegate.showErrorMessage("PCGen", errMessage); //$NON-NLS-1$
		}
	}

	
	
	/**
	 * The Class <code>SpellNodeImpl</code> holds the information required to 
	 * display and process a spell. It covers spells that are available, known, 
	 * memorised etc.
	 * 
	 * @author James Dempsey <jdempsey@users.sourceforge.net>
	 */
	public class SpellNodeImpl implements SpellNode
	{

		private final SpellFacade spell;
		private final ClassFacade cls;
		private final RootNode rootNode;
		private final String level;
		private int count;

		/**
		 * Create a new instance of SpellNodeImpl for a class spell list.
		 * @param spell The spell the node represents.
		 * @param cls The character class for the spell.  
		 * @param level The level of the spell.
		 * @param rootNode The top level node this entry will appear under. 
		 */
		public SpellNodeImpl(SpellFacade spell, ClassFacade cls, String level, RootNode rootNode)
		{
			this.spell = spell;
			this.cls = cls;
			this.level = level;
			this.rootNode = rootNode;
			this.count = 1;
		}

		/**
		 * Create a new instance of SpellNodeImpl for a non class based spell list.
		 * @param spell The spell the node represents.
		 * @param level The level of the spell.
		 * @param rootNode The top level node this entry will appear under. 
		 */
		public SpellNodeImpl(SpellFacade spell, String level, RootNode rootNode)
		{
			this(spell, (ClassFacade)null, level, rootNode);
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.SpellSupportFacade.SpellNode#getSpellcastingClass()
		 */
		@Override
		public ClassFacade getSpellcastingClass()
		{
			return cls;
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.SpellSupportFacade.SpellNode#getSpellLevel()
		 */
		@Override
		public String getSpellLevel()
		{
			return level;
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.SpellSupportFacade.SpellNode#getSpell()
		 */
		@Override
		public SpellFacade getSpell()
		{
			return spell;
		}

		/* (non-Javadoc)
		 * @see pcgen.core.facade.SpellSupportFacade.SpellNode#getRootNode()
		 */
		@Override
		public RootNode getRootNode()
		{
			return rootNode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getCount()
		{
			return count;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addCount(int num)
		{
			count += num;
		}
		
		@Override
		public String toString()
		{
			String countStr = "";
			if (count != 1)
			{
				countStr = " (x" + count + ")";
			}
			if (spell != null)
			{
				return spell.toString() + countStr;
			}
			else if (cls != null)
			{
				return cls.toString() + countStr;
			}
			else if (rootNode != null)
			{
				return rootNode.toString() + countStr;
			}
			return "Empty node" + countStr;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((cls == null) ? 0 : cls.hashCode());
			result = prime * result + ((level == null) ? 0 : level.hashCode());
			result =
					prime * result
						+ ((rootNode == null) ? 0 : rootNode.hashCode());
			result = prime * result + ((spell == null) ? 0 : spell.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			SpellNodeImpl other = (SpellNodeImpl) obj;
			if (!getOuterType().equals(other.getOuterType()))
			{
				return false;
			}
			if (level == null)
			{
				if (other.level != null)
				{
					return false;
				}
			}
			else if (!level.equals(other.level))
			{
				return false;
			}
			if (spell == null)
			{
				if (other.spell != null)
				{
					return false;
				}
			}
			else if (!spell.equals(other.spell))
			{
				return false;
			}
			if (cls == null)
			{
				if (other.cls != null)
				{
					return false;
				}
			}
			else if (!cls.equals(other.cls))
			{
				return false;
			}
			if (rootNode == null)
			{
				if (other.rootNode != null)
				{
					return false;
				}
			}
			else if (!rootNode.equals(other.rootNode))
			{
				return false;
			}
			return true;
		}

		private SpellSupportFacadeImpl getOuterType()
		{
			return SpellSupportFacadeImpl.this;
		}
		
	}

	/**
	 * The Class <code>DummySpellNodeImpl</code> holds the information required to 
	 * display an empty spell list. It is only used to ensure the spell list name 
	 * is displayed.
	 * 
	 * @author James Dempsey <jdempsey@users.sourceforge.net>
	 */
	public class DummySpellNodeImpl implements SpellNode
	{
		private final RootNode rootNode;

		/**
		 * Create a new instance of DummySpellNodeImpl
		 * @param rootNode The root node for the spell list.
		 */
		public DummySpellNodeImpl(RootNode rootNode)
		{
			this.rootNode = rootNode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public ClassFacade getSpellcastingClass()
		{
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSpellLevel()
		{
			return "";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SpellFacade getSpell()
		{
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public RootNode getRootNode()
		{
			return rootNode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getCount()
		{
			return 1;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addCount(int num)
		{
			// Ignored.
		}
		
		@Override
		public String toString()
		{
			return "Empty spell list";
		}
		
	}
}
