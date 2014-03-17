package com.volvo.tech.websds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import javax.sql.*;

import javax.naming.InitialContext;
import javax.naming.Context;

import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.directory.SearchControls;
import javax.naming.directory.Attribute;

import java.util.Hashtable;
import javax.naming.NamingEnumeration;

import javax.servlet.http.HttpServletRequest;

  
public class WebsdsApplication {

  private PreparedStatement bStmt = null;
  private java.sql.Connection bConn;
  private ResultSet bResultSet = null;
  private int biRowsAffected;
  private String bstrDateFormat = "YYYY-MM-DD";
  private String bstrErrorMessage="";
  static private String bstrHere_am_i ="";
  
  public WebsdsApplication(){  }

  public String getErrorMessage(){
    return bstrErrorMessage;
  }
  public String getHere_am_i(){
    return bstrHere_am_i;
  }
  public String getApplicationName() throws Exception{
    return getStaticVariable("APPLICATION_NAME");
  }

  public String getDateFormat(){
    return ("%Y-%m-%d");
  }
  public String getDateTimeFormat(){
    return ("yyyy-mm-dd HH24:MI:SS");
  }
  public String getBottomText() throws Exception{
    return getStaticVariable("BOTTOM_TEXT");
  }  
  //<DB-Connection stuff>

  /**
  * creates a db connection with autocommit set to false
  * @since 1.0
  */

  public void makeConnection() throws Exception {

    // Create the initial context. No JNDI properties are to be supplied here
    try{


      Context wInitialContext = new InitialContext();


      DataSource wDataSource = (DataSource)wInitialContext.lookup("java:comp/env/jdbc/websds");
 

//      DataSource wDataSource = (DataSource)wInitialContext.lookup("jdbc/websds");

      // Establishing db connection
      bConn =  wDataSource.getConnection();
      bConn.setAutoCommit(false);
    }
    catch (Exception e){
    setHere_am_i("bin.makeConnection");
      throw new Exception ("Connection error: "+e.getMessage()+"<br>"+bstrHere_am_i);
    }
  }  
  /**
  * Commit db connection
  * @since 1.0
  */
  public void commitConnection() throws Exception{
    try{
      bConn.commit();
    }catch(Exception e){
      setHere_am_i("bin.commitConnection");
      bstrErrorMessage = "Commit error: "+e.getMessage()+"<br>"+bstrHere_am_i;
      try{
        bConn.rollback();
      }catch(Exception eee){
        bstrErrorMessage = bstrErrorMessage + "Unable to rollback: "+eee.getMessage()+"<br>"+bstrHere_am_i;
        throw new Exception(bstrErrorMessage);
      }      
      throw new Exception(bstrErrorMessage);
    }
  }

  /**
  * Rollback db connection. Called anytime a exception is raised.
  * @since 1.0
  */
  public void rollbackConnection() throws Exception{
    
    try{
      bConn.rollback();
    }catch(Exception e){
      setHere_am_i("bin.rollbackConnection");
      bstrErrorMessage = bstrErrorMessage + "Unable to rollback: "+e.getMessage()+"<br>"+bstrHere_am_i;
      throw new Exception(bstrErrorMessage);
    }
  }
  
  public void closePreparedStatement() throws Exception{
    bStmt.close();
  }


  /**
  * Close db connection
  * @since 1.0
  */
  public void closeConnection() throws Exception{
    try{
      bStmt.close();
      bConn.close();
    }catch(Exception e){
      try{
        bConn.close();
      }catch(Exception ee){
        setHere_am_i("bin.closeConnection");      
        throw new Exception("Unable to close connection: "+ee.getMessage()+"<br>"+bstrHere_am_i);
      }
    }
  }
  //<END DB-Connection stuff>


  //Sql-statements

  /**
  * Creates a predefined prepared statement
  * @param pSql The sqlstatement to be executed
  * @since 1.0
  */
  public void createPreparedStatement(String pSql) throws Exception{
    try {
      bStmt = bConn.prepareStatement(pSql
              ,ResultSet.TYPE_SCROLL_INSENSITIVE
              ,ResultSet.CONCUR_READ_ONLY);
    }catch (SQLException e){
      setHere_am_i("bin.createPreparedStatement");
      bstrErrorMessage = "Unable to create statement: "+e.getMessage()+"<br>"+e.getClass()+"<br>"+bstrHere_am_i;
      throw new Exception(bstrErrorMessage);
    }
  }


  //<Select-statements>
  /**
  * Execute a select-query
  * @since 1.0
  */
  public boolean execQuery() throws Exception{
    try{
      setHere_am_i("bin.execQuery");
      bResultSet = bStmt.executeQuery();
      return(bResultSet != null);
    }catch (SQLException ee){
      bstrErrorMessage = "Sql error: "+ee.getMessage()+"<br>"+ee.getClass()+"<br>"+bstrHere_am_i;
      throw new Exception(bstrErrorMessage);
    }
  }
  
  //<END Select-statements>


  //<insert, update delete statements>
  /**
  * Executes a DML statement
  * @since 1.0
  */  
  public boolean execDML () throws Exception{
    try{
      setHere_am_i("bin.execDML");
      biRowsAffected = bStmt.executeUpdate();
      return true;
    }catch (SQLException ee){
      bstrErrorMessage = "<p>Sql error: "+ee.getMessage()+"<br>"+ee.getClass()+"<br>"+bstrHere_am_i;
      try{
        bConn.rollback();
      }catch(Exception eee){
        bstrErrorMessage = bstrErrorMessage + "Unable to rollback: "+eee.getMessage()+"<br>"+eee.getClass()+"<br>"+bstrHere_am_i;
        throw new Exception(bstrErrorMessage);
      }
      throw new Exception(bstrErrorMessage);      
    }
  }
  
  //<END insert, update delete statements>



  public void setResultSet(ResultSet pResultSet){
    bResultSet = pResultSet;
  }
  public void setResultSetFirstRow() throws Exception {
    try {
      bResultSet.beforeFirst();
    }catch (Exception e){
      throw new Exception(e.getMessage());
    }
  }  

  public void setErrorMessage(String pMsg){
    bstrErrorMessage = pMsg;
  }

  public static void setHere_am_i(String pWhere){
    bstrHere_am_i = pWhere;
  }
  //<END Set methods>

  //<Get methods>

  public java.sql.Connection getConnection(){
    return bConn;
  }
  public ResultSet getResultSet(){
    return bResultSet;
  }

  public int getRowsAffected(){
    return(biRowsAffected);
  }

  public PreparedStatement getPreparedStatement(){
    return bStmt;
  }
  
  public boolean getNextRow() throws Exception{
    try {
      return bResultSet.next();
    }catch(SQLException e){
      setHere_am_i("bin.getNextRow");
      bstrErrorMessage = "Resultset row error: "+e.getMessage()+"<br>"+e.getClass()+"<br>"+bstrHere_am_i;
      throw new Exception(bstrErrorMessage);      
    }
  }
  public boolean getNextRow(ResultSet pResultSet) throws Exception{
    try {
      return pResultSet.next();
    }catch(SQLException e){
      setHere_am_i("bin.getNextRow");
      bstrErrorMessage = "Resultset row error: "+e.getMessage();
      throw new Exception(bstrErrorMessage);
    }
  }
  public String getCurrCol(String pstrCol) throws Exception { 
    try{
      return (bResultSet.getString(pstrCol));
    }catch(SQLException e){
      bstrErrorMessage = "Resultset column error: "+e.getMessage()+"<br>"+e.getClass()+"<br>"+bstrHere_am_i;
      throw new Exception (bstrErrorMessage);
    }
  }
  public String getCurrCol(int pIndex) throws Exception {
    try{
      return (bResultSet.getString(pIndex));
    }catch(SQLException e){
      bstrErrorMessage = "Resultset column error: "+e.getMessage()+"<br>"+e.getClass()+"<br>"+bstrHere_am_i;
      throw new Exception (bstrErrorMessage);   
    }
  }
  public String getCurrCol(String pstrCol,ResultSet pResultSet) throws Exception {
    try{
      return pResultSet.getString(pstrCol);
    }catch(NullPointerException ex1){
      return null;
    }catch(SQLException e){
      bstrErrorMessage = "Resultset column error: "+e.getMessage()+"<br>"+e.getClass()+"<br>"+bstrHere_am_i;
      throw new Exception(bstrErrorMessage);
    }
  }

  public ResultSet getResultset(){
    return bResultSet;
  }  

  public int getNofRows() throws Exception{
    try{
      bResultSet.last();
      
      int x = bResultSet.getRow();
      
      bResultSet.beforeFirst();
      
      return x;       
    }catch (Exception ex) {
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }      
  }


 /**
  * Method to check null or "" on a value
  * @param pstrVal value to check
  * @param pstrVal if pstrVal is null return this value
  * @since 1.0
  */
  public String nvl(String pstrVal,String pstrSwap) throws Exception {
    try{  
      String wstrVal = "";

      if(pstrVal == null && pstrSwap == null){  
        return null;
      }
      if(pstrVal == null || pstrVal.length()<=0){
         if(pstrSwap == null || pstrSwap.length()<=0){
            wstrVal = ""; 
         }else{
            wstrVal = pstrSwap;
         }
      }else{
         wstrVal = pstrVal; 
      }
      return wstrVal;
    }catch(Exception e){
      setHere_am_i("bin.nvl");
      throw new Exception(e.getMessage()+"<br>"+e.getClass());
    }
  } 
  
  
  

  /**
  * Get variable from web.xml via jndi interface
  * @since 1.0
  */
   static public String getStaticVariable(String pVariable) throws Exception{
    setHere_am_i("binApplication.getStaticVariable");
    String wVarValue = "";
    String wLookupVariable = "java:comp/env/"+pVariable;
    try {

      Context wInitialContext = new InitialContext();     
      wVarValue = (String)wInitialContext.lookup(wLookupVariable);
      return wVarValue;
    }catch (Exception e){
      throw new Exception(e.getMessage()+"<br>"+e.getClass());
    }
  }  
  


  public boolean validDate(){
// ????????
    return true;
  }  

  public String getUrl(HttpServletRequest r, String page) throws Exception{
    try {
      
      String url = r.getScheme()+"://"+r.getServerName();
      String a = getStaticVariable("SERVER_PORT"); 
      if(a.length() > 0){
        url = url + ":"+a;
      }
      url = url + r.getContextPath()+"/"+page;
      return url;
    } catch (Exception ex) {
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }   
  }

  public String formatText (String pIn, boolean jsSafe){
    String formatText = new String(pIn);
    formatText = formatText.replaceAll("\r\n","<br>");
    if(jsSafe){
      formatText = formatText.replaceAll("\"","\\'");
      formatText = formatText.replaceAll("'","\\\\\'");
    }
    return formatText;
  }

  public String restoreText (String pIn, boolean jsSafe){
    String formatText = new String(pIn);
    formatText = formatText.replaceAll("<br>","\r\n");
    if(jsSafe){
      formatText = formatText.replaceAll("\\\\\"","\'");
      formatText = formatText.replaceAll("\\\\\'","'");
    }
    return formatText;
  }    
  

   public String getRemoteUser(HttpServletRequest r) throws Exception{
    try {
        String username = nvl(r.getRemoteUser(),"Remote user not found");
        return username.toUpperCase();
    } catch (Exception ex) {
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }   
  }  
  
   public String addWhereClauseUpper(String value, String columnName) {
    if(value != "") {
        return (" and upper(" + columnName + ") like upper(?) ");
    }
    else {
        return ("");
    }
  }

    public int specialPreparationOfStatement(String value, 
                                              int i) throws Exception {
        if (value != "") {
            i = i + 1;
            try {

                getPreparedStatement().setString(i, value);
            } catch (Exception e) {
                closeConnection();
                throw new Exception(e.getMessage());
            }
        }
        return (i);
    }



}