package com.phr.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.phr.dao.PatientDAO;
import com.phr.dao.PinDAO;
import com.phr.mobile.SendMessage;
import com.phr.model.Patient;
import com.phr.model.Pin;
import com.phr.model.User;
import com.phr.util.IDGenerator;

public class PatientServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String type = req.getParameter("reqtype");
			User user = (User) req.getSession().getAttribute("user");
			PatientDAO pDao = new PatientDAO();
			PinDAO pinDao = new PinDAO();
			if (type == null) {
				resp.sendRedirect("patients.jsp?msg=Bad Request");
			} else {
				if (type.equals("write")) {
					Patient p = new Patient();
					String dob = req.getParameter("dob");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					p.setDob(new Date(sdf.parse(dob).getTime()));
					p.setDoctor(user.getEmail());
					p.setEmail(req.getParameter("email"));
					p.setEntry_time(new Timestamp(System.currentTimeMillis()));
					p.setFname(req.getParameter("fname"));
					p.setLname(req.getParameter("lname"));
					p.setGender(req.getParameter("gender"));
					p.setMobile(req.getParameter("mobile"));
					p.setPatientId(IDGenerator.generatePatientID());
					pDao.write(p);

					String pin = IDGenerator.generatePin();
					Pin pn = new Pin();
					pn.setPatientID(p.getPatientId());
					pn.setPin(pin);
					pinDao.write(pn);

					Properties props = new Properties();
					props.load(new InputStreamReader(new FileInputStream(new File("/home/ubuntu/app.properties"))));

					String url = "http://" + props.getProperty("host") + ":" + props.getProperty("port")
							+ "/PHR/patient_access.jsp";
					SendMessage.sendSms(p.getMobile(),
							"Patient ID: " + p.getPatientId() + "  Pin: " + pin + " Access your records here: " + url);

					resp.sendRedirect(
							"patients.jsp?msg=Patient " + p.getFname() + " " + p.getLname() + " Added Successfully");
				} else if (type.equals("delete")) {
					pDao.delete(req.getParameter("patientid"));
					resp.sendRedirect("patients.jsp?msg=Patient " + req.getParameter("patientid") + " Removed");
				} else if (type.equals("update")) {
					Patient p = new Patient();
					String dob = req.getParameter("dob");
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					p.setDob(new Date(sdf.parse(dob).getTime()));
					p.setDoctor(user.getEmail());
					p.setEmail(req.getParameter("email"));
					p.setEntry_time(new Timestamp(System.currentTimeMillis()));
					p.setFname(req.getParameter("fname"));
					p.setLname(req.getParameter("lname"));
					p.setGender(req.getParameter("gender"));
					p.setMobile(req.getParameter("mobile"));
					p.setPatientId(req.getParameter("patientid"));
					pDao.update(p);
					resp.sendRedirect(
							"patients.jsp?msg=Patient " + p.getFname() + " " + p.getLname() + " Updated Successfully");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect("error.jsp?msg=" + e.getMessage());
		}
	}

}
