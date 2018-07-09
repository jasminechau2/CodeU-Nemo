package codeu.controller;

import codeu.model.data.User;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.EventStore;
import codeu.model.data.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class NotificationServletTest{
  private NotificationServlet NotificationServlet;
  private HttpSession mockSession;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private EventStore mockEventStore;
  private UserStore mockUserStore;
  private User mockUser;


  @Before
  public void setup() {
    NotificationServlet = new NotificationServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);

    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/notifications.jsp"))
        .thenReturn(mockRequestDispatcher);

    mockEventStore = Mockito.mock(EventStore.class);
    NotificationServlet.setEventStore(mockEventStore);

    mockUserStore = Mockito.mock(UserStore.class);
    NotificationServlet.setUserStore(mockUserStore);

    mockUser = Mockito.mock(User.class);
  }


  @Test
  public void tesDoGet() throws IOException, ServletException {
    List<Event> fakeEventList = new ArrayList<>();

    UUID fakeUserId = UUID.randomUUID();
    User fakeUser = new User(fakeUserId, "testUser", "test_password_hash", Instant.ofEpochMilli(1000));
    List<String> userInformation = new ArrayList<>();
    userInformation.add(fakeUser.getName());
    Event userEvent = new Event(UUID.randomUUID(), "User", fakeUser.getCreationTime(), userInformation);
    fakeEventList.add(userEvent);

    UUID fakeConversationId = UUID.randomUUID();
    Conversation fakeConversation = new Conversation(fakeConversationId, fakeUserId, "testConversation", Instant.ofEpochMilli(2000));
    List<String> conversationInformation = new ArrayList<>();
    conversationInformation.add(fakeUser.getName());
    conversationInformation.add(fakeConversation.getTitle());
    Event conversationEvent = new Event(UUID.randomUUID(), "Conversation", fakeConversation.getCreationTime(), conversationInformation);
    fakeEventList.add(conversationEvent);

    Message fakeMessage = new Message(UUID.randomUUID(), fakeConversationId, fakeUserId, "testMessage", Instant.ofEpochMilli(3000));
    List<String> messageInformation = new ArrayList<>();
    messageInformation.add(fakeUser.getName());
    messageInformation.add(fakeConversation.getTitle());
    messageInformation.add(fakeMessage.getContent());
    Event messageEvent = new Event(UUID.randomUUID(), "Message", fakeMessage.getCreationTime(), messageInformation);
    fakeEventList.add(messageEvent);

    Instant fakeLastSeenTime = Instant.now();
    mockUser.setLastSeenNotifications(fakeLastSeenTime);


    Mockito.when(mockSession.getAttribute("user")).thenReturn("test_User");
    Mockito.when(mockUserStore.getUser("test_User")).thenReturn(mockUser);
    Mockito.when(mockEventStore.getEventsSince(fakeLastSeenTime)).thenReturn(fakeEventList);

    Mockito.when(mockUser.getLastSeenNotifications()).thenReturn(fakeLastSeenTime);
    Mockito.verify(mockUser).setLastSeenNotifications(fakeLastSeenTime);
    NotificationServlet.doGet(mockRequest, mockResponse);

    Mockito.when(mockEventStore.getAllEvents()).thenReturn(fakeEventList);
    Mockito.verify(mockRequest).setAttribute("eventsToShow", fakeEventList);

    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);


  }

}