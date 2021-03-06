package fr.emmuliette.rune.mod.spells.component.effectComponent;

import fr.emmuliette.rune.mod.spells.component.AbstractSpellComponent;
import fr.emmuliette.rune.mod.spells.properties.PropertyFactory;
import fr.emmuliette.rune.mod.spells.tags.BuildTag;

public abstract class AbstractEffectComponent extends AbstractSpellComponent {
	public AbstractEffectComponent(PropertyFactory propFactory, AbstractSpellComponent parent) {
		super(propFactory, parent);
		this.addTag(BuildTag.EFFECT);
	}

	@Override
	public boolean addNextPart(AbstractSpellComponent other) {
		if (isStartingComponent()) {
			return false;
		}
		return getParent().addNextPart(other);
	}
	
	@Override
	public void clear() {}
}
