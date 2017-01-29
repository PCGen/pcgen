/**
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2013 Vincent Lhote
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin;

import java.util.Arrays;

import pcgen.system.LanguageBundle;

/**
 * The state of a combatant, or an event.
 * 
 * @author Vincent Lhote
 */
public enum State {
	/* Used in Event */
	Active {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_active"); //$NON-NLS-1$
		}
	},
	/* the rest */
	Nothing {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_nothing"); //$NON-NLS-1$
		}
	},
	Bleeding {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_bleeding"); //$NON-NLS-1$
		}
	},
	Staggered {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_staggered"); //$NON-NLS-1$
		}
	},
	Unconsious {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_unconsious"); //$NON-NLS-1$
		}
	},
	Stable {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_stable"); //$NON-NLS-1$
		}
	},
	Dead {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_dead"); //$NON-NLS-1$
		}
	},
	Dazed {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_dazed"); //$NON-NLS-1$
		}
	},
	Disabled {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_disabled"); //$NON-NLS-1$
		}
	},
	/* XXX Seems to be checked for at one place but never put to that value... */
	Defeated {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_defeated"); //$NON-NLS-1$
		}
	};

	/**
	 * The State matching a String, based on {@link #name()}, not
	 * {@link #toString()}.
	 * 
	 * @param value
	 * @return {@link #Nothing} if no match (and not {@code null})
	 */
	public static State getState(String value) {
		return Arrays.stream(State.values())
					 .filter(s -> s.name().equals(value))
					 .findFirst()
					 .orElse(Nothing);
	}

	public static State getStateLocalised(String value) {
		return Arrays.stream(State.values())
					 .filter(s -> s.toString().equals(value))
					 .findFirst()
					 .orElse(Nothing);
	}
}
