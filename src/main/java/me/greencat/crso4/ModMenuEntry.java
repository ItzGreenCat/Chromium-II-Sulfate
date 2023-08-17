package me.greencat.crso4;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return (ConfigScreenFactory<Screen>) parent -> CRSO4.config.displayGui(parent);
    }
}
