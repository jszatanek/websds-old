<%@ page contentType="text/html"
         language ="java"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "document" class="com.volvo.tech.websds.Document" scope="page"/>
<%
  session = request.getSession(true);
  com.volvo.tech.websds.User user = (com.volvo.tech.websds.User)session.getAttribute("employee");
  if(user == null){
    response.sendRedirect(document.getUrl(request,"admin/login.jsp"));
    return;
  }

  user.makeConnection();
  if(!user.verifyAccess("Language")){
    user.closeConnection();
    response.sendError(401);
    return;

  }
  user.closeConnection();
  request.setCharacterEncoding("UTF-8"); 

%>
<HTML>
<HEAD>
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

      <LINK rel="stylesheet" href="../css/websds.css" type="text/css">
      <title><%= document.getApplicationName() %></title>
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
           <form action="listDocuments.jsp" method ="post">

           <tr><td class="fieldHeadingW"> List uploaded documents per for each market </td></tr>
           <td><table  width="400px" CELLSPACING="2" CELLPADDING="3" BORDER="0" >
               <tr class="fieldHeadingW" colspan = 2>
                <th width="10" align="left">Language </th>
                <th width="10" align="left">Id </th>
                <th width="200" align="left">Number of documents </th>
                </tr>
           <%  
                document.makeConnection();
                document.getDocumentList();
                while(document.getNextRow()){
                   String listId = document.getCurrCol("LANGUAGE_ID");
            %>       
                   <tr>
                        <td class="listField"><%=document.getCurrCol("NAME")%></td>
                        <td class="listField"><%=document.getCurrCol("LANGUAGE_ID")%></td>
                        <td class="listField"><%=document.getCurrCol("COUNT_DOCS")%></td>
                   </tr>
            <%  }
                document.closeConnection();
            %>
            </table></TD>
           </form>
        </table>
      </TD>
      </TABLE>
    </TR>
  </TABLE>
</BODY>
</HTML>


