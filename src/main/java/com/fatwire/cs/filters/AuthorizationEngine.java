package com.fatwire.cs.filters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthorizationEngine {
    Log log = LogFactory.getLog(this.getClass());
    Set hashes = new HashSet();

    Set keys = new TreeSet();

    public boolean isAuthorized(Map map) {
        String hash = toHash(map);
        log.debug(hash);
        //very advanced and complex algo ;)
        return !hashes.contains(hash);
        /*
         * An alternative could be to run this on RemoteSatellite
         * and have this engine (in a background thread) poll a page on CS at a given interval
         * and based on the result of this page
         * build the hashes Set.
         * 
         */
    }

    String toHash(Map map) {
        StringBuffer b = new StringBuffer();
        for (Iterator itor = keys.iterator(); itor.hasNext();) {
            String entry = (String) itor.next();
            if (map.containsKey(entry)) {
                b.append(entry).append("=").append(map.get(entry));
                b.append("&");
            }
        }
        return b.toString();
    }

    public void shutdown() {

    }

    public void start() {
        this.keys.add("c");
        this.keys.add("cid");
        this.keys.add("pagename");
        this.hashes.add("c=Pages&");

    }

}
