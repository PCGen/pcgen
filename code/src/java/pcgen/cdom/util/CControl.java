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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.CaseInsensitiveMap;

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

	public static final CControl FACE = new CControl("FACE", "Face", Optional.empty(), "ORDEREDPAIR");

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

	/**
	 * Code control to take # of weapon hands off of WieldCategory
	 */
	public static final String WEAPONHANDS = "WEAPONHANDS";
	
	/**
	 * Code control to indicate the weight multiplier due to size difference from base
	 * size on Equipment.
	 */
	public static final String WEIGHTMULTIPLIER = "WEIGHTMULTIPLIER";

	/**
	 * Code control to take WieldCategory (Steps, etc) away from the old calculation system
	 */
	public static final String WIELDCAT = "WIELDCAT";

	/**
	 * Code Control to indicate the cost modifier due to the size difference from base
	 * size on Equipment
	 */
	public static final String COSTMULTIPLIER = "COSTMULTIPLIER";

	/**
	 * Code Control for the Alignment Input Channel.
	 */
	public static final CControl ALIGNMENTINPUT = new CControl("ALIGNMENTINPUT", "Alignment", Optional.of("ALIGNMENTFEATURE"), "ALIGNMENT", true, true);

	/**
	 * Enable/Disable the AlignmentFeature
	 */
	public static final String ALIGNMENTFEATURE = "ALIGNMENTFEATURE";

	/**
	 * Enable/Disable the DomainFeature
	 */
	public static final String DOMAINFEATURE = "DOMAINFEATURE";

	/**
	 * Code control for the Handedness of a PC.
	 */
	public static final CControl HANDEDINPUT = new CControl("HANDEDINPUT", "Handed", Optional.empty(), "HANDED", true, false);

	/**
	 * Code control for the Available Handedness on a PC.
	 */
	public static final CControl AVAILHANDEDNESS = new CControl("AVAILHANDEDNESS", "AvailableHandedness", Optional.empty(), "ARRAY[HANDED]", true, false);

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
	 * The Format identifier for the internal variable.
	 */
	private final String format;

	/**
	 * Indicates if the item is a channel.
	 */
	private final boolean isChannel;

	/**
	 * Indicates if a Channel should be auto granted.
	 */
	private final boolean isAutoGranted;

	/**
	 * Constructs a new CControl with the given characteristics.
	 */
	private CControl(String name, String defaultValue,
		Optional<String> controllingFeature, String format, boolean isChannel,
		boolean isAutoGranted)
	{
		this.name = Objects.requireNonNull(name);
		this.defaultValue = Objects.requireNonNull(defaultValue);
		this.controllingFeature = Objects.requireNonNull(controllingFeature);
		this.format = Objects.requireNonNull(format);
		this.isChannel = isChannel;
		this.isAutoGranted = isAutoGranted;
	}

	/**
	 * Constructs a new CControl with the given name, default variable name, controlling
	 * feature, and format.
	 */
	public CControl(String name, String defaultValue, Optional<String> controllingFeature, String format)
	{
		this(name, defaultValue, controllingFeature, format, false, false);
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

	public String getFormat()
	{
		return format;
	}

	public boolean isChannel()
	{
		return isChannel;
	}

	public boolean isAutoGranted()
	{
		return isAutoGranted;
	}

	private static CaseInsensitiveMap<CControl> map = null;

	static
	{
		buildMap();
	}

	private static void buildMap()
	{
		map = new CaseInsensitiveMap<>();
		Field[] fields = CControl.class.getDeclaredFields();
        for (Field field : fields)
        {
            int mod = field.getModifiers();

            if (java.lang.reflect.Modifier.isStatic(mod) && java.lang.reflect.Modifier.isFinal(mod)
                    && java.lang.reflect.Modifier.isPublic(mod))
            {
                try
                {
                    Object obj = field.get(null);
                    if (obj instanceof CControl)
                    {
                        map.put(field.getName(), (CControl) obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e)
                {
                    throw new UnreachableError(e);
                }
            }
        }
	}

	public static Collection<CControl> getChannelConstants()
	{
		return new HashSet<>(map.values());
	}
}
