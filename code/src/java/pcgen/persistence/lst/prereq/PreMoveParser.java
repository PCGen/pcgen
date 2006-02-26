/*
 * PreMoveParser.java
 * 
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on 18-Dec-2003
 * 
 * Current Ver: $Revision: 1.12 $
 * 
 * Last Editor: $Author: karianna $
 * 
 * Last Edited: $Date: 2005/10/11 10:39:55 $
 *  
 */
package pcgen.persistence.lst.prereq;

/**
 * @author wardc
 *  
 */
public class PreMoveParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface {

	public String[] kindsHandled() {
		return new String[] { "MOVE" };
	}

	/**
	 * @param moveTokens
	 * @return Prerequisite
	 * @deprecated Remove this method in 5.9.5
	 */
	/*
	protected Prerequisite getSingleMovePrereq(String moveTokens)
	{
		Prerequisite subreq = new Prerequisite();
		subreq.setKind("move");
		subreq.setOperator(PrerequisiteOperator.GTEQ);

		if (moveTokens.indexOf('.') > -1 || moveTokens.indexOf('=') > -1) 
		{
			String[] strings = moveTokens.split("=|\\.");

			subreq.setKey(strings[0]);
			subreq.setOperand(strings[1]);
		}
		else 
		{
			subreq.setKey(moveTokens);
			subreq.setOperand("0");
		}
		return subreq;
	}
	*/
	
	/**
	 * @param kind
	 * @param formula
	 * @param invertResult
	 * @param overrideQualify
	 * @return Prerequisite
	 * @throws PersistenceLayerException
	 * @deprecated REmovbe in 5.9.5
	 */
	/*
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException 
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setKind(null);

		String[] tokens = formula.split(",");
		if (tokens.length==1) 
		{
			prereq = getSingleMovePrereq(tokens[0]);
		}
		else 
		{	
			for (int i = 0; i < tokens.length; i++) 
			{
				Prerequisite subreq = getSingleMovePrereq(tokens[i]);
				prereq.addPrerequisite(subreq);
			}
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}*/
}
