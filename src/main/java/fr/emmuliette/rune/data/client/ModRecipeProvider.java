package fr.emmuliette.rune.data.client;

import java.util.function.Consumer;

import fr.emmuliette.rune.mod.ModObjects;
import fr.emmuliette.rune.mod.NotABlockException;
import fr.emmuliette.rune.mod.NotAnItemException;
import fr.emmuliette.rune.setup.Registration;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

public class ModRecipeProvider extends RecipeProvider {
	public ModRecipeProvider(DataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		try {
			ShapedRecipeBuilder.shaped(ModObjects.CASTER_BLOCK.getModBlock())
					.define('A', ModObjects.BLANK_RUNE.getModItem()).define('B', ItemTags.LOGS).pattern("BBB")
					.pattern("BAB").pattern("BBB").unlockedBy("has_item", has(ModObjects.BLANK_RUNE.getModItem()))
					.unlockedBy("has_log", has(ItemTags.LOGS)).save(consumer);
			;
		} catch (NotABlockException | NotAnItemException e) {
			e.printStackTrace();
		}

		try {
			ShapelessRecipeBuilder.shapeless(ModObjects.BLANK_RUNE.getModItem()).requires(Items.FLINT)
					.requires(Items.STONE).unlockedBy("has_flint", has(Items.FLINT))
					.unlockedBy("has_stone", has(Items.STONE)).save(consumer);
		} catch (NotAnItemException e) {
			e.printStackTrace();
		}

		CustomRecipeBuilder.special(Registration.SPELL_RECIPE.get()).save(consumer, "spell_recipe");
	}
}