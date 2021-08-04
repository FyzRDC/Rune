package fr.emmuliette.rune.mod.capabilities.caster;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import fr.emmuliette.rune.mod.RunePropertiesException;
import fr.emmuliette.rune.mod.capabilities.spell.ISpell;
import fr.emmuliette.rune.mod.capabilities.spell.SpellImpl;
import fr.emmuliette.rune.mod.gui.grimoire.GrimoireInventory;
import fr.emmuliette.rune.mod.items.GrimoireSpellItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public class Grimoire {
	private Map<String, ISpell> spellList;
	private GrimoireInventory inventory;

	public Grimoire() {
		spellList = new TreeMap<String, ISpell>();
		initInventory();
	}
	
	public void initInventory() {
		inventory = new GrimoireInventory();
		inventory.init(this);
	}

	public GrimoireInventory getInventory() {
		return inventory;
	}

	public boolean addSpell(ISpell spell) {
		System.out.println("ADDING THE SPELL TO THE GRIMOIRE CONTAINING " + spellList.size());
		addSpellInternal(spell);
		System.out.println("THERE ARE NOW " + spellList.size() + " SPELLS ");
		return true;
	}

	public boolean removeSpell(String name) {
		removeSpellInternal(name);
		return true;
	}

	public ISpell getSpell(String name) {
		return spellList.get(name);
	}

	public ArrayList<ISpell> getSpells() {
		return new ArrayList<ISpell>(spellList.values());
	}

	public ItemStack getItem(String spellName) {
		return GrimoireSpellItem.getGrimoireSpell(this, spellName);
	}

	public ItemStack getItem(int index) {
		return GrimoireSpellItem.getGrimoireSpell(this, getSpells().get(index).getSpell().getName());
	}

	private void removeSpellInternal(String name) {
		spellList.remove(name);
	}

	private void addSpellInternal(ISpell spell) {
		System.out.println("XXXADDING THE SPELL TO THE GRIMOIRE CONTAINING " + spellList.size());
		spellList.put(spell.getSpell().getName(), spell);
		initInventory();
		System.out.println("XXXTHERE ARE NOW " + spellList.size() + " SPELLS ");
	}

	private static final String SPELL_LIST_KEY = "spell_list";

	public CompoundNBT toNBT() {
		CompoundNBT retour = new CompoundNBT();
		ListNBT sList = new ListNBT();
		for (ISpell spell : spellList.values()) {
			sList.add(spell.toNBT());
		}
		retour.put(SPELL_LIST_KEY, sList);
		return retour;
	}

	public static Grimoire fromNBT(INBT inbt)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, RunePropertiesException {
		Grimoire retour = new Grimoire();
		System.out.println("[G] from nbt");
		if (inbt != null && inbt instanceof CompoundNBT) {
			System.out.println("[G] CompoundNBT found");
			if (((CompoundNBT) inbt).contains(SPELL_LIST_KEY)) {
				System.out.println("[G] CompoundNBT contains SPELL_LIST_KEY");
				ListNBT sList = (ListNBT) ((CompoundNBT) inbt).get(SPELL_LIST_KEY);
				System.out.println("[G] list is of size " + sList.size());
				for (INBT spellNBT : sList) {
					System.out.println("[G] Got spell nbt: " + spellNBT.getAsString());
					if (spellNBT instanceof CompoundNBT) {
						System.out.println("[G] Spell is compound, creating ISpell and puting it in grimoire");
						ISpell spell = new SpellImpl();
						spell.fromNBT((CompoundNBT) spellNBT);
						retour.addSpellInternal(spell);
					}
				}
			}
		}
		retour.initInventory();
		return retour;
	}

	public void sync(Grimoire other) {
		System.out.println(
				"Syncing. our: " + this.spellList.size() + " spells, other: " + other.spellList.size() + " spells.");
		spellList.clear();
		for (String spellName : other.spellList.keySet()) {
			System.out.println("We got spell " + spellName);
			if (!spellList.containsKey(spellName)) {
				System.out.println("Adding " + spellName + " to the grimoire");
				spellList.put(spellName, other.spellList.get(spellName));
			}
		}
		initInventory();
	}
}