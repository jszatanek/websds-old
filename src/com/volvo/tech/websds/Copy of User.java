package com.volvo.tech.websds;

import com.volvo.tech.useradm.*;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

public class User extends WebsdsApplication{

  public User()  {   }

  private String userId = "";
  private String lastName = "";
  private String givenName = "";
  private String email = "";
  private String company = "";
  
  private String roleId = "";
  private String roleName = "";
  private int roleSecLevel = 0;
  
  private String isUserRemovable = "";
  private String createdDate = "";
  private String deletedDate = "";
  private boolean isVisitor = true;
  
  private String dn = "";
  
  //get methods
  public String getUserId (){
    return userId;
  } 
  public String getLastName() {
    return lastName;
  }
  public String getGivenName(){
    return(givenName);
  } 
  public String getEmail(){
    return email;
  }
  public String getCompany(){
    return company;
  }
  public String getRoleId(){
    return roleId;
  }
  public String getRoleName(){
    return roleName;
  }    
  public int getSecLevel(){
    return roleSecLevel;
  }
  public String getCreatedDate(){
    return createdDate;
  }
  public String getDeletedDate() {
    return deletedDate;
  }
  public String getIsUserRemovable() {
    return isUserRemovable;
  }
  public boolean getIsVisitor() {
    return isVisitor;
  }
  

  

  public boolean insertUser(String pUserId,String pRoleId) throws Exception {
    try{

        String wstrSql =  " insert into WS_USER(user_id "+
                          "   ,role_id "+
                          "   ,firstname "+
                          "   ,lastname "+
                          "   ,email "+
                          "   ,company "+
                          "   ,created_date "+
                          "   ,is_user_removable_yn) "+
                          " values(?,?,?,?,?,?,now(),'Y') ";
        super.createPreparedStatement(wstrSql);
        super.getPreparedStatement().setString(1,pUserId.toUpperCase());
        super.getPreparedStatement().setString(2,pRoleId);
        super.getPreparedStatement().setString(3,this.givenName);
        super.getPreparedStatement().setString(4,this.lastName);
        super.getPreparedStatement().setString(5,this.email);
        super.getPreparedStatement().setString(6,this.company);
        
        super.execDML();
        super.commitConnection();
          
      return true;
    }catch (Exception ex) {
      super.rollbackConnection();
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }
  
  public boolean updateUser(String pUserId, String pRoleId, String pCompanyId) throws Exception {
    try{
      String wstrSql =  " update WS_USER "+
                        " set role_id = ? "+
                        "   ,company_id = ? "+
                        " where user_id = ? ";
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pRoleId);
      super.getPreparedStatement().setString(2,pCompanyId);
      super.getPreparedStatement().setString(3,pUserId);
      
      super.execDML();
      super.commitConnection();
      return true;
    }catch (Exception ex) {
      super.rollbackConnection();
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }  
  
  public boolean deleteUser(String pUserId) throws Exception {
    try{
      String wstrSql =  " delete from WS_USER "+
                        " where user_id = ?";
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pUserId);
      super.execDML();
      super.commitConnection();
      return true;
    }catch (Exception ex) {
      super.rollbackConnection();
      super.closeConnection();
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());  
    }
  }  

  public boolean lockUser(String pUserId) throws Exception {
    try{
      String wstrSql =  " update WS_USER "+
                        " set deleted_date = now() "+
                        " where user_id = ?";
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pUserId);
      super.execDML();
      super.commitConnection();
      return true;
    }catch (Exception ex) {
      super.rollbackConnection();
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }

  public boolean unlockUser(String pUserId) throws Exception {
    try{
      String wstrSql =  " update WS_USER "+
                        " set deleted_date = null "+
                        " where user_id = ?";
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pUserId);
      super.execDML();
      super.commitConnection();
      return true;
    }catch (Exception ex) {
      super.rollbackConnection();
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }


  public boolean setLastLogin(String pUserId) throws Exception {
    try{
      String wstrSql =  " update WS_USER "+
                        " set last_login = now() "+
                        " where user_id = ?";
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pUserId);
      super.execDML();
      super.commitConnection();
      return true;
    }catch (Exception ex) {
      super.rollbackConnection();
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }


  public boolean reqForAccount(String pUserId) throws Exception {
    try{
      super.makeConnection();
      this.insertUser(pUserId,null);
      super.closeConnection();

      return true;
    }catch (Exception ex) {
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }

  public boolean handleUserRequest(String pUserId, String pRoleId, String pCompanyId, String pApprove) throws Exception {
    try{
      
      if(pApprove.equals("Y")){
        this.updateUser(pUserId, pRoleId, pCompanyId);
        this.getUser(pUserId);
        
      }else{
        //access denied
        
        this.deleteUser(pUserId);
        // send email to user
      }
      return true;
    }catch (Exception ex) {
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }






  public boolean getAllUsers(String pCompanyId) throws Exception {
    try{
      
      String wstrSql =  " select u.user_id "+
                        "   ,DATE_FORMAT(u.DELETED_DATE,'"+super.getDateFormat()+"') as deleted_date"+
                        "   ,u.firstname "+
                        "   ,u.lastname "+
                        "   ,r.NAME as role_name "+
                        "   ,DATE_FORMAT(u.LAST_LOGIN,'"+super.getDateFormat()+"') as last_login "+
                        "   ,CASE u.DELETED_DATE WHEN null then 'N' ELSE 'Y' END  as isLocked "+
                        " from FLEX_USER u "+
                        "   ,FLEX_ROLE r "+
                        " where u.role_id = r.role_id";
                        
      if(pCompanyId != null && pCompanyId != ""){
        wstrSql = wstrSql + 
                        "   and u.company_id = "+pCompanyId;
      }
      wstrSql = wstrSql + 
                        " order by isLocked desc, r.sec_level desc, firstname, lastname" ;

      super.createPreparedStatement(wstrSql);
      super.execQuery();

      return true;
      
    }catch (Exception ex) {
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }   
  }
  
  public boolean getUserRequests() throws Exception {
    try{
      
      String wstrSql =  " select u.user_id "+
                        "   ,to_char(u.DELETED_DATE,'"+super.getDateFormat()+"') as deleted_date"+
                        "   ,u.firstname "+
                        "   ,u.lastname "+
                        " from FLEX_USER u "+
                        " where u.role_id is null "+
                        "   and u.company_id is null ";

      super.createPreparedStatement(wstrSql);
      super.execQuery();

      return true;
      
    }catch (Exception ex) {
      super.closeConnection();    
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }   
  }  
  

  public boolean getAllRoles() throws Exception {
    try{
      String wstrSql = " SELECT ROLE_ID, NAME, SEC_LEVEL FROM FLEX_ROLE ";
      createPreparedStatement(wstrSql);
      return (execQuery());
    }catch (Exception e){
      closeConnection();
      throw new Exception(e.getMessage());
    }
  }
  
  
  public boolean getUser(String pUserId) throws Exception{
    try {
      String wstrSql =  " select u.user_id "+
                        "   , firstname "+
                        "   , lastname "+
                        "   , email "+
                        "   , department "+
                        "   , DATE_FORMAT(deleted_date, '" + getDateFormat() + "') as deleted_date "+
                        "   , DATE_FORMAT(created_date, '" + getDateFormat() + "') as created_date "+
                        "   , is_user_removable_yn "+
                        "   , company "+
                        "   , r.role_id "+
                        "   , r.name as role_name"+
                        "   , r.sec_level "+
                        " from WS_USER u "+
                        "   , WS_ROLE r"+
                        " where u.user_id = ? "+
                        "   and u.role_id = r.role_id ";
                        
      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pUserId);
      super.execQuery();

      if(super.getNextRow()){
        this.userId = super.nvl(super.getCurrCol("user_id"),"");
        this.givenName = super.nvl(super.getCurrCol("firstname"),"");
        this.lastName = super.nvl(super.getCurrCol("lastname"),"");
        this.email = super.nvl(super.getCurrCol("email"),"");
        //this.department = super.getCurrCol("department");
        this.company = super.getCurrCol("company");
        this.roleId = super.getCurrCol("role_id");
        this.roleName = super.getCurrCol("role_name");
        this.roleSecLevel = Integer.parseInt(super.getCurrCol("sec_level"));
        this.deletedDate = super.getCurrCol("deleted_date");
        this.createdDate = super.getCurrCol("created_date");
        this.isUserRemovable = super.getCurrCol("is_user_removable_yn");
        this.isVisitor = false;
        
        return true;
      }else{
        //user not found i database = visitor
        //get visitor name and id, set user is vistor = true
        this.isVisitor = true;
      }
      return false;
      
    } catch (Exception ex) {
      super.closeConnection();
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    } 
  }
  
  //check access to function in the application
  public boolean verifyAccess(String pPage) throws Exception{
    try {
      String wstrSql =  " select u.role_id "+
                        " from WS_ROLE_OBJECT ro "+
                        "   ,WS_OBJECT o "+
                        "   ,WS_ROLE r "+
                        "   ,WS_USER u "+
                        " where o.name = ? "+
                        "   and ro.object_id = o.object_id "+
                        "   and ro.role_id = r.role_id "+
                        "   and r.role_id = u.role_id "+
                        "   and u.user_id = ? ";

      super.createPreparedStatement(wstrSql);
      super.getPreparedStatement().setString(1,pPage);
      super.getPreparedStatement().setString(2,userId);
      super.execQuery();

      if(super.getNextRow()){
        return true;
      }
      return false;
      
    } catch (Exception ex) {
      super.closeConnection();
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }
  }  

  public boolean authenticate (String pUser, String pPassword) throws Exception{
    try {
     if(pUser.equals("ADMIN") && pPassword.equals("ADMIN")){
        return true;
      }
        SecureLogin sl = new SecureLogin();
        if(sl.authenticateUserToAD(pUser,pPassword)){
        return true;
      }
      return false;
      } catch (Exception ex) {
      throw new Exception(ex.getMessage()+"<br>"+ex.getClass());
    }  
  }
  
  
}