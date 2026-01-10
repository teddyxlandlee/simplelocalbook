package xland.mcmod.simplelocalbook.neo;

import com.mojang.brigadier.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import xland.mcmod.simplelocalbook.SimpleLocalBookMain;

@Mod(value = SimpleLocalBookMain.MOD_ID, dist = Dist.CLIENT)
public class SimpleLocalBookNeo {
    public SimpleLocalBookNeo() {
        NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, this::registerCommand);
    }

    private void registerCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("localnotebook")
                        .executes(context -> {
                            SimpleLocalBookMain.openBook(Minecraft.getInstance());
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
