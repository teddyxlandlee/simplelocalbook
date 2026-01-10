package xland.mcmod.simplelocalbook;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;

import java.util.function.Predicate;

public class LocalBookScreen extends BookEditScreen implements LocalBookMarker {
    private LocalBookScreen(LocalPlayer player, ItemStack book, WritableBookContent bookContent, LocalBookSource bookSource) {
        super(player, book, InteractionHand.OFF_HAND, bookContent);
        this.bookSource = bookSource;
        this.bookReference = book;
    }

    private final LocalBookSource bookSource;
    private final ItemStack bookReference;
    private Button doneButton;

    public static LocalBookScreen create(WritableBookContent bookContent, LocalBookSource bookSource) {
        ItemStack book = Items.WRITABLE_BOOK.getDefaultInstance();
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, bookContent);
        return new LocalBookScreen(Minecraft.getInstance().player, book, bookContent, bookSource);
    }

    private void invalidateSignButton(Button button) {
        button.active = false;
        MutableComponent message = button.getMessage().copy();
        message.withStyle(s -> s.withHoverEvent(new HoverEvent.ShowText(
                Component.translatable("simplelocalbook.signButton.disabled")
        )));
        button.setMessage(message);
    }

    @Override
    protected void init() {
        super.init();
        this.children().stream()
                .filter(filterButtonTranslationKey("book.signButton"))
                .map(Button.class::cast)
                .findFirst()
                .ifPresent(this::invalidateSignButton);
        this.doneButton = this.children().stream()
                .filter(filterButtonTranslationKey("gui.done"))
                .map(Button.class::cast)
                .findFirst()
                .orElse(null);
    }

    private static Predicate<Object> filterButtonTranslationKey(String key) {
        return r -> r instanceof Button button &&
                button.getMessage().getContents() instanceof TranslatableContents contents &&
                key.equals(contents.getKey());
    }

    @Override
    public void onClose() {
        if (this.doneButton == null) {
            super.onClose();
        } else {
            this.doneButton.onPress();
        }
    }

    @Override
    public void simpleLocalBook$saveLocalBook() {
        WritableBookContent component = bookReference.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (component != null) {
            SimpleLocalBookMain.saveCurrentBookAsync(
                    Minecraft.getInstance(), component, bookSource,
                    () -> { // onSuccess
                        Minecraft.getInstance().getChatListener().handleSystemMessage(
                                Component.translatableWithFallback(
                                        "simplelocalbook.save.success",
                                        "Saved local notebook content!"
                                ).withStyle(ChatFormatting.GREEN),
                                /*overlay=*/true
                        );
                    },
                    () -> { // onFail
                        Minecraft.getInstance().getChatListener().handleSystemMessage(
                                Component.translatableWithFallback(
                                        "simplelocalbook.save.fail",
                                        "Failed to save local notebook content!"
                                ).withStyle(ChatFormatting.DARK_RED),
                                /*overlay=*/false
                        );
                    }
            );
        }
    }
}
