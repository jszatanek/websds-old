<%@ page contentType="text/html"
         language ="java"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "document" class="com.volvo.tech.websds.News" scope="page"/>
<%
request.setCharacterEncoding("UTF-8");    
%>
<HTML>
<HEAD>
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

      <LINK rel="stylesheet" href="css/websds.css" type="text/css">
      <title><%= document.getApplicationName() %></title>
</HEAD>

   
<BODY>

  <TABLE class="main">
    <TR>
      <TD valign="top" class=left><jsp:include page="leftmenu1.jsp"/></TD>
      <TD valign=top>
        <table class="midTable" >
        <TR><td>
           <TR><TD ><A HREF="index.jsp" class = "fieldHeadingW"> << Back</A></TD></TR>
           <tr><td>&nbsp;</td></tr>
           <tr><td>&nbsp;</td></tr>
           <TR><TD  class="pageHeading" >Latest news </TD></TR>
           <tr><td>&nbsp;</td></tr>
           <form action="news.jsp" method=post name=form1>
           <tr><td class="fieldHeadingW"> SDS </td></tr>
            <tr><td>&nbsp </td></tr>
           <tr><td class="fieldHeadingW"> Latest updates </td></tr>
           <td><table  width="400px" CELLSPACING="2" CELLPADDING="3" BORDER="0" >
           <%  
                document.makeConnection();
                document.getAll("LATEST SDS");
                while(document.getNextRow()){
                   String listId = document.getCurrCol("DOCUMENT_ID");
            %>       
                   <tr>
                        <td width="10" class="listField"><a href="document.jsp?pDocumentId=<%=listId%>"><img src="img/file_pdf.gif" border="0" title="Get document: <%=document.getCurrCol("FILE_SIZE")%> bytes">
                        </a></td>
                        <td class="listField"><%=document.getCurrCol("NAME")%>
                        </td>
                   </tr>
            <%  }
                document.closeConnection();
            %>
            </table></TD>
            <tr><td>&nbsp </td></tr>
            <tr><td class="fieldHeadingW"> MSDS </td></tr>
            <tr><td>&nbsp </td></tr>
           <tr><td class="fieldHeadingW"> Latest updates </td></tr>
           <td><table  width="400px" CELLSPACING="2" CELLPADDING="3" BORDER="0" >
           <%  
                document.makeConnection();
                document.getAll("LATEST MSDS");
                while(document.getNextRow()){
                   String listId = document.getCurrCol("DOCUMENT_ID");
            %>       
                   <tr>
                        <td width="10" class="listField"><a href="document.jsp?pDocumentId=<%=listId%>"><img src="img/file_pdf.gif" border="0" title="Get document: <%=document.getCurrCol("FILE_SIZE")%> bytes">
                        </a></td>
                        <td class="listField"><%=document.getCurrCol("NAME")%>
                        </td>
                   </tr>
            <%  }
                document.closeConnection();
            %>
            </table></TD>
           </form>

        </TD></TR>
        </TABLE>
      </TD>
    </TR>
  </TABLE>
</BODY>
</HTML>

