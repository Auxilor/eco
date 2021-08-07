
import proxy.BlockBreakProxy
import net.minecraft.server.v1_16_R3.BlockPosition
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class BlockBreak : BlockBreakProxy {
    override fun breakBlock(
        player: Player,
        block: Block
    ) {
        if (player !is CraftPlayer) {
            return
        }
        player.handle.playerInteractManager.breakBlock(BlockPosition(block.x, block.y, block.z))
    }
}