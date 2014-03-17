package com.volvo.tech.websds;
import java.sql.*;
import javax.sql.*;

public class Type extends Language {
    private String description;
    private String name;
    private String id;

    public Type() {
    }

    public String insertType(String pName, 
                               String pDescription) throws Exception {

       String wId;
        try{
                String wstrSql = " SELECT TYPE_ID from WS_TYPE where NAME = ?;";
      
                createPreparedStatement(wstrSql);
                getPreparedStatement().setString(1, pName);

                execQuery();
                if (getNextRow()) {
                    wId = getCurrCol("TYPE_ID");
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
            " INSERT INTO WS_TYPE (NAME, DESCRIPTION) " + " VALUES (?,?)";
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

    public boolean updateType(String pId, String pName, 
                                String pDescription) throws Exception {
        String wstrSql = 
            "UPDATE WS_TYPE SET NAME=?, DESCRIPTION=? WHERE TYPE_ID = ?";
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

    public boolean deleteType(String pId) throws Exception {

        
        try {

            String wstrSql = "DELETE from WS_TYPE WHERE TYPE_ID = ?";
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

    public boolean getType(String pId) throws Exception {
        try {
            String wstrSql = " select * from WS_TYPE where company_id = ?";

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pId);

            execQuery();
            getNextRow();

            id = getCurrCol("TYPE_ID");
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
            String wstrSql = " select * from WS_TYPE ORDER BY NAME";
            createPreparedStatement(wstrSql);
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
