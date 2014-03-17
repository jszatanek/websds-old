package com.volvo.tech.websds;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.MultipartRequest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import java.util.Properties;
import java.sql.Blob; 
import java.io.InputStream;
import java.io.ObjectInputStream;


public class VtecBlob extends WebsdsApplication {

  public VtecBlob()  {  }
  
  private String EMPTYBLOB = "empty_blob()";
  private MultipartRequest multi;
  private String bstrUploadFolder = "";
  
  // Variables to fetch a blob
  private String bstrFileName = null;             // Name of the file being uploaded currently
  private java.sql.Blob blBlob ;
  private long blFileLength = 0;
  private String bstrMimeType; 


          String article;
          String language;
          Blob file;
          String mimeType;

  
  public MultipartRequest  getRequestParameters(){
      return multi;
  }
/**
  *  Returns the value of a request parameter as a String, or null if
  *  the parameter does not exist.The method overrides parent method.
  *  @param <b>name</b> The name of the parameter
  *  @return <b>String </b> The value of the parameter
  *  @since 1.0
  */

  public String getBlobId( String pstrFieldName ) throws Exception {
  }
  
  public void setUpfForDemoTest() throws Exception{
    try {
      String wstrSql;
      wstrSql = "DELETE FROM VDMO_BLOBS";
      PreparedStatement wps;
      int modified;
       wps = getConnection().prepareStatement(wstrSql);
       modified = wps.executeUpdate();

      wstrSql = "DROP SEQUENCE VDMO_BLOBS_SEQ";
      wps = getConnection().prepareStatement(wstrSql);
      modified = wps.executeUpdate();


      wstrSql = "CREATE SEQUENCE VDMO_BLOBS_SEQ INCREMENT BY 1 START WITH 1 MAXVALUE 1.0E28 MINVALUE 1 NOCYCLE  NOCACHE NOORDER";
      wps = getConnection().prepareStatement(wstrSql);
      modified = wps.executeUpdate();

      getConnection().commit();
      
    }catch (Exception ex){
       throw new Exception("streamImage: "+ex.getMessage());
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
  public boolean loadBlobs(javax.servlet.ServletRequest servletRequest
                           , javax.servlet.ServletResponse  servletResponse) throws Exception {
  
  /**
  * The method reads the next line from the request input stream into a byte
  * array and returns a String form of the array.
  * Returns null if it reaches the end of the stream
  * @return <b>String</b> The next line in the stream
  * @throws <b>IOException</b>  - If there was a problem reading the stream 
  */

    String contentType =  servletRequest.getContentType();   
    // Only filter this request if it is multipart 
    // encoding                
    if (contentType.startsWith( "multipart/form-data")){
      try {

        bstrUploadFolder = getJavaTempDir();
        multi = new  MultipartRequest(servletRequest, bstrUploadFolder, 1 * 1024 * 1024 );
        
        Enumeration params =  multi.getParameterNames();
        while (params.hasMoreElements()) {
          String name = (String)params.nextElement();
 
          if (name.equals("pFld3MultiSel")){
            String values[] = multi.getParameterValues("name");
          }else{
            String value = multi.getParameter(name);
          }

        }
        Enumeration files = multi.getFileNames();
        while (files.hasMoreElements()) {
          String name = (String)files.nextElement();
          String filename = multi.getFilesystemName(name);
          String type = multi.getContentType(name);
          if (filename != null){
            String languageId = insertLanguage(filename);
            String articleId = insertArticle(filename);
            
            insertBlob(filename, languageId, articleId, type);
//         System.out.println("File " + name+" "+filename +" " + type);
          }
        }
      return true;
      }catch (IOException e) {
       throw new Exception("loadBlobs: "+e.getMessage());
      }
    } // end if
     return true;
  } // end method loadBlobs()
 

 private boolean insertBlob(String pFilename, String pLanguageId, String pArticleId, String pType) throws Exception {
    
    try {

/* New from here */      
      String wstrSql = "INSERT INTO WS_DOCUMENT (LANGUAGE_ID, ARTICLE_ID, MIME_TYPE, FILE, LAST_UPDATED) VALUES (?,?,?,sysdate)";
      
      /* Output stream */
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(bImage,"jpeg",out);
      byte[] buf = out.toByteArray();

      PreparedStatement wps = getConnection().prepareStatement(wstrSql);
      wps.setString(1,pLanguageId);    
      wps.setString(2,pArticleId);
      wps.setString(3,pType);

      /* Input stream */
      File binaryFile = new File(pFilename);
      FileInputStream inStream = new FileInputStream(binaryFile);
      wps.setBinaryStream(4,inStream,inStream.available());
      wps.executeUpdate();
      commitConnection();      
      
/* until here */      
      /*
      wstrSql = "INSERT INTO WS_DOCUMENT (LANGUAGE_ID, ARTICLE_ID, MIME_TYPE, LAST_UPDATED) VALUES (?,?,?,sysdate)";
      
      PreparedStatement wps = getConnection().prepareStatement(wstrSql);
      wps.setString(1,pLanguageId);    
      wps.setString(2,pArticleId);
      wps.setString(3,pType);

      int modified = wps.executeUpdate();

      wstrSql = " SELECT LAST_INSERT_ID() as INSERT_ID;";
      createPreparedStatement(wstrSql);
      execQuery();
      getNextRow();
      String wId = getCurrCol("INSERT_ID");
      execDML();
      
      wstrSql = "SELECT FILE FROM WS_DOCUMENT WHERE DOCUMENT_ID = ? FOR UPDATE";
      wps = getConnection().prepareStatement(wstrSql);

      wps.setString(1,wId);
      ResultSet wrs = wps.executeQuery();
      java.sql.Blob b = null;
      while (wrs.next()) {
        b = ( java.sql.Blob) wrs.getBlob(1);
      }
      wrs.close();

// Open streams for the output file and the blob
      File binaryFile = new File(bstrUploadFolder + pFilename);
      FileInputStream inStream = new FileInputStream(binaryFile);
      OutputStream outStream = b.getBinaryOutputStream();

// Get the optimum buffer size and use this to create the read/write buffer
      int size =  b.getBufferSize();
      byte[] buffer = new byte[size];
      int length = -1;
      binaryFile.length();
// Transfer the data
      while ((length = inStream.read(buffer)) != -1) {
        outStream.write(buffer, 0, length);
        outStream.flush();
      }

// Close everything down
      inStream.close();
      outStream.close(); // Updates blob.

      wlFileSize = binaryFile.length();
      binaryFile.delete();


//set the file size column in BIN_BLOBS
      wstrSql = " UPDATE WS_DOCUMENT "+
                " SET DOC_SIZE = ? "+
                " WHERE BLOB_ID = ? ";
            
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setLong(1,wlFileSize);
      super.getPreparedStatement().setString(2,wId);
      
      super.execDML();      

      super.commitConnection();      
      */
      
      return true;
    }catch (Exception sqle){ 
      try{
                getConnection().rollback();
                //setErrorMessage( "INSERT Image error ): " + sqle.getMessage());
                throw new IOException("insertImage: INSERT Image error : " + sqle.getMessage());


            }catch(Exception ex){
                //setErrorMessage( "Unable to rollback:  (INSERT Image ) error: "+ex.getMessage());
                throw new IOException("insertImage: Unable to rollback:  error: "+ex.getMessage());
            }
        }
  }

   public boolean getBlob (String pBlobId) throws Exception{

    String wstrSql = " SELECT LANGUAGE, ARTICLE, FILE, MIME_TYPE " +
                     " FROM WS_DOCUMENT " +
                     " WHERE BLOB_ID = " + pBlobId;

    try {

      super.createPreparedStatement(wstrSql);
      super.execQuery();
      super.getPreparedStatement().setMaxFieldSize(1047552);
      java.sql.ResultSet wRes = super.getResultSet();


      if( super.getNextRow(wRes) ) {
          article = wRes.getString("ARTICLE_ID");
          language = wRes.getString("LANGUAGE_ID");
          file = wRes.getBlob("FILE");
          mimeType = nvl(wRes.getString("MIME_TYPE"),"");

/* New from here */
          // setup the streams
          InputStream input = file.getBinaryStream();
          ByteArrayOutputStream output = new ByteArrayOutputStream();
          
                // set read buffer size
          byte[] rb = new byte[1024];
          int ch = 0;
          // process blob
          while ((ch=input.read(rb)) != -1){
            output.write(rb, 0, ch);
          }
          byte[] b = output.toByteArray();
          input.close();
          output.close();
          // load final buffer
          dPhoto = new ImageIcon(b);

          wRes.close();
      }
      
      return(true);      

    }catch(Exception e){
      super.closeConnection();
      throw new Exception("getDocument: "+e.getMessage());
    }
  }  
  /**
  * Returns mime type of fetched image
  * @since 1.0
  */
  public String getMimeType (){ return (bstrMimeType); }
   /**
  * Returns file length of fetched image
  * @since 1.0
  */
  public long getFileLength (){
     return (blFileLength);
  }  
  
  /**
  *  This method returns the filename, which has been set in method setHeaders
  * 
  * @since 1.0
  */
  public String getFileName() throws IOException {
    if (bstrFileName==null ) return null;
    if (bstrFileName.length()==0 ) return null;
    return bstrFileName;
  }
  
    /**
  * This method streams the private blob variable to the out object.
  * @param <b>out</b> - out object to which the blob shall be streamed
  * @since 1.0
  */
  public void streamBlob(ServletOutputStream out) throws Exception{
 
    try {
      InputStream blobStream = blBlob.getBinaryStream();

      // Open a stream to read the Blob data
      // Read from the Blob data input stream, and write to the file output
      int     bbuffSize = 100 ;
      byte[]  bbuff = new byte[bbuffSize] ;
      int     bytes_read = 0 ;

      bytes_read = blobStream.read(bbuff, 0, bbuffSize) ; 
      while ( bytes_read > 0 ) {
        out.write(bbuff, 0, bytes_read) ;
        bytes_read = blobStream.read(bbuff, 0, bbuffSize); 
      }
      // Flush and close the streams
      out.flush();
      out.close();
      blobStream.close();
      
    } catch( Exception ex ) {
      throw new Exception("streamImage: "+ex.getMessage());
    }
  }
}