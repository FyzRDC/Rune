package fr.emmuliette.rune.mod.gui.grimoire;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.emmuliette.rune.RuneMain;
import fr.emmuliette.rune.exception.CasterCapabilityException;
import fr.emmuliette.rune.exception.CasterCapabilityExceptionSupplier;
import fr.emmuliette.rune.mod.SyncHandler;
import fr.emmuliette.rune.mod.capabilities.caster.CasterCapability;
import fr.emmuliette.rune.mod.capabilities.caster.Grimoire;
import fr.emmuliette.rune.mod.capabilities.caster.ICaster;
import fr.emmuliette.rune.mod.capabilities.spell.ISpell;
import fr.emmuliette.rune.mod.gui.grimoire.spellPage.GrimoireSpellPage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GrimoireScreen extends ContainerScreen<GrimoireContainer> implements IContainerListener {
	public static final ResourceLocation GRIMOIRE_LOCATION = new ResourceLocation(RuneMain.MOD_ID,
			"textures/gui/grimoire.png");

//	private final GrimoireGui grimoireGui = new GrimoireGui();
//	private GrimoireContainer container;
	private boolean widthTooNarrow;
	private Grimoire grimoire;
	ISpell selectedSpell;
	private int spellCount;
	GrimoireSpellPage spellPage = new GrimoireSpellPage();

	public GrimoireScreen(GrimoireContainer container, PlayerInventory playerInventory, ITextComponent textComp) {
		super(container, playerInventory, textComp);
	}

	public int getSpellCount() {
		return spellCount;
	}

	public ISpell getSelectedSpell() {
		return selectedSpell;
	}

	public Grimoire getGrimoire() {
		return grimoire;
	}

	void selectSpell(int spellId) {
		selectedSpell = grimoire.getSpell(spellId);
		this.leftPos = this.spellPage.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
		System.out.println("Selected spell is now " + selectedSpell.getSpell().getName());
	}

	public void getSpellServer(final int spellId) {
		System.out.println("Getting spell in slot " + spellId);
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(CGrimoireGetSpellPacket.SPELL_ID, spellId);
		SyncHandler.sendToServer(new CGrimoireGetSpellPacket(nbt));
	}

	protected void init() {
		super.init();
		this.widthTooNarrow = this.width < 379;
		this.imageWidth = 176;
		this.imageHeight = 181;
		initSpellPage();
		this.menu.addSlotListener(this);
		this.setInitialFocus(this.spellPage);
		minecraft.player.containerMenu = menu;
	}

	private void initSpellPage() {
		this.spellPage.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this);
		this.leftPos = this.spellPage.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
		this.children.add(this.spellPage);
		this.spellPage.initVisuals(this.widthTooNarrow);
	}

	public void tick() {
		super.tick();
	}

	public void render(MatrixStack mStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(mStack);
		super.render(mStack, p_230430_2_, p_230430_3_, p_230430_4_);
		if (selectedSpell != null) {
			spellPage.render(mStack, p_230430_2_, p_230430_3_, p_230430_4_);
		}
		this.renderTooltip(mStack, p_230430_2_, p_230430_3_);

	}

	@SuppressWarnings("deprecation")
	protected void renderBg(MatrixStack mStack, float p_230430_4_, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = this.topPos;
		this.minecraft.getTextureManager().bind(GRIMOIRE_LOCATION);
		mStack.pushPose();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.blit(mStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		mStack.popPose();
	}

	protected void renderSpellPage(MatrixStack mStack, int mouseX, int mouseY, float useless) {
		if (getSelectedSpell() == null)
			return;
		this.font.draw(mStack, getSelectedSpell().getSpell().getName(), this.width / 2, 8, 4210752);
	}

	public FontRenderer getFont() {
		return font;
	}

	@Override
	protected void renderLabels(MatrixStack mStack, int x, int y) {
		this.font.draw(mStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
	}

	@Override
	protected boolean isHovering(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_,
			double p_195359_7_) {
		return super.isHovering(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
	}

	@Override
	public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
//		if (this.grimoireGui.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
//			this.setFocused(this.grimoireGui);
//			return true;
//		} else {
		return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
//		}
	}

	@Override
	protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_,
			int p_195361_7_) {
		return super.hasClickedOutside(p_195361_1_, p_195361_3_, p_195361_5_, p_195361_6_, p_195361_7_);
//		boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_
//				|| p_195361_1_ >= (double) (p_195361_5_ + this.imageWidth)
//				|| p_195361_3_ >= (double) (p_195361_6_ + this.imageHeight);
//		return this.grimoireGui.hasClickedOutside(p_195361_1_, p_195361_3_, this.leftPos, this.topPos, this.imageWidth,
//				this.imageHeight, p_195361_7_) && flag;
	}

	@Override
	protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
		super.slotClicked(slot, mouseX, mouseY, clickType);
	}

	public void removed() {
		if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
			this.minecraft.player.inventoryMenu.removeSlotListener(this);
		}
//		this.grimoireGui.removed();
		super.removed();
	}

	public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
		this.buttons.clear();
		try {
			ICaster cap = this.inventory.player.getCapability(CasterCapability.CASTER_CAPABILITY)
					.orElseThrow(new CasterCapabilityExceptionSupplier(this.inventory.player));
			grimoire = cap.getGrimoire();
			spellCount = grimoire.getSpells().size();

			for (int i = 0; i < spellCount; i++) {
				this.addButton(new SpellButton(this, grimoire, i, this.leftPos + 16, this.topPos + 16 + 16 * i));
			}

		} catch (CasterCapabilityException e) {
			e.printStackTrace();
		}
	}

	public void slotChanged(Container container, int slot, ItemStack item) {
	}

	public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
	}
}