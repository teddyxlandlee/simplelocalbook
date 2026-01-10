package xland.mcmod.simplelocalbook;

import net.minecraft.client.Minecraft;

import java.nio.file.Path;
import java.util.function.Function;

public enum LocalBookSource {
    WORLD(SimpleLocalBookMain::currentWorldBookPath),
    GLOBAL(SimpleLocalBookMain::globalBookPath),
    ;
    private final Function<Minecraft, Path> getFile;

    LocalBookSource(Function<Minecraft, Path> getFile) {
        this.getFile = getFile;
    }

     public Path getFile(Minecraft client) {
        return getFile.apply(client);
    }
}
