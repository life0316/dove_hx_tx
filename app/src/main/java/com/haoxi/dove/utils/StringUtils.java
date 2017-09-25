package com.haoxi.dove.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean checkHanZi(String needCheckStr){
        boolean isCheaked = false;
        try {
            String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
            Pattern pattern = Pattern.compile(regex);
            Matcher match = pattern.matcher(needCheckStr);
            isCheaked = match.matches();
        }catch(Exception e) {
            isCheaked = false;
        }
        return isCheaked;
    }

    public static String format4(double value) {
        return String.format("%.4f", value).toString();
    }


    public static boolean checkEmail(String email){
        boolean isEmail = false;

        try{
            String check = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]{2,3}+)+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            isEmail = matcher.matches();
        }catch(Exception e) {
            isEmail = false;
        }

        return isEmail;
    }

    public static boolean isPhoneNumberValid(String phoneNumber){
        boolean isValid = false;

        String expression = "((^(13|14|15|17|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";

        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()){
            isValid = true;
        }

        return isValid;
    }

    public static boolean isNumberValid(String number){
        boolean isValid = false;

        String regex = "[1-9]{1}[0-9]{0,4}";
        CharSequence inputStr = number;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()){

            isValid = true;
        }
        return isValid;
    }

    public static Timestamp getNowTimestamp(){
        return new Timestamp(System.currentTimeMillis());
    }

    public static long tsToString(Timestamp ts){
        DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String str = format.format(ts);
        DateFormat dformat = new SimpleDateFormat("yyyyMMddhhmmss");
        try {
            long millionS = dformat.parse(str).getTime();
            return millionS;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
