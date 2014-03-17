<%@ page contentType="text/html;charset=UTF-8"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "user" class="com.volvo.tech.websds.User" scope="page"/>

<%
  session = request.getSession(true);

  session.invalidate();

  response.sendRedirect(user.getUrl(request,"index.jsp"));

%>
  