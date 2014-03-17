<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
         import="com.oreilly.servlet.*"
%>
<jsp:useBean id = "mail" class="com.volvo.tech.websds.Mail" scope="page"/>

<%

  request.setCharacterEncoding("UTF-8");    
  String wAction = mail.nvl(request.getParameter("pAction"),"");
  boolean messageSent = false;

  if(wAction.equals("Send")){
    String wMessage = "Name: " + mail.nvl(request.getParameter("pName"),"") + "\r\n" +
    "Email: " + mail.nvl(request.getParameter("pEmail"),"") + "\r\n" +
    "Phone: " + mail.nvl(request.getParameter("pPhone"),"") + "\r\n" +
    "Part number: " + mail.nvl(request.getParameter("pPart"),"") + "\r\n" +
    "Product type: " + mail.nvl(request.getParameter("pType"),"") + "\r\n" +
    "Comment: " + mail.nvl(request.getParameter("pComment"),"");

     mail.makeConnection();
     mail.sendMail(mail.nvl(request.getParameter("pSubject"),""),wMessage,mail.nvl(request.getParameter("pEmail"),""));
     mail.closeConnection();
     messageSent = true;
  }
%>
<script>
    function validateRequestForm() {                                         

    var frm = document.form1; 
    
    if(frm.pSubject.value.length < 1){
      var mesg = "You must enter a subject.\n"
      alert(mesg);
      frm.pSubject.focus();
      return false; 
    }
    if(frm.pName.value.length < 1){
      var mesg = "You must enter a name.\n"
      alert(mesg);
      frm.pName.focus();
      return false; 
    }
    var apos=frm.pEmail.value.indexOf("@")
    if(apos < 1){
      var mesg = "You must enter a valid email address.\n"
      alert(mesg);
      frm.pEmail.focus();
      return false; 
    }
    if(frm.pPhone.value.length < 1){
      var mesg = "You must enter a phone number.\n"
      alert(mesg);
      frm.pPhone.focus();
      return false; 
    }
    if(frm.pPart.value.length < 1){
      var mesg = "You must enter a part number.\n"
      alert(mesg);
      frm.pPart.focus();
      return false; 
    }
    if(frm.pType.value.length < 1){
      var mesg = "You must enter a product type.\n"
      alert(mesg);
      frm.pType.focus();
      return false; 
    }
    if(frm.pComment.value.length < 1){
      var mesg = "You must enter a comment.\n"
      alert(mesg);
      frm.pComment.focus();
      return false; 
    }
    return true;
 }    
 
</script>

<HTML>
   <HEAD>
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">

      <LINK rel="stylesheet" href="css/websds.css" type="text/css">
      <title> Contact the SDS and Dangerous parts Support </title>
   </HEAD>
   
<BODY>


  <TABLE class="main">
    <TR>
<TD valign="top" class=left>   
        <jsp:include page="leftmenu1.jsp"/>
</TD>
<TD valign=top>
        <table   class="midtable">
           <td>
           <table cellspacing="2" cellpadding="2" style="margin-left: 0px;">
           <TR><TD ><A HREF="index.jsp" class = "fieldHeadingW"> << Back</A></TD></TR>
           <tr><td>&nbsp;</td></tr>
           <tr><td>&nbsp;</td></tr>
           <form action="contact.jsp" method=post name=form1 onsubmit="return validateRequestForm();">
           <td align="left" class="pageHeading">Contact SDS and dangerous parts support </td>

           <% if(messageSent){ %>
          <tr><td colspan="2" class="ATTENTION">Message has been sent! </td></tr>
          <% } %>

           <tr><td colspan="3"><hr width="100%"></td></tr>

           <TR>
               <TD class="fieldHeading"><B>Subject:</B></TD>
               <TD class="fieldInput"><input type=text name="pSubject" ></TD>
           </TR>

           <TR>
               <TD class="fieldHeading"><B>Name:</B></TD>
               <TD class="fieldInput"><input type=text name="pName" ></TD>
           </TR>

           <TR>
               <TD class="fieldHeading"><B>Email:</B></TD>
               <TD class="fieldInput"><input type=text name="pEmail" ></TD>
           </TR>

           <TR>
               <TD class="fieldHeading"><B>Phone number:</B></TD>
               <TD class="fieldInput"><input type=text name="pPhone" ></TD>
           </TR>

           <TR>
              <TD class="fieldHeading"><B>Part number:</B></TD>
              <TD class="fieldInput"><input type=text name="pPart" ></TD>

           </TR>
           <TR>
              <TD class="fieldHeading"><B>Product type:</B></TD>
              <TD class="fieldInput"><input type=text name="pType" ></TD>

           </TR>
           <TR>
              <TD class="fieldHeading"><B>Comment:</B></TD>
              <TD valign=top><textarea name="pComment" maxlength="200" cols="60" rows="8"></textarea></TD>

           </TR>

           <TR><td colspan="2" align="center">
               <input type="submit" name="pAction" value="Send" class="but" onClick="this.clicked=true;">
		       </TD></TR>
           </form>
        </table>
      </TD>

 <tr><td>&nbsp;</td></tr>
  <tr><td>&nbsp;</td></tr>
           <tr><td colspan="3"><hr width="100%"></td></tr>

  <tr><td class="leftmenuInfo">SDS and dangerous parts support</td></tr>
  <tr><td class="leftmenuInfo">+46 (0) 31 66 67 50</td></tr>

      </TABLE>
    </TR>
  </TABLE>
</BODY>
</HTML>

