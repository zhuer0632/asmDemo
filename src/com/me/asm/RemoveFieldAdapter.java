package com.me.asm;


import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;



/**
 * 删除指定class文件中的field_seter_geter
 * 
 * @param clz
 * @param properties
 */
public class RemoveFieldAdapter extends ClassAdapter
{

    public String  fieldName;
    public boolean isFieldPresent;
    private String seter;
    private String geter;

    public RemoveFieldAdapter(ClassVisitor cv, Class clz, String fieldName )
    {

        super(cv);
        this.seter = "set" + UT.FormatProperty(fieldName);
        this.geter = "get" + UT.FormatProperty(fieldName);
        this.fieldName=fieldName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions)
    {
        if (name.equals(geter) || name.equals(seter))
        {
            return null;
        }
        // 如果发现了指定要删除的列名 则直接返回，不做下面的处理
        return super.visitMethod(access, name, desc, signature, exceptions);
    }


    @Override
    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value)
    {
        if(this.fieldName.equals(name))//如果发现要删除的字段的时候 ，立刻返回
        {
            return null;
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd()
    {
        super.visitEnd();
    }
}
