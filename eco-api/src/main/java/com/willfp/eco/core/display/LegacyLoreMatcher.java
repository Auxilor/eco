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
 * survives the module. Lines a module left alone at the start and end are matched first, so
 * modules that only append or prepend never build the full table.
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
        if (renderedInput.equals(output)) {
            return input;
        }

        String[] inputLegacy = new String[renderedInput.size()];
        String[] outputLegacy = new String[output.size()];
        int inputEnd = inputLegacy.length;
        int outputEnd = outputLegacy.length;
        int start = 0;

        while (start < inputEnd && start < outputEnd
                && legacy(inputLegacy, renderedInput, start).equals(legacy(outputLegacy, output, start))) {
            start++;
        }

        while (inputEnd > start && outputEnd > start
                && legacy(inputLegacy, renderedInput, inputEnd - 1).equals(legacy(outputLegacy, output, outputEnd - 1))) {
            inputEnd--;
            outputEnd--;
        }

        LoreNode[] aligned = new LoreNode[outputLegacy.length];

        for (int index = 0; index < start; index++) {
            aligned[index] = input.get(index);
        }

        for (int offset = 0; outputEnd + offset < outputLegacy.length; offset++) {
            aligned[outputEnd + offset] = input.get(inputEnd + offset);
        }

        int inputSize = inputEnd - start;
        int outputSize = outputEnd - start;
        int[][] lengths = new int[inputSize + 1][outputSize + 1];

        for (int inputIndex = inputSize - 1; inputIndex >= 0; inputIndex--) {
            for (int outputIndex = outputSize - 1; outputIndex >= 0; outputIndex--) {
                lengths[inputIndex][outputIndex] = legacy(inputLegacy, renderedInput, start + inputIndex)
                        .equals(legacy(outputLegacy, output, start + outputIndex))
                        ? lengths[inputIndex + 1][outputIndex + 1] + 1
                        : Math.max(lengths[inputIndex + 1][outputIndex], lengths[inputIndex][outputIndex + 1]);
            }
        }

        int inputIndex = 0;
        int outputIndex = 0;

        while (inputIndex < inputSize && outputIndex < outputSize) {
            if (legacy(inputLegacy, renderedInput, start + inputIndex)
                    .equals(legacy(outputLegacy, output, start + outputIndex))) {
                aligned[start + outputIndex] = input.get(start + inputIndex);
                inputIndex++;
                outputIndex++;
            } else if (lengths[inputIndex + 1][outputIndex] >= lengths[inputIndex][outputIndex + 1]) {
                inputIndex++;
            } else {
                outputIndex++;
            }
        }

        List<LoreNode> result = new ArrayList<>(outputLegacy.length);

        for (int index = 0; index < outputLegacy.length; index++) {
            if (aligned[index] != null) {
                result.add(aligned[index]);
            } else if (legacy(outputLegacy, output, index).startsWith(DisplayLines.LEGACY_PREFIX)) {
                result.add(LoreNode.display(DisplayLines.withoutPrefix(output.get(index))));
            } else {
                result.add(LoreNode.foreign(output.get(index)));
            }
        }

        return result;
    }

    private static String legacy(@NotNull final String[] cache,
                                 @NotNull final List<Component> lines,
                                 final int index) {
        if (cache[index] == null) {
            cache[index] = StringUtils.toLegacy(lines.get(index));
        }

        return cache[index];
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private LegacyLoreMatcher() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
