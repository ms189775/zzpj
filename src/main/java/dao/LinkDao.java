package dao;

public interface LinkDao {
    public void setLink(String hash, String fullLink);
    public String findFullLink(String hash);
}