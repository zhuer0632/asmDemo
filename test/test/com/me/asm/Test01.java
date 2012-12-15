package test.com.me.asm;


import java.util.ArrayList;
import java.util.List;

import com.me.asm.BuildProperty;
import com.me.asm.ClassFactory;
import com.me.asm.Const;


/**
 * 
 * 创建vo类的测试类
 * 
 * @author Administrator
 * 
 */
public class Test01
{

    @SuppressWarnings("unchecked")
    /**
     * 
     * 创建一个类>>>>>>>>>>>>>>>指定类名的同时指定字段
     * 
     */
    public void test01()
    {   
        List allFields = new ArrayList();
        BuildProperty pro1 = new BuildProperty("age", Const.typeInt);
        BuildProperty pro2 = new BuildProperty("username", Const.typeString);
        allFields.add(pro1);
        allFields.add(pro2);
        Class clz = ClassFactory.buildWithFields("VOPerson",
                                                 allFields);
        System.out.println(clz.getName());
    }


    /**
     *  创建一个类>>>>>>>>>>>使用默认字段创建一个类
     */
    private void test02()
    {
        Class clz = ClassFactory.build("VOPerson");
        System.out.println(clz.getName());
    }


    /**
     *  对于已经存在的类，添加一个字段
     */
    @SuppressWarnings("unchecked")
    private void test03()
    {
        Class clz = ClassFactory.getClzByFile("com.me.asm.VOPerson");
        BuildProperty field = new BuildProperty("birthday", Const.typeDate);
        Class clzResult = ClassFactory.addField(clz,
                                                field);
        System.out.println(clzResult.getName());
    }
    

    /**
     * 对于已经存在的类，删除一个字段
     */
    private void test04()
    {
        Class clz = null;
        try
        {
            clz = Class.forName("com.me.asm.VOPerson");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        System.out.println("删除前字段数量" + clz.getDeclaredFields().length);
        Class clzResult = ClassFactory.removeField("com.me.asm.VOPerson",
                                                   "modifyUser");
        //为什么已经删除了，但是取到的删除后字段数量没有变呢？因为运行当前main方法的时候，jvm已经加载了VOPerson类，并且jvm不允许修改已经加载好的class
        //所以想要看到效果，就要直接用jd-gui.exe查看修改后的class文件. 
        System.out.println("删除后字段数量" + clzResult.getDeclaredFields().length);
        
    }
    
    /**
     * 根据类全名加载Class
     */
    @SuppressWarnings("unchecked")
    private void test05()
    {
        Class clz = ClassFactory.getClzByFile("com.me.asm.VOPerson");
        System.out.println(clz.getName());
        System.out.println("当前类的字段数量："+clz.getDeclaredFields().length);
    }
    
    public static void main(String[] args)
    {
        Test01 t = new Test01();
        t.test05();
    }
    
}
