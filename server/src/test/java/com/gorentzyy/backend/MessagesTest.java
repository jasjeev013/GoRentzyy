package com.gorentzyy.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessagesTest {

    @Test
    public void testMessage(){
        Messages obj = new Messages();
        Assertions.assertEquals("Hello dialy offers",obj.getMessage("Hello dialy offers"));
    }
}
