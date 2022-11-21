package com.phr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.phr.model.Pin;
import com.phr.util.DBConnection;

public class PinDAO
{

   
   public void write(Pin model) throws Exception
   {

      Connection con = null;
      try
      {
         con = DBConnection.connect();
         PreparedStatement ps = con.prepareStatement("insert into pin values (?,?) ");
         ps.setString(1, model.getPatientID());
         ps.setString(2, model.getPin());
         ps.execute();

      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
      finally
      {
         con.close();
      }

   }

   
   public Boolean verify(String patientID, String pin) throws Exception
   {
      boolean result = false;
      Connection con = null;
      try
      {
         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select count (*) from pin where patientid='" + patientID + "' and pin='" + pin + "' ");
         rs.next();
         if (rs.getInt(1) > 0)
            result = true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
      finally
      {
         con.close();
      }

      return result;
   }

   
   public String getPin(String patientID) throws Exception
   {
      String pin = ""; 
      Connection con = null;
      try
      {
         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select pin from pin where patientid='"+patientID+"' ");
         rs.next();
         pin = rs.getString(1);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
      finally
      {
         con.close();
      }

      return pin;
   }

}
