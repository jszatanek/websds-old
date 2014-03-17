<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
         import="com.oreilly.servlet.*"
%>

<jsp:useBean id = "myDocument" class="com.volvo.tech.websds.News" scope="page"/>
<jsp:useBean id = "type" class="com.volvo.tech.websds.Type" scope="page"/>
<%
  boolean uploadError = false;
  
  request.setCharacterEncoding("UTF-8");    

  session = request.getSession(true);
  com.volvo.tech.websds.User user = (com.volvo.tech.websds.User)session.getAttribute("employee");
  if(user == null){
    response.sendRedirect(myDocument.getUrl(request,"admin/login.jsp"));
    return;
  }

  user.makeConnection();
  if(!user.verifyAccess("uploadNews")){
    user.closeConnection();
    response.sendError(401);
    return;
  }

  if(myDocument.nvl(request.getParameter("pAction"),"").equals("delete")){
      myDocument.makeConnection();
      myDocument.deleteDocument(request.getParameter("pDocumentId"));
      myDocument.closeConnection();
  }
  else if (request.getContentLength() > 0 ) {
      myDocument.makeConnection();
      if(!(myDocument.uploadDocument(request))) {
          uploadError=true;
      }
      myDocument.closeConnection();
  }

  
  user.closeConnection();


%>
<HTML>
   <HEAD>
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

      <LINK rel="stylesheet" href="../css/websds.css" type="text/css">
      <title><%= myDocument.getApplicationName() %></title>
   </HEAD>


<BODY>
<table  >
  <td>
  <table cellspacing="2" cellpadding="2" style="margin-left: 0px;">
       <TR><TD><A HREF="../index.jsp" class="menuitem">Back</A></TD></TR>
       <tr><td>&nbsp;</td></tr>
       <tr><td>&nbsp;</td></tr>
       <form action="uploadNews.jsp" method ="post" name="form1" ENCTYPE="multipart/form-data" >
  
       <td align="left" class="pageHeading">Upload news and info kits</td>
  
       <% if(uploadError){ %>
             <tr><td colspan="2" class="ATTENTION">Error document upload - retry or contact support!</td></tr>
<%     } %>       
     

       <tr><td colspan="3"><hr width="100%"></td></tr>
  
       <TR>
           <TD class="fieldHeading"><B>Name</B></TD>
           <TD class="fieldInput"><input type=text name="pName" ></TD>
       </TR>
       
       <tr>
        <td class="fieldHeading">Type</td>
        <td><select name=pType size="1"  width=130px >
            <option value="">Please select a document type ...</option>
  <% 
              type.makeConnection();
              type.getAll();
              while(type.getNextRow()){ 
                 String wTypeId = type.getCurrCol("type_id");
  %>
            <option value="<%=wTypeId%>"><%=type.getCurrCol("name")%></option>
  <%
              }
              type.closeConnection(); %>
        </select></td>
       </tr>          

       
       
       <tr class="lgre">
           <TD class="fieldHeading"><B>File</B></td>
           <TD><input type="file" name="pFile"></td>
       </tr>
       <tr><td>&nbsp;</td></tr>
  
       <TR><td colspan="2" align="center">
           <input type="submit" name="pAction" value="upload" class="but" onClick="this.clicked=true;">
       </TD></TR>

       <tr><td colspan="3"><hr width="100%"></td></tr>
       <tr><td>&nbsp;</td></tr>
       <tr><td>&nbsp;</td></tr>
       </form>
  </table>
  </TD>
</TABLE>

<table>
<table  width="600px" CELLSPACING="1" CELLPADDING="3" BORDER="1" valign="top">
    <tr>
    <tr class="fieldHeadingW" colspan = 9>
    <th width="100" align="left">Filename </th>
    <th width="100" align="left">Last updated </th>
    <th width="100" align="left">File size </th>
    <th width="100" align="left">Type </th>
    <th width="50" align="left">Action </th>
    </tr>
<%  
    myDocument.makeConnection();
    myDocument.getAll();
    while(myDocument.getNextRow()){
       String listId = myDocument.getCurrCol("DOCUMENT_ID");
%>       

        <tr>
            <td class="listField"><%=myDocument.getCurrCol("NAME")%></td>
            <td class="listField"><%=myDocument.getCurrCol("LAST_UPDATED")%></td>
            <td class="listField"><%=myDocument.getCurrCol("FILE_SIZE")%></td>
            <td class="listField"><%=myDocument.getCurrCol("TYPE")%></td>
            <td class="listField">
               <a href="uploadNews.jsp?pDocumentId=<%=listId%>&pAction=delete" onclick="javascript:return(confirm('Are you sure you want to delete this item?!'));"><img src="../img/remove.gif" border="0" title="Delete document."></a>
  
            </td>
       </tr>
<%  }
    myDocument.closeConnection();
%>
</table>
</table>

</BODY>
</HTML>

