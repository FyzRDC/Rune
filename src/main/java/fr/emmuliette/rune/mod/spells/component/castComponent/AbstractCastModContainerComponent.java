package fr.emmuliette.rune.mod.spells.component.castComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.emmuliette.rune.RuneMain;
import fr.emmuliette.rune.exception.RunePropertiesException;
import fr.emmuliette.rune.mod.spells.SpellContext;
import fr.emmuliette.rune.mod.spells.component.AbstractSpellComponent;
import fr.emmuliette.rune.mod.spells.properties.PropertyFactory;
import fr.emmuliette.rune.mod.spells.tags.BuildTag;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RuneMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class AbstractCastModContainerComponent extends AbstractCastComponent<AbstractCastComponent<?>> {
	AbstractCastComponent<?> children;

	public AbstractCastModContainerComponent(PropertyFactory propFactory, AbstractSpellComponent parent)
			throws RunePropertiesException {
		super(propFactory, parent);
		this.addTag(BuildTag.CAST_MOD);
	}

	@Override
	public int getMaxSize() {
		return 1;
	}

	@Override
	public int getSize() {
		return (children == null) ? 0 : 1;// childrenCastMod.size() + childrenCast.size();
	}

	@Override
	public boolean canAddChildren(AbstractSpellComponent children) {
		return (getSize() < getMaxSize()) && (children instanceof AbstractCastComponent);
	}

	@Override
	public List<AbstractCastComponent<?>> getChildrens() {
		if (children != null) {
			return Arrays.asList(children);
		} else {
			return new ArrayList<AbstractCastComponent<?>>();
		}
	}

	@Override
	protected void addChildrenInternal(AbstractSpellComponent newEffect) {
		children = (AbstractCastComponent<?>) newEffect;
	}

	@Override
	protected boolean internalCast(SpellContext context) {
		if (children instanceof CallbackMod) {
			((CallbackMod) children).buildNRegisterCallback(context, this, new HashSet<Callback>());
			return true;
		} else if (children instanceof AbstractCastComponent) {
			return ((AbstractCastComponent<?>) children).internalCast(context);
		}
		System.out.println("NIKE TA MERER LE JEU");
		return false;
	}

	public boolean update(Callback cb, SpellContext context, boolean failed) {
		Set<Callback> setCB = cb.getSetCB();
		if (failed) {
			for (Callback callback : setCB) {
				callback.cancel(false);
			}
			return false;
		}
		// PAS ECHEC, CAST ?
		for (Callback callback : setCB) {
			if (!callback.isTriggered()) {
				return false;
			}
		}
		return castChildren(context);
	}

	private boolean castChildren(SpellContext context) {
		if (children instanceof AbstractCastComponent) {
			return ((AbstractCastComponent<?>) children).internalCast(context);
		}
		return false;
	}

	@Override
	public boolean addNextPart(AbstractSpellComponent other) {
		boolean result = super.addNextPart(other);
		return result;
	}
	
	public void clearChildrens() {
		this.children = null;
	}
}
