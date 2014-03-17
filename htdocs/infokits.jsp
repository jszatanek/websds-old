<%@ page contentType="text/html;charset=UTF-8"
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
<TD valign="top" class=left>   
        <jsp:include page="leftmenu1.jsp"/>
</TD>
<TD valign=top>

      <TABLE class="midtable">
      <TR><TD valign=top>
      <TR><TD ><A HREF="index.jsp" class = "fieldHeadingW"> << Back</A></TD></TR>
      <tr><td>&nbsp;</td></tr>
      <tr><td>&nbsp;</td></tr>
      
      <form action="infokits.jsp" method=post name=form1>
      <td><table>
      <tr><td align="left" class="heading3"> INFO ON KITS </td></tr>
      <tr><td>&nbsp;</td></tr>
      <tr><td>&nbsp;</td></tr>
      <table  width="400px" CELLSPACING="0" CELLPADDING="3" BORDER="0" valign="top">
      <%  
          document.makeConnection();
          document.getAll("INFO KITS");
          while(document.getNextRow()){
             String listId = document.getCurrCol("DOCUMENT_ID");
      %>       
      
             <tr>
             <td class="listField"><a href="document.jsp?pDocumentId=<%=listId%>"><img src="img/file_pdf.gif" border="0" title="Get document size: <%=document.getCurrCol("FILE_SIZE")%>"></a></td>
             <td class="listField"><%=document.getCurrCol("NAME")%></td>
             </tr>
             <tr><td>&nbsp;</td></tr>
      <%  }
          document.closeConnection();
      %>
      </table>
      </table></td>
      </form>
      
      </td></tr>
      </TABLE>
</TD>
</TR>
</TABLE>
</BODY>
</HTML>

