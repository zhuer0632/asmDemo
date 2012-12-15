package com.me.asm;

public class BuildProperty
{
    public String Type;
    public String Name;

    public BuildProperty()
    {
        
    }
    
    public BuildProperty(String name, String type)
    {
        this.Name = name;
        this.Type = type;
    }
    

    public String getType()
    {
        return Type;
    }


    public void setType(String type)
    {
        Type = type;
    }


    public String getName()
    {
        return Name;
    }


    public void setName(String name)
    {
        Name = name;
    }

}
