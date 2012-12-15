package com.me.asm;


import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * 
 * 向指定的class文件中添加field_seter_geter
 * 
 * @param clz
 * @param properties
 */
public class AddFieldAdapter extends ClassAdapter
{

    public BuildProperty field;
    public boolean isFieldPresent;


    public AddFieldAdapter(ClassVisitor cv, Class clz, BuildProperty field)
    {

        super(cv);

        String fullClassName = StringUtils.replace(clz.getName(), ".", "/");//以/分割
        // cv.visitMethod(arg0, arg1, arg2, arg3, arg4);//先添加一下field对应的get和set
        String seter = "set" + UT.FormatProperty(field.getName());
        String geter = "get" + UT.FormatProperty(field.getName());
        // geter
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, geter, "()"
                + field.getType(), null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, fullClassName, field
                .getName(), field.getType());
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        // seter
        mv = cv.visitMethod(Opcodes.ACC_PUBLIC, seter, "(" + field.getType()
                + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD,fullClassName, field
                .getName(), field.getType());
        mv.visitMaxs(2, 2);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();
        this.field = field;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions)
    {
        if (name.equals(field.getName()))
        {
            this.isFieldPresent = true;
        }
        // 下面是设置原有的方法 --之前还有设置一下当前要处理的Field的get和set

        return super.visitMethod(access, name, desc, signature, exceptions);
    }


    @Override
    public void visitEnd()
    {
        if (!isFieldPresent)
        {
            FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE, field
                    .getName(), field.getType(), null, null);
        }
        super.visitEnd();
    }
}

