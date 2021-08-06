
import com.willfp.eco.core.display.Display
import com.willfp.eco.proxy.VillagerTradeProxy
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMerchantRecipe
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe
import java.lang.reflect.Field

class VillagerTrade : VillagerTradeProxy {
    private var handle: Field

    override fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe {
        val oldRecipe = recipe as CraftMerchantRecipe
        val newRecipe = CraftMerchantRecipe(
            Display.display(recipe.getResult().clone()),
            recipe.getUses(),
            recipe.getMaxUses(),
            recipe.hasExperienceReward(),
            recipe.getVillagerExperience(),
            recipe.getPriceMultiplier()
        )
        for (ingredient in recipe.getIngredients()) {
            newRecipe.addIngredient(Display.display(ingredient.clone(), player))
        }
        getHandle(newRecipe).specialPrice = getHandle(oldRecipe).specialPrice
        return newRecipe
    }

    private fun getHandle(recipe: CraftMerchantRecipe): net.minecraft.server.v1_16_R3.MerchantRecipe {
        try {
            return handle[recipe] as net.minecraft.server.v1_16_R3.MerchantRecipe
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        throw IllegalArgumentException("Not CMR")
    }

    init {
        try {
            handle = CraftMerchantRecipe::class.java.getDeclaredField("handle")
            handle.isAccessible = true
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            throw RuntimeException("Error!")
        }
    }
}