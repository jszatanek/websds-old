package com.volvo.tech.websds;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.MultipartRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLConnection;
import java.sql.*;
import javax.sql.*;

import java.util.ArrayList;
import java.util.Iterator;

//io imports
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;
import java.util.*;

public class Document extends Article{

  public Document(){  }

  private BufferedOutputStream buffOut = null;

  private String folderName;
  private String objectType;
  
  private String action="";
  
  private java.sql.Blob blob ;
  private String bstrMimeType;
  
  private String filePath, fileName, contentType, fileSize;
  private FileInputStream inStream;
  private String pArticle, pLanguage;
  private String pArticleId, pLanguageId;
  private long fileLength = 0;
  private int numberOfFiles = 1;
  private static int MAX_UPLOAD_SIZE = 16777216;
  
  private File fileDocument = null;
  private int     bbuffSize = 100 ;
  private byte[]  bbuff = new byte[bbuffSize] ;
  private int     bytes_read = 0 ;

  
  byte [] fileContent = new byte[numberOfFiles];
  private Hashtable m_Parameters = new Hashtable();
  private ArrayList bFolders = new ArrayList();

  private int x = 0;
  private int y = 0;
  
    /**
  *  Delete a document
  *  
  * @parmeter pDocId - the document to be deleted
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */   
  public boolean deleteDocument(String pDocId) throws Exception{
    try{

      //documents
     String sql =  " delete from WS_DOCUMENT "+
                        " where document_id ="+pDocId;      
      super.createPreparedStatement(sql);
      super.execDML();

      //document blobs
       sql =  " delete from WS_DOCUMENT_BLOB "+
                    " where document_id ="+pDocId;
      super.createPreparedStatement(sql);
      super.execDML();      
      
      return true;
      
    }catch (Exception e){
      super.rollbackConnection();
      closeConnection();    
      throw new Exception(e.getMessage());
    }  
  }     


  public boolean deleteDocument(String[] pLanguageId, String pArticle) throws Exception{
      try{
        String wstrSql = "";
        String wId = "";
        boolean wStatus = false;
        
        for(int i= 0;i<pLanguageId.length;i++) {
          if(pLanguageId[0].equals("")) {
            wstrSql = " select document_id from WS_DOCUMENT where  "+
                   " article_id in (select article_id from WS_ARTICLE where name = ?)";
      
                super.createPreparedStatement(wstrSql);
                super.getPreparedStatement().setString(1,pArticle);
                execQuery();
                wStatus = false;
                while (getNextRow()) {
                       wId = getCurrCol("DOCUMENT_ID");
                       deleteDocument(wId);                    
                       wStatus = true;
                }
                return(wStatus);
          }
        }
        for(int i= 0;i<pLanguageId.length;i++) {
            wstrSql = " select document_id from WS_DOCUMENT where language_id = ? "+
                   " and article_id in (select article_id from WS_ARTICLE where name = ?)";
      
            super.createPreparedStatement(wstrSql);
            super.getPreparedStatement().setString(1,pLanguageId[i]);
            super.getPreparedStatement().setString(2,pArticle);
            execQuery();
            if (getNextRow()) {
                wId = getCurrCol("DOCUMENT_ID");
                deleteDocument(wId);
                wStatus = true;
              }
       }
     return(wStatus);               
    } catch (Exception e){
      super.rollbackConnection();
      closeConnection();    
      throw new Exception(e.getMessage());
    }  
  }     


  /**
  *  This method handles a multipart form when uploading files. It assignes byte 
  *  array fileContent the documentdata and the other parameters sent is available by getParameter()
  *  
  * @parmeter request - reference to the entire request object
  * @parmeter maxPostSize - sets the limit for how large a document can be
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */ 
  private void organizeInput(HttpServletRequest request,
                          int maxPostSize) throws Exception {
    try{
    MultipartParser parser = new MultipartParser(request, maxPostSize);
    Part part;
   
    while ((part = parser.readNextPart()) != null) {
      String name =new String();
      name = part.getName();
      if (part.isParam()) {

        ParamPart paramPart = (ParamPart) part;
        String value = new String();
        value = paramPart.getStringValue();
        //Vector existingValues = (Vector)m_Parameters.get(name);
        //if (existingValues == null) {
        //  existingValues = new Vector();
          m_Parameters.put(name, value);
        //}
        //existingValues.addElement(value);
      }
      else if (part.isFile()) {

        FilePart filePart = (FilePart) part;
        fileName = filePart.getFileName();
        filePath = filePart.getFilePath();
        contentType = filePart.getContentType();
        if (fileName != null) {

          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          filePart.writeTo(byteArrayOutputStream);
          byte [] myByte = byteArrayOutputStream.toByteArray();
          fileContent = myByte;
          fileSize = "" + fileContent.length;
          byteArrayOutputStream.close();
          
          
          //vOut.write(fileContent);          
          //m_Files.put(fileName,fileContent);
        }
      }
    }
    }catch(Exception e){
      throw new Exception(e.getMessage());
    }
  }

  /**
  *  Get parameters from multipart form upload
  *  
  * @parmeter pVar - the name of the form variable
  * @since 1.1
  */     
  public String getParameter(String pVar){
    return ((String)m_Parameters.get(pVar));
  }

  /**
  *  This method inserts the blob into the database table.
  * 
  * @throws <b>Exception</b>  - If any error
  * @since 1.0
  */
  protected boolean insertDocument(String pLanguageId, String pArticleId) throws IOException {
  
    String sql;
    try {
      
      String documentId = getDocumentId();

      // Insert the blob
      insertBlob(documentId);

      // Insert document information
      sql = " insert into WS_DOCUMENT(document_id, language_id, article_id, last_updated, file_size) "+
           " values(?,?,?, now(),?)";
      super.createPreparedStatement(sql);
      super.getPreparedStatement().setString(1,documentId);
      super.getPreparedStatement().setString(2,pLanguageId);
      super.getPreparedStatement().setString(3,pArticleId);
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

      pLanguage = super.nvl(getParameter("pLanguage"),"");
      pArticle = super.nvl(getParameter("pArticle"),"");

      String languageId = insertLanguage(pLanguage, "");
      String articleId = insertArticle(pArticle, "");

      String wId;
      String wstrSql = " SELECT DOCUMENT_ID from WS_DOCUMENT where LANGUAGE_ID = ? AND ARTICLE_ID = ?;";
      
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1, languageId);
      super.getPreparedStatement().setString(2, articleId);

      execQuery();
      if (getNextRow()) {
            wId = getCurrCol("DOCUMENT_ID");

            // Update document
            sql = " update WS_DOCUMENT set last_updated=now(), file_size=? where document_id=? ";
            super.createPreparedStatement(sql);
            super.getPreparedStatement().setString(1,getFileSize());
            super.getPreparedStatement().setString(2,wId);
            super.execDML();
            
            updateBlob(wId);
            super.commitConnection();
            return true;
      }
      else  {
           insertDocument(languageId, articleId);
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

  /**
  * This method is the main method for upload documents
  * @param <b>req</b> request input stream
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */

  public boolean uploadDocument(HttpServletRequest req) throws Exception {

    try{
      organizeInput(req,MAX_UPLOAD_SIZE);
      action = getParameter("pAction");
      if (action.equals("upload")){
        this.updateDocument();
      }else{
        return false;
      }

      return true;
    }catch (Exception e) {
      rollbackConnection();
      closeConnection();
      throw new Exception(e.getMessage());
    }
  }
  

  /**
  * Get a specific document
  * @param <b>pDocId</b> document id
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */  
  public boolean getDocumentOld (String pDocId) throws Exception{

    String sql = " select BLOB_CONTENT, MIME_TYPE, PHYSICAL_FILE_NAME " +
                 " From   WS_DOCUMENT_BLOB " +
                 " WHERE DOCUMENT_ID = " + pDocId;

    try {

      super.createPreparedStatement(sql);
      super.execQuery();
      super.getPreparedStatement().setMaxFieldSize(1047552);
      java.sql.ResultSet wRes = super.getResultSet();


      if( super.getNextRow(wRes) ) {
        fileName = wRes.getString("PHYSICAL_FILE_NAME");
        blob = wRes.getBlob("BLOB_CONTENT");
        fileLength = blob.length();
        bstrMimeType = wRes.getString("MIME_TYPE");
    
        fileDocument = File.createTempFile(fileName,".tmp");

        FileOutputStream dest = new FileOutputStream(fileDocument);
        buffOut = new BufferedOutputStream(dest);
        
        InputStream blobStream = blob.getBinaryStream();
        bytes_read = blobStream.read(bbuff, 0, bbuffSize); 
        while ( bytes_read > 0 ) {
          buffOut.write(bbuff, 0, bytes_read) ;
          bytes_read = blobStream.read(bbuff, 0, bbuffSize); 
        }
        blobStream.close();          
        buffOut.close();
        dest.close();
      }
      wRes.close();
      
      return(true);      

    }catch(Exception e){
      super.closeConnection();
      throw new Exception(e.getMessage());
    }
  }
  
  /**
  * Get a specific document
  * @param <b>pDocId</b> document id
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */  
  public boolean getDocument (String pDocId) throws Exception{

    String sql = " select BLOB_CONTENT, MIME_TYPE, PHYSICAL_FILE_NAME " +
                 " From   WS_DOCUMENT_BLOB " +
                 " WHERE DOCUMENT_ID = " + pDocId;

    try {

      super.createPreparedStatement(sql);
      super.execQuery();
      super.getPreparedStatement().setMaxFieldSize(1047552);
      java.sql.ResultSet wRes = super.getResultSet();

      if( super.getNextRow(wRes) ) {
        fileName = wRes.getString("PHYSICAL_FILE_NAME");
        blob = wRes.getBlob("BLOB_CONTENT");
        fileLength = blob.length();
        bstrMimeType = wRes.getString("MIME_TYPE");
   
      }
      wRes.close();
      
      return(true);      
    }catch(Exception e){
      super.closeConnection();
      throw new Exception(e.getMessage());
    }
  }
  
  
  /**
  * Returns mime type of fetched image
  * @since 1.1
  */
  public String getMimeType (){
      return (bstrMimeType);
  }
  /**
  * Returns file name of fetched image
  * @since 1.1
  */
  public String getFileName (){
      return (fileName);
  }

  /**
  * Returns file name of fetched image
  * @since 1.1
  */
  public long getFileLength (){
      return (fileLength);
  }
  /**
  * Returns file length of fetched image
  * @since 1.1
  */
  public String getFileSize (){
      return (fileSize);
  }
  /**
  * This method streams the private blob image variable to the out object.
  * With tmpfile, in order to support folder download, from version 1.1
  * @param <b>out</b> - out object to which the blob shall be streamed
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */
  public void streamDocumentOld(ServletOutputStream wout) throws Exception{
 
    try {

      FileInputStream in = new FileInputStream(fileDocument);
      
      bytes_read = in.read(bbuff, 0, bbuffSize); 
      while ( bytes_read > 0 ) {
        wout.write(bbuff, 0, bytes_read);
        bytes_read = in.read(bbuff, 0, bbuffSize); 
      }
      // Flush and close the streams
      wout.flush();
      wout.close();
      in.close();
      
      fileDocument.delete();
      
    } catch( Exception ex ) {
      throw new Exception(ex.getMessage());
    }
  }  
  
    /**
  * This method streams the private blob image variable to the out object.
  * With tmpfile, in order to support folder download, from version 1.1
  * @param <b>out</b> - out object to which the blob shall be streamed
  * @throws <b>Exception</b>  - If any error
  * @since 1.1
  */
  public void streamDocument(ServletOutputStream wout) throws Exception{
 
    try {

      InputStream blobStream = blob.getBinaryStream();
      bytes_read = blobStream.read(bbuff, 0, bbuffSize); 
      while ( bytes_read > 0 ) {
          wout.write(bbuff, 0, bytes_read);
          bytes_read = blobStream.read(bbuff, 0, bbuffSize); 
     }

     blobStream.close();
        
     // Flush and close the streams
      wout.flush();
      wout.close();
    } catch( Exception ex ) {
      throw new Exception(ex.getMessage());
    }
  } 
  
  public boolean getAll(String pLanguageId, String pArticle) throws Exception {
        try {
        if (!(pLanguageId.equals("null"))) {
            String wstrSql = 
                " select d.DOCUMENT_ID, d.FILE_SIZE, a.NAME as ARTICLE_NAME, " +
                " DATE_FORMAT(d.LAST_UPDATED, '" + getDateFormat() +  "') as LAST_UPDATED, l.NAME as LANGUAGE_NAME " +
                " from WS_DOCUMENT d, WS_LANGUAGE l, WS_ARTICLE a "+
                " where d.language_id = l.language_id and d.article_id = a.article_id " +
                " and l.language_id = ? " +
                addWhereClauseUpper(pArticle, "a.NAME")  +
                "order by a.name ";
            
            //System.out.println("SQL::" + wstrSql);
            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pLanguageId);

            specialPreparationOfStatement(pArticle, 1);

            return (execQuery());
        } else {
            String wstrSql = 
                " select d.DOCUMENT_ID, d.FILE_SIZE, a.NAME as ARTICLE_NAME, " +
                " DATE_FORMAT(d.LAST_UPDATED, '" + getDateFormat() +  "') as LAST_UPDATED, l.NAME as LANGUAGE_NAME " +
                " from WS_DOCUMENT d, WS_LANGUAGE l, WS_ARTICLE a "+
                " where d.language_id = l.language_id and d.article_id = a.article_id " +
                addWhereClauseUpper(pArticle, "a.NAME")  +
                "order by a.name, l.name";
            createPreparedStatement(wstrSql);
            getPreparedStatement().setString(1, pArticle);
  //          specialPreparationOfStatement(pArticle, 1);
            return (execQuery());
        }
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }

    public void updateBlob(String documentId) throws Exception {
        try {
  
             // Update blob
            String sql = "UPDATE WS_DOCUMENT_BLOB set MIME_TYPE=?, CONTENT_TYPE=?, PHYSICAL_FILE_NAME=?, BLOB_CONTENT=? where DOCUMENT_ID=?";
      
            java.sql.PreparedStatement wps = getConnection().prepareStatement(sql);
            wps.setString(1,contentType);
            wps.setString(2,"BLOB");
            wps.setString(3,fileName);
            wps.setBytes(4,fileContent);
            wps.setString(5,documentId);
            int modified = wps.executeUpdate();
            wps.close();
 
        }catch(Exception e){
            super.rollbackConnection();
            throw new Exception(e.getMessage());
        }
    }


    public void insertBlob(String documentId) throws Exception {
        try {
            String sql = "INSERT INTO WS_DOCUMENT_BLOB (DOCUMENT_ID,  MIME_TYPE, CONTENT_TYPE, PHYSICAL_FILE_NAME, BLOB_CONTENT) VALUES (?,?,?,?,?)";
      
            java.sql.PreparedStatement wps = getConnection().prepareStatement(sql);
            wps.setString(1,documentId);
            wps.setString(2,contentType);
            wps.setString(3,"BLOB");
            wps.setString(4,fileName);
            wps.setBytes(5,fileContent);
            int modified = wps.executeUpdate();
            wps.close();
        }catch(Exception e){
            super.rollbackConnection();
            throw new Exception(e.getMessage());
        }
    }
    
    protected String getDocumentId() throws Exception {
    
        try {
            String sql = "UPDATE WS_SEQUENCE SET DOCUMENT_ID=LAST_INSERT_ID(DOCUMENT_ID+1)";
            super.createPreparedStatement(sql);
            super.execDML();
      
            sql = "SELECT LAST_INSERT_ID()";
            super.createPreparedStatement(sql);
            super.execQuery();
            super.getNextRow();
            return (super.getCurrCol(1));

        }catch (Exception e) { 
            super.rollbackConnection();
            throw new Exception(e.getMessage());
        }
    }
    
  public boolean getLatestUpdates() throws Exception {
        try {
            String wstrSql = 
                " select d.DOCUMENT_ID, d.FILE_SIZE, l.NAME as LANGUAGE_NAME, a.NAME as ARTICLE_NAME, " +
                " DATE_FORMAT(d.LAST_UPDATED, '" + getDateFormat() +  "') as LAST_UPDATED " +
                " from WS_DOCUMENT d, WS_LANGUAGE l, WS_ARTICLE a "+
                " where d.language_id = l.language_id and d.article_id = a.article_id " +
                "order by d.LAST_UPDATED desc";

            createPreparedStatement(wstrSql);
            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }    
    
    private String getJavaTempDir() throws Exception {
    
     Properties props = System.getProperties();  
     String wstrDir = props.getProperty("java.io.tmpdir");
     String wstrFileSep = props.getProperty("file.separator");

     if (wstrDir.length() > 0 ) {
       
       if (wstrDir.lastIndexOf(wstrFileSep) + 1 < wstrDir.length()) {
          wstrDir = wstrDir + wstrFileSep;
       }
       return wstrDir;
     }else {
        throw new Exception("Can't find temp dir");
     }
  }

  public boolean getDocumentList() throws Exception {
        try {
            String wstrSql = 
                " SELECT WS_LANGUAGE.name, WS_LANGUAGE.language_id, count(*) as COUNT_DOCS FROM WS_DOCUMENT, " +
                " WS_LANGUAGE where WS_DOCUMENT.LANGUAGE_ID = WS_LANGUAGE.LANGUAGE_ID " +
                " group BY WS_DOCUMENT.LANGUAGE_ID ";
                

            createPreparedStatement(wstrSql);
            return (execQuery());
        } catch (Exception e) {
            closeConnection();
            throw new Exception(e.getMessage());
        }
    }    


}
