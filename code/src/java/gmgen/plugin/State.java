/**
 * 
 */
package gmgen.plugin;

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
			return LanguageBundle.getString("in_plugin_state_active");
		}
	},
	/* the rest */
	Nothing {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_nothing");
		}
	},
	Bleeding {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_bleeding");
		}
	},
	Staggered {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_staggered");
		}
	},
	Unconsious {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_unconsious");
		}
	},
	Stable {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_stable");
		}
	},
	Dead {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_dead");
		}
	},
	Dazed {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_dazed");
		}
	},
	Disabled {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_disabled");
		}
	},
	/* XXX Seems to be checked for at one place but never put to that value… */
	Defeated {
		@Override
		public String toString() {
			return LanguageBundle.getString("in_plugin_state_defeated");
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
		for (State s : State.values()) {
			if (s.name().equals(value)) {
				return s;
			}
		}
		return Nothing;
	}

	public static State getStateLocalised(String value) {
		for (State s : State.values()) {
			if (s.toString().equals(value)) {
				return s;
			}
		}
		return Nothing;
	}
}
