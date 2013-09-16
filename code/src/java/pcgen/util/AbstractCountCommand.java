/*
 * AbstractCountCommand.java
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
 * Created on 11/08/2013
 *
 * $Id$
 */
package pcgen.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.helper.Aspect;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.VariableProcessor;
import pcgen.core.character.Follower;
import pcgen.util.enumeration.Visibility;

/**
 * AbstractCountCommand is the base for the CountCommand and 
 * CountDistinctCommand. It is a container for common behavior between the 
 * two commands.
 * 
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public abstract class AbstractCountCommand extends PCGenCommand
{

	public enum JepAbilityCountEnum
	{
		CATEGORY,
		NAME,
		NATURE,
		TYPE,
		VISIBILITY,
		ASPECT,
		CAT,
		NAM,
		NAT,
		TYP,
		VIS,
		KEY
	}

	public enum JepEquipmentCountEnum
	{
		TYPE,
		WIELDCATEGORY,
		LOCATION,
		TYP,
		WDC,
		LOC
	}

	public enum JepCountEnum
		{
			ABILITIES
				{
					// Abilities are PObjects, we can implement the filterSet directly
					// i.e. without using PObject proxies.
	
					public Map<Nature, Set<Ability>> abdata;
					private List<String> assocFilter = new ArrayList<String>();
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						abdata = pc.getAbilitiesSet();
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
	
						final Object[] par = validateParams(params);
	
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
	
						//return (double) doFilterP(pt).size();
						final Set<? extends CDOMObject> filtered = doFilterP(pt);
						return countData(filtered, pc);
					}
	
					protected Object countData(final Iterable<? extends CDOMObject> filtered,
							PlayerCharacter pc)
					{
						double accum = 0;
						
						for (final CDOMObject ab : filtered)
						{
							if (assocFilter.isEmpty())
							{
								final double ac = pc.getSelectCorrectedAssociationCount(ab);
								accum += 1.01 >= ac ? 1 : ac;
							}
							else
							{
								for (String assoc : pc.getAssociationList(ab))
								{
									if (assocFilter.contains(assoc))
									{
										accum++;
									}
								}
							}
						}
						return accum;
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c)
					{
						final String[] keyValue = c.split("=");
						final JepAbilityCountEnum en;
	
						try
						{
							en = JepAbilityCountEnum.valueOf(keyValue[0]);
						}
						catch (IllegalArgumentException ex)
						{
							Logging.errorPrint(
								"Bad parameter to count(\"Ability\"), " + c);
							return new HashSet<CDOMObject>();
						}
	
						Set<Ability> cs = null;
						Ability a;
						final Iterator<? extends CDOMObject> abIt;
	
						switch (en)
						{
							case CATEGORY:
							case CAT:
								final String cat = keyValue[1];
								cs = new HashSet<Ability>(abdata.get(Nature.ANY));
	
								abIt = cs.iterator();
	
								while (abIt.hasNext())
								{
									a = (Ability) abIt.next();
									if (!a.getCategory().equalsIgnoreCase(cat))
									{
										abIt.remove();
									}
								}
								break;
	
							case NAME:
							case NAM:
								cs = filterAbilitiesByName(keyValue);
								break;
	
							case KEY:
								cs = filterAbilitiesByKeyName(keyValue);
								break;
	
							case NATURE:
							case NAT:
								Nature n;
								try
								{
									n = Nature.valueOf(keyValue[1]);
								}
								catch (IllegalArgumentException ex)
								{
									Logging.errorPrint(
										"Bad parameter to count(\"Ability\"), no such NATURE "
											+ c);
									n = Nature.ANY;
								}
								cs = new HashSet<Ability>(abdata.get(n));
								break;
	
							case TYPE:
							case TYP:
								cs = new HashSet<Ability>(abdata.get(Nature.ANY));
								abIt = cs.iterator();
	
								filterPObjectByType(abIt, keyValue[1]);
								break;
	
							case VISIBILITY:
							case VIS:
								cs = new HashSet<Ability>(abdata.get(Nature.ANY));
	
								try
								{
									final Visibility vi = Visibility.valueOf(keyValue[1]);
									abIt = cs.iterator();
	
									while (abIt.hasNext())
									{
										a = (Ability) abIt.next();
										if (!a.getSafe(ObjectKey.VISIBILITY).equals(vi))
										{
											abIt.remove();
										}
									}
								}
								catch (IllegalArgumentException ex)
								{
									Logging.errorPrint(
										"Bad parameter to count(\"Ability\"), no such Visibility "
											+ keyValue[1]);
								}
								break;
	
							case ASPECT:
								cs = filterAbilitiesByAspect(keyValue);
								break;
						}
	
						return cs;
					}
	
					/**
					 * Filter the abilities by their display name.
					 * @param keyValue The count parameters. The 2nd entry is the name to be matched.
					 * @return The set of abilities matching the name.
					 */
					private Set<Ability> filterAbilitiesByName(final String[] keyValue)
					{
						Set<Ability> cs;
						final Iterator<? extends CDOMObject> abIt;
						String targetName =
								AbilityUtilities.getUndecoratedName(keyValue[1],
									assocFilter);
						cs = new HashSet<Ability>(abdata.get(Nature.ANY));
						abIt = cs.iterator();
	
						while (abIt.hasNext())
						{
							final Ability ab = (Ability) abIt.next();
	
							final String name = (ab.getSafe(ObjectKey.MULTIPLE_ALLOWED)) ?
								AbilityUtilities.getUndecoratedName(
									ab.getDisplayName(),
									new ArrayList<String>()) :
								ab.getDisplayName();
	
							if (!name.equalsIgnoreCase(targetName))
							{
								abIt.remove();
							}
						}
						return cs;
					}
	
					/**
					 * Filter the abilities by their key name.
					 * @param keyValue The count parameters. The 2nd entry is the key to be matched.
					 * @return The set of abilities matching the key.
					 */
					private Set<Ability> filterAbilitiesByKeyName(final String[] keyValue)
					{
						Set<Ability> cs;
						final Iterator<? extends CDOMObject> abIt;
						String targetKey =
								AbilityUtilities.getUndecoratedName(keyValue[1],
									assocFilter);
						cs = new HashSet<Ability>(abdata.get(Nature.ANY));
						abIt = cs.iterator();
	
						while (abIt.hasNext())
						{
							final Ability ab = (Ability) abIt.next();
	
							final String name = ab.getKeyName();
	
							if (!name.equalsIgnoreCase(targetKey))
							{
								abIt.remove();
							}
						}
						return cs;
					}
	
					/**
					 * Filter the abilities by aspect.
					 * @param keyValue The count parameters. The 2nd entry is the aspect to be matched.
					 * @return The set of abilities matching the key.
					 */
					private Set<Ability> filterAbilitiesByAspect(final String[] keyValue)
					{
						Set<Ability> cs;
						final Iterator<? extends CDOMObject> abIt;
						cs = new HashSet<Ability>(abdata.get(Nature.ANY));
						abIt = cs.iterator();
	
						while (abIt.hasNext())
						{
							final Ability ab = (Ability) abIt.next();
	
							List<Aspect> target = ab.get(MapKey.ASPECT, AspectName.getConstant(keyValue[1]));
							if (target == null)
							{
								abIt.remove();
							}
						}
						return cs;
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
							"Ability is a PObject, should be calling filterSetP");
					}
				},
	
			CLASSES
				{
					// Classes are PObjects, we can implement the filterSet directly
					// i.e. without using PObject proxies.
	
					public Set<PCClass> objdata;
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<PCClass>();
						objdata.addAll(pc.getDisplay().getClassSet());
					}
	
					@Override
					public Object count(
						final PlayerCharacter pc, final Object[] params) throws
						ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
						final Set<? extends CDOMObject> filtered = doFilterP(pt);
	
						return (double) filtered.size();
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
							"PCClass is a PObject, should be calling filterSetP");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						final String[] keyValue = c.split("=");
	
						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
								"Bad parameter to count(\"CLASSES\" ... )" + c);
						}
	
						final Set<PCClass> cs = new HashSet<PCClass>(objdata);
						final Iterator<? extends CDOMObject> it = cs.iterator();
	
						filterPObjectByType(it, keyValue[1]);
	
						return cs;
					}
				},
	
			DOMAINS
				{
					// Domains are not PObjects
	
					public PlayerCharacter pc;
	
					@Override
					protected void getData(final PlayerCharacter aPc)
					{
						this.pc = aPc;
					}
	
					@Override
					public Object count(final PlayerCharacter aPc,
					                    final Object[] params) throws ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(aPc);
						final Set<String> filtered = doFilterS(pt);
	
						return (double) filtered.size();
					}
	
					@Override
					protected Set<String> filterSetS(final String kv) throws
						ParseException
					{
						final String[] keyValue = kv.split("=");
	
						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
								"Bad parameter to count(\"" + this + "\" ... )" + kv);
						}
	
						final Set<String> pSet = new HashSet<String>();
	
						// Hack (should allow various values, but PCGen can only do PCClass)
						if (keyValue[1].equals("PCClass"))
						{
							// at this point we have a set of character domains
							// which meet the
							// selection criteria of this leaf node of the parameter tree.
							// we now convert this to a set of Strings so that the generic doFilterS
							// can perform set operations on them
							for (Domain d : pc.getDisplay().getDomainSet())
							{
								ClassSource source = pc.getDomainSource(d);
								pSet.add(source.getPcclass().getKeyName());
							}
						}
	
						return pSet;
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						throw new ParseException(
							"CharacterDomain is not a PObject, should be calling filterSetS");
					}
				},
	
			EQUIPMENT
				{
					// Equipment is/are PObjects, we can implement the filterSet directly
	
					public Set<Equipment> objdata;
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<Equipment>();
						objdata.addAll(pc.getEquipmentListInOutputOrder());
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
						final Set<? extends CDOMObject> filtered = doFilterP(pt);
	
						return (double) filtered.size();
					}
	
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
							"Equipment is a PObject, should be calling filterSetP");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						final String[] keyValue = c.split("=");
	
						final JepEquipmentCountEnum en;
	
						try
						{
							en = JepEquipmentCountEnum.valueOf(keyValue[0]);
						}
						catch (IllegalArgumentException ex)
						{
							Logging.errorPrint(
								"Bad parameter to count(\"Equipment\"), " + c);
							return new HashSet<CDOMObject>();
						}
	
						final Set<Equipment> cs = new HashSet<Equipment>(objdata);
						final Iterator<? extends CDOMObject> it = cs.iterator();
	
						switch (en)
						{
							case TYPE:
								filterPObjectByType(it, keyValue[1]);
								break;
	
							case WIELDCATEGORY:
								while (it.hasNext())
								{
									final Equipment e = (Equipment) it.next();
									if (!e.getWieldName().equalsIgnoreCase(keyValue[1]))
									{
										it.remove();
									}
								}
								break;
	
							// TODO have no idea how to get a suitable list of equipment
							// and test for this.
	
							case LOCATION:
								if ("CARRIED".equalsIgnoreCase(keyValue[1])
									|| "Equipped".equalsIgnoreCase(keyValue[1]))
								{
	//						while (it.hasNext())
	//						{
	//							Equipment e = (Equipment) it.next();
	//							if (! e.getParent().equalsIgnoreCase(keyValue[1]));
	//							{
	//								it.remove();
	//							}
	//						}
								}
							case LOC:
								break;
							case TYP:
								break;
							case WDC:
								break;
						}
	
						return cs;
					}
				},
	
			FOLLOWERS
				{
					// Followers are not PObjects.
	
					public Map<Follower, String> objdata;
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						for (final Follower f : pc.getDisplay().getFollowerList())
						{
	
							// map each follower to an empty string. Each of these
							// Strings is unique and is mapped to exactly one Follower.
	
							objdata.put(f, "");
						}
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
						final Set<String> filtered = doFilterS(pt);
	
						return countDataS(filtered);
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						throw new ParseException(
							"Follower is not a PObject, should be calling filterSetS");
					}
	
					// If we need to be able to filter out any of these followers,
					// this is where it should be done.
					@Override
					protected Set<String> filterSetS(final String c)
					{
						return (Set<String>) objdata.values();
					}
				},
	
			LANGUAGES
				{
					// Languages are PObjects, we can implement the filterSet directly
					// i.e. without using PObject proxies.
	
					public Set<Language> objdata;
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<Language>();
						objdata.addAll(pc.getDisplay().getLanguageSet());
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
						final Set<? extends CDOMObject> filtered = doFilterP(pt);
	
						return (double) filtered.size();
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						final String[] keyValue = c.split("=");
	
						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
								MessageFormat.format(
									"Bad parameter to count(\"CLASSES\" ... ){0}",
									c));
						}
	
						final Set<Language> cs = new HashSet<Language>(objdata);
						final Iterator<? extends CDOMObject> it = cs.iterator();
	
						filterPObjectByType(it, keyValue[1]);
	
						return cs;
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
							"Language is a PObject, should be calling filterSetP");
					}
				},
	
			RACESUBTYPES
				{
					// RaceSubTypes are not PObjects
	
					public Set<RaceSubType> objdata = new HashSet<RaceSubType>();
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						objdata.addAll(pc.getDisplay().getRacialSubTypes());
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
						final Set<String> filtered = doFilterS(pt);
	
						return (double) filtered.size();
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						throw new ParseException(
							"RaceSubType is not a PObject, should be calling filterSetS");
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						final String[] keyValue = c.split("=");
	
						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
								MessageFormat.format(
									"Bad parameter to count(\"CLASSES\" ... ){0}",
									c));
						}
	
						final Set<String> rSet = new HashSet<String>();
						for (RaceSubType rst : objdata)
						{
							rSet.add(rst.toString());
						}
	
						// If we want all then we don't need to filter.
						if (!"ALL".equalsIgnoreCase(keyValue[1]))
						{
	
							final Iterator<String> pcRaceSubTypeIterator =
								rSet.iterator();
	
							// Make a List of all the types that each RaceSubType should match
							final Collection<String> typeList = new ArrayList<String>();
							Collections.addAll(typeList, keyValue[1].split("\\."));
	
							// These nested loops remove all raceSubTypes from rSet that do not
							// match all of the types in typeList
							while (pcRaceSubTypeIterator.hasNext())
							{
								final String pcRaceSubType = pcRaceSubTypeIterator.next();
	
								for (final String aType : typeList)
								{
									if (!pcRaceSubType.equals(aType))
									{
										pcRaceSubTypeIterator.remove();
										break;
									}
								}
							}
						}
	
						return rSet;
					}
				},
	
			SKILLS
				{
					// Skill is a PObject
					public Set<Skill> objdata;
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<Skill>();
						pc.refreshSkillList();
						objdata.addAll(pc.getDisplay().getSkillSet());
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);
	
						getData(pc);
						final Set<? extends CDOMObject> filtered = doFilterP(pt);
	
						return (double) filtered.size();
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
							"Skill is a PObject, should be calling filterSetP");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						final String[] keyValue = c.split("=");
	
						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
								"Bad parameter to count(\"CLASSES\" ... )" + c);
						}
	
						final Set<Skill> cs = new HashSet<Skill>(objdata);
						final Iterator<? extends CDOMObject> it = cs.iterator();
	
						filterPObjectByType(it, keyValue[1]);
	
						return cs;
					}
				},
	
			SPELLBOOKS
				{
	
					/**
					 * Count a character's Spell Books.
					 *
					 * @param pc The character being counted.
					 * @param params The parameters determining which spell books get counted.
					 * @return A Double with the number of matching spell books.
					 * @throws ParseException If any invalid parameters are encountered.
					 */
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						return pc.getDisplay().getSpellBookCount();
					}
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c)
					{
						return new HashSet<CDOMObject>();
					}
					@Override
					protected Set<String> filterSetS(final String c)
					{
						return new HashSet<String>();
					}
				},
	
			SPELLS
				{
					@Override
					protected void getData(final PlayerCharacter pc)
					{
					}
	
					@Override
					public Object count(
						final PlayerCharacter pc, final Object[] params) throws
						ParseException
					{
						return Double.valueOf("0.0");
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException("Not implemented yet");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						throw new ParseException("Not implemented yet");
					}
				},
	
			SPELLSINBOOK
				{
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						return Double.valueOf("0.0");
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException("Not implemented yet");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						throw new ParseException("Not implemented yet");
					}
				},
	
			SPELLSKNOWN
				{
					@Override
					protected void getData(final PlayerCharacter pc)
					{
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						return Double.valueOf("0.0");
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException("Not implemented yet");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						throw new ParseException("Not implemented yet");
					}
				},
	
			TEMPLATES
				{
					public Set<PCTemplate> objdata = new HashSet<PCTemplate>();
	
					@Override
					protected void getData(final PlayerCharacter pc)
					{
						objdata.addAll(pc.getDisplay().getTemplateSet());
					}
	
					@Override
					public Object count(final PlayerCharacter pc,
					                    final Object[] params) throws ParseException
					{
						return Double.valueOf("0.0");
					}
	
					@Override
					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
							"PCTemplate is a PObject, should be calling filterSetP");
					}
	
					@Override
					protected Set<? extends CDOMObject> filterSetP(final String c) throws
						ParseException
					{
						final String[] keyValue = c.split("=");
	
						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
								"Bad parameter to count(\"CLASSES\" ... )" + c);
						}
	
						final Set<PCTemplate> cs = new HashSet<PCTemplate>(objdata);
						final Iterator<? extends CDOMObject> it = cs.iterator();
	
						filterPObjectByType(it, keyValue[1]);
						return cs;
					}
	
				};
	
			public static String CountType = "";
	
			protected static ParameterTree convertParams(final Object[] params)
			{
				ParameterTree pt = null;
	
				for (final Object param : params)
				{
					try
					{
						if (pt == null)
						{
							pt = ParameterTree.makeTree((String) param);
						}
						else
						{
							final ParameterTree npt =
								ParameterTree.makeTree(ParameterTree.andString);
							npt.setLeftTree(pt);
							pt = npt;
							final ParameterTree npt1 = ParameterTree.makeTree((String) param);
							pt.setRightTree(npt1);
						}
					}
					catch (ParseException pe)
					{
						Logging.errorPrint(
							MessageFormat.format(
								"Malformed parameter to count {0}", param), pe);
					}
				}
				return pt;
			}
	
			protected static void filterPObjectByType(final Iterator<? extends CDOMObject> it, final String tString)
			{
				// If we want all then we don't need to filter.
				if (!"ALL".equalsIgnoreCase(tString))
				{
	
					// Make a List of all the types that each PObject should match
					final Collection<String> typeList = new ArrayList<String>();
					Collections.addAll(typeList, tString.split("\\."));
	
					// These nested loops remove all PObjects from the collection being
					// iterated that do not match all of the types in typeList
					while (it.hasNext())
					{
						final CDOMObject pObj = it.next();
	
						for (final String type : typeList)
						{
							if (!pObj.isType(type))
							{
								it.remove();
								break;
							}
						}
					}
				}
			}
	
	
			// By adding this it means that we can call count with just the object to be
			// counted and get a count of all e.g. count("ABILITIES") will return a
			// count of all abilities with no filtering at all.
			protected Object[] validateParams(final Object[] params) throws ParseException
			{
				Object[] p = new Object[1];
				if (1 > params.length)
				{
					p[0] = "TYPE=ALL";
				}
				else
				{
					p = params;
				}
				return p;
			}
	
			public abstract Object count(PlayerCharacter pc, Object[] params) throws
				ParseException;
	
			public Object countP(final PlayerCharacter pc, final Object[] params) throws
				ParseException
			{
				final Object[] par = validateParams(params);
				final ParameterTree pt = convertParams(par);
	
				getData(pc);
				final Set<? extends CDOMObject> filtered = doFilterP(pt);
	
				return (double) filtered.size();
			}
	
			//@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
			protected Set<? extends CDOMObject> doFilterP(final ParameterTree pt) throws ParseException
			{
				final String c = pt.getContents();
	
				if (c.equalsIgnoreCase(ParameterTree.orString) || c.equalsIgnoreCase(
					ParameterTree.andString))
				{
					final Set<CDOMObject> a = new HashSet<CDOMObject>(doFilterP(pt.getLeftTree()));
					final Set<? extends CDOMObject> b = doFilterP(pt.getRightTree());
					if (c.equalsIgnoreCase(ParameterTree.orString))
					{
						a.addAll(b);
					}
					else
					{
						a.retainAll(b);
					}
					return a;
				}
	
				return filterSetP(c);
			}
	
			public Object countS(final PlayerCharacter pc, final Object[] params) throws
				ParseException
			{
				final Object[] par = validateParams(params);
				final ParameterTree pt = convertParams(par);
	
				getData(pc);
				final Set<String> filtered = doFilterS(pt);
	
				return countDataS(filtered);
			}
	
			protected Object countDataS(final Collection<String> filtered)
			{
				return (double) filtered.size();
			}
	
			//@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
			protected Set<String> doFilterS(final ParameterTree pt) throws ParseException
			{
				final String c = pt.getContents();
	
				if (c.equalsIgnoreCase(ParameterTree.orString) || c.equalsIgnoreCase(
					ParameterTree.andString))
				{
					final Set<String> a = doFilterS(pt.getLeftTree());
					final Set<String> b = doFilterS(pt.getRightTree());
					if (c.equalsIgnoreCase(ParameterTree.orString))
					{
						a.addAll(b);
					}
					else
					{
						a.retainAll(b);
					}
					return a;
				}
	
				return filterSetS(c);
			}
	
	
			protected abstract void getData(PlayerCharacter pc);
	
			protected abstract Set<? extends CDOMObject> filterSetP(String c) throws ParseException;
	
			protected abstract Set<String> filterSetS(String c) throws ParseException;
		}

	/**
	 * Get the PC that will be used to do the counting.
	 *
	 * @return the pc
	 */
	protected PlayerCharacter getPC()
	{
		PlayerCharacter pc = null;
		if (parent instanceof VariableProcessor)
		{
			pc = ((VariableProcessor) parent).getPc();
		}
		else
		if (parent instanceof PlayerCharacter)
		{
			pc = (PlayerCharacter) parent;
		}
		return pc;
	}

	/**
	 * pop maxParam parameters off the stack and populate the array.  Note, this method
	 * leaves one parameter on the stack
	 *
	 * @param inStack  the stack of Objects
	 * @param maxParam number of entries to pop from the stack
	 *
	 * @return an array of Objects in reverse order, i.e. the last param popped is element
	 *         0 of the array.
	 */
	protected Object[] paramStackToArray(final Stack inStack, final int maxParam)
	{
		final Object[] par = new Object[maxParam];
	
		if (0 < maxParam)
		{
			for (int i = maxParam - 1; 0 <= i; i--)
			{
				par[i] = inStack.pop();
			}
		}
	
		return par;
	}

}
