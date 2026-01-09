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

public class LocalBookScreen extends BookEditScreen implements LocalBookMarker {
    private LocalBookScreen(LocalPlayer player, ItemStack book, WritableBookContent bookContent) {
        super(player, book, InteractionHand.OFF_HAND, bookContent);
        this.bookReference = book;
    }

    private final ItemStack bookReference;
    private Button doneButton;

    public static LocalBookScreen create(WritableBookContent bookContent) {
        ItemStack book = Items.WRITABLE_BOOK.getDefaultInstance();
        book.set(DataComponents.WRITABLE_BOOK_CONTENT, bookContent);
        return new LocalBookScreen(Minecraft.getInstance().player, book, bookContent);
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
        this.renderables.stream()
                .filter(r -> r instanceof Button button &&
                        button.getMessage().getContents() instanceof TranslatableContents contents &&
                        "book.signButton".equals(contents.getKey()))
                .map(Button.class::cast)
                .findFirst()
                .ifPresent(this::invalidateSignButton);
        this.doneButton = this.renderables.stream()
                .filter(r -> r instanceof Button button &&
                        button.getMessage().getContents() instanceof TranslatableContents contents &&
                        "gui.done".equals(contents.getKey()))
                .map(Button.class::cast)
                .findFirst()
                .orElse(null);
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
                    Minecraft.getInstance(), component,
                    () -> { // onSuccess
                        Minecraft.getInstance().getChatListener().handleSystemMessage(
                                Component.translatable("simplelocalbook.save.success").withStyle(ChatFormatting.GREEN),
                                /*overlay=*/true
                        );
                    },
                    () -> { // onFail
                        Minecraft.getInstance().getChatListener().handleSystemMessage(
                                Component.translatable("simplelocalbook.save.fail").withStyle(ChatFormatting.DARK_RED),
                                /*overlay=*/false
                        );
                    }
            );
        }
    }
}
