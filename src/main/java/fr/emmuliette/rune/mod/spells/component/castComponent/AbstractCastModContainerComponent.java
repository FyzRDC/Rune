package fr.emmuliette.rune.mod.spells.component.castComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.emmuliette.rune.RuneMain;
import fr.emmuliette.rune.exception.CasterCapabilityException;
import fr.emmuliette.rune.exception.CasterCapabilityExceptionSupplier;
import fr.emmuliette.rune.mod.RunePropertiesException;
import fr.emmuliette.rune.mod.caster.capability.CasterCapability;
import fr.emmuliette.rune.mod.caster.capability.ICaster;
import fr.emmuliette.rune.mod.spells.SpellContext;
import fr.emmuliette.rune.mod.spells.component.AbstractSpellComponent;
import fr.emmuliette.rune.mod.spells.properties.PropertyFactory;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RuneMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class AbstractCastModContainerComponent extends AbstractCastComponent<AbstractCastComponent<?>> {
	AbstractCastComponent<?> children;

	public AbstractCastModContainerComponent(PropertyFactory propFactory, AbstractSpellComponent parent)
			throws RunePropertiesException {
		super(propFactory, parent);
	}

	@Override
	public Boolean canCast(SpellContext context) {
		try {
			ICaster cap = context.getCaster().getCapability(CasterCapability.CASTER_CAPABILITY)
					.orElseThrow(new CasterCapabilityExceptionSupplier(context.getCaster()));
			Boolean checkCd = checkCooldown(cap, context);
			if (checkCd == null || !checkCd)
				return checkCd;

			Boolean checkManaCost = checkCost(cap, context);
			if (checkManaCost == null || !checkManaCost)
				return checkManaCost;

			Boolean checkChildrens = checkChildrenCastType(context);
			if (checkChildrens == null || !checkChildrens)
				return checkChildrens;

			return true;
		} catch (CasterCapabilityException e) {
			e.printStackTrace();
		}
		return false;
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
}
