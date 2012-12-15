package com.me.asm;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class MyClassLoader extends ClassLoader
{

    public static String path = "";


    // public Class defineClass(String name, byte[] b)
    // {
    // // System.out.println("MyClassLoader-->>" + name);
    // path = name;
    // return defineClass(name, b, 0, b.length);
    // }

    @SuppressWarnings("unchecked")
    public MyClassLoader(Class clz)
    {
        path = MyClassLoader.path = UT.getClassPath() + clz.getSimpleName()
                + ".class";
    }
    

    public Class<?> findClass(String name)
    {
        if (path == null || (path != null && path.length() == 0))
        {
            throw new RuntimeException("没有指定类" + name + "的位置");
        }
        byte[] data = loadClassData(path);
        return this.defineClass(name,
                                data,
                                0,
                                data.length);
    }


    /**
     * 
     * @param classfilePath
     * @return
     */
    private byte[] loadClassData(String classfilePath)
    {
        byte[]out=null;
        try
        {
            FileInputStream is = new FileInputStream(new File(classfilePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = is.read()) != -1)
            {
                baos.write(b);
            }
            out= baos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return out;
    }
}
