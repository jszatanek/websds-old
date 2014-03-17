<%@ page  contentType="text/html;charset=UTF-8"
          isErrorPage="true"
%>

<jsp:useBean id = "appl" class="com.volvo.tech.websds.WebsdsApplication" scope="page"/>
<%
  request.setCharacterEncoding("UTF-8");    
%>
  <HTML>
    <HEAD>
      <LINK rel="stylesheet" href="css/websds.css" type="text/css">
      <TITLE>
        Errorpage
      </TITLE>
  </HEAD>
  <BODY>


    <TABLE WIDTH="670" class="TABLE_BACKGROUND_COLOR" CELLSPACING="0" CELLPADDING="3" BORDER="0" >
      <TR class="TABLE_HEADER_ROW_COLOR">
        <TD colspan = 2>
          <h1>An application error has occured:</h1>
        </TD>
      </TR>

      <tr>
        <td class="ATTENTION"><%= exception.getMessage() %></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
      </tr>
      
      
      <tr><td><a href="javascript:history.back(1)">Back</a></td></tr>
      <tr>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td class=medium>If this error appears again please contact <a href="mailto:<%= appl.getStaticVariable("MAILADDRESS") %>?subject=WEBSDS application error" class=medium>Websds administrators</a><br>Try to describe your actions as detailed as possible</td>
      </tr>
    </table>
  </BODY>
  </HTML>