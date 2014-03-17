<%@ page contentType="text/html;charset=UTF-8"
         errorPage="errorpage.jsp"
%>
<jsp:useBean id = "redir" class="com.volvo.tech.websds.WebsdsApplication" scope="page"/>
<jsp:useBean id = "language" class="com.volvo.tech.websds.Language" scope="page"/>

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
  <TD valign="top" class=left>   
        <jsp:include page="leftmenu1.jsp"/>
  </TD>
  <TD valign=top>

      <TABLE WIDTH="550" class="midTable">
      <td colspan="1"><IMG SRC="img/DangerousParts.jpg"></td>
      <tr><td><BR>This database contains safety data sheets for chemical products supplied by Volvo Car Corporation, Customer Service; Volvo Parts Corporation; AB Volvo Penta; and Volvo Construction Equipment AB, Customer Support Division. </td></tr>
      <tr><td><BR>For dangerous parts you can find transport information in English or, if relevant, safety data sheets.</td></tr>
      <tr><td class="heading3"><BR><IMG SRC="img/rubrik2.jpg"></td></tr>
    
      <tr><td>  Be aware of that the safety data sheet that you search for on this site is only valid on the market you have chosen. The markets are shown as Country Language, i.e. the country is equal to market.</td></tr>
      <tr><td>&nbsp;</td></tr>
    
      <form name="form1" action="documents.jsp" method ="post">
      <table class="midTable">
      
      <tr>
         <td><select name=pLanguage  size="1"  width=130px >
         <option value=null>Please select a market ... </option>
    <% 
                 language.makeConnection();
                 language.getAll();
                 while(language.getNextRow()) { 
                     if(language.getCurrCol("description").length() > 0) {
                         String wLanguageId = language.getCurrCol("language_id");
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
                 language.closeConnection(); %>
           </select></td>
           <TD>
               <input type="submit" name="OK" value="OK" onclick="if(document.form1.pLanguage[document.form1.pLanguage.selectedIndex].value =='null'){alert('Please select market');return false;} " >
           </TD>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <TR><TD><A HREF="infokits.jsp" class="leftmenuInfo">INFO ON KITS</A></TD></TR>
    <% if(user == null){ %>
    <% } else {%>
          <TR><TD><A HREF="admin/language.jsp" class="leftmenuInfo">Register new market</A></TD></TR>
          <TR><TD><A HREF="admin/uploadNews.jsp" class="leftmenuInfo">Upload news and info kits</A></TD></TR>
          <TR><TD><A HREF="admin/adminDocs.jsp" class="leftmenuInfo">Delete document</A></TD></TR>
          <TR><TD><A HREF="admin/listDocuments.jsp" class="leftmenuInfo">Document summary</A></TD></TR>
          <TR><TD><A HREF="admin/logout.jsp" class="leftmenuInfo">Logout</A></TD></TR>
    <% } %>
      <tr><td>&nbsp;</td></tr>
      </table>
      </form>
      </TABLE>
  </TD>
  </TR>
  </TABLE>
</BODY>
</HTML>

