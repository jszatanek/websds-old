package com.volvo.tech.websds;
import java.sql.*;
import javax.sql.*;

public class Language extends WebsdsApplication {
    private String description;
    private String name;
    private String id;

    public Language() {
    }

    public String insertLanguage(String pName, 
                               String pDescription) throws Exception {


        String wId;
        try{
                String wstrSql = " SELECT LANGUAGE_ID from WS_LANGUAGE where NAME = ? ;";
      
                createPreparedStatement(wstrSql);
                getPreparedStatement().setString(1, pName);

                execQuery();
                if (getNextRow()) {
                    wId = getCurrCol("LANGUAGE_ID");
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
            " INSERT INTO WS_LANGUAGE (NAME, DESCRIPTION) " + " VALUES (?,?)";
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

    public boolean updateLanguage(String pId, 
                                String pDescription) throws Exception {
        String wstrSql = 
            "UPDATE WS_LANGUAGE SET DESCRIPTION=? WHERE LANGUAGE_ID = ?";
        try {

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pDescription);
            getPreparedStatement().setString(2, pId);

            execDML();
            return true;
        } catch (Exception e) {
            rollbackConnection();
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public boolean deleteLanguage(String pId) throws Exception {

/* Check if there are documents with the language reference before delete */
        try{
                String wstrSql = " SELECT LANGUAGE_ID from WS_DOCUMENT where LANGUAGE_ID = ? ;";
      
                createPreparedStatement(wstrSql);
                getPreparedStatement().setString(1, pId);

                execQuery();
                if (getNextRow()) {
                    return false;
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
        
        try {
            String wstrSql = "DELETE from WS_LANGUAGE WHERE LANGUAGE_ID = ?";
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

    public boolean getLanguage(String pId) throws Exception {
        try {
            String wstrSql = " select * from WS_LANGUAGE where language_id = ?";

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pId);

            execQuery();
            getNextRow();

            id = getCurrCol("LANGUAGE_ID");
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
            String wstrSql = " select * from WS_LANGUAGE ORDER BY NAME";
            createPreparedStatement(wstrSql);
            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public boolean getAll(String wName) throws Exception {
        try {
            String wstrSql = " select * from WS_LANGUAGE where NAME like ?";
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
