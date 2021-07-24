package fr.emmuliette.rune.mod.spells.tags;

public class SpellTag extends OtherTag {
	public static SpellTag CHARGING = new SpellTag(true);
	public static SpellTag LOADING = new SpellTag(true);
	public static SpellTag CHANNELING = new SpellTag(true);
	// public static final RestrictionTag NO_AGGRO = new RestrictionTag();
	// public static final RestrictionTag NO_PARCHMENT = new RestrictionTag();
	// public static final RestrictionTag NO_IA = new RestrictionTag();

	private boolean unique;

	private SpellTag(boolean unique) {
		this.unique = unique;
	}

	static void init() {
		CHARGING.touch();
		LOADING.touch();
		CHANNELING.touch();
	}

	private void touch() {
	}

	public boolean isUnique() {
		return unique;
	}
}
