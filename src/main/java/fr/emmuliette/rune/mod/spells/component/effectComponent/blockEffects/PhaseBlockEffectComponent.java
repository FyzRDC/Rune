package fr.emmuliette.rune.mod.spells.component.effectComponent.blockEffects;

import fr.emmuliette.rune.mod.spells.SpellContext;
import fr.emmuliette.rune.mod.spells.component.AbstractSpellComponent;
import fr.emmuliette.rune.mod.spells.cost.Cost;
import fr.emmuliette.rune.mod.spells.cost.ManaCost;
import fr.emmuliette.rune.mod.spells.entities.blockEffect.PhaseBlockEffectEntity;
import fr.emmuliette.rune.mod.spells.properties.BlockGrid;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhaseBlockEffectComponent extends BlockEffectComponent {
	public PhaseBlockEffectComponent(AbstractSpellComponent parent) {
		super(parent);
	}

	@Override
	public Cost<?> getCost() {
		Cost<?> retour = new ManaCost(1);
		retour.add(super.getCost());
		return retour;
	}

	@Override
	protected boolean blockEffects(World world, BlockPos block, SpellContext context) {
//		Direction direction = Direction.byName(this.getEnumProperty(KEY_DIRECTION));
		BlockGrid grid = this.getGridProperty(KEY_BLOCKS_LEVEL);
		for (BlockPos newBlock : grid.getBlockPos(world, block)) {
			System.out.println("Applying on " + newBlock.toShortString());
			world.setBlockAndUpdate(newBlock, Blocks.AIR.defaultBlockState());
		}
		PhaseBlockEffectEntity bee = new PhaseBlockEffectEntity(world, block, grid, 120);
		world.addFreshEntity(bee);
		return true;
	}
}
