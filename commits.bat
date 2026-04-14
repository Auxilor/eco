@echo off
setlocal enabledelayedexpansion

git checkout -b "fix/eco_perf_bug_fix"
if errorlevel 1 exit /b 1

REM === HIGH PRIORITY FIXES ===

REM H1: Make PDC initialization lazy in EcoFastItemStack
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/item/EcoFastItemStack.kt"
git commit -m "perf(H1): Lazy PDC initialization in EcoFastItemStack"
if errorlevel 1 exit /b 1

REM H2: Skip PDC serialization when unchanged
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/item/EcoFastItemStack.kt"
git commit -m "perf(H2): Skip PDC serialization in apply() when PDC is untouched"
if errorlevel 1 exit /b 1

REM H3: Avoid full NMS-Bukkit round-trip for unchanged slots
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/packet/display/PacketWindowItems.kt"
git commit -m "perf(H3): Only convert changed slots in PacketWindowItems"
if errorlevel 1 exit /b 1

REM H4: Avoid String[] allocation on config path lookups
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoConfig.kt"
git commit -m "perf(H4): Replace path.split with indexOf/substring in EcoConfig"
if errorlevel 1 exit /b 1

REM H5: Cache reflected Field in PacketSetCursorItem
git add "eco-core/core-nms/v1_21_5/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/v1_21_5/packet/PacketSetCursorItem.kt"
git commit -m "perf(H5): Cache reflection Field in PacketSetCursorItem"
if errorlevel 1 exit /b 1

REM H6: Fix expression cache hash collisions
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/math/DelegatedExpressionHandler.kt"
git commit -m "fix(H6): Use composite cache key for math expression cache"
if errorlevel 1 exit /b 1

REM H7: Fix defaultPage delegating to maxPages
git add "eco-api/src/main/java/com/willfp/eco/core/gui/menu/MenuBuilder.java"
git commit -m "fix(H7): Fix MenuBuilder.defaultPage(int) delegation target"
if errorlevel 1 exit /b 1

REM H8: Fix HashedItem.equals hash-only comparison
git add "eco-api/src/main/java/com/willfp/eco/core/items/HashedItem.java"
git commit -m "fix(H8): Fix HashedItem.equals to handle hash collisions"
if errorlevel 1 exit /b 1

REM H9: Fix double onExecute for Player senders
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/command/HandledCommand.kt"
git commit -m "fix(H9): Fix HandledCommand executing onExecute twice for Players"
if errorlevel 1 exit /b 1

REM H10: Fix non-deterministic keys in Items.getOrWrap
git add "eco-api/src/main/java/com/willfp/eco/core/items/Items.java"
git commit -m "fix(H10): Use deterministic keys and caching in Items.getOrWrap"
if errorlevel 1 exit /b 1

REM H11: Fix 0%% cache hit rate in Recipes.RECIPES_FROM_MATRIX
git add "eco-api/src/main/java/com/willfp/eco/core/recipe/Recipes.java"
git commit -m "fix(H11): Fix Recipes cache using ItemStack[] as key (0%% hit rate)"
if errorlevel 1 exit /b 1

REM H12: Fix EcoConfig.equals using identity hash
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoConfig.kt"
git commit -m "fix(H12): Fix EcoConfig.equals to use content-based comparison"
if errorlevel 1 exit /b 1

REM H13: Fix thread-unsafe originals map + memory leak
git add "eco-core/core-nms/v1_21_5/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/v1_21_5/packet/PacketContainerClick.kt"
git commit -m "fix(H13): Use ConcurrentHashMap and cleanup for PacketContainerClick originals"
if errorlevel 1 exit /b 1

REM H14: Fix CollatedRunnable dropping concurrent entries
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/drops/CollatedRunnable.kt"
git commit -m "fix(H14): Fix CollatedRunnable race condition dropping queued drops"
if errorlevel 1 exit /b 1

REM === MEDIUM PRIORITY FIXES ===

REM M1: Thread-safe material/item caches
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/NMSCommons.kt"
git commit -m "fix(M1): Use ConcurrentHashMap for MATERIAL_TO_ITEM/ITEM_TO_MATERIAL"
if errorlevel 1 exit /b 1

REM M2: Cache player reference in channel handler
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/packet/EcoChannelDuplexHandler.kt"
git commit -m "perf(M2): Cache Player reference in EcoChannelDuplexHandler"
if errorlevel 1 exit /b 1

REM M3+L2: Display.java optimizations
git add "eco-api/src/main/java/com/willfp/eco/core/display/Display.java"
git commit -m "perf(M3+L2): Optimize Display.display and Display.revert"
if errorlevel 1 exit /b 1

REM M4: Cache DecimalFormat in NumberUtils
git add "eco-api/src/main/java/com/willfp/eco/util/NumberUtils.java"
git commit -m "perf(M4): Cache DecimalFormat via ThreadLocal in NumberUtils.format"
if errorlevel 1 exit /b 1

REM M5: Cache entity lookups
git add "eco-api/src/main/java/com/willfp/eco/core/entities/Entities.java"
git commit -m "perf(M5): Add Caffeine cache for Entities.getEntity/isCustomEntity"
if errorlevel 1 exit /b 1

REM M6: Fix player multiplier memory leaks in Price classes
git add "eco-api/src/main/java/com/willfp/eco/core/price/impl/PriceEconomy.java" "eco-api/src/main/java/com/willfp/eco/core/price/impl/PriceItem.java" "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/price/PriceFactoryXP.kt" "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/price/PriceFactoryXPLevels.kt"
git commit -m "fix(M6): Replace multiplier HashMaps with Caffeine caches in Price classes"
if errorlevel 1 exit /b 1

REM M7: Optimize DisplayFrame with array-backed storage
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/packet/display/frame/DisplayFrame.kt"
git commit -m "perf(M7): Replace boxed Byte map with array in DisplayFrame"
if errorlevel 1 exit /b 1

REM M8: Cache getKeys(true) in EcoUpdatableConfig.update
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoUpdatableConfig.kt"
git commit -m "perf(M8): Compute getKeys(true) once in EcoUpdatableConfig.update"
if errorlevel 1 exit /b 1

REM M9: Cache YAML parser instances
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/ConfigUtils.kt"
git commit -m "perf(M9): Use ThreadLocal Yaml to reuse YAML parser instances"
if errorlevel 1 exit /b 1

REM M10: Fix translateGradients matcher source
git add "eco-api/src/main/java/com/willfp/eco/util/StringUtils.java"
git commit -m "fix(M10): Fix translateGradients matching against wrong string"
if errorlevel 1 exit /b 1

REM M11: Fix parseTokens bounds check
git add "eco-api/src/main/java/com/willfp/eco/util/StringUtils.java"
git commit -m "fix(M11): Add bounds check in StringUtils.parseTokens for unmatched quotes"
if errorlevel 1 exit /b 1

REM M12: Fix DefaultMap.get TOCTOU race
git add "eco-api/src/main/java/com/willfp/eco/core/map/DefaultMap.java"
git commit -m "fix(M12): Replace check-then-act with computeIfAbsent in DefaultMap.get"
if errorlevel 1 exit /b 1

REM M13: Fix takeXP stack overflow
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/price/PriceFactoryXP.kt"
git commit -m "fix(M13): Convert recursive takeXP to iterative loop"
if errorlevel 1 exit /b 1

REM M14: Fix duplicate tab completions for Player senders
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/command/HandledCommand.kt"
git commit -m "fix(M14): Fix HandledCommand duplicating tab completions for Players"
if errorlevel 1 exit /b 1

REM M15: Fix unbounded non-thread-safe expression cache
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/math/ExpressionHandlers.kt"
git commit -m "fix(M15): Replace unbounded mutableMap with Caffeine cache in LazyPlaceholderTranslationExpressionHandler"
if errorlevel 1 exit /b 1

REM M16: Fix inverted distance check for exp bottles
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/eventlisteners/NaturalExpGainListeners.kt"
git commit -m "fix(M16): Fix inverted distance check in NaturalExpGainListeners"
if errorlevel 1 exit /b 1

REM M17: Fix MySQL withRetries blocking with runBlocking
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/data/handlers/impl/MySQLPersistentDataHandler.kt"
git commit -m "perf(M17): Replace runBlocking delay with Thread.sleep in MySQL withRetries"
if errorlevel 1 exit /b 1

REM M18: Fix MongoDB blocking main thread
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/data/handlers/impl/MongoDBPersistentDataHandler.kt"
git commit -m "perf(M18): Dispatch MongoDB operations onto Dispatchers.IO"
if errorlevel 1 exit /b 1

REM M19: Fix shift-click crash on full inventory
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/gui/GUIListener.kt"
git commit -m "fix(M19): Guard against firstEmpty=-1 in GUIListener shift-click"
if errorlevel 1 exit /b 1

REM M20: Fix PlayerBlockListener hash collision keys
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/data/PlayerBlockListener.kt"
git commit -m "fix(M20): Use coordinate-based keys in PlayerBlockListener"
if errorlevel 1 exit /b 1

REM M21: Fix ArgParserAttribute logic scope
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/items/ArgParserAttribute.kt"
git commit -m "fix(M21): Move emptiness check and meta-application outside arg loop"
if errorlevel 1 exit /b 1

REM === LOW PRIORITY FIXES ===

REM L1: Fix EcoFastItemStack.equals hash-only comparison
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/item/EcoFastItemStack.kt"
git commit -m "fix(L1): Fix EcoFastItemStack.equals to use full item comparison"
if errorlevel 1 exit /b 1

REM L3: Short-circuit ShapedCraftingRecipe.test
git add "eco-api/src/main/java/com/willfp/eco/core/recipe/recipes/ShapedCraftingRecipe.java"
git commit -m "perf(L3): Short-circuit ShapedCraftingRecipe.test on first mismatch"
if errorlevel 1 exit /b 1

REM L4: Cache item meta in DurabilityUtils
git add "eco-api/src/main/java/com/willfp/eco/util/DurabilityUtils.java"
git commit -m "perf(L4): Fetch item meta once in DurabilityUtils.damageItem"
if errorlevel 1 exit /b 1

REM L5: Skip update for already-met prerequisites
git add "eco-api/src/main/java/com/willfp/eco/core/Prerequisite.java"
git commit -m "perf(L5): Only refresh unmet prerequisites in Prerequisite.areMet"
if errorlevel 1 exit /b 1

REM L6: Optimize IntegrationRegistry.allSafely
git add "eco-api/src/main/java/com/willfp/eco/core/integrations/IntegrationRegistry.java"
git commit -m "perf(L6): Simplify IntegrationRegistry.allSafely filtering"
if errorlevel 1 exit /b 1

REM L7: Cache EmptyTestableItem in ShapelessCraftingRecipe
git add "eco-api/src/main/java/com/willfp/eco/core/recipe/recipes/ShapelessCraftingRecipe.java"
git commit -m "perf(L7): Promote EmptyTestableItem to static constant in ShapelessCraftingRecipe"
if errorlevel 1 exit /b 1

REM L8: Replace exception-based isFinite
git add "eco-api/src/main/java/com/willfp/eco/util/VectorUtils.java"
git commit -m "perf(L8): Use Double.isFinite checks instead of try-catch in VectorUtils"
if errorlevel 1 exit /b 1

REM L9: Use cardinal faces only in getVein
git add "eco-api/src/main/java/com/willfp/eco/util/BlockUtils.java"
git commit -m "perf(L9): Use CARDINAL_FACES EnumSet in BlockUtils.getVein"
if errorlevel 1 exit /b 1

REM L10: Cache EmptyTestableItem in RenderedInventory
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/gui/menu/RenderedInventory.kt"
git commit -m "perf(L10): Hoist EmptyTestableItem to companion object in RenderedInventory"
if errorlevel 1 exit /b 1

REM L11: Remove defensive copies in LayeredComponents.addOffsetComponent
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/gui/menu/LayeredComponents.kt"
git commit -m "perf(L11): Remove unnecessary defensive copies in LayeredComponents"
if errorlevel 1 exit /b 1

REM L12: Array-backed slot lookup in LayeredComponents
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/gui/menu/LayeredComponents.kt"
git commit -m "perf(L12): Use array-indexed slot lookup in LayeredComponents"
if errorlevel 1 exit /b 1

REM L13: Cache zero vector in EcoDropQueue
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/drops/EcoDropQueue.kt"
git commit -m "perf(L13): Use shared ZERO_VECTOR constant in EcoDropQueue"
if errorlevel 1 exit /b 1

REM L14: Use UUID keys in EcoFastCollatedDropQueue
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/drops/EcoFastCollatedDropQueue.kt"
git commit -m "fix(L14): Use UUID keys instead of Player objects in EcoFastCollatedDropQueue"
if errorlevel 1 exit /b 1

REM L15: Thread-safe injections map in EcoConfig
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoConfig.kt"
git commit -m "fix(L15): Use ConcurrentHashMap for EcoConfig.injections"
if errorlevel 1 exit /b 1

REM L16: Remove redundant toList in EcoConfig
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoConfig.kt"
git commit -m "perf(L16): Remove redundant .toList() in EcoConfig.getSubsectionsOrNull"
if errorlevel 1 exit /b 1

REM L17: Use StringBuilder in toPlaintext
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoLoadableConfig.kt"
git commit -m "perf(L17): Use StringBuilder in EcoLoadableConfig.toPlaintext"
if errorlevel 1 exit /b 1

REM L18: Add change guard to saveAsync
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/config/EcoLoadableConfig.kt"
git commit -m "fix(L18): Add requiresChangesToSave guard to EcoLoadableConfig.saveAsync"
if errorlevel 1 exit /b 1

REM L19: Fix permission bypass for non-Player senders
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/command/HandledCommand.kt"
git commit -m "fix(L19): Check permissions for non-Player senders in HandledCommand"
if errorlevel 1 exit /b 1

REM L20: Fix shaded Guava import from Factions
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/eventlisteners/NaturalExpGainBuilder.kt"
git commit -m "fix(L20): Use standard Guava import instead of Factions-shaded package"
if errorlevel 1 exit /b 1

REM L21: Remove System.gc() call
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/EcoImpl.kt"
git commit -m "perf(L21): Remove System.gc() call from EcoImpl.clean"
if errorlevel 1 exit /b 1

REM L22: Use async HTTP in PlayerflowHandler
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/metrics/PlayerflowHandler.kt"
git commit -m "perf(L22): Use sendAsync instead of blocking send in PlayerflowHandler"
if errorlevel 1 exit /b 1

REM L23: Early return for integer-only inputs in fastToDoubleOrNull
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/math/ExpressionHandlers.kt"
git commit -m "perf(L23): Skip decimal computation for integer inputs in fastToDoubleOrNull"
if errorlevel 1 exit /b 1

REM L24: Thread-safe trackedForceRendered map
git add "eco-core/core-backend/src/main/kotlin/com/willfp/eco/internal/gui/menu/RenderedInventory.kt"
git commit -m "fix(L24): Use ConcurrentHashMap for trackedForceRendered in RenderedInventory"
if errorlevel 1 exit /b 1

REM L25: Remove dead code in EcoEntityController
git add "eco-core/core-nms/v1_21_4/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/v1_21_4/entity/EcoEntityController.kt" "eco-core/core-nms/v1_21_5/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/v1_21_5/entity/EcoEntityController.kt"
git commit -m "fix(L25): Remove dead nms.targetSelector statement in EcoEntityController"
if errorlevel 1 exit /b 1

REM L26: Fix fragile declaredClasses index lookup in Illusioner factories
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/ai/entity/IllusionerBlindnessSpellGoalFactory.kt" "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/ai/entity/IllusionerMirrorSpellGoalFactory.kt"
git commit -m "fix(L26): Use name-based inner class lookup in Illusioner goal factories"
if errorlevel 1 exit /b 1

REM L27: Fix fragile field-index reflection in DisplayName
git add "eco-core/core-nms/v1_21_4/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/v1_21_4/DisplayName.kt" "eco-core/core-nms/v1_21_5/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/v1_21_5/DisplayName.kt"
git commit -m "fix(L27): Use serializer-based field lookup in DisplayName"
if errorlevel 1 exit /b 1

REM L28: Clean up player maps on disconnect
git add "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/packet/PacketInjectorListener.kt" "eco-core/core-nms/common/src/main/kotlin/com/willfp/eco/internal/spigot/proxy/common/packet/display/frame/DisplayFrame.kt"
git commit -m "fix(L28): Clean up lastKnownWindowIDs and frames on player disconnect"
if errorlevel 1 exit /b 1

REM L29: Fix AntigriefHuskTowns operation type
git add "eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/integrations/antigrief/AntigriefHuskTowns.kt"
git commit -m "fix(L29): Select correct OperationType in AntigriefHuskTowns.canInjure"
if errorlevel 1 exit /b 1

echo All 64 fixes committed successfully.
