/**
 * 
 */
package com.fatwire.cs.filters;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class MPRequestWrapper extends HttpServletRequestWrapper {
    final ServletInputStream in;

    public MPRequestWrapper(HttpServletRequest original,
            ServletInputStream in) {
        super(original);
        this.in = in;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        return in;
    }

}