<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "document" class="com.volvo.tech.websds.Document" scope="page"/>
<jsp:useBean id = "language" class="com.volvo.tech.websds.Language" scope="page"/>

<%
String wLanguage = document.nvl(request.getParameter("pLanguage"),"");
String wSearchProfile = document.nvl(request.getParameter("pSearchProfile"),"");
String wSearch = document.nvl(request.getParameter("pSearch"),"");
wSearchProfile = wSearchProfile.replaceAll("%25","%");
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
<TD valign=top>

      <TABLE WIDTH="550" class="midtable">
      
      <TR>
      <TD valign=top>
      <TR><TD ><A HREF="penta_index.jsp" class = "fieldHeadingW"> << Back</A></TD></TR>
      <tr><td>&nbsp;</td></tr>
      <tr><td>&nbsp;</td></tr>
    
      </td></tr>
      <form action="penta_documents.jsp" method=post name=form1 onsubmit="return validateRequestForm();">
      <jsp:include page="search_document.jsp"/>

      </form>
      </table>
</td>      
</tr>      
</table>
</HTML>

