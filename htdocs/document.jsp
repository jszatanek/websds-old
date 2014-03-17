<%@ page contentType="text/html;charset=UTF-8"
         language ="java"
         errorPage="errorpage.jsp"
%>

<jsp:useBean id = "document" class="com.volvo.tech.websds.Document" scope="page"/>
<%
  String documentId = request.getParameter("pDocumentId");
  session = request.getSession(true);

  request.setCharacterEncoding("UTF-8");    
  if (documentId == null) {
    response.sendError(response.SC_NO_CONTENT);
    out.write("&nbsp;");
    out.flush();
  }else{
    document.makeConnection();
    if ( document.getDocument(documentId)) {
      if(document.getMimeType().indexOf("image") != -1){
        response.setHeader("Content-Disposition", "inline; filename=" +document.getFileName());
      }else{
        response.setHeader("Content-Disposition", "attachment; filename=" +document.getFileName());
      }
      response.setContentLength((int)document.getFileLength());
      response.setContentType(document.getMimeType());

      document.streamDocument(response.getOutputStream());
      response.flushBuffer();
    }else{
      response.sendError(401);
    }
    document.closeConnection();
  }
%>
