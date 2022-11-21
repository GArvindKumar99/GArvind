package com.phr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.phr.model.Discussion;
import com.phr.util.DBConnection;

public class DiscussionsDAO
{

   
   public void write(Discussion model) throws Exception
   {

      Connection con = null;
      try
      {
         con = DBConnection.connect();
         PreparedStatement ps = con.prepareStatement("insert into discussion (patientid, data, author, entry_time) values (?,?,?,?) ");
         ps.setString(1, model.getPatientID());
         ps.setString(2, model.getData());
         ps.setString(3, model.getAuthor());
         ps.setTimestamp(4, model.getEntry_time());
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

   
   public List<Discussion> getAll(String patientID) throws Exception
   {
      List<Discussion> result = new ArrayList<Discussion>();
      Connection con = null;
      try
      {
         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select * from discussion where patientid='" + patientID + "' ");
         while (rs.next())
         {
            Discussion d = new Discussion();
            d.setAuthor(rs.getString("author"));
            d.setData(rs.getString("data"));
            d.setEntry_time(rs.getTimestamp("entry_time"));
            d.setPatientID(patientID);
            result.add(d);
         }
      }
      catch (Exception e)
      {

      }
      finally
      {
         con.close();
      }
      return result;
   }

}
