package com.phr.util;

public class IDGenerator
{

   public static String generatePatientID()
   {
      return "PATIENT-" + System.currentTimeMillis();
   }

   public static String generateRecordID()
   {
      return "RECORD-" + System.currentTimeMillis();
   }
   
   public static String generatePin()
   {

      String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
               + "0123456789"
               + "abcdefghijklmnopqrstuvxyz";
      StringBuilder sb = new StringBuilder(10);
      for (int i = 0; i < 10; i++)
      {
         int index = (int) (alphaNumericString.length() * Math.random());
         sb.append(alphaNumericString.charAt(index));
      }
      return sb.toString();
   
   }
}
