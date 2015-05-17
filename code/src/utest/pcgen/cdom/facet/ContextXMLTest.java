/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet;

import java.io.IOException;
import java.util.zip.ZipException;

import junit.framework.TestCase;

import org.junit.Test;

public class ContextXMLTest extends TestCase
{

	@Test
	public void test() throws ZipException, IOException
	{
//		Collection<AbstractStorageFacet> storageBeans =
//				SpringHelper.getStorageBeans();
//		List<String> beanList = new ArrayList<String>();
//		List<String> missed = new ArrayList<String>();
//		for (AbstractStorageFacet bean : storageBeans)
//		{
//			beanList.add(bean.getClass().getName());
//		}
//		File f = new File("code/bin/pcgen.jar");
//		Enumeration<? extends ZipEntry> entries = new ZipFile(f).entries();
//		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//		for (; entries.hasMoreElements();)
//		{
//			String name = entries.nextElement().getName();
//			if (name.startsWith("pcgen/cdom/facet") && name.endsWith(".class"))
//			{
//				String clName =
//						name.substring(0, name.length() - 6).replace('/', '.');
//				try
//				{
//					Class<?> cl = classLoader.loadClass(clName);
//					if (Modifier.isAbstract(cl.getModifiers()))
//					{
//						//No need to worry about abstract classes
//						continue;
//					}
//					if (AbstractStorageFacet.class.isAssignableFrom(cl))
//					{
//						if (!beanList.remove(clName))
//						{
//							missed.add(clName);
//						}
//					}
//				}
//				catch (ClassNotFoundException e)
//				{
//					if (clName.indexOf("$") == -1)
//					{
//						missed.add(clName);
//					}
//					else
//					{
//						System.out.println("Ignoring " + clName
//							+ " as not found (assuming inner class is safe)");
//					}
//				}
//			}
//		}
//		if (!beanList.isEmpty())
//		{
//			System.err.println("Extra: " + beanList);
//			fail("Strangely, Storage Beans found in applicationContext.xml,"
//				+ " but not in pcgen/cdom/facet: " + beanList);
//		}
//		missed.remove("pcgen.cdom.facet.PlayerCharacterTrackingFacet");
//		if (!missed.isEmpty())
//		{
//			System.err.println("Missed: " + missed);
//			fail("AbstractStorageFacets found that are not in applicationContext.xml: "
//				+ missed);
//		}
	}

}
