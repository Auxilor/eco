package com.willfp.eco.core.blocks.impl;

import com.willfp.eco.core.blocks.TestableBlock;
import com.willfp.eco.util.NumberUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A group of testable blocks.
 */
public class GroupedTestableBlocks implements TestableBlock {
    /**
     * The children.
     */
    private final Collection<TestableBlock> children;

    /**
     * Create a new group of testable blocks.
     *
     * @param children The children.
     */
    public GroupedTestableBlocks(@NotNull final Collection<TestableBlock> children) {
        Validate.isTrue(!children.isEmpty(), "Group must have at least one child!");

        this.children = children;
    }

    @Override
    public boolean matches(@Nullable final Block other) {
        for (TestableBlock child : children) {
            if (child.matches(other)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull Block place(@NotNull final Location location) {
        return new ArrayList<>(children)
                .get(NumberUtils.randInt(0, children.size() - 1))
                .place(location);
    }

    /**
     * Get the children.
     *
     * @return The children.
     */
    public Collection<TestableBlock> getChildren() {
        return new ArrayList<>(children);
    }
}
