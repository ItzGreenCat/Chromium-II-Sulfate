package me.greencat.crso4;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Properties;


public class ModConfig {

    public static Properties properties = new Properties();
    @ConfigEntry
    public boolean isRenderThreadEnabled;
    @ConfigEntry
    public boolean isMainThreadEnabled;
    public Screen displayGui(Screen province){
        try {
            properties.load(new FileInputStream(new File(MinecraftClient.getInstance().runDirectory, "config/CrSO4.properties")));
            properties.forEach((key,value) -> {
                ModConfig instance = CRSO4.config;
                for(Field field : ModConfig.class.getDeclaredFields()){
                    boolean hasAnnotations = false;
                    for(Annotation annotation : field.getAnnotations()){
                        if(annotation instanceof ConfigEntry){
                            hasAnnotations = true;
                            break;
                        }
                    }
                    if(hasAnnotations){
                        field.setAccessible(true);
                        if(field.getName().equals(key)){
                            try {
                                if(Boolean.class.getName().toLowerCase().contains(field.getType().getName().toLowerCase())) {
                                    field.set(instance,Boolean.valueOf(value.toString()));
                                }
                                if(Integer.class.getName().toLowerCase().contains(field.getType().getName().toLowerCase())) {
                                    field.set(instance,Integer.valueOf(value.toString()));
                                }
                                if(Double.class.getName().toLowerCase().contains(field.getType().getName().toLowerCase())) {
                                    field.set(instance,Double.valueOf(value.toString()));
                                }
                                if(Float.class.getName().toLowerCase().contains(field.getType().getName().toLowerCase())) {
                                    field.set(instance,Float.valueOf(value.toString()));
                                }
                                if(String.class.getName().toLowerCase().contains(field.getType().getName().toLowerCase())) {
                                    field.set(instance,value);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch(Exception e){
            e.printStackTrace();
        }
        //Generate Gui Builder
        YetAnotherConfigLib.Builder guiBuilder = YetAnotherConfigLib.createBuilder();
        guiBuilder.title(Text.literal("Chromium(II) Sulfate Mod Configure"));
        //Generate Category Builder
        ConfigCategory.Builder commonCategoryBuilder = ConfigCategory.createBuilder();
        commonCategoryBuilder.name(Text.literal("Common"));
        //Generate OptionGroup Builder
        OptionGroup.Builder threadOptionGroupBuilder = OptionGroup.createBuilder();
        threadOptionGroupBuilder.name(Text.literal("Thread Log Controller"));
        //Add Options
        threadOptionGroupBuilder.option(Option.<Boolean>createBuilder().name(Text.literal("Render thread Log")).binding(false,() -> isRenderThreadEnabled,newVal -> isRenderThreadEnabled = newVal).controller(TickBoxControllerBuilder::create).build());
        threadOptionGroupBuilder.option(Option.<Boolean>createBuilder().name(Text.literal("Main thread Log")).binding(false,() -> isMainThreadEnabled,newVal -> isMainThreadEnabled = newVal).controller(TickBoxControllerBuilder::create).build());

        //add OptionGroup into Category
        commonCategoryBuilder.group(threadOptionGroupBuilder.build());
        //add Category into Gui
        guiBuilder.category(commonCategoryBuilder.build());
        //Build Gui
        guiBuilder.save(() -> {
            ModConfig instance = CRSO4.config;
            for (Field field : ModConfig.class.getDeclaredFields()) {
                boolean hasAnnotations = false;
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation instanceof ConfigEntry) {
                        hasAnnotations = true;
                        break;
                    }
                }
                if (hasAnnotations) {
                    field.setAccessible(true);
                    try {
                        properties.setProperty(field.getName(),field.get(instance).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                properties.store(new FileOutputStream(new File(MinecraftClient.getInstance().runDirectory, "config/CrSO4.properties")),null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        YetAnotherConfigLib guiInstance = guiBuilder.build();
        //display Gui
        return guiInstance.generateScreen(province);
    }
}
