package pcgen.util.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class VisionType extends AbstractConstant {

	private static Map<String, VisionType> typeMap;

	public static VisionType getVisionType(String s) {
		if (typeMap == null) {
			buildMap();
		}
		/*
		 * CONSIDER Right now this is CASE SENSITIVE. Should this really be the
		 * case? - thpr 10/25/06
		 */
		VisionType o = typeMap.get(s);
		if (o == null) {
			o = new VisionType();
			typeMap.put(s, o);
		}
		return o;
	}

	private static void buildMap() {
		typeMap = new HashMap<String, VisionType>();
		Field[] fields = VisionType.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod)) {
				try {
					Object o = fields[i].get(null);
					if (o instanceof VisionType) {
						typeMap.put(fields[i].getName(), (VisionType) o);
					}
				} catch (IllegalArgumentException e) {
					throw new InternalError();
				} catch (IllegalAccessException e) {
					throw new InternalError();
				}
			}
		}
	}

	public String toString() {
		/*
		 * CLEANUP Oh my, this should NOT be this way
		 */
		for (Map.Entry<String, VisionType> me : typeMap.entrySet()) {
			if (me.getValue().equals(this)) {
				return me.getKey();
			}
		}
		// Error
		return "";
	}

	public static void clearConstants() {
		buildMap();
	}

	public static Collection<VisionType> getAllVisionTypes() {
		return Collections.unmodifiableCollection(typeMap.values());
	}
}
