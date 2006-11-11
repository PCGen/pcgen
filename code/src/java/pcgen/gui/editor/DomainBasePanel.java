/*
 * DomainBasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 8, 2003, 10:23 AM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Description;
import pcgen.core.Domain;
import pcgen.core.PObject;
import pcgen.persistence.lst.DomainLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;

/**
 * <code>DomainBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class DomainBasePanel extends BasePanel
{
	private DescriptionPanel pnlDescription;

	/** Creates new form DomainBasePanel */
	public DomainBasePanel()
	{
		initComponents();
	}

	/**
	 * Set the descIsPI
	 * @param descIsPI
	 */
	public void setDescIsPI(final boolean descIsPI)
	{
		pnlDescription.setDescIsPI(descIsPI);
	}

	/**
	 * Get the descIsPI
	 * @return the descIsPI
	 */
	public boolean getDescIsPI()
	{
		return pnlDescription.getDescIsPI();
	}

	/**
	 * Set the description text
	 * @param aString
	 */
	public void setDescriptionText(String aString)
	{
		pnlDescription.setText(aString);
	}

	/**
	 * Get the description Text
	 * @return description text
	 */
	public String getDescriptionText()
	{
		return pnlDescription.getText();
	}

	public void updateData(PObject thisPObject)
	{
		final String desc = getDescriptionText();
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
			DomainLstToken.class);
		DomainLstToken tokenParser = (DomainLstToken) tokenMap.get("DESC");
		if (tokenParser != null)
		{
			final StringTokenizer tok = new StringTokenizer(desc, "\t");
			while (tok.hasMoreTokens())
			{
				tokenParser.parse((Domain) thisPObject, tok.nextToken());
			}
		}
		thisPObject.setDescIsPI(getDescIsPI());
	}

	public void updateView(PObject thisPObject)
	{
		final StringBuffer buf = new StringBuffer();
		for ( final Description desc : thisPObject.getDescriptionList() )
		{
			if ( buf.length() == 0 )
			{
				buf.append("\t");
			}
			buf.append(desc.getPCCText());
		}
		setDescriptionText(buf.toString()); // don't want PI here
		setDescIsPI(thisPObject.getDescIsPI());
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		pnlDescription = new DescriptionPanel();

		setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlDescription, gridBagConstraints);
	}
}
