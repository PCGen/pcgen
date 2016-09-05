/*
 * SortKeyLstTest.java
 * Copyright James Dempsey, 2010
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
 * Created on 18/07/2010 8:37:13 AM
 *
 * $Id$
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalStringTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

/**
 * The Class <code>SortKeyLstTest</code> is a round robin test for the SORTKEY tag. 
 * It checks that the parse and unparse are working correctly and that a parsed 
 * result can be unparsed back to the original input.  
 *
 * <br/>
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
public class SortKeyLstTest extends AbstractGlobalStringTokenTestCase
{
	static CDOMPrimaryToken<CDOMObject> token = new SortKeyLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>();

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Override
	public StringKey getStringKey()
	{
		return StringKey.SORT_KEY;
	}
}
