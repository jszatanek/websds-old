package com.volvo.tech.websds;
import java.sql.*;
import javax.sql.*;

public class Article extends Language {
    private String description;
    private String name;
    private String id;

    public Article() {
    }

    public String insertArticle(String pName, 
                               String pDescription) throws Exception {

       String wId;
        try{
                String wstrSql = " SELECT ARTICLE_ID from WS_ARTICLE where NAME = ?;";
      
                createPreparedStatement(wstrSql);
                getPreparedStatement().setString(1, pName);

                execQuery();
                if (getNextRow()) {
                    wId = getCurrCol("ARTICLE_ID");
                    return wId;
                }
      
        }catch (SQLException ee){
               // Record not found!
               if(ee.getErrorCode() == 100) {
 
               } else {
                   rollbackConnection();
                   closeConnection();
          
                   String bstrErrorMessage = "Sql code: "+ee.getErrorCode()+" Sql error: "+ee.getMessage();
                   throw new Exception(bstrErrorMessage);
               }
        }

        String wstrSql = 
            " INSERT INTO WS_ARTICLE (NAME, DESCRIPTION) " + " VALUES (?,?)";
        try {
            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pName);
            getPreparedStatement().setString(2, pDescription);
            execDML();

            wstrSql = " SELECT LAST_INSERT_ID() as INSERT_ID;";
            createPreparedStatement(wstrSql);
            execQuery();
            getNextRow();
            wId = getCurrCol("INSERT_ID");

            return wId;
        } catch (Exception e) {
            rollbackConnection();
            closeConnection();
            throw new Exception(e.getMessage());
        }


    }

    public boolean updateArticle(String pId, String pName, 
                                String pDescription) throws Exception {
        String wstrSql = 
            "UPDATE WS_ARTICLE SET NAME=?, DESCRIPTION=? WHERE ARTICLE_ID = ?";
        try {

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pName);
            getPreparedStatement().setString(2, pDescription);
            getPreparedStatement().setString(3, pId);

            execDML();
            return true;
        } catch (Exception e) {
            rollbackConnection();
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public boolean deleteArticle(String pId) throws Exception {

        
        try {

            String wstrSql = "DELETE from WS_ARTICLE WHERE ARTICLE_ID = ?";
            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pId);
            execDML();
            return true;
        } catch (Exception e) {
            rollbackConnection();
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public boolean getArticle(String pId) throws Exception {
        try {
            String wstrSql = " select * from WS_ARTICLE where company_id = ?";

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pId);

            execQuery();
            getNextRow();

            id = getCurrCol("ARTICLE_ID");
            name = getCurrCol("NAME");
            description = getCurrCol("DESCRIPTION");

            return true;
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public boolean getAll() throws Exception {
        try {
            String wstrSql = " select * from WS_ARTICLE ORDER BY NAME";
            createPreparedStatement(wstrSql);
            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public boolean getAll(String wName) throws Exception {
        try {
            String wstrSql = " select * from WS_ARTICLE where NAME like ?";
            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, wName);
            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public String getName() {
        return (name);
    }

    public String getDescription() {
        return (description);
    }
    
    public String getId() {
        return (id);
    }


}
