
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import proxy.SkullProxy
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Method
import java.util.*

class Skull : SkullProxy {
    private var setProfile: Method? = null

    override fun setSkullTexture(
        meta: SkullMeta,
        base64: String
    ) {
        try {
            if (setProfile == null) {
                setProfile = meta.javaClass.getDeclaredMethod("setProfile", GameProfile::class.java)
                setProfile!!.isAccessible = true
            }
            val uuid = UUID(
                base64.substring(base64.length - 20).hashCode().toLong(),
                base64.substring(base64.length - 10).hashCode().toLong()
            )
            val profile = GameProfile(uuid, "eco")
            profile.properties.put("textures", Property("textures", base64))
            setProfile!!.invoke(meta, profile)
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
        }
    }
}