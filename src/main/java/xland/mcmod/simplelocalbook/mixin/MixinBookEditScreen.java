package xland.mcmod.simplelocalbook.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xland.mcmod.simplelocalbook.LocalBookMarker;

@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen {
    @WrapOperation(method = "saveChanges", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
    ))
    private void captureSavePacket(ClientPacketListener instance, Packet<?> packet, Operation<Void> original) {
        if (this instanceof LocalBookMarker localBook) {
            localBook.simpleLocalBook$saveLocalBook();
        } else {
            original.call(instance, packet);
        }
    }
}
