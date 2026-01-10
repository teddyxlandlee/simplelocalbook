package xland.mcmod.simplelocalbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SimpleLocalBookMain {
    public static final String MOD_ID = "simplelocalbook";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void openBook(Minecraft client, LocalBookSource bookSource) {
        loadCurrentBookAsync(
                client,
                bookContent -> client.setScreen(LocalBookScreen.create(bookContent, bookSource)),
                bookSource,
                () -> Minecraft.getInstance().getChatListener().handleSystemMessage(
                        Component.translatableWithFallback(
                                "simplelocalbook.load.fail",
                                "Failed to load local notebook content!"
                        ).withStyle(ChatFormatting.DARK_RED),
                        /*overlay=*/false
                ));
    }

    private static final ExecutorService BOOK_IO_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static void loadCurrentBookAsync(Minecraft client, Consumer<WritableBookContent> onLoad, LocalBookSource bookSource, Runnable onError) {
        CompletableFuture.supplyAsync(() -> loadBook(client, bookSource), BOOK_IO_EXECUTOR)
                .thenAccept(c -> client.execute(() -> {
                    if (c != null) {
                        onLoad.accept(c);
                    } else {
                        onError.run();
                    }
                }));
    }

    public static void saveCurrentBookAsync(Minecraft client, WritableBookContent bookContent, LocalBookSource bookSource, Runnable onSuccess, Runnable onError) {
        CompletableFuture.supplyAsync(() -> saveBook(client, bookContent, bookSource), BOOK_IO_EXECUTOR)
                .thenAccept(success -> {
                    Runnable runnable = success ? onSuccess : onError;
                    client.execute(runnable);
                });
    }

    private static @Nullable WritableBookContent loadBook(Minecraft client, LocalBookSource bookSource) {
        Path bookPath = null;
        try {
            bookPath = bookSource.getFile(client);
            if (Files.notExists(bookPath)) {
                Files.createDirectories(bookPath.getParent());
                return WritableBookContent.EMPTY;
            }

            RegistryOps<JsonElement> registryOps = getRegistryOps(client);
            JsonElement encoded;
            try (var reader = Files.newBufferedReader(bookPath)) {
                encoded = GSON.fromJson(reader, JsonElement.class);
            }
            return WritableBookContent.CODEC.parse(registryOps, encoded)
                    .result()
                    .orElseThrow();
        } catch (Exception e) {
            LOGGER.error("Failed to load book from {}", Objects.requireNonNullElse(bookPath, "<unknown>"), e);
            return null;
        }
    }

    private static boolean saveBook(Minecraft client, WritableBookContent bookContent, LocalBookSource bookSource) {
        Path bookPath = null;
        try {
            bookPath = bookSource.getFile(client);
            RegistryOps<JsonElement> registryOps = getRegistryOps(client);
            JsonElement encoded = WritableBookContent.CODEC.encodeStart(registryOps, bookContent)
                    .result()
                    .orElseThrow();
            try (var writer = Files.newBufferedWriter(bookPath)) {
                GSON.toJson(encoded, writer);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save book to {}", Objects.requireNonNullElse(bookPath, "<unknown>"), e);
            return false;
        }
    }

    private static String currentWorldId(Minecraft client) {
        if (client.hasSingleplayerServer()) {
            IntegratedServer server = client.getSingleplayerServer();
            Objects.requireNonNull(server);
            String rawId = server.getWorldPath(LevelResource.ROOT)
                    .normalize()
                    .getFileName()
                    .toString();
            return "singleplayer/" + rawId;
        } else {
            String ip = Optional.ofNullable(client.getConnection())
                    .map(ClientPacketListener::getServerData)
                    .orElseThrow(NullPointerException::new)
                    .ip;
            return "multiplayer/" + ip;
        }
    }

    static Path currentWorldBookPath(Minecraft client) {
        return client.gameDirectory.toPath()
                .resolve("SimpleLocalBook")
                .resolve(currentWorldId(client))
                .resolve("book.json");
    }

    static Path globalBookPath(Minecraft client) {
        return client.gameDirectory.toPath()
                .resolve("SimpleLocalBook")
                .resolve("book_global.json");
    }

    private static RegistryOps<JsonElement> getRegistryOps(Minecraft minecraft) {
        return RegistryOps.create(JsonOps.INSTANCE, Objects.requireNonNull(minecraft.player).registryAccess());
    }
}
