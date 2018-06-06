<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.store.basic.UserStore" %>

<nav>
  <% String navBarUser = (String)request.getSession().getAttribute("user"); %>
  <% User ifAdmin = UserStore.getInstance().getUser(navBarUser);%>
  <a id="navTitle" href="/">CodeU Chat App - Nemo</a>
  <a href="/conversations">Conversations</a>
  <% if(navBarUser != null){ %>
    <a href="/profile/<%=navBarUser %>">
      Hello <%= navBarUser %>!</a>
  <% } else{ %>
    <a href="/login">Login</a>
  <% } %>
  <a href="/about.jsp">About</a>
  <a href="/activityfeed">Activity Feed</a>
  <%if(navBarUser != null && ifAdmin.getIsAdmin() == true){%>
      <a href="/admin"> Admin Page</a>
  <%}%>
</nav>
