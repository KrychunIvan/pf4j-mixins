package ua.wildwinner.boot;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;
import java.util.Set;

public class DependencyCollector extends ClassVisitor {
    private final Set<String> dependencies = new HashSet<>();
    private final String filterPackage;

    public DependencyCollector(String filterPackage) {
        super(Opcodes.ASM9);
        this.filterPackage = filterPackage;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (opcode == Opcodes.NEW || opcode == Opcodes.CHECKCAST || opcode == Opcodes.INSTANCEOF) {
                    if (type.startsWith(filterPackage)) {
                        dependencies.add(type);
                    }
                }
                super.visitTypeInsn(opcode, type);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (owner.startsWith(filterPackage)) {
                    dependencies.add(owner);
                }
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        };
    }
}

