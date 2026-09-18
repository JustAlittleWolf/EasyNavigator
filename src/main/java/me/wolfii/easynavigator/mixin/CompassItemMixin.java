package me.wolfii.easynavigator.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.wolfii.easynavigator.item.EasyNavigatorComponentData;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CompassItem.class)
public class CompassItemMixin {
    @WrapMethod(method = "isFoil")
    public boolean removeGlint(ItemStack itemStack, Operation<Boolean> original) {
        if (itemStack.getComponents().getOrDefault(EasyNavigatorComponentData.REMOVE_ENCHANTMENT_GLINT, false)) {
            return false;
        }
        return original.call(itemStack);
    }
}
