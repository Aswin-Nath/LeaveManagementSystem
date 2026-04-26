package com.kira.leavemanagement.utils;


public class EmployeeHelper {
    private static final String EMAIL_REGEX =
    "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    public static Boolean isValidMailAndName(String mail,String name){
        if(name==null || name.trim().length()==0){
            throw new IllegalArgumentException("Employee name is not given");
        }
        if (mail == null || !mail.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return true;
    }
    public static Boolean isValidPhoneNumber(String phoneNumber){
        if(phoneNumber==null || phoneNumber.trim().length()!=10){
            throw new IllegalArgumentException("Phone number should be indian type and should have 10 numbers");
        }
        for(int i=0;i<phoneNumber.length();i++){
            char digit = phoneNumber.charAt(i);
            if(!Character.isDigit(digit)){
                throw new IllegalArgumentException("Phone number should consist only of digits (0-9)");
            }
        }
        return true;
    }
    
    public static Boolean isValidPassword(String password){
        if(password==null || password.length()<8){
            throw new IllegalArgumentException("Password should have a minimum length of 8");
        }
        int upper=0;
        int lower=0;
        int digit=0;
        int special=0;
        for(int i=0;i<password.length();i++){
            char letter = password.charAt(i);

            if(Character.isLowerCase(letter)){
                lower++;
            }
            else if(Character.isUpperCase(letter)){
                upper++;
            }
            else if(Character.isDigit(letter)){
                digit++;
            }
            else{
                special++;
            }
        }
        if(upper==0 || lower==0 || digit==0 || special==0){
            throw new IllegalArgumentException("password should have atleast one upper,one lower,one digit,one special");
        }
        return true;
    }
}
