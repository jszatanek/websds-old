package com.volvo.tech.websds;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;
import javax.sql.*;
import java.io.IOException;


public class News extends Document{

  public News(){  }

  /**
  *  This method inserts the blob into the database table.
  * 
  * @throws <b>Exception</b>  - If any error
  * @since 1.0
  */
  protected boolean insertDocument(String pName, String pType) throws IOException {
  
    String sql;
    try {
      
      String documentId = getDocumentId();

      // Insert the blob
      insertBlob(documentId);
      sql = " insert into WS_NEWS(document_id, name, type_id, last_updated, file_size) "+
           " values(?,?,?, now(),?)";
           
      // Update documentinformation           
      super.createPreparedStatement(sql);
      super.getPreparedStatement().setString(1,documentId);
      super.getPreparedStatement().setString(2,pName);
      super.getPreparedStatement().setString(3,pType);
      super.getPreparedStatement().setString(4,getFileSize());
      super.execDML();

      super.commitConnection();
      return true;
    }catch (Exception e) { 
      try{
        super.rollbackConnection();
        throw new Exception(e.getMessage());
      }catch(Exception ex){
        throw new IOException(ex.getMessage());
      }
    }
  }


/**
  *  This method inserts the blob into the database table.
  * 
  * @throws <b>Exception</b>  - If any error
  * @since 1.0
  */
  protected boolean updateDocument() throws IOException {
  
    String sql;
    try {

      String pName = super.nvl(getParameter("pName"),"");
      String pType = super.nvl(getParameter("pType"),"");

      String wId;
      String wstrSql = " SELECT DOCUMENT_ID from WS_NEWS where NAME = ? and TYPE_ID = ?;";
      
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1, pName);
      super.getPreparedStatement().setString(2, pType);

      execQuery();
      if (getNextRow()) {
            wId = getCurrCol("DOCUMENT_ID");

            // Update document
            sql = " update WS_NEWS set last_updated=now(), file_size=? where document_id=? ";
            super.createPreparedStatement(sql);
            super.getPreparedStatement().setString(1,wId);
            super.getPreparedStatement().setString(2,super.getFileSize());
            super.execDML();
            
            updateBlob(wId);
            super.commitConnection();
            return true;
      }
      else  {
           insertDocument(pName, pType);
           return true;
      }
    }catch (Exception e) { 
      try{
        super.rollbackConnection();
        throw new Exception(e.getMessage());
      }catch(Exception ex){
        throw new IOException(ex.getMessage());
      }
    }
  }

   public boolean getAll(String pType, String pName) throws Exception {
        try {
            String wstrSql = 
                " select n.DOCUMENT_ID, n.FILE_SIZE, n.NAME as NAME, " +
                " DATE_FORMAT(n.LAST_UPDATED, '" + getDateFormat() +  "') as LAST_UPDATED " +
                " from WS_NEWS n , WS_TYPE t where n.TYPE_ID = t.TYPE_ID and " +
                " t.name = ? and n.NAME = ?" +
                "order by n.LAST_UPDATED desc";

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pType);
            getPreparedStatement().setString(2, pName);

            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }
 
 
   public boolean getAll(String pType) throws Exception {
        try {
            String wstrSql = 
                " select n.DOCUMENT_ID, n.FILE_SIZE, n.NAME as NAME, " +
                " DATE_FORMAT(n.LAST_UPDATED, '" + getDateFormat() +  "') as LAST_UPDATED " +
                " from WS_NEWS n , WS_TYPE t where n.TYPE_ID = t.TYPE_ID and " +
                " t.name = ? " +
                "order by n.LAST_UPDATED desc";

            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pType);

            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }
 
   public boolean getAll() throws Exception {
        try {
            String wstrSql = 
                " select n.DOCUMENT_ID, t.NAME as TYPE, n.FILE_SIZE, n.NAME as NAME, " +
                " DATE_FORMAT(n.LAST_UPDATED, '" + getDateFormat() +  "') as LAST_UPDATED " +
                " from WS_NEWS n, WS_TYPE t where n.TYPE_ID = t.TYPE_ID " +
                "order by n.LAST_UPDATED desc";

            createPreparedStatement(wstrSql);


            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }
    
  public boolean uploadDocument(HttpServletRequest req) throws Exception {
    return (super.uploadDocument(req));

 }
 
   public boolean deleteDocument(String pDocId) throws Exception{
    try{

      //documents
      String sql =  " delete from WS_NEWS "+
                        " where document_id ="+pDocId;      
      super.createPreparedStatement(sql);
      super.execDML();

      //document blobs
      sql =  " delete from WS_DOCUMENT_BLOB "+
                    " where document_id ="+pDocId;
      super.createPreparedStatement(sql);
      super.execDML();      
      
      super.commitConnection();
      
      return true;
      
    }catch (Exception e){
      super.rollbackConnection();
      closeConnection();    
      throw new Exception(e.getMessage());
    }  
  }     
 
}