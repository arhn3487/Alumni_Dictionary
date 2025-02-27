package com.company.alumniloginpage;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static void main(String[] args) {
        //sendEmail();
        Broadcast test=new Broadcast();
        String a[]={"arafathasan1711493048@gmail.com","arhasan3487@gmail.com"};
        Integer res=test.sendEmail(a,"subject","text","C:\\Users\\ASUS\\Downloads\\Alumni_Dictionaryy\\Alumni_Dictionary\\src\\main\\resources\\com\\company\\alumniloginpage\\picture\\MISTLogo.png");
        System.out.println(res);
        //C:\Users\ASUS\Downloads\DALL·E 2025-02-06 23.43.39 - A professionally designed invitation card for the MIST CSE Alumni Reunion. The card should have a clean, elegant layout with a blue and gold theme, fe.webp
    }
}
