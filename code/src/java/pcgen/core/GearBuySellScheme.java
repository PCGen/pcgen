/*
 * GearBuySellScheme.java
 * Copyright James Dempsey, 2012
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
 * Created on 11/02/2012 7:14:29 PM
 *
 * $Id$
 */
package pcgen.core;

import java.math.BigDecimal;
import java.text.NumberFormat;

import pcgen.facade.core.GearBuySellFacade;

/**
 * The Class {@code GearBuySellScheme} defines a named set of rates for
 * buying and selling gear.
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public class GearBuySellScheme implements GearBuySellFacade
{

	private String name;
	private BigDecimal buyRate;
	private BigDecimal sellRate;
	private BigDecimal cashSellRate;
	
	/**
	 * Create a new named instance of GearBuySellScheme
	 * @param name The name of the scheme.
	 * @param buyRate The percentage of list price at which a pc buys gear. 
	 * @param sellRate The percentage of list price at which a pc sells gear. 
	 * @param cashSellRate The percentage of list price at which a pc sells coins, gems etc.
	 */
	public GearBuySellScheme(String name, BigDecimal buyRate, BigDecimal sellRate, BigDecimal cashSellRate)
	{
		this.name = name;
		this.buyRate = buyRate;
		this.sellRate = sellRate;
		this.cashSellRate = cashSellRate;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * @return the buyRate
	 */
	public BigDecimal getBuyRate()
	{
		return buyRate;
	}

	/**
	 * @param buyRate the buyRate to set
	 */
	public void setBuyRate(BigDecimal buyRate)
	{
		this.buyRate = buyRate;
	}

	/**
	 * @return the sellRate
	 */
	public BigDecimal getSellRate()
	{
		return sellRate;
	}

	/**
	 * @param sellRate the sellRate to set
	 */
	public void setSellRate(BigDecimal sellRate)
	{
		this.sellRate = sellRate;
	}

	/**
	 * @return the cashSellRate
	 */
	public BigDecimal getCashSellRate()
	{
		return cashSellRate;
	}

	/**
	 * @param cashSellRate the cashSellRate to set
	 */
	public void setCashSellRate(BigDecimal cashSellRate)
	{
		this.cashSellRate = cashSellRate;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		NumberFormat numFmt = NumberFormat.getNumberInstance();
		
		StringBuilder sb = new StringBuilder(name);
		sb.append(" - Buy ");
		sb.append(numFmt.format(buyRate));
		sb.append(" Sell ");
		sb.append(numFmt.format(sellRate));

		return sb.toString();
	}

	
}
