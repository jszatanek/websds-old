<%@ page contentType="text/html;charset=UTF-8"
         errorPage="errorpage.jsp"
%>
<jsp:useBean id = "redir" class="com.volvo.tech.websds.WebsdsApplication" scope="page"/>
<jsp:useBean id = "language" class="com.volvo.tech.websds.Language" scope="page"/>
<jsp:useBean id = "document" class="com.volvo.tech.websds.News" scope="page"/>

<% 
  session = request.getSession(true);
  com.volvo.tech.websds.User user = (com.volvo.tech.websds.User)session.getAttribute("employee");
  request.setCharacterEncoding("UTF-8");    
%>
<HTML>
<HEAD>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <LINK rel="stylesheet" href="css/websds.css" type="text/css">
  <title><%= redir.getApplicationName() %></title>
  
  

</HEAD>

<BODY>
  <TABLE class="main">
  <TR>
  <TD valign=top>

      <TABLE WIDTH="550" class="midTable">
      <tr><td>&nbsp;</td></tr>
    
      <tr><td>This database contains Safety Data Sheets for chemical products supplied by Volvo Parts Corporation and AB Volvo Penta.  </td></tr>
      <tr><td>&nbsp;</td></tr>
    
      <table class="midTable">
      <form action="penta_documents.jsp" method ="post">

      <jsp:include page="select_market.jsp"/>
      <tr><td>&nbsp;</td></tr>
      <td><table  width="400px" CELLSPACING="0" BORDER="0" valign="top">
      <%  
          document.makeConnection();
          document.getAll("INFO KITS", "AB Volvo Penta");
          while(document.getNextRow()){
             String listId = document.getCurrCol("DOCUMENT_ID");
      %>       
      
             <tr>
             <td class="listField"><a href="document.jsp?pDocumentId=<%=listId%>"><img src="img/file_pdf.gif" border="0" title="Get document size: <%=document.getCurrCol("FILE_SIZE")%>"></a></td>
             <td class="listField"><%=document.getCurrCol("NAME")%></td>
             </tr>
      <%  }
          document.closeConnection();
      %>
      </table></td>
      </form>
      </table>
      </TABLE>
  </TD>
  </TR>
  </TABLE>
</BODY>
</HTML>


