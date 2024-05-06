package com.gmail.picono435.foodcooldown;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FoodCooldown.MODID)
public class FoodCooldown {

    public static final String MODID = "foodcooldown";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static CommentedConfigurationNode config;

    public FoodCooldown() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        try {
            HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder()
                    .path(FMLPaths.CONFIGDIR.get().resolve("foodcooldown.conf"))
                    .build();
            config = configLoader.load();

            if(config.node("cooldowns").virtual()) {
                config.node("cooldowns").node("minecraft:golden_apple").set(30);
                configLoader.save(config);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onEatItem(LivingEntityUseItemEvent.Finish event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!event.getItem().isEdible()) return;
        int cooldown = config.node("cooldowns").node(ForgeRegistries.ITEMS.getKey(event.getItem().getItem()).toString()).getInt();
        if(cooldown > 0) {
            Player player = (Player) event.getEntity();
            player.getCooldowns().addCooldown(event.getItem().getItem(), cooldown * 20);
        }
    }
}
