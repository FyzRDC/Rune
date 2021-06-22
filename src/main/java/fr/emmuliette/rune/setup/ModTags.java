package fr.emmuliette.rune.setup;

import fr.emmuliette.rune.RuneMain;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ModTags {
	
	public static final class Blocks {
		public static final ITag.INamedTag<Block> CASTER_BLOCK = forge("storage_blocks/caster");
		
		private static ITag.INamedTag<Block> forge(String path) {
			return BlockTags.bind(new ResourceLocation("forge", path).toString());
		}
		private static ITag.INamedTag<Block> mod(String path) {
			return BlockTags.bind(new ResourceLocation(RuneMain.MOD_ID, path).toString());
		}
	}
	
	public static final class Items {
		public static final INamedTag<Item> CASTER_BLOCK = forge("storage_blocks/caster");
		public static final INamedTag<Item> BLANK_RUNE = forge("runes/blank");
		
		private static INamedTag<Item> forge(String path) {
			return ItemTags.bind(new ResourceLocation("forge", path).toString());
		}
		private static INamedTag<Item> mod(String path) {
			return ItemTags.bind(new ResourceLocation(RuneMain.MOD_ID, path).toString());
		}
	}

}
