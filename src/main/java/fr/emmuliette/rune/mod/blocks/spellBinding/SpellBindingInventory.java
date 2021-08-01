package fr.emmuliette.rune.mod.blocks.spellBinding;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;

public class SpellBindingInventory implements IInventory, IRecipeHelperPopulator {
	private final List<ItemStack> items;
	private final Container menu;

	public SpellBindingInventory(Container container, int width, int height) {
		this.items = new ArrayList<ItemStack>(width * height);
		for(int i = 0; i < width * height; ++i)
			this.items.add(ItemStack.EMPTY);
		this.menu = container;
	}

	public int getContainerSize() {
		return this.items.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public ItemStack getItem(int index) {
		return index >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(index);
	}

	public ItemStack removeItemNoUpdate(int index) {
		return ItemStackHelper.takeItem(this.items, index);
	}

	public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
		ItemStack itemstack = ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
		if (!itemstack.isEmpty()) {
			this.menu.slotsChanged(this);
		}

		return itemstack;
	}

	public void setItem(int index, ItemStack itemStack) {
		while(index >= this.items.size()) {
			this.items.add(ItemStack.EMPTY);
		}
		this.items.set(index, itemStack);
		this.menu.slotsChanged(this);
	}

	public void setChanged() {
	}

	public boolean stillValid(PlayerEntity p_70300_1_) {
		return true;
	}

	public void clearContent() {
		this.items.clear();
	}

	public void fillStackedContents(RecipeItemHelper p_194018_1_) {
		for (ItemStack itemstack : this.items) {
			p_194018_1_.accountSimpleStack(itemstack);
		}

	}
}