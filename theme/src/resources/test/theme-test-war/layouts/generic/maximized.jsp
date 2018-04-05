<%@ taglib uri="/WEB-INF/theme/portal-layout.tld" prefix="p" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <meta http-equiv="Content-Type" content="text/html;"/>
   <p:theme themeName="renaissance"/>
   <p:headerContent/>
</head>
<body id="body">
<p:region regionName='AJAXScripts' regionID='AJAXScripts'/>
<div id="portal-container">
   <div id="sizer">
      <div id="expander">
         <div id="content-container">
            <p:region regionName='maximized' regionID='regionMaximized'/>
         </div>
      </div>
   </div>
</div>
<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>
</body>
</html>
