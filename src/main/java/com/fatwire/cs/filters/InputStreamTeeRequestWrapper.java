/**
 * 
 */
package com.fatwire.cs.filters;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class InputStreamTeeRequestWrapper extends HttpServletRequestWrapper {
    private TeeStream in;
    
    public byte[] toBytes(){
        return in.toByteArray();
    }

    public InputStreamTeeRequestWrapper(HttpServletRequest original) {
        super(original);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        if (in == null){
            in = new TeeStream(super.getInputStream());
        }
        return in;
    }

}