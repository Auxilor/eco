package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Maps the lore a legacy display module wrote back onto lore nodes.
 * <p>
 * Legacy modules read and write lore as legacy strings, which rebuilds every line. Lines that
 * read the same before and after are matched up by the longest common subsequence of their
 * legacy text, and keep their original component, so formatting that legacy text can't hold
 * survives the module.
 */
final class LegacyLoreMatcher {
    /**
     * Match module output to the nodes it was made from.
     *
     * @param input         The nodes before the module ran.
     * @param renderedInput The lore that was set on the item for the module, one line per node.
     * @param output        The lore the module left on the item.
     * @return The nodes after the module ran.
     */
    @NotNull
    static List<LoreNode> match(@NotNull final List<LoreNode> input,
                                @NotNull final List<Component> renderedInput,
                                @NotNull final List<Component> output) {
        List<String> inputLegacy = renderedInput.stream().map(StringUtils::toLegacy).toList();
        List<String> outputLegacy = output.stream().map(StringUtils::toLegacy).toList();

        int inputSize = inputLegacy.size();
        int outputSize = outputLegacy.size();
        int[][] lengths = new int[inputSize + 1][outputSize + 1];

        for (int inputIndex = inputSize - 1; inputIndex >= 0; inputIndex--) {
            for (int outputIndex = outputSize - 1; outputIndex >= 0; outputIndex--) {
                lengths[inputIndex][outputIndex] = inputLegacy.get(inputIndex).equals(outputLegacy.get(outputIndex))
                        ? lengths[inputIndex + 1][outputIndex + 1] + 1
                        : Math.max(lengths[inputIndex + 1][outputIndex], lengths[inputIndex][outputIndex + 1]);
            }
        }

        LoreNode[] aligned = new LoreNode[outputSize];
        int inputIndex = 0;
        int outputIndex = 0;

        while (inputIndex < inputSize && outputIndex < outputSize) {
            if (inputLegacy.get(inputIndex).equals(outputLegacy.get(outputIndex))) {
                aligned[outputIndex] = input.get(inputIndex);
                inputIndex++;
                outputIndex++;
            } else if (lengths[inputIndex + 1][outputIndex] >= lengths[inputIndex][outputIndex + 1]) {
                inputIndex++;
            } else {
                outputIndex++;
            }
        }

        List<LoreNode> result = new ArrayList<>(outputSize);

        for (int index = 0; index < outputSize; index++) {
            if (aligned[index] != null) {
                result.add(aligned[index]);
            } else if (outputLegacy.get(index).startsWith(DisplayLines.LEGACY_PREFIX)) {
                result.add(LoreNode.display(DisplayLines.withoutPrefix(output.get(index))));
            } else {
                result.add(LoreNode.foreign(output.get(index)));
            }
        }

        return result;
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private LegacyLoreMatcher() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
