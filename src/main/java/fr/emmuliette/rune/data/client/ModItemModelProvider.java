package fr.emmuliette.rune.data.client;

import javax.annotation.Nonnull;

import fr.emmuliette.rune.RuneMain;
import fr.emmuliette.rune.mod.AbstractModObject;
import fr.emmuliette.rune.mod.ModObjectsManager;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, RuneMain.MOD_ID, existingFileHelper);
	}

	@Nonnull
	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	protected void registerModels() {
		withExistingParent("caster_block", modLoc("block/caster_block"));

		ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
		for(AbstractModObject entity:ModObjectsManager.getItemRegister()) {
//			File f = new File("item/" + entity.getName());
//			if(f.exists() && !f.isDirectory()) { 
			try {	
				builder(itemGenerated, entity.getName());
			} catch(Exception e) {
				System.err.println("Texture for item " + entity.getName() + " doesn't exist ! " + e.getMessage());
			}
//			} else {
//				System.err.println("Texture for item " + entity.getName() + " doesn't exist !");
//			}
		}
	}

	private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
		return getBuilder(name).parent(itemGenerated).texture("layer0","item/"+name);
	}

}
