package com.phr.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Util
{

   public static String getSecret(String str) throws Exception
   {
      MessageDigest md = MessageDigest.getInstance("MD5");

      // digest() method is called to calculate message digest
      // of an input digest() return array of byte
      byte[] messageDigest = md.digest(str.getBytes());

      // Convert byte array into signum representation
      BigInteger no = new BigInteger(1, messageDigest);

      // Convert message digest into` hex value
      String hashtext = no.toString(16);
      while (hashtext.length() < 32)
      {
         hashtext = "0" + hashtext;
      }
      return hashtext;

   }
   
   public static void main (String arg[]) throws Exception
   {
      System.out.println(getSecret("PATIENT-343453545454"));
   }
}
