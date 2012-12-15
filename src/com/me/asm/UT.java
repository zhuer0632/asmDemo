package com.me.asm;


import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;


public class UT
{
    /**
     * 
     * 首字母大写返回
     * 
     * @param propertyName
     * @return
     */
    public static String FormatProperty(String propertyName)
    {
        String first = propertyName.substring(0,
                                              1).toUpperCase();
        String last = propertyName.substring(1);
        return first + last;
    }


    /**
     * 取得com.me.asm所在的绝对路径 <br>
     * D:\workspace_demo\asmDemo\bin\com\me\asm\
     * 
     * @return
     */
    public static String getClassPath()
    {
        URL url = UT.class.getResource(".");
        String urlPath = url.getPath();
        File classFile = new File(urlPath);
        String classPath = classFile.getPath();
        return classPath + File.separatorChar;
    }


    /**
     *  取得类所在的文件夹 
     *  <br>eg: D:\workspace_demo\asmDemo\bin\com\me\asm\
     * @param clz
     * @return
     */
    public static String getClassPath(Class clz)
    {
        URL url = clz.getResource(".");
        String urlPath = url.getPath();
        File classFile = new File(urlPath);
        String classPath = classFile.getPath();
        return classPath + File.separatorChar;
    }

    /**
     * 返回 simpleClassName
     * 
     * @param fullclassName
     *            com.me.asm.VOPerson
     * @return
     */
    public static String getClassNameByFullClassName(String fullclassName)
    {
        String[] arr = StringUtils.split(fullclassName,
                                         ".");
        return arr[arr.length - 1];
    }


    public static String getPackage(String fullclassName)
    {
        String[] arr = StringUtils.split(fullclassName,
                                         ".");
        String out="";
        for (int i = 0; i < arr.length-1; i++)
        {
            if(i==arr.length-1-1)
            {
                out=out+arr[i];
            }
            else
            {
                out=out+arr[i]+".";
            }
        }
        return out;
    }


    public static void main(String[] args)
    {
        String classname=getPackage("com.me.asm.VOPerson");
        System.out.println(classname);
        
    }



}
