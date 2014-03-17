<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
         import="com.oreilly.servlet.*"
%>

<jsp:useBean id = "myDocument" class="com.volvo.tech.websds.Document" scope="page"/>
<jsp:useBean id = "language" class="com.volvo.tech.websds.Language" scope="page"/>
<%
  request.setCharacterEncoding("UTF-8");    
  String wAction = myDocument.nvl(request.getParameter("pAction"),"");

  session = request.getSession(true);
  com.volvo.tech.websds.User user = (com.volvo.tech.websds.User)session.getAttribute("employee");
  if(user == null){
    response.sendRedirect(language.getUrl(request,"admin/login.jsp"));
    return;
  }

  user.makeConnection();
  if(!user.verifyAccess("Language")){
    user.closeConnection();
    response.sendError(401);
    return;

  }
  user.closeConnection();
  
 int statusCode = 0;
 int constNotExist = 1;
 int constDeleted = 2;

  if(wAction.equals("Delete")){
    myDocument.makeConnection();
    if(myDocument.deleteDocument(request.getParameterValues("pLanguage"), myDocument.nvl(request.getParameter("pArticle"),"")) == true) {
      statusCode = constDeleted;
    } else {
      statusCode = constNotExist;
    }
    myDocument.commitConnection();
    myDocument.closeConnection();
  }

%>
<HTML>
   <HEAD>
      <LINK rel="stylesheet" href="../css/websds.css" type="text/css">
      <title><%= myDocument.getApplicationName() %></title>
   </HEAD>
   
<BODY>


  <TABLE class="main">
    <TR>

      <TD valign=top>
        <table  >
           <td>
           <table cellspacing="2" cellpadding="2" style="margin-left: 30px;">
           <TR><TD><A HREF="../index.jsp" class="menuitem">Back</A></TD></TR>
           <tr><td>&nbsp;</td></tr>
           <tr><td>&nbsp;</td></tr>
           <form action="adminDocs.jsp" method ="post">
          <tr>
           <td align="left" class="pageHeading" colspan = "3">Delete document: </td></tr>
          
         <% if(statusCode == constDeleted){ %>
          <tr><td colspan="2" class="ATTENTION">Document deleted </td></tr>
         <% } %>
         <% if(statusCode == constNotExist){ %>
          <tr><td colspan="2" class="ATTENTION">Document does not exist </td></tr>
         <% } %>

           <tr><td colspan="3"><hr width="100%"></td></tr>

         <tr><TD class="fieldHeading" width=130px><B>Market</B></TD>
         <td><select name=pLanguage  size="10"  width=130px multiple >
<%       String wLanguageId = "";             %>
         <option value="<%=wLanguageId%>" selected>All markets</option>
         <%
             
             language.makeConnection();
             language.getAll();          
             while(language.getNextRow()) { 
                 if(language.getCurrCol("description").length() > 0) {
                     wLanguageId = language.getCurrCol("language_id");
                     if(wLanguageId.equals(language.nvl(language.getId(),""))) {
  %>                     
                         <option value="<%=wLanguageId%>" selected><%=language.getCurrCol("description") %></option>
  <%
                      }
                      else {
  %>
                         <option value="<%=wLanguageId%>"><%=language.getCurrCol("description")%></option>
  <%
                      }        
                  }
             }
             language.closeConnection(); 
             %>
           </select></td>
</tr>
           <TR>
              <TD class="fieldHeading" width=130px><B>Article</B></TD>
              <TD class="fieldInput"  width=130px><input type=text name="pArticle" ></TD>
              

           </TR>

           <TR><td colspan="2" align="center">
               <input type="submit" name="pAction" value="Delete" class="but" onclick="javascript:return(confirm('Are you sure you want to delete this item?!'));">
           </TD></TR>
           </form>
        </table>
        <tr><td align="left" class="pageHeading" colspan = "3">To select more than one market, press Ctrl and click on the markets<br></td></tr>
        <TR><TD class="bodytext">&nbsp;</TD></TR>
           <TR><TD class="bodytext" ><B>When "All markets" is selected,   </B></TD></TR>
           <TR><TD class="bodytext"><B>the part number document will be removed in all markets. </B></TD></TR>

      </TD>
      </TABLE>
    </TR>
  </TABLE>
</BODY>
</HTML>

