package com.fatwire.cs.filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityFilter implements Filter {
    Log log = LogFactory.getLog(this.getClass());
    private SecurityGuard methodGetGuard;

    private SecurityGuard methodPostGuard;

    private SecurityGuard methodMultiPartGuard;

    private UnknownMethodSecurityGuard methodUnknowGuard;

    private AuthorizationEngine authEngine;

    public void destroy() {
        this.authEngine.shutdown();

    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            doFilter((HttpServletRequest) request,
                    (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }

    }

    protected void doFilter(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("filtering http request");
        SecurityGuard guard = getGuard(request);
        try {
            HttpServletRequest ret = guard.isAuthorized(request);
            //forward request with the returned request. This might be the original
            chain.doFilter(ret, response);
        } catch (NotAuthorizedException e) {
            log.error(e.getMessage(),e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            //stop processing the request
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
        this.authEngine = new AuthorizationEngine();
        this.authEngine.start();
        this.methodGetGuard = new GetSecurityGuard();
        this.methodPostGuard = new PostSecurityGuard();
        this.methodMultiPartGuard = new MultiPartMimeSecurityGuard();
        this.methodUnknowGuard = new UnknownMethodSecurityGuard();

    }

    private SecurityGuard getGuard(HttpServletRequest request) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {

            return methodGetGuard;
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (isMultipart) {
                return this.methodMultiPartGuard;
            } else {
                return this.methodPostGuard;
            }
        } else {
            return this.methodUnknowGuard;
        }

    }

    interface SecurityGuard {

        HttpServletRequest isAuthorized(HttpServletRequest request)
                throws NotAuthorizedException;

    }

    class GetSecurityGuard implements SecurityGuard {
        Log log = LogFactory.getLog(this.getClass());

        String[] securityPars = new String[] { "c", "cid", "pagename" };

        public HttpServletRequest isAuthorized(HttpServletRequest request) throws NotAuthorizedException {
            log.debug("processing request");
            Map map = new HashMap();
            for (int i = 0; i < securityPars.length; i++) {
                if (request.getParameter(securityPars[i]) != null) {
                    log.debug("adding" + securityPars[i]+"="+ request
                            .getParameter(securityPars[i]));
                    map.put(securityPars[i], request
                            .getParameter(securityPars[i]));
                }
            }
            if (!authEngine.isAuthorized(map)) {
                throw new NotAuthorizedException();
            }
            return request;

        }

    }

    class PostSecurityGuard implements SecurityGuard {
        Log log = LogFactory.getLog(this.getClass());
        String[] securityPars = new String[] { "c", "cid", "pagename" };

        public HttpServletRequest isAuthorized(HttpServletRequest request) throws NotAuthorizedException {
            log.debug("processing request");
            Map map = new HashMap();
            for (int i = 0; i < securityPars.length; i++) {
                if (request.getParameter(securityPars[i]) != null) {
                    log.debug("adding: " + securityPars[i]+"="+ request
                            .getParameter(securityPars[i]));

                    map.put(securityPars[i], request
                            .getParameter(securityPars[i]));
                }
            }
            if (!authEngine.isAuthorized(map)) {
                throw new NotAuthorizedException();
            }
            return request;
        }

    }

    class MultiPartMimeSecurityGuard implements SecurityGuard {
        Log log = LogFactory.getLog(this.getClass());

        Set names = new HashSet();

        //      Create a factory for disk-based file items
        FileItemFactory factory = new DiskFileItemFactory();

        //         Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        /**
         * 
         */
        public MultiPartMimeSecurityGuard() {
            super();
            //we're only interested in c cid and pagename 
            names.add("c");
            names.add("cid");
            names.add("pagename");
        }

        public HttpServletRequest isAuthorized(HttpServletRequest request)
                throws NotAuthorizedException {
            log.debug("processing request");
            

            //MyServletInputStream myIs = new MyServletInputStream(in);
            
            InputStreamTeeRequestWrapper mpwrapper = new InputStreamTeeRequestWrapper(request);
            //          Parse the request
            Map map = new HashMap();
            try {
                List /* FileItem */items = upload.parseRequest(mpwrapper);
                for (Iterator itor = items.iterator(); itor.hasNext();) {
                    FileItem item = (FileItem) itor.next();
                    if (item.isFormField()) {
                        String name = item.getFieldName();
                        if (names.contains(name)) {
                            //c cid and pagename are stings
                            String value = item.getString();
                            log.debug("adding:" + name+"="+ value);

                            map.put(name, value);
                        }
                    }
                    item.delete();
                }

            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            if (!authEngine.isAuthorized(map)) {
                throw new NotAuthorizedException();
            }
            //get the bytes from the orginal request
            InputStream in = new ByteArrayInputStream(mpwrapper.toBytes());
            //create a wrapper that holds the cached 'orginal' request inputstream
            MPRequestWrapper wrapper = new MPRequestWrapper(request, new MyServletInputStream(in));
            
            return wrapper;
        }
    }

    class UnknownMethodSecurityGuard implements SecurityGuard {

        public HttpServletRequest isAuthorized(HttpServletRequest request) {
            // always auth
            return request;
        }
    }

}
