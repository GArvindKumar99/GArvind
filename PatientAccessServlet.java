package com.phr.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.phr.crypto.Elgamal;
import com.phr.dao.DiscussionsDAO;
import com.phr.dao.PatientDAO;
import com.phr.dao.PinDAO;
import com.phr.dao.RecordsDAO;
import com.phr.model.Discussion;
import com.phr.model.Patient;
import com.phr.model.Record;
import com.phr.util.Util;

public class PatientAccessServlet extends HttpServlet
{
   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      try
      {
         String req_type = req.getParameter("req_type");
         PinDAO pinDao = new PinDAO();
         PatientDAO pDao = new PatientDAO();
         DiscussionsDAO dDao = new DiscussionsDAO();
         RecordsDAO rDao = new RecordsDAO();
         if (req_type.equals("verify"))
         {
            String pid = req.getParameter("pid");
            String pin = req.getParameter("pin");
            if (pinDao.verify(pid, pin))
            {
               List<Record> records = rDao.getAll(pid);
               List<Record> records2 = new ArrayList<Record>();
               for (Record r : records)
               {
                  Record r2 = new Record();
                  r2.setDoctor(r.getDoctor());
                  r2.setEntry_time(r.getEntry_time());
                  r2.setPatientID(r.getPatientID());
                  r2.setRecordID(r.getRecordID());
                  r2.setUpdate_time(r.getUpdate_time());

                  String data = r.getData();
                  String key = Util.getSecret(r.getDoctor().substring(0, r.getDoctor().indexOf("@")));
                  String arr[] = data.split("\n");
                  String decData = "";
                  for (String a: arr)
                  {
                     decData += Elgamal.decrypt(a, key);
                     decData += "\n";
                  }
                  System.out.println(decData);
                  
                  String key2 = Util.getSecret(pid);
                  String arr2[] = decData.split("\n");
                  String encData = "";
                  for (String a: arr2)
                  {
                     encData += Elgamal.encrypt(a, key2);
                     encData += "\n";
                  }
                    
                  System.out.println(encData);

                  r2.setData(encData);
                  records2.add(r2);
               }

               req.setAttribute("records", records2);
               req.setAttribute("patientid", pid);
               req.getSession().setAttribute("pid", pid);
               req.getRequestDispatcher("patient_access.jsp").forward(req, resp);
            }
            else
            {
               resp.sendRedirect("patient_access.jsp?msg=Sorry, Invalid PIN");
            }
         }
         else if (req_type.equals("dwrite"))
         {
            Discussion d = new Discussion();
            String pid = (String) req.getSession().getAttribute("pid");
            Patient patient = pDao.get(pid);
            d.setAuthor(patient.getFname() + " " + patient.getLname());
            d.setData(req.getParameter("data"));
            d.setPatientID(req.getParameter("patientid"));
            d.setEntry_time(new Timestamp(System.currentTimeMillis()));
            dDao.write(d);

            List<Record> records = rDao.getAll(pid);
            List<Record> records2 = new ArrayList<Record>();
            for (Record r : records)
            {
               Record r2 = new Record();
               r2.setDoctor(r.getDoctor());
               r2.setEntry_time(r.getEntry_time());
               r2.setPatientID(r.getPatientID());
               r2.setRecordID(r.getRecordID());
               r2.setUpdate_time(r.getUpdate_time());

               String data = r.getData();
               String key = Util.getSecret(r.getDoctor().substring(0, r.getDoctor().indexOf("@")));
               String arr[] = data.split("\n");
               String decData = "";
               for (String a: arr)
               {
                  decData += Elgamal.decrypt(a, key);
                  decData += "\n";
               }
               
               
               String key2 = Util.getSecret(pid);
               String arr2[] = decData.split("\n");
               String encData = "";
               for (String a: arr2)
               {
                  encData += Elgamal.encrypt(a, key2);
                  encData += "\n";
               }


               r2.setData(encData);
               records2.add(r2);
            }

            req.setAttribute("records", records2);

            req.setAttribute("patientid", pid);
            req.getRequestDispatcher("patient_access.jsp").forward(req, resp);
         }
         else if (req_type.equals("logout"))
         {
            req.getSession().invalidate();
            resp.sendRedirect("patient_access.jsp");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         resp.sendRedirect("patient_access.jsp?msg=Sorry! Something went wrong: " + e.getMessage());
      }
   }

}
