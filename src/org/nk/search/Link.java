package org.nk.search;

import java.util.Date;  



public class Link {  
    private String uuid;  
    private String url;   
    private Date lastAccess; //上次访问时间  
    private Link parent; //父节点  
    private String source;  
    private int depth;  
      
    public int getDepth() {  
        return depth;  
    }  
    public void setDepth(int depth) {  
        this.depth = depth;  
    }  
    public String getUuid() {  
        return uuid;  
    }  
    public void setUuid(String uuid) {  
        this.uuid = uuid;  
    }  
    public String getUrl() {  
        return url;  
    }  
    public void setUrl(String url) {  
        this.url = url;  
    }  
    public Date getLastAccess() {  
        return lastAccess;  
    }  
    public void setLastAccess(Date lastAccess) {  
        this.lastAccess = lastAccess;  
    }  
    public Link getParent() {  
        return parent;  
    }  
    public void setParent(Link parent) {  
        this.parent = parent;  
    }  
    public String getSource() {  
        return source;  
    }  
    public void setSource(String source) {  
        this.source = source;  
    }  
}  
