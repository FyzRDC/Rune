package fr.emmuliette.rune.mod.capabilities.caster;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import fr.emmuliette.rune.exception.RunePropertiesException;
import fr.emmuliette.rune.mod.capabilities.spell.ISpell;
import fr.emmuliette.rune.mod.capabilities.spell.SpellImpl;
import fr.emmuliette.rune.mod.items.spellItems.SpellItem;
import fr.emmuliette.rune.mod.items.spellItems.SpellItem.ItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class Grimoire {
//	private Map<Integer, ISpell> spellList;
	List<ISpell> spellList;

	public Grimoire() {
		spellList = new ArrayList<ISpell>();
	}

	public boolean addSpell(ISpell spell) {
		addSpellInternal(spell);
		return true;
	}

	public boolean removeSpell(Integer spellId) {
		removeSpellInternal(spellId);
		return true;
	}

	public ISpell getSpell(Integer spellId) {
		return spellList.get(spellId);
	}

	public List<ISpell> getSpells() {
		return spellList;
	}

	public ItemStack getItem(Integer spellId) {
		ISpell spell = this.getSpell(spellId);
		if (spell == null) {
			System.out.println("Spell is null");
			return ItemStack.EMPTY;
		}
		if (spell.getSpell() == null) {
			System.out.println("Spell.getSpell() is null");
			return ItemStack.EMPTY;
		}
		return SpellItem.buildSpellItem(spell.getSpell(), ItemType.SPELL);
//		SpellItem spellitem;
//		spellitem = (SpellItem) ModItems.SPELL.get();
//		ItemStack itemStack = new ItemStack(spellitem);
//		itemStack.addTagElement(AbstractSpellItem.SPELL_ID, IntNBT.valueOf(spellId));
//		return itemStack;
	}

	private void removeSpellInternal(int spellId) {
		spellList.remove(spellId);
	}

	private void addSpellInternal(ISpell spell) {
		spellList.add(spell);
	}

	private static final String SPELL_LIST_KEY = "spell_list";

	public CompoundNBT toNBT() {
		CompoundNBT retour = new CompoundNBT();
		ListNBT sList = new ListNBT();
		for (ISpell spell : spellList) {
			sList.add(spell.toNBT());
		}
		retour.put(SPELL_LIST_KEY, sList);
		return retour;
	}

	public static Grimoire fromNBT(INBT inbt)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, RunePropertiesException {
		Grimoire retour = new Grimoire();
		if (inbt != null && inbt instanceof CompoundNBT) {
			if (((CompoundNBT) inbt).contains(SPELL_LIST_KEY)) {
				ListNBT sList = (ListNBT) ((CompoundNBT) inbt).get(SPELL_LIST_KEY);
				for (INBT spellNBT : sList) {
					if (spellNBT instanceof CompoundNBT) {
						ISpell spell = new SpellImpl();
						spell.fromNBT((CompoundNBT) spellNBT);
						retour.addSpellInternal(spell);
					}
				}
			}
		}
		return retour;
	}

	public void sync(Grimoire other) {
		spellList.clear();
		spellList.addAll(other.spellList);
	}
}
