/*
 * Copyright 2016-18 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.util;

import java.util.Objects;
import java.util.Optional;

/**
 * Code Controls
 */
public final class CControl
{

	public static final String CRITMULT = "CRITMULT";

	public static final String CRITRANGE = "CRITRANGE";

	public static final String LEGS = "LEGS";

	public static final String CREATUREHANDS = "CREATUREHANDS";

	public static final String FUMBLERANGE = "FUMBLERANGE";

	public static final String ALTHP = "ALTHP";

	public static final String EQMAXDEX = "EQMAXDEX";
	public static final String PCMAXDEX = "PCMAXDEX";

	public static final String EQACCHECK = "EQACCHECK";
	public static final String PCACCHECK = "PCACCHECK";

	public static final String EQSPELLFAILURE = "EQSPELLFAILURE";
	public static final String PCSPELLFAILURE = "PCSPELLFAILURE";

	public static final String EDR = "EDR";

	public static final CControl FACE = new CControl("FACE", "Face");

	public static final String EQRANGE = "EQRANGE";

	public static final String SIZEMODDEFENSE = "SIZEMODDEFENSE";
	public static final String EQBASEACMOD = "EQBASEACMOD";
	public static final String EQACMOD = "EQACMOD";
	public static final String ALTERSAC = "ALTERSAC";
	public static final String ACVARTOTAL = "ACVARTOTAL";
	public static final String ACVARARMOR = "ACVARARMOR";

	public static final String EQREACH = "EQREACH";
	public static final String PCREACH = "PCREACH";

	public static final String INITIATIVE = "INITIATIVE";
	public static final String INITIATIVESTAT = "INITIATIVESTAT";
	public static final String INITIATIVEMISC = "INITIATIVEMISC";
	public static final String INITIATIVEBONUS = "INITIATIVEBONUS";

	public static final String STATINPUT = "STATINPUT";

	public static final String BASESAVE = "BASESAVE";
	public static final String TOTALSAVE = "TOTALSAVE";
	public static final String MISCSAVE = "MISCSAVE";
	public static final String EPICSAVE = "EPICSAVE";
	public static final String MAGICSAVE = "MAGICSAVE";
	public static final String STATMODSAVE = "STATMODSAVE";
	public static final String RACESAVE = "RACESAVE";

	/**
	 * Code Control for the Base Size (original size for the race) of a PC.
	 */
	public static final String BASESIZE = "BASESIZE";

	/**
	 * Code Control for the Current Size of a PC.
	 */
	public static final String PCSIZE = "PCSIZE";

	public static final CControl ALIGNMENTINPUT = new CControl("ALIGNMENTINPUT", "Alignment");


	/**
	 * The name of a code control that contains a default value. This is used when a Code
	 * Control is already used internally and is overridden by data (rather than just
	 * being an on/off switch for data)
	 */
	private final String name;

	/**
	 * The default value (the internal variable name used)
	 */
	private final String defaultValue;

	/**
	 * The controlling Feature for the CodeControl
	 */
	private final Optional<String> controllingFeature;

	/**
	 * Constructs a new CControl with the given name and default variable name
	 */
	private CControl(String name, String defaultValue)
	{
		this(name, defaultValue, Optional.empty());
	}

	/**
	 * Constructs a new CControl with the given name, default variable name, and
	 * controlling feature.
	 */
	private CControl(String name, String defaultValue, String controllingFeature)
	{
		this(name, defaultValue, Optional.of(controllingFeature));
	}

	/**
	 * Constructs a new CControl with the given name, default variable name, and
	 * controlling feature.
	 */
	private CControl(String name, String defaultValue, Optional<String> controllingFeature)
	{
		this.name = Objects.requireNonNull(name);
		this.defaultValue = Objects.requireNonNull(defaultValue);
		this.controllingFeature = Objects.requireNonNull(controllingFeature);
	}

	public String getName()
	{
		return name;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Returns the Controlling Feature (if any) for this Code Control
	 */
	public Optional<String> getControllingFeature()
	{
		return controllingFeature;
	}
}
