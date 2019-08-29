package com.example.mobileapplication.Model;

public abstract class Data {
    public abstract String getName();
    public abstract String toString();
    public abstract String getInitials();

    @Override
    public boolean equals(Object object){
        if (object instanceof Data)
        {
            Data temp = (Data) object;
            if(this.getName().replace(" ", "").toUpperCase().equals(temp.getName().replace(" ", "").toUpperCase()))
                return true;
        }
        return false;
    }

    public int hashCode(){
        return (this.getName().hashCode());
    }
}
