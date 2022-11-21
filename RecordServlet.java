package com.phr.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.phr.crypto.Elgamal;
import com.phr.dao.DiscussionsDAO;
import com.phr.dao.PatientDAO;
import com.phr.dao.PinDAO;
import com.phr.dao.RecordsDAO;
import com.phr.mobile.SendMessage;
import com.phr.model.Discussion;
import com.phr.model.Patient;
import com.phr.model.Record;
import com.phr.model.User;
import com.phr.util.IDGenerator;
import com.phr.util.Util;

public class RecordServlet extends HttpServlet
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
         String type = req.getParameter("reqtype");
         User user = (User) req.getSession().getAttribute("user");
         PatientDAO pDao = new PatientDAO();
         DiscussionsDAO dDao = new DiscussionsDAO();
         RecordsDAO rDao = new RecordsDAO();
         PinDAO pinDao = new PinDAO();
         if (type == null)
         {
            resp.sendRedirect("patients.jsp?msg=Bad Request");
         }
         else
         {
            if (type.equals("read"))
            {
               String patientid = req.getParameter("patientid");
               List<Record> records = rDao.getAll(patientid);
               req.setAttribute("records", records);
               req.setAttribute("patientid", patientid);
               req.getRequestDispatcher("records.jsp").forward(req, resp);
            }
            else if (type.equals("write"))
            {
               Record r = new Record();
               String data = req.getParameter("data");
               String key = Util.getSecret(user.getEmail().substring(0, user.getEmail().indexOf("@")));
               
               String arr[] = data.split("\n");
               String encData = "";
               for (String a: arr)
               {
                  encData += Elgamal.encrypt(a, key);
                  encData += "\n";
               }
               r.setData(encData);
               r.setDoctor(user.getEmail());
               r.setEntry_time(new Timestamp(System.currentTimeMillis()));
               r.setPatientID(req.getParameter("patientid"));
               r.setRecordID(IDGenerator.generateRecordID());
               r.setUpdate_time(r.getEntry_time());
               rDao.write(r);

               Patient patient = pDao.get(r.getPatientID());
               String pin = pinDao.getPin(r.getPatientID());
               

				Properties props = new Properties();
				props.load(new InputStreamReader(new FileInputStream(new File("/home/ubuntu/app.properties"))));

				String url = "http://" + props.getProperty("host") + ":" + props.getProperty("port")
						+ "/PHR/patient_access.jsp";

               SendMessage.sendSms(patient.getMobile(), "New Health Record is created. Use Patient ID: "+ r.getPatientID() +" and PIN: " + pin + "to access it in the PHR portal: "+url);

               resp.sendRedirect("record?reqtype=read&patientid="+r.getPatientID()+"&msg=New Record for " + patient.getFname() + " " + patient.getLname() + "written");
            }
            else if (type.equals("delete"))
            {
               rDao.delete(req.getParameter("recordid"));
               Patient patient = pDao.get(req.getParameter("recordid"));
               resp.sendRedirect("records.jsp?msg=Record Deleted for " + patient.getFname() + " " + patient.getLname() + "written");
            }
            else if (type.equals("update"))
            {

               Record r = new Record();
               r.setData(req.getParameter("data"));
               r.setDoctor(user.getEmail());
               r.setPatientID(req.getParameter("patient_id"));
               r.setRecordID(req.getParameter("recordid"));
               r.setUpdate_time(r.getEntry_time());
               rDao.update(r);

               Patient patient = pDao.get(req.getParameter("recordid"));

               resp.sendRedirect("records.jsp?msg=Updated Record for " + patient.getFname() + " " + patient.getLname());
            }
            else if (type.equals("dwrite"))
            {
               Discussion d = new Discussion();
               d.setAuthor("Dr. "+ user.getFname()+" "+user.getLname());
               d.setData(req.getParameter("data"));
               d.setPatientID(req.getParameter("patientid"));
               d.setEntry_time(new Timestamp(System.currentTimeMillis()));
               dDao.write(d);
               resp.sendRedirect("record?reqtype=read&patientid="+d.getPatientID());
            }
               
            
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         resp.sendRedirect("error.jsp?msg=" + e.getMessage());
      }
   }

}
