/*
 * CountDistinctCommand.java
 * Copyright 2013 (C) James Dempsey
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
import pcgen.cdom.helper.Aspect;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.util.AbstractCountCommand;
import pcgen.util.Logging;
import pcgen.util.ParameterTree;
import pcgen.util.enumeration.Visibility;


/**
 * <code>CountDistinctCommand</code> deals with the count() JEP command. The first parameter will
 * be the type of object being counted and further parameters will specify the criteria.
 * <p/> 
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CountDistinctCommand extends AbstractCountCommand
{

	public enum JepCountDistinctEnum
	{
		ABILITIES
			{
				// Abilities are PObjects, we can implement the filterSet directly
				// i.e. without using PObject proxies.

				public Map<Nature, Set<Ability>> abdata;

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

					return (double) doFilterP(pt).size();
				}

				@Override
				protected Set<? extends CDOMObject> filterSetP(final String c)
				{
					final String[] keyValue = c.split("=");
					final AbstractCountCommand.JepAbilityCountEnum en;

					try
					{
						en = AbstractCountCommand.JepAbilityCountEnum.valueOf(keyValue[0]);
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

						if (!name.equalsIgnoreCase(keyValue[1]))
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
					cs = new HashSet<Ability>(abdata.get(Nature.ANY));
					abIt = cs.iterator();

					while (abIt.hasNext())
					{
						final Ability ab = (Ability) abIt.next();

						final String name = ab.getKeyName();

						if (!name.equalsIgnoreCase(keyValue[1]))
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

			};

		public static String CountType = "";

		public static JepCountDistinctEnum lookupKey(String key)
		{
			for (JepCountDistinctEnum val : JepCountDistinctEnum.values())
			{
				if (val.toString().equalsIgnoreCase(key))
				{
					return val;
				}
			}
			return null;
		}
		
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

		private static void filterPObjectByType(final Iterator<? extends CDOMObject> it, final String tString)
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
	 * Constructor.
	 */
	public CountDistinctCommand()
	{
		numberOfParameters = -1;
	}

	/**
	 * Gets the name of the function handled by this class.
	 * @return The name of the function.
	 */
	@Override
	public String getFunctionName()
	{
		return "COUNTDISTINCT";
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

			// retrieve the first Object, this should be a String which will map directly to
			// a JepCountDistinctEnum or JepCountEnum, this specifies the type of count to perform
			final Object toCount = inStack.pop();

			if (toCount instanceof String)
			{
				final JepCountDistinctEnum countDistinctEnum =
						JepCountDistinctEnum.lookupKey((String) toCount);
				if (countDistinctEnum != null)
				{
					// Count the requested object type.
					final Double result =
							(Double) countDistinctEnum.count(pc, params);
					inStack.push(result);
				}
				else
				{
					// Fall back to count
					final AbstractCountCommand.JepCountEnum countEnum =
							AbstractCountCommand.JepCountEnum.valueOf((String) toCount);
					final Double result = (Double) countEnum.count(pc, params);
					inStack.push(result);
				}
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
