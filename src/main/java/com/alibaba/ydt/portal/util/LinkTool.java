package com.alibaba.ydt.portal.util;

import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.view.ServletUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * link tool
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 *          Created on 15/1/13 下午12:42.
 */
public class LinkTool extends BaseRequestTool implements Cloneable {
    public static final String HTML_QUERY_DELIMITER = "&";
    public static final String XHTML_QUERY_DELIMITER = "&amp;";
    public static final String APPEND_PARAMS_KEY = "appendParameters";
    public static final String FORCE_RELATIVE_KEY = "forceRelative";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String DEFAULT_SCHEME = "http";
    public static final String SECURE_SCHEME = "https";
    public static final String URI_KEY = "uri";
    public static final String SCHEME_KEY = "scheme";
    public static final String USER_KEY = "user";
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String PATH_KEY = "requestPath";
    public static final String QUERY_KEY = "params";
    public static final String FRAGMENT_KEY = "anchor";
    public static final String CHARSET_KEY = "charset";
    public static final String XHTML_MODE_KEY = "xhtml";
    protected Log LOG;
    protected String scheme = null;
    protected String user = null;
    protected String host = null;
    protected int port = -1;
    protected String path = null;
    protected Map query = null;
    protected String fragment = null;
    protected String charset = "UTF-8";
    protected String queryDelim = "&amp;";
    protected boolean appendParams = true;
    protected boolean forceRelative = false;
    protected boolean opaque = false;
    protected final LinkTool self = this;

    public LinkTool() {
    }

    @Override
    public void init(Object obj) throws Exception {
        super.init(obj);
        if(null != request) {
            this.setScheme(request.getScheme());
            this.setPort(Integer.valueOf(request.getServerPort()));
            this.setHost(request.getServerName());
            String ctx = request.getContextPath();
//            String pth = ServletUtils.getPath(request);
            String pth = "";
            this.setPath(this.combinePath(ctx, pth));
        }
    }

    protected final void debug(String msg, Object... args) {
        this.debug(msg, (Throwable)null, args);
    }

    protected final void debug(String msg, Throwable t, Object... args) {
        if(this.LOG != null && this.LOG.isDebugEnabled()) {
            this.LOG.debug("LinkTool: " + String.format(msg, args), t);
        }

    }

    protected LinkTool duplicate() {
        return this.duplicate(false);
    }

    protected LinkTool duplicate(boolean deep) {
        try {
            LinkTool e = (LinkTool)this.clone();
            if(deep && this.query != null) {
                e.query = new LinkedHashMap(this.query);
            }

            return e;
        } catch (CloneNotSupportedException var4) {
            String msg = "Could not properly clone " + this.getClass();
            if(this.LOG != null) {
                this.LOG.error(msg, var4);
            }

            throw new RuntimeException(msg, var4);
        }
    }

    public void setCharacterEncoding(String chrst) {
        this.charset = chrst;
    }

    public void setXHTML(boolean xhtml) {
        this.queryDelim = xhtml?"&amp;":"&";
    }

    public void setAppendParams(boolean addParams) {
        this.appendParams = addParams;
    }

    public void setForceRelative(boolean forceRelative) {
        this.forceRelative = forceRelative;
    }

    public void setScheme(Object obj) {
        if(obj == null) {
            this.scheme = null;
        } else {
            this.scheme = String.valueOf(obj);
            if(this.scheme.length() == 0) {
                this.scheme = null;
            }

            if(this.scheme.endsWith(":")) {
                this.scheme = this.scheme.substring(0, this.scheme.length() - 1);
            }
        }

    }

    public void setUserInfo(Object obj) {
        this.user = obj == null?null:String.valueOf(obj);
    }

    public void setHost(Object obj) {
        this.host = obj == null?null:String.valueOf(obj);
    }

    public void setPort(Object obj) {
        if(obj == null) {
            this.port = -1;
        } else if(obj instanceof Number) {
            this.port = ((Number)obj).intValue();
        } else {
            try {
                this.port = Integer.parseInt(String.valueOf(obj));
            } catch (NumberFormatException var3) {
                this.debug("Could convert \'%s\' to int", var3, new Object[]{obj});
                this.port = -2;
            }
        }

    }

    public void setPath(Object obj) {
        if(obj == null) {
            this.path = null;
        } else {
            this.path = String.valueOf(obj);
            if(!this.opaque && !this.path.startsWith("/")) {
                this.path = '/' + this.path;
            }
        }

    }

    public void appendPath(Object obj) {
        if(obj != null && !this.opaque) {
            this.setPath(this.combinePath(this.getPath(), String.valueOf(obj)));
        }

    }

    protected String combinePath(String start, String end) {
        if(end == null) {
            return start;
        } else if(start == null) {
            return end;
        } else {
            boolean startEnds = start.endsWith("/");
            boolean endStarts = end.startsWith("/");
            return startEnds ^ endStarts?start + end:(startEnds & endStarts?start + end.substring(1, end.length()):start + '/' + end);
        }
    }

    public void setQuery(Object obj) {
        if(obj == null) {
            this.query = null;
        } else if(obj instanceof Map) {
            this.query = new LinkedHashMap((Map)obj);
        } else {
            String qs = this.normalizeQuery(String.valueOf(obj));
            this.query = this.parseQuery(qs);
        }

    }

    protected String normalizeQuery(String qs) {
        if(qs.indexOf(38) >= 0) {
            qs = qs.replaceAll("&(amp;)?", this.queryDelim);
        }

        return qs;
    }

    public String toQuery(Map parameters) {
        if(parameters == null) {
            return null;
        } else {
            StringBuilder query = new StringBuilder();

            Map.Entry entry;
            for(Iterator i$ = parameters.entrySet().iterator(); i$.hasNext(); query.append(this.toQuery(entry.getKey(), entry.getValue()))) {
                Object e = i$.next();
                entry = (Map.Entry)e;
                if(query.length() > 0) {
                    query.append(this.queryDelim);
                }
            }

            return query.toString();
        }
    }

    public void appendQuery(Object obj) {
        if(obj != null) {
            this.setQuery(this.combineQuery(this.getQuery(), String.valueOf(obj)));
        }

    }

    public void setParam(Object key, Object value, boolean append) {
        String key1 = String.valueOf(key);
        if(this.query == null) {
            this.query = new LinkedHashMap();
            this.putParam(key1, value);
        } else if(append) {
            this.appendParam((String)key1, value);
        } else {
            this.putParam(key1, value);
        }

    }

    private void appendParam(String key, Object value) {
        if(this.query.containsKey(key)) {
            Object cur = this.query.get(key);
            if(cur instanceof List) {
                this.addToList((List)cur, value);
            } else {
                ArrayList vals = new ArrayList();
                vals.add(cur);
                this.addToList(vals, value);
                this.putParam(key, vals);
            }
        } else {
            this.putParam(key, value);
        }

    }

    private void putParam(Object key, Object value) {
        if(value instanceof Object[]) {
            ArrayList vals = new ArrayList();
            Object[] arr$ = (Object[])((Object[])value);
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Object v = arr$[i$];
                vals.add(v);
            }

            value = vals;
        }

        this.query.put(key, value);
    }

    private void addToList(List vals, Object value) {
        if(value instanceof List) {
            Iterator arr$ = ((List)value).iterator();

            while(arr$.hasNext()) {
                Object len$ = arr$.next();
                vals.add(len$);
            }
        } else if(value instanceof Object[]) {
            Object[] var7 = (Object[])((Object[])value);
            int var8 = var7.length;

            for(int i$ = 0; i$ < var8; ++i$) {
                Object v = var7[i$];
                vals.add(v);
            }
        } else {
            vals.add(value);
        }

    }

    public void setParams(Object obj, boolean append) {
        if(!append) {
            this.setQuery(obj);
        } else if(obj != null) {
            if(!(obj instanceof Map)) {
                obj = this.parseQuery(String.valueOf(obj));
            }

            if(obj != null) {
                if(this.query == null) {
                    this.query = new LinkedHashMap();
                }

                Iterator i$ = ((Map)obj).entrySet().iterator();

                while(i$.hasNext()) {
                    Object e = i$.next();
                    Map.Entry entry = (Map.Entry)e;
                    String key = String.valueOf(entry.getKey());
                    this.appendParam(key, entry.getValue());
                }
            }
        }

    }

    public Object removeParam(Object key) {
        if(this.query != null) {
            String key1 = String.valueOf(key);
            return this.query.remove(key1);
        } else {
            return null;
        }
    }

    protected void handleParamsBoolean(boolean keep) {
        if(!keep) {
            this.setQuery((Object)null);
        }

    }

    protected String combineQuery(String current, String add) {
        if(add != null && add.length() != 0) {
            if(add.startsWith("?")) {
                add = add.substring(1, add.length());
            }

            if(current != null && current.length() != 0) {
                if(current.endsWith(this.queryDelim)) {
                    current = current.substring(0, current.length() - this.queryDelim.length());
                } else if(current.endsWith("&")) {
                    current = current.substring(0, current.length() - 1);
                }

                if(add.startsWith(this.queryDelim)) {
                    return current + add;
                } else {
                    if(add.startsWith("&")) {
                        add = add.substring(1, add.length());
                    }

                    return current + this.queryDelim + add;
                }
            } else {
                return add;
            }
        } else {
            return current;
        }
    }

    protected String toQuery(Object key, Object value) {
        StringBuilder out = new StringBuilder();
        if(value == null) {
            out.append(this.encode(key));
            out.append('=');
        } else if(value instanceof List) {
            this.appendAsArray(out, key, ((List)value).toArray());
        } else if(value instanceof Object[]) {
            this.appendAsArray(out, key, (Object[])((Object[])value));
        } else {
            out.append(this.encode(key));
            out.append('=');
            out.append(this.encode(value));
        }

        return out.toString();
    }

    protected void appendAsArray(StringBuilder out, Object key, Object[] arr) {
        String encKey = this.encode(key);

        for(int i = 0; i < arr.length; ++i) {
            out.append(encKey);
            out.append('=');
            if(arr[i] != null) {
                out.append(this.encode(arr[i]));
            }

            if(i + 1 < arr.length) {
                out.append(this.queryDelim);
            }
        }

    }

    protected Map<String, Object> parseQuery(String query) {
        return this.parseQuery(this.normalizeQuery(query), this.queryDelim);
    }

    protected Map<String, Object> parseQuery(String query, String queryDelim) {
        if(query.startsWith("?")) {
            query = query.substring(1, query.length());
        }

        String[] pairs = query.split(queryDelim);
        if(pairs.length == 0) {
            return null;
        } else {
            LinkedHashMap params = new LinkedHashMap(pairs.length);
            String[] arr$ = pairs;
            int len$ = pairs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String pair = arr$[i$];
                String[] kv = pair.split("=");
                String key = kv[0];
                Object value = kv.length > 1?kv[1]:null;
                if(params.containsKey(kv[0])) {
                    Object oldval = params.get(key);
                    if(oldval instanceof List) {
                        ((List)oldval).add((String)value);
                        value = oldval;
                    } else {
                        ArrayList list = new ArrayList();
                        list.add((String)oldval);
                        list.add((String)value);
                        value = list;
                    }
                }

                params.put(key, value);
            }

            return params;
        }
    }

    public void setFragment(Object obj) {
        if(obj == null) {
            this.fragment = null;
        } else {
            this.fragment = String.valueOf(obj);
            if(this.fragment.length() == 0) {
                this.fragment = null;
            }
        }

    }

    protected boolean setFromURI(Object obj) {
        if(obj == null) {
            this.setScheme((Object)null);
            this.setUserInfo((Object)null);
            this.setHost((Object)null);
            this.setPort((Object)null);
            this.setPath((Object)null);
            this.setQuery((Object)null);
            this.setFragment((Object)null);
            return true;
        } else {
            URI uri = this.toURI(obj);
            if(uri == null) {
                return false;
            } else {
                this.setScheme(uri.getScheme());
                if(uri.isOpaque()) {
                    this.opaque = true;
                    this.setPath(uri.getSchemeSpecificPart());
                } else {
                    this.setUserInfo(uri.getUserInfo());
                    this.setHost(uri.getHost());
                    this.setPort(Integer.valueOf(uri.getPort()));
                    String pth = uri.getPath();
                    if(pth.equals("/") || pth.length() == 0) {
                        pth = null;
                    }

                    this.setPath(pth);
                    this.setQuery(uri.getQuery());
                }

                this.setFragment(uri.getFragment());
                return true;
            }
        }
    }

    protected URI toURI(Object obj) {
        if(obj instanceof URI) {
            return (URI)obj;
        } else {
            try {
                return new URI(String.valueOf(obj));
            } catch (Exception var3) {
                this.debug("Could convert \'%s\' to URI", var3, new Object[]{obj});
                return null;
            }
        }
    }

    protected URI createURI() {
        try {
            if(this.port > -2) {
                if(this.opaque) {
                    return new URI(this.scheme, this.path, this.fragment);
                }

                if(this.forceRelative) {
                    if(this.path == null && this.query == null && this.fragment == null) {
                        return null;
                    }

                    return new URI((String)null, (String)null, (String)null, -1, this.path, this.toQuery(this.query), this.fragment);
                }

                if(this.scheme == null && this.user == null && this.host == null && this.path == null && this.query == null && this.fragment == null) {
                    return null;
                }

                return new URI(this.scheme, this.user, this.host, this.port, this.path, this.toQuery(this.query), this.fragment);
            }
        } catch (Exception var2) {
            this.debug("Could not create URI", var2, new Object[0]);
        }

        return null;
    }

    public String getCharacterEncoding() {
        return this.charset;
    }

    public boolean isXHTML() {
        return this.queryDelim.equals("&amp;");
    }

    public boolean getAppendParams() {
        return this.appendParams;
    }

    public LinkTool scheme(Object scheme) {
        LinkTool copy = this.duplicate();
        copy.setScheme(scheme);
        return copy;
    }

    public LinkTool secure() {
        return this.scheme("https");
    }

    public LinkTool insecure() {
        return this.scheme("http");
    }

    public String getScheme() {
        return this.scheme;
    }

    public boolean isSecure() {
        return "https".equalsIgnoreCase(this.getScheme());
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public LinkTool user(Object info) {
        LinkTool copy = this.duplicate();
        copy.setUserInfo(info);
        return copy;
    }

    public String getUser() {
        return this.user;
    }

    public LinkTool host(Object host) {
        LinkTool copy = this.duplicate();
        copy.setHost(host);
        if(copy.getHost() != null && !copy.isAbsolute()) {
            copy.setScheme("http");
        }

        return copy;
    }

    public String getHost() {
        return this.host;
    }

    public LinkTool port(Object port) {
        LinkTool copy = this.duplicate();
        copy.setPort(port);
        return copy;
    }

    public Integer getPort() {
        return this.port < 0?null:Integer.valueOf(this.port);
    }

    public LinkTool path(Object pth) {
        LinkTool copy = this.duplicate();
        copy.setPath(pth);
        return copy;
    }

    public String getPath() {
        return this.path;
    }

    public LinkTool append(Object pth) {
        LinkTool copy = this.duplicate();
        copy.appendPath(pth);
        return copy;
    }

    public String getDirectory() {
        if(this.path != null && !this.opaque) {
            int lastSlash = this.path.lastIndexOf(47);
            return lastSlash < 0?"":this.path.substring(0, lastSlash + 1);
        } else {
            return null;
        }
    }

    public String getFile() {
        if(this.path != null && !this.opaque) {
            int lastSlash = this.path.lastIndexOf(47);
            return lastSlash < 0?this.path:this.path.substring(lastSlash + 1, this.path.length());
        } else {
            return null;
        }
    }

    public String getRoot() {
        LinkTool root = this.root();
        return root == null?null:root.toString();
    }

    public LinkTool root() {
        if(this.host != null && !this.opaque && this.port != -2) {
            LinkTool copy = this.absolute();
            copy.setPath((Object)null);
            copy.setQuery((Object)null);
            copy.setFragment((Object)null);
            return copy;
        } else {
            return null;
        }
    }

    public LinkTool directory() {
        LinkTool copy = this.root();
        if(copy == null) {
            copy = this.duplicate();
            copy.setQuery((Object)null);
            copy.setFragment((Object)null);
        }

        copy.setPath(this.getDirectory());
        return copy;
    }

    public boolean isRelative() {
        return this.forceRelative || this.scheme == null;
    }

    public LinkTool relative() {
        LinkTool copy = this.duplicate();
        copy.setForceRelative(true);
        return copy;
    }

    public LinkTool relative(Object obj) {
        LinkTool copy = this.relative();
        String pth;
        if(obj == null) {
            pth = this.getContextPath();
        } else {
            pth = this.combinePath(this.getContextPath(), String.valueOf(obj));
        }

        copy.setPath(pth);
        return copy;
    }

    public String getContextPath() {
        return this.getDirectory();
    }

    public boolean isAbsolute() {
        return this.scheme != null && !this.forceRelative;
    }

    public LinkTool absolute() {
        LinkTool copy = this.duplicate();
        copy.setForceRelative(false);
        if(copy.getScheme() == null) {
            copy.setScheme("http");
        }

        return copy;
    }

    public LinkTool absolute(Object obj) {
        LinkTool copy = this.absolute();
        String pth;
        if(obj == null) {
            pth = this.getDirectory();
        } else {
            pth = String.valueOf(obj);
            if(pth.startsWith("http")) {
                URI uri = this.toURI(pth);
                if(uri == null) {
                    return null;
                }

                copy.setScheme(uri.getScheme());
                copy.setUserInfo(uri.getUserInfo());
                copy.setHost(uri.getHost());
                copy.setPort(Integer.valueOf(uri.getPort()));
                pth = uri.getPath();
                if(pth.equals("/") || pth.length() == 0) {
                    pth = null;
                }

                copy.setPath(pth);
                if(uri.getQuery() != null) {
                    copy.setQuery(uri.getQuery());
                }

                if(uri.getFragment() != null) {
                    copy.setFragment(uri.getFragment());
                }

                return copy;
            }

            if(!pth.startsWith("/")) {
                pth = this.combinePath(this.getDirectory(), pth);
            }
        }

        copy.setPath(pth);
        return copy;
    }

    public LinkTool uri(Object uri) {
        LinkTool copy = this.duplicate();
        return copy.setFromURI(uri)?copy:null;
    }

    public String getBaseRef() {
        LinkTool copy = this.duplicate();
        copy.setQuery((Object)null);
        copy.setFragment((Object)null);
        return copy.toString();
    }

    public LinkTool query(Object query) {
        LinkTool copy = this.duplicate();
        copy.setQuery(query);
        return copy;
    }

    public String getQuery() {
        return this.toQuery(this.query);
    }

    public LinkTool param(Object key, Object value) {
        LinkTool copy = this.duplicate(true);
        copy.setParam(key, value, this.appendParams);
        return copy;
    }

    public LinkTool append(Object key, Object value) {
        LinkTool copy = this.duplicate(true);
        copy.setParam(key, value, true);
        return copy;
    }

    public LinkTool set(Object key, Object value) {
        LinkTool copy = this.duplicate(true);
        copy.setParam(key, value, false);
        return copy;
    }

    public LinkTool remove(Object key) {
        LinkTool copy = this.duplicate(true);
        copy.removeParam(key);
        return copy;
    }

    public LinkTool params(Object parameters) {
        if(parameters == null) {
            return this;
        } else if(parameters instanceof Boolean) {
            Boolean copy2 = Boolean.valueOf(((Boolean)parameters).booleanValue());
            LinkTool copy1 = this.duplicate(true);
            copy1.handleParamsBoolean(copy2.booleanValue());
            return copy1;
        } else if(parameters instanceof Map && ((Map)parameters).isEmpty()) {
            return this.duplicate(false);
        } else {
            LinkTool copy = this.duplicate(this.appendParams);
            copy.setParams(parameters, this.appendParams);
            return copy;
        }
    }

    public Map getParams() {
        return this.query != null && !this.query.isEmpty()?this.query:null;
    }

    public LinkTool anchor(Object anchor) {
        LinkTool copy = this.duplicate();
        copy.setFragment(anchor);
        return copy;
    }

    public String getAnchor() {
        return this.fragment;
    }

    public LinkTool getSelf() {
        return this.self;
    }

    public String toString() {
        URI uri = this.createURI();
        return uri == null?null:(this.query != null?this.decodeQueryPercents(uri.toString()):uri.toString());
    }

    protected String decodeQueryPercents(String url) {
        StringBuilder out = new StringBuilder(url.length());
        boolean inQuery = false;
        boolean havePercent = false;
        boolean haveTwo = false;

        for(int i = 0; i < url.length(); ++i) {
            char c = url.charAt(i);
            if(inQuery) {
                if(havePercent) {
                    if(haveTwo) {
                        out.append('%');
                        if(c != 53) {
                            out.append('2').append(c);
                        }

                        haveTwo = false;
                        havePercent = false;
                    } else if(c == 50) {
                        haveTwo = true;
                    } else {
                        out.append('%').append(c);
                        havePercent = false;
                    }
                } else if(c == 37) {
                    havePercent = true;
                } else {
                    out.append(c);
                }

                if(c == 35) {
                    inQuery = false;
                }
            } else {
                out.append(c);
                if(c == 63) {
                    inQuery = true;
                }
            }
        }

        if(havePercent) {
            out.append('%');
            if(haveTwo) {
                out.append('2');
            }
        }

        return out.toString();
    }

    public boolean equals(Object obj) {
        if(obj != null && obj instanceof LinkTool) {
            String that = obj.toString();
            return that == null && this.toString() == null?true:that.equals(this.toString());
        } else {
            return false;
        }
    }

    public int hashCode() {
        String hashme = this.toString();
        return hashme == null?-1:hashme.hashCode();
    }

    public String encode(Object obj) {
        if(obj == null) {
            return null;
        } else {
            try {
                return URLEncoder.encode(String.valueOf(obj), this.charset);
            } catch (UnsupportedEncodingException var3) {
                this.debug("Character encoding \'%s\' is unsupported", var3, new Object[]{this.charset});
                return null;
            }
        }
    }

    public String decode(Object obj) {
        if(obj == null) {
            return null;
        } else {
            try {
                return URLDecoder.decode(String.valueOf(obj), this.charset);
            } catch (UnsupportedEncodingException var3) {
                this.debug("Character encoding \'%s\' is unsupported", var3, new Object[]{this.charset});
                return null;
            }
        }
    }
}