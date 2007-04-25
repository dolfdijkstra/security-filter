/**
 * 
 */
package com.fatwire.cs.filters;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

class MyServletInputStream extends ServletInputStream {
    private final InputStream in;

    /**
     * @param in
     */
    public MyServletInputStream(final InputStream in) {
        super();
        this.in = in;
    }

    public int read() throws IOException {
        return in.read();
    }

}