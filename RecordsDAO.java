package com.phr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.phr.model.Record;
import com.phr.util.DBConnection;

public class RecordsDAO
{

   
   public void write(Record model) throws Exception
   {

      Connection con = null;
      try
      {
         con = DBConnection.connect();
         PreparedStatement ps = con.prepareStatement("insert into records (data, doctor, patientid, recordid, entry_time, update_time) values (?,?,?,?,?,?) ");
         ps.setString(1, model.getData());
         ps.setString(2, model.getDoctor());
         ps.setString(3, model.getPatientID());
         ps.setString(4, model.getRecordID());
         ps.setTimestamp(5, model.getEntry_time());
         ps.setTimestamp(6, model.getUpdate_time());
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

   
   public List<Record> getAll(String patientID) throws Exception
   {
      List<Record> result = new ArrayList<Record>();
      Connection con = null;
      try
      {
         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select * from records where patientid='"+patientID+"' ");
         while (rs.next())
         {
            Record r = new Record();
            r.setData(rs.getString("data"));
            r.setDoctor(rs.getString("doctor"));
            r.setEntry_time(rs.getTimestamp("entry_time"));
            r.setUpdate_time(rs.getTimestamp("update_time"));
            r.setPatientID(rs.getString("patientid"));
            r.setRecordID(rs.getString("recordid"));
            result.add(r);
         }
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

   
   public Record get(String recordID) throws Exception
   {

      Connection con = null;
      Record r = null;

      try
      {

         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select * from records");
         rs.next();
         r = new Record();
         r.setData(rs.getString("data"));
         r.setDoctor(rs.getString("doctor"));
         r.setEntry_time(rs.getTimestamp("entry_time"));
         r.setUpdate_time(rs.getTimestamp("update_time"));
         r.setPatientID(rs.getString("patientid"));
         r.setRecordID(rs.getString("recordid"));

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

      return r;
   }

   
   public void delete(String recordID) throws Exception
   {

      Connection con = null;
      try
      {
         con = DBConnection.connect();
         con.createStatement().execute("delete from records where recordid='" + recordID + "' ");

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

   
   public void update(Record model) throws Exception
   {

      Connection con = null;
      try
      {

         con = DBConnection.connect();
         PreparedStatement ps = con.prepareStatement("update records set data=?, update_time=? where recordid=?");
         ps.setString(1, model.getData());
         ps.setString(3, model.getRecordID());
         ps.setTimestamp(2, model.getUpdate_time());
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

}
