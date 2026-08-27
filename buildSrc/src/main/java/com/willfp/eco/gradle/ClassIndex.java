package com.willfp.eco.gradle;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Lazily reads class files out of a set of jars, keyed by internal name.
 *
 * <p>Classes are parsed with ASM rather than loaded by a classloader so that the checker can run on
 * an older JVM than the one the classes were compiled for.
 */
final class ClassIndex implements AutoCloseable {
    private final Map<String, ZipFile> locations = new HashMap<>();
    private final Map<String, ClassNode> cache = new HashMap<>();
    private final List<ZipFile> open = new ArrayList<>();

    ClassIndex(List<File> jars) throws IOException {
        for (File jar : jars) {
            if (!jar.isFile() || !jar.getName().endsWith(".jar")) {
                continue;
            }

            ZipFile zip = new ZipFile(jar);
            open.add(zip);

            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!name.endsWith(".class")) {
                    continue;
                }

                locations.putIfAbsent(name.substring(0, name.length() - ".class".length()), zip);
            }
        }
    }

    ClassNode find(String internalName) {
        if (cache.containsKey(internalName)) {
            return cache.get(internalName);
        }

        ClassNode node = null;
        ZipFile zip = locations.get(internalName);

        if (zip != null) {
            try (InputStream in = zip.getInputStream(zip.getEntry(internalName + ".class"))) {
                node = new ClassNode(Opcodes.ASM9);
                new ClassReader(in).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (IOException e) {
                node = null;
            }
        }

        cache.put(internalName, node);
        return node;
    }

    boolean isSubclassOf(String child, String parent) {
        String current = child;

        while (current != null) {
            if (current.equals(parent)) {
                return true;
            }

            ClassNode node = find(current);
            if (node == null) {
                return false;
            }

            current = node.superName;
        }

        return false;
    }

    @Override
    public void close() {
        for (ZipFile zip : open) {
            try {
                zip.close();
            } catch (IOException ignored) {
                // Nothing to do.
            }
        }
    }
}
