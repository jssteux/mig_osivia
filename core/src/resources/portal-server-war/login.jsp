<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ JBoss, a division of Red Hat                                             ~
  ~ Copyright 2009, Red Hat Middleware, LLC, and individual
  ~ contributors as indicated by the @authors tag. See the
  ~ copyright.txt in the distribution for a full listing of
  ~ individual contributors.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.                 ~
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@ page pageEncoding="utf-8" %>
<%@page import="org.jboss.portal.identity.UserStatus" %>
<%@page import="java.util.ResourceBundle" %>

<%
    ResourceBundle rb = ResourceBundle.getBundle("Resource", request.getLocale());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><%= rb.getString("LOGIN_TITLE")%>
    </title>

    <script type="text/javascript" src="/portal-core/js/login.js"></script>

    <link rel="stylesheet" href="/portal-core/css/login.css" type="text/css"/>
</head>
<body onload="JBossPortal.login.setFocusOnLoginForm();JBossPortal.login.displayStatus();">

<div class="login-container">


    <div class="login-header">
        <%--text title--%>
        <%--<h2><%= rb.getString("LOGIN_TITLE") %></h2>--%>
        <%--img title--%>
        <img src="/portal-core/images/logo-small-blue.gif" width="118px" height="17px" alt=""/>
    </div>
    <div class="login-content" id="login-content">
        <div class="error-message" id="login-error-message"
             style="<%=(request.getAttribute(!UserStatus.OK.equals("org.jboss.portal.userStatus") ? "" : "display:none"))%>;">
            <%

                if (UserStatus.DISABLE.equals(request.getAttribute("org.jboss.portal.userStatus"))) {
                    out.println(rb.getString("ACCOUNT_DISABLED"));
                } else if (UserStatus.WRONGPASSWORD.equals(request.getAttribute("org.jboss.portal.userStatus")) || UserStatus.UNEXISTING.equals(request.getAttribute("org.jboss.portal.userStatus"))) {
                    out.println(rb.getString("ACCOUNT_INEXISTING_OR_WRONG_PASSWORD"));
                } else if (UserStatus.NOTASSIGNEDTOROLE.equals(request.getAttribute("org.jboss.portal.userStatus"))) {
                    out.println(rb.getString("ACCOUNT_NOTASSIGNEDTOROLE"));
                }
            %>
        </div>
        <form method="post" action="<%= response.encodeURL("j_security_check") %>" name="loginform" id="loginForm"
              target="_parent" style="margin:0;padding:0">
            <div class="form-field">
                <label for="j_username"><%= rb.getString("LOGIN_USERNAME") %>
                </label>
                <input type="text" style="width:155px;" name="j_username" id="j_username" value="" onFocus="JBossPortal.login.highlight('j_username');" onblur="JBossPortal.login.highlight('j_username');"/>
            </div>
            <div class="form-field">
                <label for="j_password"><%= rb.getString("LOGIN_PASSWORD") %>
                </label>
                <input type="password" style="width:155px;" name="j_password" id="j_password" value="" onFocus="JBossPortal.login.highlight('j_password');" onblur="JBossPortal.login.highlight('j_password');"/>
            </div>
            <br class="clear"/>

            <div class="button-container">
                <br class="clear"/>
                <input type="button" name="cancel" id="login-cancel"
                       value="<%= rb.getString("LOGIN_CANCEL")  %>" class="cancel-button"
                       onclick="window.parent.hideContentModal('login-modal');"/>
                <br class="clear"/>
                <input type="submit" name="login" id="login-submit"
                       value="<%= rb.getString("LOGIN_SUBMIT")  %>" class="login-button"/>
            </div>
        </form>

    </div>
</div>
</body>
</html>
