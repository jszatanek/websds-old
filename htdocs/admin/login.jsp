<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "user" class="com.volvo.tech.websds.User" scope="page"/>

<%
  request.setCharacterEncoding("UTF-8");    

  boolean loginError = false;
  boolean userLocked = false;
  boolean applyForAccess = false;
  
  if(request.getParameter("u") != null && request.getParameter("p") != null){
        if(user.authenticate(request.getParameter("u").toUpperCase(),request.getParameter("p"))){
    
          int secLevel = 0;
          user.makeConnection();
         
          if(user.getUser(request.getParameter("u").toUpperCase())){
            secLevel = user.getSecLevel();
            if(user.getDeletedDate() != null){
              loginError = true;
              userLocked = true;
            }else{        
                session = request.getSession(true);
                session.setAttribute("employee",user);
                session.setMaxInactiveInterval(14400);
                user.setLastLogin(user.getUserId());
                String temp = user.getUrl(request,"index.jsp");
                response.sendRedirect(user.getUrl(request,"index.jsp"));
                return;
            }
          }else{
            //user is visitor - show message to apply for access
            loginError = true;
            applyForAccess = true;
          }
          user.closeConnection();
        }else{
          loginError = true;
        }
  }
%>

<HTML>
<HEAD>
  <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
  <LINK rel="stylesheet" href="../css/std.css" type="text/css">
  <title><%= user.getApplicationName() %></title>

</HEAD>

<BODY onLoad="document.forms[0].u.focus();" margin-height="1px">

<TABLE>
<tr><td colspan="2"><BIG class="b">Login for Administrators</BIG></td></tr>
</TABLE>


  <TABLE class="main" valign="top" width="290" border="0">

<tr><td colspan="2"><a href="..\index.jsp">Back</a><br/><br/></td></tr>

      <form action="login.jsp" method=post name=form1>

           <% if(loginError){ %>
          <tr><td colspan="2" class="ATTENTION">Error login - retry or contact support!</td></tr>
          <% } %>


  
          <tr><td colspan="2" class="outlinedproperty">
          Enter your username and password to log in.
          </td></tr>
          <!-- space -->
          <tr>
          <td colspan="2">&nbsp;</td>
          </tr>
          
          <TR>
          <TD colspan="2" class="fieldHeading"><B>Username</b>
          <br>
          <input type="text" size="25" maxsize="25" name="u" value="<%= user.nvl(request.getParameter("u"),"") %>"></TD>
          <br>
          </TR>

          <TR>
          <TD colspan="2" class="fieldHeading"><B>Password</B>
          <br>
          <input type="password"  size="25" maxsize="25" name="p"></TD>
          </TR>

          <TR>
          <td colspan="2" align="right">
          <input type="submit" value="Login" class="button">
          </TD>
          </TR>
      </form>
      
  </TABLE>
</BODY>
</HTML>


