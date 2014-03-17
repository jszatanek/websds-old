<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
         import="com.oreilly.servlet.*"
%>

<jsp:useBean id = "myDocument" class="com.volvo.tech.websds.Document" scope="page"/>
<%
try {

  request.setCharacterEncoding("UTF-8");    
  if (request.getContentLength() > 0 ) {
      myDocument.makeConnection();
      myDocument.uploadDocument(request);
    //  skicka tillbaka OK   response.sendRedirect(blob.getUrl(request,"UploadFile.jsp"));
      myDocument.closeConnection();
      out.println("RETURKOD=0");
  }
}catch(Exception e){out.println("ERROR="+e.getMessage());
return;
}
%>
<HTML>
   <HEAD>
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

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
           <form action="UploadFiles.jsp" method ="post" ENCTYPE="multipart/form-data">

           <td align="left" class="pageHeading">Upload </td>

           <tr><td colspan="3"><hr width="100%"></td></tr>

           <TR>
               <TD class="fieldHeading"><B>Language</B></TD>
               <TD class="fieldInput"><input type=text name="pLanguage" ></TD>
           </TR>

           <TR>
              <TD class="fieldHeading"><B>Article</B></TD>
              <TD class="fieldInput"><input type=text name="pArticle" ></TD>

           </TR>
           <tr class="lgre">
               <TD class="fieldHeading"><B>File</B></td>
               <TD><input type="file" name="pFile"></td>
           </tr>

           <TR><td colspan="2" align="center">
               <input type="submit" name="pAction" value="upload" class="but" onClick="this.clicked=true;">
           </TD></TR>
           </form>
        </table>
      </TD>
      </TABLE>
    </TR>
  </TABLE>
</BODY>
</HTML>

