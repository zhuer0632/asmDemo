package com.me.asm;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class ClassFactory extends ClassLoader implements Opcodes
{

    /**
     * 
     * 生成Class文件，保存在和ClassFactory.class同一目录
     * 
     * @param fullClassName
     * 
     * @param savepath
     * 
     * @param properties
     *            property集合，type只能是： Ljava/util/Date; Ljava/lang/String;
     *            Ljava/lang/String; Ljava/lang/Float;
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class build(String className)
    {
        Collection<BuildProperty> properties = new ArrayList<BuildProperty>();
        properties.add(new BuildProperty("objId", Const.typeString));
        properties.add(new BuildProperty("className", Const.typeString));
        properties.add(new BuildProperty("objState", Const.typeInt));
        properties.add(new BuildProperty("modifyDate", Const.typeDate));
        properties.add(new BuildProperty("modifyUser", Const.typeString));

        String fullClassName = Const.packagePath + "/" + className;// cn/comdev/domain/Person
        String savepath = UT.getClassPath() + className + ".class";// c:/.../cn/comdev/domain/Person.class
        Class cls = null;
        try
        {
            ClassWriter cw = new ClassWriter(0);

            // 建立构造函数--javabean无需

            cw.visit(V1_1,
                     ACC_PUBLIC,
                     fullClassName,
                     null,
                     "java/lang/Object",
                     null);
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC,
                                              "<init>",
                                              "()V",
                                              null,
                                              null);
            mw.visitVarInsn(ALOAD,
                            0);
            mw.visitMethodInsn(INVOKESPECIAL,
                               "java/lang/Object",
                               "<init>",
                               "()V");
            mw.visitInsn(RETURN);
            mw.visitMaxs(1,
                         1);
            mw.visitEnd();

            BuildProperty property = null;

            // 建立属性
            Iterator iterator = properties.iterator();
            while (iterator.hasNext())
            {
                // 建立属性对应的类变量
                property = (BuildProperty) iterator.next();
                String sproperty = property.getType();
                String propertyname = property.getName();
                propertyname = UT.FormatProperty(propertyname);
                cw.visitField(ACC_PRIVATE,
                              property.getName(),
                              sproperty,
                              null,
                              null).visitEnd();
                // 建立get方法
                mw = cw.visitMethod(ACC_PUBLIC,
                                    "get" + propertyname,
                                    "()" + sproperty,
                                    null,
                                    null);
                mw.visitCode();
                mw.visitVarInsn(ALOAD,
                                0);
                mw.visitFieldInsn(GETFIELD,
                                  fullClassName,
                                  property.getName(),
                                  sproperty);
                mw.visitInsn(ARETURN);
                mw.visitMaxs(1,
                             1);
                mw.visitEnd();
                // 建立set方法
                mw = cw.visitMethod(ACC_PUBLIC,
                                    "set" + propertyname,
                                    "(" + sproperty + ")V",
                                    null,
                                    null);
                mw.visitCode();
                mw.visitVarInsn(ALOAD,
                                0);
                mw.visitVarInsn(ALOAD,
                                1);
                mw.visitFieldInsn(PUTFIELD,
                                  fullClassName,
                                  property.getName(),
                                  sproperty);
                mw.visitMaxs(2,
                             2);
                mw.visitInsn(RETURN);
                mw.visitEnd();
            }

            cw.visitEnd();
            byte[] code = cw.toByteArray();
            if (savepath != null)
            {
                FileOutputStream fos = new FileOutputStream(savepath);
                fos.write(code);
                fos.close();
            }
            fullClassName = StringUtils.replace(fullClassName,
                                                "/",
                                                ".");
            Class cls1 = Class.forName(fullClassName);
            MyClassLoader clzloader = new MyClassLoader(cls1);
            cls = clzloader.loadClass(fullClassName);
            return cls;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 
     * 往指定的类中添加字段
     * 
     */
    @SuppressWarnings("unchecked")
    public static Class addField(Class clz,
                                 BuildProperty field)
    {
        Field[] hasfields = clz.getDeclaredFields();
        for (Field f : hasfields)
        {
            if (f.getName().equals(field.getName()))
            {
                System.out.println("要添加的字段已经存在");
                return null;//
            }
        }

        // 1 // 读取class内容 理解成成得到了byte[]
        // ClassReader cr = new ClassReader(b);//为什么用className会报异常呢？
        InputStream ins = Const.class.getResourceAsStream(clz.getSimpleName()
                + ".class");
        ClassReader cr = null;
        try
        {
            cr = new ClassReader(ins);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        // 2
        ClassWriter cw = new ClassWriter(cr, 0); // 设置写方法

        // 3
        ClassVisitor cv = new AddFieldAdapter(cw, clz, field);// 对byte[]进行写操作

        // 4 执行回调
        cr.accept(cv,
                  0);

        // System.out.println(clz.getDeclaredFields());
        String path = UT.getClassPath(clz) + clz.getSimpleName() + ".class";
        // 保存到物理文件

        byte[] code = cw.toByteArray();
        if (path != null)
        {
            FileOutputStream fos;
            try
            {
                fos = new FileOutputStream(path);
                fos.write(code);
                fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        // 再读一次看看

        // Class return_clz = new MyClassLoader().defineClass(clz.getName(),
        // code);
        Class return_clz = null;
        try
        {
            MyClassLoader clzloader = new MyClassLoader(clz);
            return_clz=clzloader.loadClass(clz.getName());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        // Field[] fs = return_clz.getDeclaredFields();
        System.out.println("class[" + return_clz.getSimpleName()
                + "]中添加字段over:" + field.getName());
        // System.out.println(clz.getDeclaredFields());
        return return_clz;
    }

 


    /**
     *  
     *  [如此重新加载是无效的，如果jvm中已经加载了某类]
     *  className:例子：VOPerson,不要带.classs
     *  <br>
     *  Class.forName("com.me.asm.VOPerson");一样可以
     *  
     */
    @SuppressWarnings(
    {
            "unused", "unchecked"
    })
    public static Class<?> getClzByFile(String fullclassName)
    {
        String className = UT.getClassNameByFullClassName(fullclassName);
        // 1
        InputStream ins = Const.class.getResourceAsStream(className + ".class");
        ClassReader cr = null;
        try
        {
            cr = new ClassReader(ins);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            throw new RuntimeException(e1.getMessage(), e1);
        }
        // 2
        byte[] code = cr.b;
        String fullClassName = UT.getPackage(fullclassName) + "." + className;
        Class clz = null;
        try
        {
            Class cls1=Class.forName(fullClassName);
            MyClassLoader clzloader = new MyClassLoader(cls1);
            clz = clzloader.loadClass(fullclassName);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return clz;

    }


    /**
     * 
     * 添加字段或者修改字段，不再单独对class文件操作。而是完全重新建立一field完整的class文件。
     * 
     * @param className
     * @param allFields
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Class buildWithFields(String className,
                                        List<BuildProperty> allFields)
    {

        // properties.add(new BuildProperty("objId", Const.typeString));
        // properties.add(new BuildProperty("className", Const.typeString));
        // properties.add(new BuildProperty("objState", Const.typeInt));
        // properties.add(new BuildProperty("modifyDate", Const.typeDate));
        // properties.add(new BuildProperty("modifyUser", Const.typeString));

        if (allFields == null || (allFields != null && allFields.size() == 0))
        {
            throw new RuntimeException("allFields 不能为空");
        }

        String fullClassName = Const.packagePath + "/" + className;// cn/comdev/domain/Person
        String savepath = UT.getClassPath() + className + ".class";// c:/.../cn/comdev/domain/Person.class
        Class cls = null;
        try
        {
            ClassWriter cw = new ClassWriter(0);

            // 建立构造函数--javabean无需

            cw.visit(V1_1,
                     ACC_PUBLIC,
                     fullClassName,
                     null,
                     "java/lang/Object",
                     null);
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC,
                                              "<init>",
                                              "()V",
                                              null,
                                              null);
            mw.visitVarInsn(ALOAD,
                            0);
            mw.visitMethodInsn(INVOKESPECIAL,
                               "java/lang/Object",
                               "<init>",
                               "()V");
            mw.visitInsn(RETURN);
            mw.visitMaxs(1,
                         1);
            mw.visitEnd();

            BuildProperty property = null;
            // 建立属性
            Iterator iterator = allFields.iterator();
            while (iterator.hasNext())
            {
                // 建立属性对应的类变量
                property = (BuildProperty) iterator.next();
                String sproperty = property.getType();
                String propertyname = property.getName();
                propertyname = UT.FormatProperty(propertyname);
                cw.visitField(ACC_PRIVATE,
                              property.getName(),
                              sproperty,
                              null,
                              null).visitEnd();
                // 建立get方法
                mw = cw.visitMethod(ACC_PUBLIC,
                                    "get" + propertyname,
                                    "()" + sproperty,
                                    null,
                                    null);
                mw.visitCode();
                mw.visitVarInsn(ALOAD,
                                0);
                mw.visitFieldInsn(GETFIELD,
                                  fullClassName,
                                  property.getName(),
                                  sproperty);
                mw.visitInsn(ARETURN);
                mw.visitMaxs(1,
                             1);
                mw.visitEnd();
                // 建立set方法
                mw = cw.visitMethod(ACC_PUBLIC,
                                    "set" + propertyname,
                                    "(" + sproperty + ")V",
                                    null,
                                    null);
                mw.visitCode();
                mw.visitVarInsn(ALOAD,
                                0);
                mw.visitVarInsn(ALOAD,
                                1);
                mw.visitFieldInsn(PUTFIELD,
                                  fullClassName,
                                  property.getName(),
                                  sproperty);
                mw.visitMaxs(2,
                             2);
                mw.visitInsn(RETURN);
                mw.visitEnd();
            }

            cw.visitEnd();
            byte[] code = cw.toByteArray();
            if (savepath != null)
            {
                FileOutputStream fos = new FileOutputStream(savepath);
                fos.write(code);
                fos.close();
            }
            fullClassName = StringUtils.replace(fullClassName,
                                                "/",
                                                ".");
            // cls = Class.forName(fullClassName);
            // cls = new MyClassLoader().defineClass(fullClassName, code);
            Class cls1 = Class.forName(fullClassName);
            MyClassLoader clzloader = new MyClassLoader(cls1);
            cls=clzloader.loadClass(fullClassName);
            return cls;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 删除一个字段,并且删除geter，seter
     * 
     * @param fullclassName
     * @param fieldName
     * @return
     */
    public static Class removeField(String fullclassName,
                                    String fieldName)
    {
        Class clz = null;
        try
        {
            clz = Class.forName(fullclassName);
        }
        catch (ClassNotFoundException e2)
        {
            e2.printStackTrace();
            throw new RuntimeException("指定的类不存在");
        }

        try
        {

            Field[] hasfields = clz.getDeclaredFields();
            boolean flag = false;
            for (Field f : hasfields)
            {
                if (f.getName().equals(fieldName))
                {
                    flag = true; // 找到要删除的字段
                }
            }
            if (!flag)
            {
                System.out.println("要删除的字段不存在");
                return clz;
            }

            // 1
            InputStream ins = Const.class.getResourceAsStream(clz
                    .getSimpleName()
                    + ".class");
            ClassReader cr = null;
            try
            {
                cr = new ClassReader(ins);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            // byte[]
            // 2
            ClassWriter cw = new ClassWriter(cr, 0); // 设置写方法
            // 3
            ClassVisitor cv = new RemoveFieldAdapter(cw, clz, fieldName);// 对byte[]进行写操作
            // 4
            cr.accept(cv,
                      0);// 执行回调
            String savepath = UT.getClassPath() + clz.getSimpleName()
                    + ".class";// c:/.../cn/comdev/domain/Person.class

            // 保存到物理文件

            byte[] code = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(savepath);
            fos.write(code);
            fos.close();

            // Class return_clz = new MyClassLoader().defineClass(clz.getName(),
            // code);

            Class return_clz = getClzByFile(clz.getName());
            // System.out.println(return_clz);
            System.out.println("class中删除字段over:" + fieldName);
            return return_clz;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
