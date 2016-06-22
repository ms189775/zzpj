package dao;

import java.sql.Types;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
 
public class LinkDaoImpl extends JdbcDaoSupport implements LinkDao{

    @Override
    public String findFullLink(String hash) {
        String query = "select name from links where emp_id=?";
        Object[] inputs = new Object[] {hash};
        String fullLink = getJdbcTemplate().queryForObject(query, inputs, String.class);
        return fullLink;
    }

    @Override
    public void setLink(String hash, String fullLink) {
        getJdbcTemplate().update("INSERT INTO SomeTable(hash, full_link) VALUES(?,?)", hash, fullLink);
    }
    
}