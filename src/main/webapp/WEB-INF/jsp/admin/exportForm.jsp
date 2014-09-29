<%@ include file="/WEB-INF/jsp/common/tags-head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/WEB-INF/jsp/common/meta-head.jsp"%>
</head>
<body>
    <%@ include file="/WEB-INF/jsp/common/fixed-top-ldod-header.jsp"%>
	
    <div class="container">
        <h1 class="text-center">
         EXPORTAR ARQUIVO
        </h1>
        <div class="row">
            <form class="form-inline" method="GET"
                action="${contextPath}/admin/export">
                <button type="submit" class="btn btn-primary">
                    <span class="glyphicon glyphicon-export"></span>
                    EXPORTAR
                </button>
            </form>
        </div>
    </div>

</body>
</html>
