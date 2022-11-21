package com.phr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.phr.model.Patient;
import com.phr.util.DBConnection;

public class PatientDAO
{

   
   public void write(Patient model) throws Exception
   {
      Connection con = null;
      try
      {
         con = DBConnection.connect();
         PreparedStatement ps = con.prepareStatement("insert into patients (patientid, doctor, email, fname, lname, mobile, dob, entry_time, gender) values (?,?,?,?,?,?,?,?,?) ");
         ps.setString(1, model.getPatientId());
         ps.setString(2, model.getDoctor());
         ps.setString(3, model.getEmail());
         ps.setString(4, model.getFname());
         ps.setString(5, model.getLname());
         ps.setString(6, model.getMobile());
         ps.setDate(7, model.getDob());
         ps.setTimestamp(8, model.getEntry_time());
         ps.setString(9, model.getGender());
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

   
   public List<Patient> getAll(String doctor) throws Exception
   {
      List<Patient> result = new ArrayList<>();
      Connection con = null;
      try
      {
         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select * from patients where doctor='" + doctor + "' ");
         while (rs.next())
         {
            Patient p = new Patient();
            p.setDob(rs.getDate("dob"));
            p.setDoctor(rs.getString("doctor"));
            p.setEmail(rs.getString("email"));
            p.setEntry_time(rs.getTimestamp("entry_time"));
            p.setFname(rs.getString("fname"));
            p.setLname(rs.getString("lname"));
            p.setGender(rs.getString("gender"));
            p.setMobile(rs.getString("mobile"));
            p.setPatientId(rs.getString("patientid"));
            result.add(p);
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

   
   public Patient get(String patientId) throws Exception
   {

      Connection con = null;
      Patient p = null;
      try
      {

         con = DBConnection.connect();
         ResultSet rs = con.createStatement().executeQuery("select * from patients where patientid='" + patientId + "' ");
         rs.next();
         p = new Patient();

         p.setDob(rs.getDate("dob"));
         p.setDoctor(rs.getString("doctor"));
         p.setEmail(rs.getString("email"));
         p.setEntry_time(rs.getTimestamp("entry_time"));
         p.setFname(rs.getString("fname"));
         p.setLname(rs.getString("lname"));
         p.setGender(rs.getString("gender"));
         p.setMobile(rs.getString("mobile"));
         p.setPatientId(rs.getString("patientid"));

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

      return p;
   }

   
   public void delete(String patientId) throws Exception
   {

      Connection con = null;
      try
      {
         con = DBConnection.connect();
         con.createStatement().execute("delete from patients where patientid='" + patientId + "' ");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         con.close();
      }

   }

   
   public void update(Patient model) throws Exception
   {

      Connection con = null;
      try
      {
         con = DBConnection.connect();
         PreparedStatement ps = con.prepareStatement("update patients set email=?, fname=?, lname=?, mobile=?, dob=?, gender=? where patientid=? ");
         ps.setString(8, model.getPatientId());
         ps.setString(1, model.getEmail());
         ps.setString(2, model.getFname());
         ps.setString(3, model.getLname());
         ps.setString(4, model.getMobile());
         ps.setDate(5, model.getDob());
         ps.setTimestamp(6, model.getEntry_time());
         ps.setString(7, model.getGender());
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
