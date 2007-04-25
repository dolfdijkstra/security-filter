package com.fatwire.cs.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class HelloWorldServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 7783047708818549188L;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.write("Hello World");
        for (Enumeration itor = request.getAttributeNames(); itor.hasMoreElements();
                ) {
            String entry = (String) itor.nextElement();
            writer.write(entry + "=" + request.getParameter(entry));
            writer.println();

        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            response.setContentType("text/plain");
            ServletInputStream in = request.getInputStream();
            ServletOutputStream out = response.getOutputStream();
            int c = 0;
            byte[] b = new byte[2048];
            while ((c = in.read(b)) != -1) {
                out.write(b, 0, c);
            }
            out.close();
        } else {
            doGet(request, response);
        }
    }
}
