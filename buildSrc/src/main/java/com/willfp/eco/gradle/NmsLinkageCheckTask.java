package com.willfp.eco.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Verifies that every platform member referenced by a shaded NMS jar actually exists, and is
 * accessible, on the Minecraft version that jar is used for.
 *
 * <p>NMS modules are compiled once against an old version and then relocated into jars for newer
 * versions. That trick breaks silently whenever Mojang or Paper removes a member, or narrows its
 * visibility, and the failure only surfaces at runtime as a {@link NoSuchMethodError},
 * {@link NoSuchFieldError} or {@link IllegalAccessError}. This task turns that into a build failure.
 */
@CacheableTask
public abstract class NmsLinkageCheckTask extends DefaultTask {
    private static final String[] PLATFORM_PACKAGES = {
        "net/minecraft/",
        "org/bukkit/",
        "io/papermc/",
        "com/mojang/"
    };

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getJar();

    @Classpath
    public abstract ConfigurableFileCollection getClasspath();

    /**
     * Findings to tolerate, as {@code owner#name descriptor} (the exact text of a reported member).
     */
    @Input
    public abstract SetProperty<String> getIgnored();

    @OutputFile
    public abstract RegularFileProperty getReport();

    @TaskAction
    public void check() throws IOException {
        File jar = getJar().get().getAsFile();

        List<File> index = new ArrayList<>(getClasspath().getFiles());
        index.add(jar);

        Set<String> problems = new LinkedHashSet<>();

        try (ZipFile zip = new ZipFile(jar); ClassIndex classes = new ClassIndex(index)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (!entry.getName().endsWith(".class") || !entry.getName().startsWith("com/willfp/")) {
                    continue;
                }

                ClassNode node = new ClassNode(Opcodes.ASM9);

                try (InputStream in = zip.getInputStream(entry)) {
                    new ClassReader(in).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                }

                checkClass(classes, node, problems);
            }
        }

        problems.removeAll(getIgnored().get());

        File report = getReport().get().getAsFile();
        report.getParentFile().mkdirs();
        Files.write(report.toPath(), String.join("\n", problems).getBytes(StandardCharsets.UTF_8));

        if (!problems.isEmpty()) {
            throw new GradleException(
                problems.size() + " broken reference(s) in " + jar.getName() + ":\n  "
                    + String.join("\n  ", problems)
            );
        }
    }

    private void checkClass(ClassIndex classes, ClassNode node, Set<String> problems) {
        for (MethodNode method : node.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode ref = (MethodInsnNode) insn;
                    checkMember(classes, node, ref.owner, ref.name, ref.desc, true, problems);
                } else if (insn instanceof FieldInsnNode) {
                    FieldInsnNode ref = (FieldInsnNode) insn;
                    checkMember(classes, node, ref.owner, ref.name, ref.desc, false, problems);
                } else if (insn instanceof TypeInsnNode) {
                    checkType(classes, node, ((TypeInsnNode) insn).desc, problems);
                } else if (insn instanceof InvokeDynamicInsnNode) {
                    for (Object argument : ((InvokeDynamicInsnNode) insn).bsmArgs) {
                        if (argument instanceof Handle) {
                            Handle handle = (Handle) argument;
                            boolean isMethod = handle.getTag() != Opcodes.H_GETFIELD
                                && handle.getTag() != Opcodes.H_GETSTATIC
                                && handle.getTag() != Opcodes.H_PUTFIELD
                                && handle.getTag() != Opcodes.H_PUTSTATIC;

                            checkMember(classes, node, handle.getOwner(), handle.getName(), handle.getDesc(), isMethod, problems);
                        }
                    }
                }
            }
        }
    }

    private void checkType(ClassIndex classes, ClassNode from, String owner, Set<String> problems) {
        if (owner.startsWith("[") || !isPlatform(owner)) {
            return;
        }

        ClassNode target = classes.find(owner);

        if (target == null) {
            problems.add("missing class " + owner + " (referenced by " + from.name + ")");
        } else if (!isAccessible(classes, from, target.access, target.name, owner)) {
            problems.add("inaccessible class " + owner + " (referenced by " + from.name + ")");
        }
    }

    private void checkMember(
        ClassIndex classes,
        ClassNode from,
        String owner,
        String name,
        String desc,
        boolean isMethod,
        Set<String> problems
    ) {
        if (owner.startsWith("[") || !isPlatform(owner)) {
            return;
        }

        checkType(classes, from, owner, problems);

        ClassNode target = classes.find(owner);
        if (target == null) {
            return;
        }

        String label = owner + "#" + name + " " + desc + " (referenced by " + from.name + ")";

        Integer access = findMember(classes, owner, name, desc, isMethod);

        if (access == null) {
            problems.add("missing " + (isMethod ? "method " : "field ") + label);
            return;
        }

        if (!isAccessible(classes, from, access, declaringClass(classes, owner, name, desc, isMethod), owner)) {
            problems.add("inaccessible " + (isMethod ? "method " : "field ") + label);
        }
    }

    private Integer findMember(ClassIndex classes, String owner, String name, String desc, boolean isMethod) {
        for (String current : hierarchy(classes, owner)) {
            ClassNode node = classes.find(current);

            if (node == null) {
                // An unresolvable link in the hierarchy means we cannot prove absence.
                return Opcodes.ACC_PUBLIC;
            }

            if (isMethod) {
                for (MethodNode method : node.methods) {
                    if (method.name.equals(name) && method.desc.equals(desc)) {
                        return method.access;
                    }
                }
            } else {
                for (FieldNode field : node.fields) {
                    if (field.name.equals(name) && field.desc.equals(desc)) {
                        return field.access;
                    }
                }
            }
        }

        return null;
    }

    private String declaringClass(ClassIndex classes, String owner, String name, String desc, boolean isMethod) {
        for (String current : hierarchy(classes, owner)) {
            ClassNode node = classes.find(current);

            if (node == null) {
                return current;
            }

            if (isMethod) {
                for (MethodNode method : node.methods) {
                    if (method.name.equals(name) && method.desc.equals(desc)) {
                        return current;
                    }
                }
            } else {
                for (FieldNode field : node.fields) {
                    if (field.name.equals(name) && field.desc.equals(desc)) {
                        return current;
                    }
                }
            }
        }

        return owner;
    }

    private List<String> hierarchy(ClassIndex classes, String owner) {
        List<String> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        List<String> queue = new ArrayList<>();
        queue.add(owner);

        while (!queue.isEmpty()) {
            String current = queue.remove(0);

            if (current == null || !seen.add(current)) {
                continue;
            }

            result.add(current);

            ClassNode node = classes.find(current);
            if (node == null) {
                continue;
            }

            queue.add(node.superName);
            queue.addAll(node.interfaces);
        }

        return result;
    }

    private boolean isAccessible(ClassIndex classes, ClassNode from, int access, String declaring, String owner) {
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            return true;
        }

        if (samePackage(from.name, declaring) || samePackage(from.name, owner)) {
            return true;
        }

        // Protected members are reachable from subclasses.
        return (access & Opcodes.ACC_PROTECTED) != 0 && classes.isSubclassOf(from.name, declaring);
    }

    private boolean samePackage(String a, String b) {
        return packageOf(a).equals(packageOf(b));
    }

    private String packageOf(String internalName) {
        int index = internalName.lastIndexOf('/');
        return index == -1 ? "" : internalName.substring(0, index);
    }

    private boolean isPlatform(String owner) {
        for (String prefix : PLATFORM_PACKAGES) {
            if (owner.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}
