package com.gorentzyy.backend;

public class Messages {
    public String getMessage(String name){
        StringBuilder s = new StringBuilder();
        if (name==null){
            s = s.append("PLease provce name:");
        }else {
            s.append("Hello dialy offers");
        }
        return s.toString();
    }
}
