/*
 * ClassDataParser.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 */
package pcgen.core.npcgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Parse a generator class data file.
 * 
 * @author boomer70 &lt;boomer70@yahoo.com&gt;
 * 
 */
public class ClassDataParser
{
	private SAXParser theParser;
	private GameMode theMode;
	
	/**
	 * Creates a new <tt>ClassDataParser</tt> for the specified game mode.
	 * 
	 * @param aMode The game mode to parse class options for.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public ClassDataParser(final GameMode aMode) 
		throws ParserConfigurationException, SAXException
	{
		theMode = aMode;
		
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		theParser = parserFactory.newSAXParser();
	}
	
	/**
	 * Parses a XML class data options file.
	 * 
	 * @param aFileName File to parse.
	 * 
	 * @return A <tt>List</tt> of <tt>ClassData</tt> objects representing the
	 * options in the file.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<ClassData> parse( final File aFileName ) 
		throws SAXException, IOException
	{
		final List<ClassData> ret = new ArrayList<>();
		
		try
		{
			theParser.parse(aFileName, new ClassDataHandler(theMode, ret));
		}
		catch (IllegalArgumentException ex )
		{
			// Do nothing, means we weren't the right game mode for this file.
		}
		return ret;
	}
}

/**
 * This is the parsing event handler class.  The methods in this class are
 * called by the SAX parser as it finds various elements in the XML file.
 * 
 * @author boomer70 &lt;boomer70@yahoo.com&gt;
 *
 */
class ClassDataHandler extends DefaultHandler
{
	private List<ClassData> theList;
	
	private GameMode theGameMode = null;
	private boolean theValidFlag = false;
	
	/** An enum for the current state in the state machine the parser is in */
	private enum ParserState 
	{
		/** The initial state of the parser */
		INIT,
		/** Found a class tag */
		CLASSDATA,
		/** Found stat data */ 
		STATDATA,
		/** Found skill data */
		SKILLDATA,
		/** Found Ability data */
		ABILITYDATA,
		/** Found spell data */
		SPELLDATA,
		/** Found spells of a level */
		SPELLLEVELDATA,
		/** Found subclass data */
		SUBCLASSDATA
	}
	
	private ParserState theState = ParserState.INIT;
	
	private ClassData theCurrentData = null;
	
	private AbilityCategory theCurrentCategory = null;

	private enum SpellType
	{
		/** Adding Known spells */
		KNOWN,
		/** Adding Prepared Spells */
		PREPARED
	}
	private SpellType theCurrentSpellType = SpellType.KNOWN;
	
	private int theCurrentLevel = -1;
	
	// Weight for any skills added from *
	private transient int remainingWeight = -1;
	private transient List<String> removeList = new ArrayList<>();
	
	/**
	 * Constructs the handler
	 * 
	 * @param aMode The game mode to expect the file to be for.
	 * @param aList The list of <tt>ClassData</tt> objects to fill
	 */
	public ClassDataHandler( final GameMode aMode, final List<ClassData> aList )
	{
		theGameMode = aMode;
		theList = aList;
	}
	
	/**
	 * @throws SAXException
	 * @throws IllegalArgumentException if the file being processed is not the
	 * same GameMode as requested.
	 *  
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(	final String uri, final String localName,
								final String aName, final Attributes anAttrs) 
		throws SAXException
	{
		if ( theState == ParserState.INIT && "class_data".equals(aName) ) //$NON-NLS-1$
		{
			if ( anAttrs != null )
			{
				final String gm = anAttrs.getValue("game_mode"); //$NON-NLS-1$
				if ( ! SystemCollections.getGameModeNamed(gm).equals(theGameMode) )
				{
					throw new IllegalArgumentException("Incorrect game mode"); //$NON-NLS-1$
				}
				theValidFlag = true;
			}
			return;
		}
		
		if (!theValidFlag )
		{
			throw new SAXException("NPCGen.Options.InvalidFileFormat"); //$NON-NLS-1$
		}
		
		if ( theState == ParserState.INIT )
		{
			if ( "class".equals(aName) ) //$NON-NLS-1$
			{
				if ( anAttrs != null )
				{
					final String classKey = anAttrs.getValue("key"); //$NON-NLS-1$
					final PCClass pcClass = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, classKey);
					if ( pcClass == null )
					{
						Logging.errorPrintLocalised("Exceptions.PCGenParser.ClassNotFound", classKey); //$NON-NLS-1$
					}
					else
					{
						theCurrentData = new ClassData( pcClass );
						theState = ParserState.CLASSDATA;
					}
				}
			}
		}
		else if ( theState == ParserState.CLASSDATA )
		{
			if ( "stats".equals(aName) ) //$NON-NLS-1$
			{
				theState = ParserState.STATDATA;
			}
			else if ( "skills".equals(aName) ) //$NON-NLS-1$
			{
				theState = ParserState.SKILLDATA;
			}
			else if ( "abilities".equals(aName) ) //$NON-NLS-1$
			{
				theState = ParserState.ABILITYDATA;
				theCurrentCategory = AbilityCategory.FEAT;
				if ( anAttrs != null )
				{
					final String catName = anAttrs.getValue("category"); //$NON-NLS-1$
					if ( catName != null )
					{
						theCurrentCategory = SettingsHandler.getGame().getAbilityCategory(catName);
					}
				}
			}
			else if ( "spells".equals(aName) )  //$NON-NLS-1$
			{
				theState = ParserState.SPELLDATA;
				theCurrentSpellType = SpellType.KNOWN;
				if ( anAttrs != null )
				{
					final String bookName = anAttrs.getValue("type"); //$NON-NLS-1$
					if ( bookName != null )
					{
						if ( "Prepared Spells".equals(bookName) ) //$NON-NLS-1$
						{
							theCurrentSpellType = SpellType.PREPARED;
						}
					}
				}
			}
			else if ( "subclasses".equals(aName) ) //$NON-NLS-1$
			{
				theState = ParserState.SUBCLASSDATA;
			}
		}
		else if ( theState == ParserState.STATDATA )
		{
			if ( "stat".equals(aName) ) //$NON-NLS-1$
			{
				if ( anAttrs != null )
				{
					final int weight = getWeight(anAttrs);
					final String statAbbr = anAttrs.getValue("value"); //$NON-NLS-1$
					if ( statAbbr != null )
					{
						PCStat stat = Globals.getContext().getReferenceContext()
								.silentlyGetConstructedCDOMObject(PCStat.class, statAbbr);
						theCurrentData.addStat(stat, weight);
					}
				}
			}
		}
		else if ( theState == ParserState.SKILLDATA )
		{
			if ( "skill".equals(aName) ) //$NON-NLS-1$
			{
				if ( anAttrs != null )
				{
					final int weight = getWeight(anAttrs);

					final String key = anAttrs.getValue("value"); //$NON-NLS-1$
					if ( key != null )
					{
						if ( "*".equals(key) ) //$NON-NLS-1$
						{
							remainingWeight = weight;
						}
						else if (key.startsWith("TYPE")) //$NON-NLS-1$
						{
							final List<Skill> skillsOfType = Globals.getPObjectsOfType(Globals
									.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class),
									key.substring(5));
							if (skillsOfType.isEmpty())
							{
								Logging.debugPrint("NPCGenerator: No skills of type found (" + key + ")"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						else
						{
							final Skill skill = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, key);
							if (skill == null)
							{
								Logging.debugPrint("NPCGenerator: Skill not found (" + key + ")"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						if ( weight > 0 && !key.equals("*") ) //$NON-NLS-1$
						{
							theCurrentData.addSkill(key, weight);
						}
						else
						{
							removeList.add(key);
						}
					}
				}
			}
		}
		else if ( theState == ParserState.ABILITYDATA )
		{
			if ( "ability".equals(aName) ) //$NON-NLS-1$
			{
				if ( anAttrs != null )
				{
					final int weight = getWeight(anAttrs);
					
					final String key = anAttrs.getValue("value"); //$NON-NLS-1$
					if ( key != null )
					{
						if ( "*".equals(key) ) //$NON-NLS-1$
						{
							remainingWeight = weight;
						}
						else if (key.startsWith("TYPE")) //$NON-NLS-1$
						{
							Type type = Type.getConstant(key.substring(5));
							for (final Ability ability : Globals.getContext().getReferenceContext()
									.getManufacturer(Ability.class,
											theCurrentCategory).getAllObjects())
							{
								if (!ability.containsInList(ListKey.TYPE, type))
								{
									continue;
								}
								if (ability.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
								{
									if (weight > 0)
									{
										theCurrentData.addAbility(theCurrentCategory, ability, weight);
									}
									else
									{
										// We have to remove any feats of this
										// type.
										// TODO - This is a little goofy.  We
										// already have the feat but we will 
										// store the key and reretrieve it.
										removeList.add(ability.getKeyName());
									}
								}
							}
						}
						else
						{
							final Ability ability = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
									Ability.class, theCurrentCategory, key);
							if (ability == null)
							{
								Logging.debugPrint("Ability (" + key + ") not found"); //$NON-NLS-1$ //$NON-NLS-2$
							} else 
							if (weight > 0)
							{
								theCurrentData.addAbility(theCurrentCategory, ability, weight);
							}
							else
							{
								// We have to remove any feats of this
								// type.
								// TODO - This is a little goofy.  We
								// already have the feat but we will 
								// store the key and reretrieve it.
								removeList.add(ability.getKeyName());
							}
						}
					}
				}
			}
		}
		else if ( theState == ParserState.SPELLDATA )
		{
			if ( "level".equals(aName) && anAttrs != null ) //$NON-NLS-1$
			{
				final String lvlStr = anAttrs.getValue("id"); //$NON-NLS-1$
				if ( lvlStr != null )
				{
					theCurrentLevel = Integer.parseInt( lvlStr );
					theState = ParserState.SPELLLEVELDATA;
				}
			}
		}
		else if ( theState == ParserState.SPELLLEVELDATA )
		{
			if ( "spell".equals(aName) && anAttrs != null ) //$NON-NLS-1$
			{
				final int weight = getWeight(anAttrs);

				final String key = anAttrs.getValue("name"); //$NON-NLS-1$
				if ( key != null )
				{
					if ( "*".equals(key) ) //$NON-NLS-1$
					{
						remainingWeight = weight;
					}
					else if (key.startsWith("SCHOOL")) //$NON-NLS-1$
					{
						// Not sure how to do this yet
					}
					else
					{
						final Spell spell = Globals.getContext().getReferenceContext()
								.silentlyGetConstructedCDOMObject(Spell.class, key);
						if ( spell != null )
						{
							if ( theCurrentSpellType == SpellType.KNOWN )
							{
								theCurrentData.addKnownSpell( theCurrentLevel, spell, weight );
							}
							else if ( theCurrentSpellType == SpellType.PREPARED )
							{
								theCurrentData.addPreparedSpell( theCurrentLevel, spell, weight );
							}
						}
						else
						{
							Logging.errorPrint("Spell \"" + key + "\" not found.");
						}
					}
				}
			}
		}
		else if ( theState == ParserState.SUBCLASSDATA )
		{
			if ( "subclass".equals(aName) && anAttrs != null ) //$NON-NLS-1$
			{
				final int weight = getWeight(anAttrs);
				final String key = anAttrs.getValue("value"); //$NON-NLS-1$
				theCurrentData.addSubClass(key, weight);
			}
		}
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(final String uri, final String localName, final String qName)
    {
		// If we aren't in a nested state, ignore the end tag as the 
		// start tag was obviously ignored.
		if ( theState == ParserState.INIT )
		{
			return;
		}
		if ( "skills".equals(qName) && theState == ParserState.SKILLDATA ) //$NON-NLS-1$
		{
			if (remainingWeight > 0)
			{
				// Add all remaining skills at this weight.
				for ( final Skill skill : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class) )
				{
					if (skill.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
					{
						theCurrentData.addSkill(skill.getKeyName(), remainingWeight);
					}
				}
				remainingWeight = -1;
			}
			for ( final String remove : removeList )
			{
				theCurrentData.removeSkill(remove);
			}
			removeList = new ArrayList<>();
			theState = ParserState.CLASSDATA;
		}
		else if ( "abilities".equals(qName) && theState == ParserState.ABILITYDATA ) //$NON-NLS-1$
		{
			if ( remainingWeight > 0 )
			{
				// Add all abilities at this weight.
				for (Ability ability : Globals.getContext().getReferenceContext()
						.getManufacturer(Ability.class, theCurrentCategory)
						.getAllObjects())
				{
					if ( ability.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT)
					{
						theCurrentData.addAbility(theCurrentCategory, ability, remainingWeight);
					}
				}
				remainingWeight = -1;
			}
			for ( final String remove : removeList )
			{
				Ability ability = Globals.getContext().getReferenceContext()
						.silentlyGetConstructedCDOMObject(Ability.class,
								theCurrentCategory, remove);
				theCurrentData.removeAbility(theCurrentCategory, ability);
			}
			removeList = new ArrayList<>();
			theCurrentCategory = null;
			theState = ParserState.CLASSDATA;
		}
		else if ( "class".equals(qName) && theState != ParserState.INIT  ) //$NON-NLS-1$
		{
			theList.add(theCurrentData);
			theState = ParserState.INIT;
		}
		else if ( "stats".equals(qName)) //$NON-NLS-1$
		{
			theState = ParserState.CLASSDATA;
		}
		else if ( "level".equals(qName)) //$NON-NLS-1$
		{
			if ( remainingWeight > 0 )
			{
				// Add all spells at this weight.
				final List<Spell> allSpells = getSpellsIn(theCurrentLevel,  Collections.singletonList(theCurrentData.getPCClass().get(ObjectKey.CLASS_SPELLLIST)));
				for ( final Spell spell : allSpells )
				{
					if ( theCurrentSpellType == SpellType.KNOWN )
					{
						theCurrentData.addKnownSpell( theCurrentLevel, spell, remainingWeight );
					}
					else if ( theCurrentSpellType == SpellType.PREPARED )
					{
						theCurrentData.addPreparedSpell( theCurrentLevel, spell, remainingWeight );
					}
				}
				remainingWeight = -1;
			}
			for ( final String remove : removeList )
			{
				final Spell spell = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Spell.class,  remove );
				if ( theCurrentSpellType == SpellType.KNOWN )
				{
					theCurrentData.removeKnownSpell(theCurrentLevel, spell);
				}
				else if ( theCurrentSpellType == SpellType.PREPARED )
				{
					theCurrentData.removeKnownSpell(theCurrentLevel, spell);
				}
			}
			removeList = new ArrayList<>();
			theCurrentLevel = -1;
			theState = ParserState.SPELLDATA;
		}
		else if ( "spells".equals(qName) ) //$NON-NLS-1$
		{
			theState = ParserState.CLASSDATA;
			theCurrentSpellType = SpellType.KNOWN;
		}
		else if ( "subclasses".equals(qName) ) //$NON-NLS-1$
		{
			theState = ParserState.CLASSDATA;
		}
    }

    private int getWeight( final Attributes anAttrs )
	{
		int weight = 1;
		final String wtStr = anAttrs.getValue("weight"); //$NON-NLS-1$
		if ( wtStr != null )
		{
			weight = Integer.parseInt(wtStr.trim());
		}
		return weight;
	}

	/**
	 * Returns a List of Spell with following criteria:
	 *
	 * @param level      (optional, ignored if < 0),
	 * @param spellLists the lists of spells
	 * @param pc TODO
	 * @return a List of Spell
	 */
	public static List<Spell> getSpellsIn(final int level, List<? extends CDOMList<Spell>> spellLists)
	{
		MasterListInterface masterLists = SettingsHandler.getGame().getMasterLists();
		ArrayList<CDOMReference<CDOMList<Spell>>> useLists = new ArrayList<>();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			for (CDOMList<Spell> list : spellLists)
			{
				if (ref.contains(list))
				{
					useLists.add(ref);
					break;
				}
			}
		}
		boolean allLevels = level == -1;
		Set<Spell> spellList = new HashSet<>();
		for (CDOMReference<CDOMList<Spell>> ref : useLists)
		{
			for (Spell spell : masterLists.getObjects(ref))
			{
				Collection<AssociatedPrereqObject> assoc = masterLists
						.getAssociations(ref, spell);
				for (AssociatedPrereqObject apo : assoc)
				{
					// TODO This null for source is incorrect!
					// TODO Not sure if effect of null for PC
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), (PlayerCharacter) null,
							null))
					{
						int lvl = apo
								.getAssociation(AssociationKey.SPELL_LEVEL);
						if (allLevels || level == lvl)
						{
							spellList.add(spell);
							break;
						}
					}
				}
			}
		}
		return new ArrayList<>(spellList);
	}
}
