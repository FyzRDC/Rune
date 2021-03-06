package fr.emmuliette.rune.setup.modelBake;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * Created by TGG on 20/10/2016.
 */
public class RuneItemOverrideList extends ItemOverrideList {
	public RuneItemOverrideList() {
		super();
	}

	/**
	 * getModelWithOverrides() is used to create/select a suitable IBakedModel based
	 * on the itemstack information. For vanilla, the ItemOverrideList contains a
	 * list of IBakedModels, each with a corresponding ItemOverride (predicate),
	 * read in from the item json, which matches a PropertyOverride on the item. See
	 * mbe12 (ItemNBTAnimate) for example In this case, we extend ItemOverrideList
	 * to return a dynamically-generated series of BakedQuads, instead of relying on
	 * a fixed BakedModel. Typically you would use this by extracting NBT
	 * information from the itemstack and customising the quads based on that. I've
	 * just used the itemstack count because it's easier / less complicated. It's
	 * probably safest to return a new model or at least an immutable one, rather
	 * than modifying the originalModel passed in, in case the rendering is
	 * multithreaded (block rendering has this problem, for example).
	 * 
	 * @param originalModel
	 * @param stack
	 * @param world
	 * @param entity
	 * @return // old name: getModelWithOverrides
	 */
	@Override
	public IBakedModel resolve(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world,
			@Nullable LivingEntity entity) {
		int numberOfChessPieces = 0;
		if (stack != null) {
			numberOfChessPieces = stack.getCount();
		}
		return new RuneFinalisedModel(originalModel, numberOfChessPieces);
	}
}
