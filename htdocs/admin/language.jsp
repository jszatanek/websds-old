<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "language" class="com.volvo.tech.websds.Language" scope="page"/>

<%
 int statusCode = 0;
 int constUpdated = 1;
 int constDeleted = 2;
 int constReferencesExist = 3;

  request.setCharacterEncoding("UTF-8");
  
//  String hej = response.getCharacterEncoding();
//  response.setCharacterEncoding("UTF-8");

  String wId = language.nvl(request.getParameter("pId"), "");
  String wAction = language.nvl(request.getParameter("pAction"),"");

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

%>
<%

  if(wAction.equals("Save")){
    language.makeConnection();
    language.updateLanguage(wId, language.nvl(request.getParameter("pDescription"),""));
    language.commitConnection();
    language.closeConnection();
    statusCode = constUpdated;

  }
  if(wAction.equals("Delete")){
    language.makeConnection();
    if(language.deleteLanguage(wId) == true) {
      statusCode = constDeleted;
      wId = "";
    }
    else {
      statusCode = constReferencesExist;
    }
    language.commitConnection();
    language.closeConnection();
  }
  if (wId != "") {
      language.makeConnection();
      language.getLanguage(wId);
      language.closeConnection();
  }

%>
<HTML>
<HEAD>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

      <LINK rel="stylesheet" href="../css/websds.css" type="text/css">
      <title><%= language.getApplicationName() %></title>
</HEAD>

<BODY>
<TABLE class="main">
   <form action="language.jsp" method=post name=form1 ENCTYPE="application/x-www-form-urlencoded">
   <table  WIDTH="500" CELLSPACING="0" CELLPADDING="3" BORDER="0">

       <TR><TD><A HREF="../index.jsp" class="menuitem">Back</A></TD></TR>
       <tr><td>&nbsp;</td></tr>
       <tr><td>&nbsp;</td></tr>

         <% if(statusCode == constDeleted){ %>
          <tr><td colspan="2" class="ATTENTION">Language code deleted </td></tr>
         <% } %>
         <% if(statusCode == constReferencesExist){ %>
          <tr><td colspan="2" class="ATTENTION">Cannot delete language code while there are documents with references to the code</td></tr>
         <% } %>

         <tr BGCOLOR="WHITE">
         <td align="left" class="pageHeading">Markets 
         </td></tr>
         <td>New markets are automatically added when the files are uploaded. 
             New markets/language codes are imported with the uploaded safety data sheets.
             When you update language description you will add the market to the select box on the index page. </td>
         </tr>
       </table>
       <table  width="500px" CELLSPACING="0" CELLPADDING="3" BORDER="0" valign="top">

           <tr><td colspan="2"></td></tr>
           <TR>
               <TD class="fieldHeading"><B>Language code</B></TD>
               <TD class="fieldHeading"><B><%= language.nvl(language.getName(),"")%></B></TD>
           </TR>
           <TR>
               <TD class="fieldHeading"><B>Description</B></TD>
               <TD class="fieldInput"><input type=text name="pDescription" value="<%= language.nvl(language.getDescription(),"")%>">&nbsp;&nbsp;

          <% if( wId != null && wId.length()>0){ %>   
              <input type="submit" name="pAction" value="Save" class="but" onClick="this.clicked=true;">
              <input type="hidden" name="pId" value="<%=wId%>">

           <% } else {%>
           <% } %>
           </TD></TR>

       </table>

       <table  width="500px" CELLSPACING="2" CELLPADDING="3" BORDER="0" valign="top">
           <tr><td colspan="3"><hr width="100%"></td></tr>

          <tr class="fieldHeading" colspan = 3>
          <th width="100" align="left">Language code (directory) </th>
          <th width="200" align="left">Description (displayed on index page) </th>
          <th width="70" align="left">Action</th>
          </tr>
<% 
          language.makeConnection();
          language.getAll();
          int i=0;     
          while(language.getNextRow()){
             String listId = language.getCurrCol("LANGUAGE_ID");

             if ( (i % 2) == 0 )  { %>
             <tr  BGCOLOR="WHITE" colspan = 3 >
<%           }
             else { %>
             <tr class="lgre" colspan = 3 >
<%           }
             i = i+1; %>
         
            <td class="listField"><%=language.getCurrCol("NAME")%></td>
            <td class="listField"><%=language.getCurrCol("DESCRIPTION")%></td>
            <td class="listField">
                <a href="language.jsp?pId=<%=listId%>"> <img src="../img/edit.gif" border="0" title="Update language description"></a>&nbsp;
                <a href="language.jsp?pId=<%=listId%>&pAction=Delete" onclick="javascript:return(confirm('Are you sure you want to delete this item?!'));"><img src="../img/remove.gif" border="0" title="Delete document."></a>
            </td>
            
            </tr>
<%        } //slut while
          language.closeConnection();
%>
  </table>
  </form>
</table>
</BODY>
</HTML>
