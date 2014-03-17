<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
         import="com.oreilly.servlet.*"
%>

<jsp:useBean id = "article" class="com.volvo.tech.websds.Article" scope="page"/>

<%

  String wAction = article.nvl(request.getParameter("SaveNew"),"");
  boolean updated=false;
  if(wAction.equals("Save")){
        article.makeConnection();
        article.insertArticle(request.getParameter("pDrawingNo"), request.getParameter("pPartNo"));
      article.commitConnection();
        article.closeConnection();
        updated = true;
  }
%>

<HTML>
   <HEAD>
      <LINK rel="stylesheet" href="../css/websds.css" type="text/css">
      <title>Test </title>
   </HEAD>
   
<BODY>


  <TABLE class="main">
    <TR>

      <TD valign=top>
        <table  >
           <tr><td> <%if (updated == true)           {%>
                 <td align="left" class="pageHeading">Uppdaterat!!!!</td>
           <% } %>
           </td></tr>
           <td>
           <table cellspacing="2" cellpadding="2" style="margin-left: 30px;">
           <TR><TD><A HREF="index.jsp" class="menuitem">Back</A></TD></TR>
           <tr><td>&nbsp;</td></tr>
           <tr><td>&nbsp;</td></tr>
            <form action="UploadTest.jsp" method=post name=form1>

           <td align="left" class="pageHeading">Upload test</td>

           <tr><td colspan="3"><hr width="100%"></td></tr>

           <TR>
               <TD class="fieldHeading"><B>Language</B></TD>
               <TD class="fieldInput"><input type=text name="pDrawingNo" ></TD>
           </TR>

           <TR>
              <TD class="fieldHeading"><B>Article</B></TD>
              <TD class="fieldInput"><input type=text name="pPartNo" ></TD>

           </TR>

           <TR><td colspan="2" align="center">
               <input type="submit" name="SaveNew" value="Save" class="but" onClick="this.clicked=true;">
           </TD></TR>
           </form>
        </table>
      </TD>
      </TABLE>
    </TR>
  </TABLE>
</BODY>
</HTML>

