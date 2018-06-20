/*
 * Copyright 2012 Vincent Lhote
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
 */
package plugin.overland.model;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import plugin.overland.gui.XMLFilter;
import plugin.overland.model.TravelMethodImplementation.Choice;
import plugin.overland.model.TravelMethodImplementation.Combo;
import plugin.overland.model.TravelMethodImplementation.Method;
import plugin.overland.model.TravelMethodImplementation.Pace;
import plugin.overland.util.Localized;

/**
 * Builds a Travel Method instance from an XML document.
 * 
 */
public class TravelMethodFactory
{
	/** Default locale for number parsing */
	public static final Locale DEFAULT_LOCALE = Locale.UK;

	/** directory where the XML and DTD is stored, under the plugin specific directory */
	private static final String DIR_TRAVELMETHODS = "travel_methods"; //$NON-NLS-1$

	// ### XML Constants ###

	private static final String XML_ELEMENT_TRAVEL = "travel"; //$NON-NLS-1$
	private static final String XML_ELEMENT_COMBO = "combo"; //$NON-NLS-1$
	private static final String XML_ELEMENT_CHOICE = "choice"; //$NON-NLS-1$
	private static final String XML_ELEMENT_CHOOSE_FROM = "chooseFrom"; //$NON-NLS-1$
	private static final String XML_ELEMENT_PACE = "pace"; //$NON-NLS-1$
	private static final String XML_ELEMENT_METHOD = "method"; //$NON-NLS-1$
	private static final String XML_ELEMENT_ROUTE = "route"; //$NON-NLS-1$
	private static final String XML_ELEMENT_TERRAIN = "terrain"; //$NON-NLS-1$
	private static final String XML_ELEMENT_WAY = "way"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_HOURSINDAY = "hoursInDay"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_MPH = "mph"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_KMH = "kmh"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_COMMENT = "comment"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_DAYS = "days"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_ADDKMH = "addKmh"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_ADDMPH = "addMph"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_MULT = "mult"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String XML_ATTRIBUTE_NUMBERFORMAT = "numberFormat"; //$NON-NLS-1$

	// ### Factory methods ###

	public static Vector<TravelMethod> load(File datadir)
	{
		//Create a new list for the travel methods
		Vector<TravelMethod> tms = new Vector<>();

		File path = new File(datadir, DIR_TRAVELMETHODS);

		if (path.isDirectory())
		{
			File[] dataFiles = path.listFiles(new XMLFilter());
			SAXBuilder builder = new SAXBuilder();

			for (File dataFile : dataFiles)
			{
				try
				{
					Document methodSet = builder.build(dataFile);
					DocType dt = methodSet.getDocType();

					if (dt.getElementName().equals(XML_ELEMENT_TRAVEL))
					{
						//Do work here
						TravelMethod tm = TravelMethodFactory.create(methodSet);
						tms.add(tm);
					}
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}
			}
		}
		else
		{
			Logging.errorPrintLocalised("in_plugin_overland_noDatafile", path.getPath()); //$NON-NLS-1$
		}
		return tms;
	}

	public static TravelMethod create(Document methodSet)
	{
		Localized name;
		Map<String, Map<String, Combo>> multByRoadByTerrains;
		Map<String, List<Localized>> terrains2;
		Map<String, Map<Localized, String>> terrainsById2;
		Map<String, List<Localized>> routes2;
		Map<String, Map<Localized, String>> routesById2;
		List<Method> methods;

		Element travel = methodSet.getRootElement();

		NumberFormat nf = getNumberFormat(travel);

		name = new Localized(travel);

		multByRoadByTerrains = new HashMap<>();
		terrains2 = new HashMap<>();
		terrainsById2 = new HashMap<>();
		routes2 = new HashMap<>();
		routesById2 = new HashMap<>();
		methods = new ArrayList<>();

		for (Object methodObj : travel.getChildren())
		{
			Element child = (Element) methodObj;
			if (child.getName().equals(XML_ELEMENT_WAY))
			{
				String wayId = child.getAttributeValue(XML_ATTRIBUTE_ID);
				List<Localized> terrains = new ArrayList<>();
				terrains2.put(wayId, terrains);
				List<Localized> routes = new ArrayList<>();
				routes2.put(wayId, routes);
				Map<Localized, String> terrainsById = new HashMap<>();
				terrainsById2.put(wayId, terrainsById);
				Map<Localized, String> routesById = new HashMap<>();
				routesById2.put(wayId, routesById);

				for (Object o : child.getChildren())
				{
					if (o instanceof Element)
					{
						Element grandchild = (Element) o;
						if (grandchild.getName().equals(XML_ELEMENT_TERRAIN))
						{
							String id = grandchild.getAttributeValue(XML_ATTRIBUTE_ID);
							Localized terrain = new Localized(grandchild);
							terrains.add(terrain);
							terrainsById.put(terrain, id);
							if (!multByRoadByTerrains.containsKey(id))
							{
								multByRoadByTerrains.put(id, new TreeMap<>());
							}
						}
						else if (grandchild.getName().equals(XML_ELEMENT_ROUTE))
						{
							String id = grandchild.getAttributeValue(XML_ATTRIBUTE_ID);
							Localized route = new Localized(grandchild);
							routes.add(route);
							routesById.put(route, id);
							for (Object gcc : grandchild.getChildren(XML_ELEMENT_COMBO))
							{
								if (gcc instanceof Element)
								{
									Element grandgrandchild = (Element) gcc;
									String idTerrain = grandgrandchild.getAttributeValue(XML_ELEMENT_TERRAIN);
									Number mult = parseNumber(nf, grandgrandchild, XML_ATTRIBUTE_MULT, 1);
									Number addMph = parseNumber(nf, grandgrandchild, XML_ATTRIBUTE_ADDMPH, 0);
									Number addKmh = parseNumber(nf, grandgrandchild, XML_ATTRIBUTE_ADDKMH, 0);
									if (!multByRoadByTerrains.containsKey(idTerrain))
									{
										multByRoadByTerrains.put(idTerrain, new TreeMap<>());
									}
									multByRoadByTerrains.get(idTerrain).put(id, new Combo(mult, addMph, addKmh));
								}

							}
						}
					}
				}
				// Sort the terrains by locale name
				// TODO sort, but with one that do toString on the object. Collections.sort(terrains, Collator.getInstance());
				// not sorting routes intentionally (it goes from easier to navigate to hardest)
			}
			else if (child.getName().equals(XML_ELEMENT_METHOD))
			{
				String way = child.getAttributeValue(XML_ELEMENT_WAY);
				Method method = new Method(new Localized(child), way);
				methods.add(method);

				for (Object o : child.getChildren())
				{
					if (o instanceof Element)
					{
						Element grandchild = (Element) o;
						if (grandchild.getName().equals(XML_ELEMENT_PACE))
						{
							Localized pace = new Localized(grandchild);
							boolean useDays = Boolean.parseBoolean(grandchild.getAttributeValue(XML_ATTRIBUTE_DAYS));
							Localized comment = new Localized(grandchild, XML_ATTRIBUTE_COMMENT);
							Number mult = parseNumber(nf, grandchild, XML_ATTRIBUTE_MULT, 1);
							Pace newPace = new Pace(pace, comment, useDays, mult);
							method.add(newPace);
						}
						if (grandchild.getName().equals(XML_ELEMENT_CHOOSE_FROM))
						{
							Number kmh = parseNumber(nf, grandchild, XML_ATTRIBUTE_KMH, 0.75); // XXX other default?
							Number mph = parseNumber(nf, grandchild, XML_ATTRIBUTE_MPH, 0.5); // XXX other default?
							Number hoursInDay = parseNumber(nf, grandchild, XML_ATTRIBUTE_HOURSINDAY, 24); // XXX other default?
							for (Object o2 : grandchild.getChildren(XML_ELEMENT_CHOICE))
							{
								if (o2 instanceof Element)
								{
									Element grandgrandchild = (Element) o2;
									Localized choiceName = new Localized(grandgrandchild);
									Number mult = parseNumber(nf, grandgrandchild, XML_ATTRIBUTE_MULT, 1);
									Choice c =
											new Choice(choiceName, hoursInDay, mult.doubleValue() * kmh.doubleValue(),
												mult.doubleValue() * mph.doubleValue());
									method.add(c);
								}
							}
						}
					}
				}
			}
		}
		return new TravelMethodImplementation(name, multByRoadByTerrains, terrains2, terrainsById2, routes2, routesById2, methods);
	}

	/**
	 * @param nf
	 * @param e
	 * @param string
	 * @param def
	 * @return
	 */
	private static Number parseNumber(NumberFormat nf, Element e, String string, Number def)
	{
		Number n = def;
		String attributeValue = e.getAttributeValue(string);
		if (attributeValue != null)
		{
			try
			{
				n = nf.parse(attributeValue);
			}
			catch (ParseException exception)
			{
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}
		return n;
	}

	/**
	 * Use the XML defined locale to provide a number format instance.
	 * Use {@link #DEFAULT_LOCALE} if no locale are specified specified.
	 * @param e an XML element with {@value #XML_ATTRIBUTE_NUMBERFORMAT}
	 * @return a number format
	 */
	public static NumberFormat getNumberFormat(Element e)
	{
		String numFormLoc = e.getAttributeValue(XML_ATTRIBUTE_NUMBERFORMAT, ""); //$NON-NLS-1$
		String[] split = numFormLoc.split("_"); //$NON-NLS-1$
		Locale l = null;
		switch (split.length)
		{
			case 0:
				// Default numberLocale
				l = DEFAULT_LOCALE;
				break;
			case 1:
				l = new Locale(split[0]);
				break;
			case 2:
				l = new Locale(split[0], split[1]);
				break;
			case 3:
				l = new Locale(split[0], split[1], split[2]);
				break;

			default:
				Logging
					.log(Level.WARNING, LanguageBundle.getFormattedString(
						"in_log_localeInvalid", numFormLoc, split[0], split[1], split[2])); //$NON-NLS-1$
				l = new Locale(split[0], split[1], split[2]);
				break;
		}
		return NumberFormat.getNumberInstance(l);
	}
}
