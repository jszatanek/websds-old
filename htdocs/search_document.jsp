<%@ page contentType="text/html;charset=UTF-8"
         errorPage="errorpage.jsp"
%>
<% 
  session = request.getSession(true);
  request.setCharacterEncoding("UTF-8");    
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

<script>
    function init() {
      var curPageStyle;
      var curPage = "pageno1";
      curPageStyle  = document.getElementById(curPage).style;
      curPageStyle.visibility = "visible";
    }

    function getPage(pCurPage,pNextPage) {
      var prevPage="pageno"+pCurPage;
      var nextPage="pageno"+pNextPage;
      newPage(prevPage, nextPage);
    }

    function newPage(prevPage, nextPage) {
      var curPageStyle, prevPageStyle;

      if (document.getElementById) 
      {
         prevPageStyle  = document.getElementById(prevPage).style;
         nextPageStyle = document.getElementById(nextPage).style;
      }
      
      // toggle visibility
      prevPageStyle.visibility = "hidden";
      nextPageStyle.visibility  = "visible";
  
      // remember current page
      currentPage = nextPage;
    }


function validateRequestForm() {                                         

    var frm = document.form1; 
    
    if(frm.pSearchProfile.value.length < 3){
    // Build alert box message showing how many characters entered
        var mesg = "You have only entered " + frm.pSearchProfile.value.length + " character(s).\n"
        mesg = mesg + "Please enter at least 3 leading characters."
        alert(mesg);
        frm.pSearchProfile.focus();
        return false; 
    }
    var apos=frm.pSearchProfile.value.indexOf("%")
    if(apos < 3 && apos > 0){
        var mesg = "In the 3 leading characters % is not allowed.\n"
        mesg = mesg + "Please re-enter the search profile."
        alert(mesg);
        frm.pSearchProfile.focus();
        return false; 
    }
        return true;
}    

</script>



      <table class="midtable" >
      <tr>
      <td>
             <table  WIDTH="500" CELLSPACING="0" CELLPADDING="3" BORDER="0">
      
               <tr BGCOLOR="WHITE">
      <%         
               String wDescription="";
               if (!(wLanguage.equals(""))) {
                 language.makeConnection();
                 language.getLanguage(wLanguage);
                 wDescription = language.getCurrCol("DESCRIPTION");
               } %>
               
               <tr><td>&nbsp;</td></tr>
               <tr><td align="left" class="pageHeading"><%=wDescription%> </td></tr>
               <tr><td>&nbsp;</td></tr>
               <tr>
               <TD class="fieldHeadingW"><B>Enter part number: </B></td></tr><tr>
               <TD class="fieldInputW"><input type=text name="pSearchProfile" value="<%= wSearchProfile%>" >&nbsp;&nbsp;
               &nbsp&nbsp <input type="submit" name="pAction" value="Search SDS" class="but" onClick="this.clicked=true;"></TD>
               <input type="hidden" name="pLanguage" value="<%=wLanguage%>">
               <input type="hidden" name="pSearch" value="TRUE">
               </tr>
               <tr><td>&nbsp</td></tr>
               <tr><td>&nbsp</td></tr>
             </table>
      
      </td>
      </tr>
      <% if(wSearch.equals("TRUE")) { %>
      <td>
      <%  
          int nofRecsPerPage=15;
          int currentPage=0;
          int i = 1;     
          int nofRecords = 0;
          int pageno = 1;  
      
      
          document.makeConnection();
          document.getAll(wLanguage, wSearchProfile +"%");
          int nofRows = document.getNofRows();
          if (nofRows == 0) { %>
               <tr BGCOLOR="WHITE">
               <td align="left" class="pageHeading">Document not found &nbsp;&nbsp;
               </td></tr>
      <%    } else {
          int lastPage=nofRows/nofRecsPerPage+1;
          int nofRecsOnLastPage = nofRows - ((nofRows/nofRecsPerPage)*nofRecsPerPage);
      
          while(document.getNextRow()){
             String listId = document.getCurrCol("DOCUMENT_ID");
             
             if(i==1){
      %>
              <DIV id="pageno<%= pageno %>" style="visibility:hidden;position:absolute;">
              <table  width="400px" CELLSPACING="2" CELLPADDING="3" BORDER="0" valign="top">
                <tr class="fieldHeadingW" colspan = 2>
                <th width="10" align="left">Type </th>
                <th width="200" align="left">Part </th>
                </tr>
      
      <%     } %>
      
             <tr>
      
                  <td class="listField" width="10"><a href="document.jsp?pDocumentId=<%=listId%>"><img src="img/file_pdf.gif" border="0" title="Get document: <%=document.getCurrCol("FILE_SIZE")%> bytes"></a></td>
                  <td class="listField"><%=document.getCurrCol("ARTICLE_NAME")%></td>
             </tr>
      
      <%
             if(i==nofRecsPerPage || (pageno == lastPage && i == nofRecsOnLastPage)){
      %>       
                 <td colspan=8>
                 <table  width="400px" CELLSPACING="2" CELLPADDING="3" BORDER="0" valign="top">
                 <tr class="fieldHeadingW" >
                 <td>
      <%
                   if(pageno != 1){
      %>
                      <a href="#" onClick="getPage('<%= pageno %>','<%= pageno-1 %>'); " return(false);>Prev </a> &nbsp;&nbsp;
      <%           
                   } 
      %>
      <%
                   if(lastPage != 1){
                      for(int x=1;x<=lastPage;x++) {
      %>                 
                          <a href="#" onClick="getPage('<%= pageno %>','<%= x %>'); " return(false);><%= x %> </a> &nbsp;&nbsp;
      <%              }  
                   }%>
      <%
                   if(pageno < lastPage){
      %>
                      <a href="#" onClick="getPage('<%= pageno %>','<%= pageno+1 %>'); " return(false);>Next </a> &nbsp;&nbsp;
      <%           
                   }
      %>             
                 </td>
                 </tr>
                 </table>
                 </td>
                 </table>
                 </DIV>
      <%    
                 i = 0;
                 pageno = pageno + 1;
             }
             i ++;       
          }
          
          nofRecords = i;
          
          if(i != 1){
      %>    
            </table>
            </div>
      <%
          }
          }
          document.closeConnection();
      %>
      
      <script>
           init();
      </script>
      </BODY>
      </td>
      <% } %>
      </table>
