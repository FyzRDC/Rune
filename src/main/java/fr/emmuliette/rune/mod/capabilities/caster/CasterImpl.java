package fr.emmuliette.rune.mod.capabilities.caster;

import java.lang.reflect.InvocationTargetException;

import fr.emmuliette.rune.exception.NotEnoughManaException;
import fr.emmuliette.rune.mod.RunePropertiesException;
import fr.emmuliette.rune.mod.capabilities.CapabilitySyncHandler;
import fr.emmuliette.rune.mod.items.wand.ManaSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;

public class CasterImpl implements ICaster {
	private final static float BASE_MANA = 50, BASE_POWER = 1f;
	private final static int BASE_MANA_REGEN = 40;
	private Grimoire grimoire;
	private Entity owner;
	private float power;
	private float currentMana;
	private float maxMana;
	private int currentManaCd;
	private int manaRegen;
	private int cooldown;

	public CasterImpl(Entity owner) {
		this.owner = owner;
		grimoire = new Grimoire();
		power = BASE_POWER;
		currentMana = BASE_MANA;
		maxMana = BASE_MANA;
		manaRegen = BASE_MANA_REGEN;
		currentManaCd = 0;
		cooldown = 0;
	}

	public CasterImpl() {
		this(null);
	}

	@Override
	public void setOwner(Entity owner) {
		this.owner = owner;
	}

	@Override
	public Entity getOwner() {
		return owner;
	}

	@Override
	public Grimoire getGrimoire() {
		return grimoire;
	}

	@Override
	public void setGrimoire(Grimoire grimoire) {
		Grimoire old = this.grimoire;
		if (old != grimoire) {
			this.grimoire = grimoire;
			sync();
		}
	}

	@Override
	public float getMana() {
		float equipmentMana = 0f;
		for (ItemStack item : owner.getAllSlots()) {
			if (item == ItemStack.EMPTY)
				continue;
			if (item.getItem() instanceof ManaSource)
				equipmentMana += ((ManaSource) item.getItem()).getMana(item);
		}
		return this.currentMana + equipmentMana;
	}

	@Override
	public void setMana(float mana) {
		if (mana != currentMana) {
			this.currentMana = Math.min(mana, getMaxMana());
			sync();
		}
	}

	@Override
	public float getMaxMana() {
		float equipmentMana = 0f;
		for (ItemStack item : owner.getAllSlots()) {
			if (item == ItemStack.EMPTY)
				continue;
			if (item.getItem() instanceof ManaSource)
				equipmentMana += ((ManaSource) item.getItem()).getMaxMana(item);
		}
		return this.maxMana + equipmentMana;
	}

	@Override
	public void setMaxMana(float maxMana) {
		if (maxMana != this.maxMana) {
			this.maxMana = maxMana;
			sync();
		}
	}

	@Override
	public float getManaInternal() {
		return this.currentMana;
	}

	@Override
	public float getMaxManaInternal() {
		return this.maxMana;
	}

	@Override
	public float getPowerInternal() {
		return this.power;
	}

	@Override
	public int getManaRegenTick() {
		return currentManaCd;
	}

	@Override
	public int getManaRegen() {
		return manaRegen;
	}

	@Override
	public void setManaCooldown(int newCooldown) {
		if (this.currentManaCd != newCooldown) {
			this.currentManaCd = newCooldown;
			// sync();
		}
	}

	@Override
	public void setManaRegen(int newRegen) {
		if (this.manaRegen != newRegen) {
			int diff = this.manaRegen - newRegen;
			this.manaRegen = newRegen;
			this.currentManaCd -= diff;
			sync();
		}
	}

	@Override
	public boolean isCooldown() {
		return cooldown > 0;
	}

	@Override
	public void setCooldown(int cd) {
		int previous = this.cooldown;
		this.cooldown = cd;
		if ((previous == 0 && cd != 0) || previous > 0 && cd == 0) {
			sync();
		}
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	private final static String POWER_KEY = "power", MANA_KEY = "mana", MAXMANA_KEY = "max_mana",
			MANA_REGEN_KEY = "mana_regen", COOLDOWN_KEY = "cooldown", GRIMOIRE_KEY = "grimoire";

	@Override
	public CompoundNBT toNBT() {
		CompoundNBT retour = new CompoundNBT();
//		retour.put(OWNER_KEY, StringNBT.valueOf(owner.getStringUUID()));
		retour.put(POWER_KEY, FloatNBT.valueOf(this.power));
		retour.put(MANA_KEY, FloatNBT.valueOf(this.currentMana));
		retour.put(MAXMANA_KEY, FloatNBT.valueOf(this.maxMana));
		retour.put(MANA_REGEN_KEY, IntNBT.valueOf(this.manaRegen));
		retour.put(COOLDOWN_KEY, IntNBT.valueOf(this.cooldown));

		// GRIMOIRE
		if (this.grimoire != null) {
			retour.put(GRIMOIRE_KEY, getGrimoire().toNBT());
		}

		return retour;
	}

	@Override
	public void fromNBT(INBT nbt) {
		System.out.println("From nbt:\n\tOLD\n" + this.toNBT().getAsString() + "\n\n\tNEW\n" + nbt.getAsString());
		if (nbt instanceof CompoundNBT) {
			CompoundNBT cnbt = (CompoundNBT) nbt;

//			if (cnbt.contains(OWNER_KEY))
//				this.owner = UUID.fromString(cnbt.getString(OWNER_KEY)).;
			if (cnbt.contains(POWER_KEY))
				this.power = cnbt.getFloat(POWER_KEY);
			if (cnbt.contains(MANA_KEY))
				this.currentMana = cnbt.getFloat(MANA_KEY);
			if (cnbt.contains(MAXMANA_KEY))
				this.maxMana = cnbt.getFloat(MAXMANA_KEY);
			if (cnbt.contains(MANA_REGEN_KEY))
				this.manaRegen = cnbt.getInt(MANA_REGEN_KEY);
			if (cnbt.contains(COOLDOWN_KEY)) {
				this.cooldown = cnbt.getInt(COOLDOWN_KEY);
			}

			// GRIMOIRE
			if (cnbt.contains(GRIMOIRE_KEY)) {
				System.out.println("We got a grimoire");
				try {
					this.grimoire.sync(Grimoire.fromNBT(cnbt.get(GRIMOIRE_KEY)));
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
						| SecurityException | RunePropertiesException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void sync(ServerPlayerEntity player) {
		player.getCapability(CasterCapability.CASTER_CAPABILITY).ifPresent(c -> c.sync());
	}

	@Override
	public void sync(ICaster caster) {
		if (caster instanceof CasterImpl) {
			syncInternal((CasterImpl) caster);
		} else {
			this.power = caster.getPower();
			this.currentMana = caster.getMana();
			this.maxMana = caster.getMaxMana();
			this.manaRegen = caster.getManaRegen();
			this.currentManaCd = caster.getManaRegenTick();
			this.grimoire = caster.getGrimoire();
		}
	}

	private void syncInternal(CasterImpl caster) {
		this.power = caster.power;
		this.currentMana = caster.currentMana;
		this.maxMana = caster.maxMana;
		this.manaRegen = caster.manaRegen;
		this.currentManaCd = caster.currentManaCd;
		this.grimoire = caster.grimoire;
	}

	@Override
	public void sync() {
		if (owner instanceof ServerPlayerEntity) {
			CapabilitySyncHandler.sendTo(new CasterPacket(this.toNBT()), (ServerPlayerEntity) owner);
		}
	}

	@Override
	public float getPower() {
		return power;
	}

	@Override
	public void setPower(float power) {
		this.power = power;
	}

	@Override
	public void delMana(float cost) throws NotEnoughManaException {
		float remainder = cost;
		for (ItemStack item : owner.getAllSlots()) {
			if (item == ItemStack.EMPTY)
				continue;
			if (item.getItem() instanceof ManaSource) {
				float equipmentMana = ((ManaSource) item.getItem()).getMana(item);
				if (remainder > equipmentMana) {
					remainder -= equipmentMana;
					((ManaSource) item.getItem()).useMana(item, equipmentMana);
				} else {
					remainder = 0f;
					((ManaSource) item.getItem()).useMana(item, equipmentMana - remainder);
				}
			}
		}
		if (remainder > 0f)
			delManaInternal(remainder);
	}
}