package fr.emmuliette.rune.mod.spells.entities;

import java.util.Random;
import java.util.UUID;

import fr.emmuliette.rune.exception.NotEnoughManaException;
import fr.emmuliette.rune.mod.capabilities.caster.CasterCapability;
import fr.emmuliette.rune.mod.capabilities.caster.ICaster;
import fr.emmuliette.rune.mod.spells.SpellContext;
import fr.emmuliette.rune.mod.spells.AI.castAI.CastAI;
import fr.emmuliette.rune.mod.spells.AI.moveAI.MoveAI;
import fr.emmuliette.rune.mod.spells.AI.renderAI.RenderAI;
import fr.emmuliette.rune.mod.spells.AI.targetAI.ClosestEntityAI;
import fr.emmuliette.rune.mod.spells.AI.targetAI.TargetAI;
import fr.emmuliette.rune.mod.spells.component.structureComponent.MagicEntityComponent;
import fr.emmuliette.rune.mod.spells.component.structureComponent.AI.AbstractAIComponent;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.EntityAnchorArgument.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.network.NetworkHooks;

public class MagicEntity extends Entity {
	private static final DataParameter<Integer> DATA_ID_MANA = EntityDataManager.defineId(MagicEntity.class,
			DataSerializers.INT);
	private UUID ownerUUID;

	private SpellContext context;
	private MagicEntityComponent<?> component;

	private BlockPos startingPosition;

	private MoveAI moveAI = MoveAI.DEFAULT.get();
	private TargetAI targetAI = new ClosestEntityAI(false);// TargetAI.DEFAULT;;
	private CastAI castAI = CastAI.DEFAULT.get();
	private RenderAI renderAI = RenderAI.DEFAULT.get();
	private int mana;
	// private IA brain;
	// Used to define comportment, aka where yo go, when to cast spell, how long
	// survive
	// Where to go: don't move, toward/backward closest entity (ally/ennemy),
	// to/back from caster, move in circle around caster ?
	// When to cast spell: Contact ? Every interval of time ? Continuously ?
	// how long to survive: amount of spell detemined ? amount of time ?

	public MagicEntity(EntityType<? extends MagicEntity> magicEntity, World world) {
		super(magicEntity, world);
		this.noPhysics = true;
		this.setInvulnerable(true);
	}

	public MagicEntity(SpellContext context, MagicEntityComponent<?> component, World world, BlockPos position) {
		this(ModEntities.MAGIC_ENTITY.get(), world);
		this.setPos(position.getX(), position.getY() + 1, position.getZ());
		this.refreshDimensions();
		this.mana = 0;
		this.context = context;
		this.startingPosition = position;
		this.lookAt(Type.EYES, context.getCaster().getPosition(0));
		context.setCurrentCaster(this);
		context.setBlock(null);
		context.setTarget(null);
		this.component = component;
		for (AbstractAIComponent aiComp : component.getAIChildren()) {
			if (aiComp.getAI(context) instanceof MoveAI) {
				moveAI = (MoveAI) aiComp.getAI(context);
				continue;
			}
			if (aiComp.getAI(context) instanceof TargetAI) {
				targetAI = (TargetAI) aiComp.getAI(context);
				continue;
			}
			if (aiComp.getAI(context) instanceof CastAI) {
				castAI = (CastAI) aiComp.getAI(context);
				continue;
			}
			if (aiComp.getAI(context) instanceof RenderAI) {
				renderAI = (RenderAI) aiComp.getAI(context);
				continue;
			}
		}
	}

	public void setTarget(LivingEntity target) {
		context.setTarget(target);
	}

	@Override
	public ActionResultType interact(PlayerEntity player, Hand hand) {
		System.out.println("INTERACTING !!!");
		// TODO chargeable Magic Entity
		if (!this.level.isClientSide) { // TODO changer par this.isChargeable()
			ActionResultType retour[] = new ActionResultType[] { ActionResultType.PASS };
			player.getCapability(CasterCapability.CASTER_CAPABILITY).ifPresent(new NonNullConsumer<ICaster>() {
				@Override
				public void accept(ICaster cap) {
					System.out.println("GIVING ONE MANA");
					if (cap.getMana() >= 1f) {
						try {
							cap.delMana(1f);
							addMana(1);
							System.out.println("MANA GIVETH");
							retour[0] = ActionResultType.SUCCESS;
							player.startUsingItem(hand);
						} catch (NotEnoughManaException e) {
							e.printStackTrace();
						}
					}
				}
			});
			return retour[0];
		}
		return super.interact(player, hand);
	}

	public ActionResultType interactAt(PlayerEntity player, Vector3d dir, Hand hand) {
		System.out.println("INTERACT AT !!!");
		if (!this.level.isClientSide) { // TODO changer par this.isChargeable()
			ActionResultType retour[] = new ActionResultType[] { ActionResultType.PASS };
			player.getCapability(CasterCapability.CASTER_CAPABILITY).ifPresent(new NonNullConsumer<ICaster>() {
				@Override
				public void accept(ICaster cap) {
					System.out.println("GIVING ONE MANA");
					if (cap.getMana() >= 1f) {
						try {
							cap.delMana(1f);
							addMana(1);
							System.out.println("MANA GIVETH");
							retour[0] = ActionResultType.SUCCESS;
							player.startUsingItem(hand);
						} catch (NotEnoughManaException e) {
							e.printStackTrace();
						}
					}
				}
			});
			return retour[0];
		}
		return super.interact(player, hand);
	}

	/*
	 * @Override public void playerTouch(PlayerEntity player) {
	 * System.out.println("PLAYER TOUCH !!!"); super.playerTouch(player); }
	 */

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_ID_MANA, 0);
		/*
		 * this.getEntityData().define(DATA_COLOR, 0);
		 * this.getEntityData().define(DATA_RADIUS, 0.5F);
		 * this.getEntityData().define(DATA_WAITING, false);
		 * this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
		 */
	}

	/*
	 * @Override public void refreshDimensions() { double d0 = this.getX(); double
	 * d1 = this.getY(); double d2 = this.getZ(); super.refreshDimensions();
	 * this.setPos(d0, d1, d2); }
	 */

	@Override
	public void tick() {
		super.tick();
		if (this.level.isClientSide) {
			renderAI.render(this);
		} else {
			if (getContext() == null || !castAI.isAlive(getContext(), this)) {
				this.remove();
				return;
			}
			moveAI.move(this);
			LivingEntity target = targetAI.getEntityTarget(this);
			BlockPos block = targetAI.getBlockTarget(this);
			getContext().setTarget(target);
			if (target != null) {
				this.lookAt(Type.EYES, target.getPosition(0));
				this.lookAt(Type.FEET, target.getPosition(0));
				System.out.println("TARGET IS " + target.getName());
			}
			getContext().setBlock(block);
			if (block != null) {
				this.lookAt(Type.EYES, new Vector3d(block.getX(), block.getY(), block.getZ()));
				this.lookAt(Type.FEET, new Vector3d(block.getX(), block.getY(), block.getZ()));
			}
			// TODO updateContext here
			if (castAI.canCast(getContext(), this)) {
				component.castChildren(getContext());
				castAI.cast();
			}
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		this.tickCount = nbt.getInt("Age");
		this.mana = nbt.getInt("Mana");
		if (nbt.hasUUID("Owner")) {
			this.ownerUUID = nbt.getUUID("Owner");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		nbt.putInt("Age", this.tickCount);
		nbt.putFloat("Mana", this.mana);
		if (this.ownerUUID != null) {
			nbt.putUUID("Owner", this.ownerUUID);
		}
	}

	@Override
	public boolean isAttackable() {
		return super.isAttackable();
		// return false;
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
		this.refreshDimensions();
		super.onSyncedDataUpdated(p_184206_1_);
	}

	public RenderAI getRenderAI() {
		return renderAI;
	}

	public SpellContext getContext() {
		return context;
	}

	public BlockPos getStartingPosition() {
		return startingPosition;
	}

	public void setMana(int newMana) {
		this.entityData.set(DATA_ID_MANA, newMana);
	}

	public int getMana() {
		return this.entityData.get(DATA_ID_MANA);
	}

	public void addMana(int mana) {
		this.mana += mana;
	}

	public Random getRandom() {
		return this.random;
	}

	@Override
	public boolean canBeCollidedWith() {
		// return false;
		return true; // TODO return true only if can be interacted with
		// Can be interacted for: registering player
		// giving mana
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	public void render() {
		renderAI.render(this);
	}
}