/*
 * CountCommand.java
 * Copyright 2006 (C) James Dempsey
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
 * Created on 23/11/2006
 *
 * $Id$
 */

package plugin.jepcommands;

import org.nfunk.jep.ParseException;
import pcgen.core.*;
import pcgen.core.character.Follower;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;
import pcgen.util.ParameterTree;
import pcgen.util.enumeration.Visibility;

import java.text.MessageFormat;
import java.util.*;


/**
 * <code>CountCommand</code> deals with the count() JEP command. The first parameter will
 * be the type of object being counted and further parameters will specify the criteria.
 * <p/> Last Editor: $Author$ Last Edited: $Date: 2007-10-26 21:23:23 +0100 (Fri,
 * 26 Oct 2007) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CountCommand extends PCGenCommand
{

	public enum JepAbilityCountEnum
	{
		CATEGORY,
		NAME,
		NATURE,
		TYPE,
		VISIBILITY,
		CAT,
		NAM,
		NAT,
		TYP,
		VIS
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

					public Map<Ability.Nature, Set<Ability>> abdata;

					protected void getData(final PlayerCharacter pc)
					{
						abdata = pc.getAbilitiesSet();
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{

						final Object[] par = validateParams(params);

						final ParameterTree pt = convertParams(par);

						getData(pc);

						final Set<PObject> filtered = doFilterP(pt);

						return countData(filtered);
					}

					protected Object countData(final Iterable<PObject> filtered)
					{
						double accum = 0;

						for (final PObject ab : filtered)
						{
							final double ac = ab.getAssociatedCount();
							accum += 1.01 >= ac ? 1 : ab.getAssociatedCount();
						}
						return accum;
					}

					protected Set<PObject> filterSetP(final String c)
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
									"Bad paramter to count(\"Ability\"), " + c);
							return new HashSet<PObject>();
						}

						Set<Ability> cs = null;
						Ability a;
						final Iterator<? extends PObject> abIt;

						switch (en)
						{
							case CATEGORY:
							case CAT:
								final String cat = keyValue[1];
								cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));

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
								cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));
								abIt = cs.iterator();

								while (abIt.hasNext())
								{
									final Ability ab = (Ability) abIt.next();

									final String name = (ab.isMultiples()) ?
												AbilityUtilities.getUndecoratedName(
														ab.getDisplayName(),
														new ArrayList<String>()) :
												ab.getDisplayName();
									
									if (!name.equalsIgnoreCase(keyValue[1]))
									{
										abIt.remove();
									}
								}
								break;

							case NATURE:
							case NAT:
								Ability.Nature n;
								try
								{
									n = Ability.Nature.valueOf(keyValue[1]);
								}
								catch (IllegalArgumentException ex)
								{
									Logging.errorPrint(
											"Bad paramter to count(\"Ability\"), no such NATURE "
											+ c);
									n = Ability.Nature.ANY;
								}
								cs = new HashSet<Ability>(abdata.get(n));
								break;

							case TYPE:
							case TYP:
								cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));
								abIt = cs.iterator();

								filterPObjectByType(abIt, keyValue[1]);
								break;

							case VISIBILITY:
							case VIS:
								cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));

								try
								{
									final Visibility vi = Visibility.valueOf(keyValue[1]);
									abIt = cs.iterator();

									while (abIt.hasNext())
									{
										a = (Ability) abIt.next();
										if (!a.getVisibility().equals(vi))
										{
											abIt.remove();
										}
									}
								}
								catch (IllegalArgumentException ex)
								{
									Logging.errorPrint(
											"Bad paramter to count(\"Ability\"), no such Visibility "
											+ keyValue[1]);
								}
						}

						final Set<PObject> pSet = new HashSet<PObject>();

						for (final Ability ab : cs)
						{
							pSet.add(ab);
						}

						return pSet;
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
								"PCClass is a PObject, should be calling filterSetP");
					}
				},

		CLASSES
				{
					// Classes are PObjects, we can implement the filterSet directly
					// i.e. without using PObject proxies.

					public Set<PCClass> objdata;

					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<PCClass>();
						objdata.addAll(pc.getClassList());
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<PObject> filtered = doFilterP(pt);

						return (double) filtered.size();
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
								"PCClass is a PObject, should be calling filterSetP");
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						final String[] keyValue = c.split("=");

						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
									"Bad parameter to count(\"CLASSES\" ... )" + c);
						}

						final Set<PCClass> cs = new HashSet<PCClass>(objdata);
						final Iterator<? extends PObject> It = cs.iterator();

						filterPObjectByType(It, keyValue[1]);

						final Set<PObject> pSet = new HashSet<PObject>();

						for (final PCClass ab : cs)
						{
							pSet.add(ab);
						}

						return pSet;
					}
				},

		DOMAINS
				{
					// Domains are not PObjects

					public Map<CharacterDomain, String> objdata;

					protected void getData(final PlayerCharacter pc)
					{
						final Collection<CharacterDomain> domdata =
								new HashSet<CharacterDomain>();

						domdata.addAll(pc.getCharacterDomainList());

						for (final CharacterDomain c : domdata)
						{
							// map each domain to a String.
							// Each of these Strings is unique and is mapped to exactly
							// one Character domain.  This means we can perform set
							// operations on a collection of them and it will behave as
							// if we were doing it on the set of CharacterDomains.  We could
							// then map the Strings back to a set of CharacterDomains, but we
							// only really need to count them (and we can just as easily
							// count Strings as CharacterDomains).
							objdata.put(c, c.getObjectName());
						}
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<String> filtered = doFilterS(pt);

						return (double) filtered.size();
					}

					protected Set<String> filterSetS(final String kv) throws
																	  ParseException
					{
						final String[] keyValue = kv.split("=");

						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
									"Bad parameter to count(\"" + this + "\" ... )" + kv);
						}

						final Iterable<CharacterDomain> cs =
								new HashSet<CharacterDomain>(objdata.keySet());
						final Iterator It = cs.iterator();

						if (!"ALL".equalsIgnoreCase(keyValue[1]))
						{
							while (It.hasNext())
							{
								final CharacterDomain c = (CharacterDomain) It.next();
								if (!c.getDomainType().equalsIgnoreCase(keyValue[1]))
								{
									It.remove();
								}
							}
						}

						// at this point we have a set of character domains which meet the
						// selection criteria of this leaf node of the parameter tree.
						// we now convert this to a set of Strings so that the generic doFilterS
						// can perform set operations on them

						final Set<String> pSet = new HashSet<String>();

						//noinspection TypeMayBeWeakened
						for (final CharacterDomain d : cs)
						{
							pSet.add(objdata.get(d));
						}

						return pSet;
					}

					protected Set<PObject> filterSetP(final String c) throws
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

					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<Equipment>();
						objdata.addAll(pc.getEquipmentListInOutputOrder());
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<PObject> filtered = doFilterP(pt);

						return (double) filtered.size();
					}


					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
								"Equipment is a PObject, should be calling filterSetP");
					}

					protected Set<PObject> filterSetP(final String c) throws
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
									"Bad paramter to count(\"Equipment\"), " + c);
							return new HashSet<PObject>();
						}

						final Set<Equipment> cs = new HashSet<Equipment>(objdata);
						final Iterator<? extends PObject> It = cs.iterator();

						switch (en)
						{
							case TYPE:
								filterPObjectByType(It, keyValue[1]);
								break;

							case WIELDCATEGORY:
								while (It.hasNext())
								{
									final Equipment e = (Equipment) It.next();
									if (!e.getWieldName().equalsIgnoreCase(keyValue[1]))
									{
										It.remove();
									}
								}
								break;

								// TODO have no idea how to get a suitable list of equipement
								// and test for this.

							case LOCATION:
								if ("CARRIED".equalsIgnoreCase(keyValue[1])
									|| "Equipped".equalsIgnoreCase(keyValue[1]))
								{
//						while (It.hasNext())
//						{
//							Equipment e = (Equipment) It.next();
//							if (! e.getParent().equalsIgnoreCase(keyValue[1]));
//							{
//								It.remove();
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

						final Set<PObject> pSet = new HashSet<PObject>();

						for (final Equipment e : cs)
						{
							pSet.add(e);
						}

						return pSet;
					}
				},

		FOLLOWERS
				{
					// Followers are not PObjects.

					public Map<Follower, String> objdata;

					protected void getData(final PlayerCharacter pc)
					{
						final List<Follower> followerdata = pc.getFollowerList();

						for (final Follower f : followerdata)
						{

							// map each followoer to an empty string. Each of these
							// Strings is unique and is mapped to exactly one Follower.

							objdata.put(f, "");
						}
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<String> filtered = doFilterS(pt);

						return countDataS(filtered);
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						throw new ParseException(
								"Follower is not a PObject, should be calling filterSetS");
					}

					// If we need to be able to filter out any of these followers,
					// this is where it should be done.
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

					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<Language>();
						objdata.addAll(pc.getLanguagesList());
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<PObject> filtered = doFilterP(pt);

						return (double) filtered.size();
					}

					protected Set<PObject> filterSetP(final String c) throws
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
						final Iterator<? extends PObject> It = cs.iterator();

						filterPObjectByType(It, keyValue[1]);

						final Set<PObject> pSet = new HashSet<PObject>();

						for (final Language l : cs)
						{
							pSet.add(l);
						}

						return pSet;
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
								"Language is a PObject, should be calling filterSetP");
					}
				},

		RACESUBTYPES
				{
					// RaceSubTypes are not PObjects

					public Set<String> objdata = new HashSet<String>();

					protected void getData(final PlayerCharacter pc)
					{
						objdata.addAll(pc.getRacialSubTypes());
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<String> filtered = doFilterS(pt);

						return (double) filtered.size();
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						throw new ParseException(
								"RaceSubType is not a PObject, should be calling filterSetS");
					}

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

						final Set<String> rSet = new HashSet<String>(objdata);

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

								for (final Object aType : typeList)
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

					protected void getData(final PlayerCharacter pc)
					{
						objdata = new HashSet<Skill>();
						objdata.addAll(pc.getAllSkillList(true));
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						final Object[] par = validateParams(params);
						final ParameterTree pt = convertParams(par);

						getData(pc);
						final Set<PObject> filtered = doFilterP(pt);

						return (double) filtered.size();
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
								"Skill is a PObject, should be calling filterSetP");
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						final String[] keyValue = c.split("=");

						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
									"Bad parameter to count(\"CLASSES\" ... )" + c);
						}

						final Set<Skill> cs = new HashSet<Skill>(objdata);
						final Iterator<? extends PObject> It = cs.iterator();

						filterPObjectByType(It, keyValue[1]);

						final Set<PObject> pSet = new HashSet<PObject>();

						for (final Skill s : cs)
						{
							pSet.add(s);
						}

						return pSet;
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
					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						return (double) pc.getSpellBooks().size();
					}

					protected void getData(final PlayerCharacter pc)
					{
					}

					protected Set<PObject> filterSetP(final String c)
					{
						return new HashSet<PObject>();
					}
					protected Set<String> filterSetS(final String c)
					{
						return new HashSet<String>();
					}
				},

		SPELLS
				{
					public Set<Spell> objdata = new HashSet<Spell>();

					protected void getData(final PlayerCharacter pc)
					{
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						return Double.valueOf("0.0");
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException("Not implemented yet");
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						throw new ParseException("Not implemented yet");
					}
				},

		SPELLSINBOOK
				{
					public Set<Spell> objdata = new HashSet<Spell>();

					protected void getData(final PlayerCharacter pc)
					{
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						return Double.valueOf("0.0");
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException("Not implemented yet");
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						throw new ParseException("Not implemented yet");
					}
				},

		SPELLSKNOWN
				{
					public Set<Spell> objdata = new HashSet<Spell>();

					protected void getData(final PlayerCharacter pc)
					{
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						return Double.valueOf("0.0");
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException("Not implemented yet");
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						throw new ParseException("Not implemented yet");
					}
				},

		TEMPLATES
				{
					public Set<PCTemplate> objdata = new HashSet<PCTemplate>();

					protected void getData(final PlayerCharacter pc)
					{
						objdata.addAll(pc.getTemplateList());
					}

					public Object count(
							final PlayerCharacter pc, final Object[] params) throws
																			 ParseException
					{
						return Double.valueOf("0.0");
					}

					protected Set<String> filterSetS(final String c) throws ParseException
					{
						throw new ParseException(
								"PCTemplate is a PObject, should be calling filterSetP");
					}

					protected Set<PObject> filterSetP(final String c) throws
																	  ParseException
					{
						final String[] keyValue = c.split("=");

						if (!"TYPE".equalsIgnoreCase(keyValue[0]))
						{
							throw new ParseException(
									"Bad parameter to count(\"CLASSES\" ... )" + c);
						}

						final Set<PCTemplate> cs = new HashSet<PCTemplate>(objdata);
						final Iterator<? extends PObject> It = cs.iterator();

						filterPObjectByType(It, keyValue[1]);

						final Set<PObject> pSet = new HashSet<PObject>();

						for (final PCTemplate t : cs)
						{
							pSet.add(t);
						}

						return pSet;
					}

				};

		public static String CountType = "";

		private static ParameterTree convertParams(final Object[] params)
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

		private static void filterPObjectByType(
				final Iterator<? extends PObject> It, final String tString)
		{
			// If we want all then we don't need to filter.
			if (!"ALL".equalsIgnoreCase(tString))
			{

				// Make a List of all the types that each PObject should match
				final Collection<String> typeList = new ArrayList<String>();
				Collections.addAll(typeList, tString.split("\\."));

				// These nested loops remove all PObjects from the collection being
				// iterated that do not match all of the types in typeList
				while (It.hasNext())
				{
					final PObject pObj = It.next();

					for (final String type : typeList)
					{
						if (!pObj.isType(type))
						{
							It.remove();
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
			final Set<PObject> filtered = doFilterP(pt);

			return countDataP(filtered);
		}

		protected Object countDataP(final Collection<PObject> filtered)
		{
			return (double) filtered.size();
		}

		//@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
		protected Set<PObject> doFilterP(final ParameterTree pt) throws ParseException
		{
			final String c = pt.getContents();

			if (c.equalsIgnoreCase(ParameterTree.orString) || c.equalsIgnoreCase(
					ParameterTree.andString))
			{
				final Set<PObject> a = doFilterP(pt.getLeftTree());
				final Set<PObject> b = doFilterP(pt.getRightTree());
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

		protected abstract Set<PObject> filterSetP(String c) throws ParseException;

		protected abstract Set<String> filterSetS(String c) throws ParseException;
	}

	/**
	 * Constructor
	 */
	public CountCommand()
	{
		numberOfParameters = -1;
	}

	/**
	 * @see pcgen.util.PCGenCommand#getFunctionName()
	 */
	@Override
	public String getFunctionName()
	{
		return "COUNT";
	}

	/**
	 * Get the PC that will be used to do the counting.
	 *
	 * @return the pc
	 */
	private PlayerCharacter getPC()
	{
		PlayerCharacter pc = null;
		//noinspection InstanceofInterfaces
		if (parent instanceof VariableProcessor)
		{
			pc = ((VariableProcessor) parent).getPc();
		}
		else //noinspection InstanceofInterfaces
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
	private Object[] paramStackToArray(final Stack inStack, final int maxParam)
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

	/**
	 * Runs count on the inStack. The parameter is popped off the <code>inStack</code>,
	 * and the variable's value is pushed back to the top of <code>inStack</code>.
	 *
	 * @param inStack The jep stack that the count command will process
	 *
	 * @throws ParseException
	 */
	@Override
	@SuppressWarnings("unchecked")
	//Uses JEP, which doesn't use generics
	public void run(final Stack inStack) throws ParseException
	{
		// Grab the character under scrutiny
		final PlayerCharacter pc = getPC();

		if (pc == null)
		{
			throw new ParseException(
					"Invalid parent (no PC): "
					+ parent.getClass().getName());
		}

		// check the stack
		checkStack(inStack);

		if (1 <= curNumberOfParameters)
		{
			// move all but the first parameter from the stack into and array of Objects
			final Object[] params = paramStackToArray(inStack, curNumberOfParameters - 1);

			// retrieve the first Object, this should be a String whch will map directly to
			// a JepCountEnum, this specifies the type of count to perform
			final Object toCount = inStack.pop();

			if (toCount instanceof String)
			{
				final JepCountEnum CountEnum = JepCountEnum.valueOf((String) toCount);

				// Count the requested object type.
				final Double result = (Double) CountEnum.count(pc, params);

				inStack.push(result);
			}
			else
			{
				throw new ParseException("Invalid parameter type");
			}
		}
		else
		{
			throw new ParseException("missing parameter, nothing to count");
		}
	}
}
