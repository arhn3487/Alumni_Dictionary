package com.company.alumniloginpage;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static void main(String[] args) {
        //sendEmail();
        SendOTP test=new  SendOTP();
        String a[]={"arafathasan1711493048@gmail.com","arhasan3487@gmail.com"};
        Integer res=test.sendEmail("arafat","arhasan3487@gmail.com");
        System.out.println(res);
    }
}
