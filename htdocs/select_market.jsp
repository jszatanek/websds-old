<%@ page contentType="text/html;charset=UTF-8"
         errorPage="errorpage.jsp"
%>
<% 
  session = request.getSession(true);
  request.setCharacterEncoding("UTF-8");    
%>

<jsp:useBean id = "redir" class="com.volvo.tech.websds.WebsdsApplication" scope="page"/>
<jsp:useBean id = "language" class="com.volvo.tech.websds.Language" scope="page"/>

      
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
           <tr>
           <TD>
               <input type="submit" name="OK" value="OK" >
           </TD>
           </TR>
      </tr>
