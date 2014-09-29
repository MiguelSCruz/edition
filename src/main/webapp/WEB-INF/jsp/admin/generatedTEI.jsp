<%@ include file="/WEB-INF/jsp/common/tags-head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/common/meta-head.jsp"%>
</head>
<body>
    <%@ include file="/WEB-INF/jsp/common/fixed-top-ldod-header.jsp"%>
	
	<script type="text/javascript">
		var xmlcontent = decodeURIComponent(escape(window.atob("${generator.getBase64XMLResult()}")));
		var blob = new Blob([xmlcontent], {type: "text/plain;charset=utf-8"});
		var file = "tei.xml";
	</script>
	
	
    <div class="container">
        <h1 class="text-center">
         TEI GERADO
        </h1>
       ${generator.getWriter().getResult()} 
       <br><br><br>
        <form class="form-inline" method="GET"
                action="${contextPath}/admin/downloadTei">
                <button type="submit" class="btn btn-primary">
                    <span class="glyphicon glyphicon-export"></span>
                    EXPORTAR
                </button>
          </form>
       <br>
       <a  onclick="saveAs(blob, file)" href="#">export</a>
        <pre>${generator.getHTMLXMLResult()}</pre>
       
       <br><br><br><br>
    </div>
</body>
</html>
