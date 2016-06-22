package com.zzpj.model;

public class Link {
    private String hash, fullLink;

    public Link(String hash, String fullLink) {
        this.hash = hash;
        this.fullLink = fullLink;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    @Override
    public String toString() {
        return "Link{" + "hash=" + hash + ", fullLink=" + fullLink + '}';
    }
    
}
