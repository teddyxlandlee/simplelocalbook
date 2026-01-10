package xland.mcmod.simplelocalbook.fabric;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import xland.mcmod.simplelocalbook.LocalBookSource;
import xland.mcmod.simplelocalbook.SimpleLocalBookMain;

public class SimpleLocalBookFabric {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(SimpleLocalBookFabric::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
                ClientCommandManager.literal("localnotebook")
                        .executes(context -> {
                            SimpleLocalBookMain.openBook(context.getSource().getClient(), LocalBookSource.WORLD);
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(ClientCommandManager.literal("global")
                                .executes(context -> {
                                    SimpleLocalBookMain.openBook(context.getSource().getClient(), LocalBookSource.GLOBAL);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }
}
