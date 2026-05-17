
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.callbacks = {};
    this.subscriptions = {};
  }

  connect(token) {
    if (this.connected) {
      console.log('Already connected');
      return;
    }

    try {
      const socket = new SockJS(WS_URL);
      this.stompClient = Stomp.over(socket);
      
      // Disable debug logs in production
      this.stompClient.debug = () => {};

      const headers = {
        'Authorization': `Bearer ${token}`,
      };

      this.stompClient.connect(
        headers,
        (frame) => {
          console.log('✓ WebSocket connected successfully');
          this.connected = true;

          // Subscribe to personal message queue
          this.stompClient.subscribe('/user/queue/messages', (message) => {
            try {
              const msg = JSON.parse(message.body);
              if (this.callbacks.onMessageReceived) {
                this.callbacks.onMessageReceived(msg);
              }
            } catch (error) {
              console.error('Error parsing message:', error);
            }
          });

          // Subscribe to user status changes
          this.stompClient.subscribe('/topic/user-status', (message) => {
            try {
              const status = JSON.parse(message.body);
              if (this.callbacks.onUserStatusChanged) {
                this.callbacks.onUserStatusChanged(status);
              }
            } catch (error) {
              console.error('Error parsing status:', error);
            }
          });

          console.log('Subscribed to message queues');
        },
        (error) => {
          console.error('✗ WebSocket connection failed:', error);
          this.connected = false;
          
          // Retry connection after 5 seconds
          setTimeout(() => {
            if (!this.connected) {
              console.log('Attempting to reconnect...');
              this.connect(token);
            }
          }, 5000);
        }
      );
    } catch (error) {
      console.error('Error during WebSocket setup:', error);
    }
  }

  // Send a message to a conversation
  sendMessage(conversationId, content, type = 'text') {
    if (!this.stompClient?.connected) {
      console.error('✗ WebSocket not connected. Cannot send message');
      return false;
    }

    try {
      this.stompClient.send('/app/message', {}, JSON.stringify({
        conversationId,
        content,
        type,
        timestamp: new Date().toISOString(),
      }));
      return true;
    } catch (error) {
      console.error('Error sending message:', error);
      return false;
    }
  }

  // Register callback for incoming messages
  onMessageReceived(callback) {
    this.callbacks.onMessageReceived = callback;
  }

  // Register callback for user status changes
  onUserStatusChanged(callback) {
    this.callbacks.onUserStatusChanged = callback;
  }

  // Send typing indicator
  sendTyping(conversationId, userId) {
    if (!this.stompClient?.connected) {
      console.error('WebSocket not connected');
      return;
    }

    try {
      this.stompClient.send('/app/typing', {}, JSON.stringify({
        conversationId,
        userId,
        timestamp: new Date().toISOString(),
      }));
    } catch (error) {
      console.error('Error sending typing indicator:', error);
    }
  }

  // Register callback for typing indicators
  onTyping(callback) {
    this.callbacks.onTyping = callback;

    if (this.stompClient?.connected) {
      this.stompClient.subscribe('/topic/typing', (message) => {
        try {
          const data = JSON.parse(message.body);
          callback(data);
        } catch (error) {
          console.error('Error parsing typing data:', error);
        }
      });
    }
  }

  // Subscribe to messages from a specific conversation
subscribeToConversation(conversationId, callback) {
  // If not connected yet, retry every 500ms until connected
  if (!this.stompClient?.connected) {
    console.log(`⏳ WS not ready, retrying subscription to conversation ${conversationId}...`);
    setTimeout(() => this.subscribeToConversation(conversationId, callback), 500);
    return;
  }

  // Don't double-subscribe to the same conversation
  if (this.subscriptions[conversationId]) {
    console.log(`Already subscribed to conversation ${conversationId}`);
    return;
  }

  try {
    const subscription = this.stompClient.subscribe(
      `/topic/conversation/${conversationId}`,
      (message) => {
        try {
          const msg = JSON.parse(message.body);
          console.log(`📨 Received message on conversation ${conversationId}:`, msg);
          callback(msg);
        } catch (error) {
          console.error('Error parsing conversation message:', error);
        }
      }
    );

    this.subscriptions[conversationId] = subscription;
    console.log(`✓ Subscribed to conversation ${conversationId}`);
  } catch (error) {
    console.error('Error subscribing to conversation:', error);
  }
}

  // Unsubscribe from a conversation
  unsubscribeFromConversation(conversationId) {
    if (this.subscriptions[conversationId]) {
      this.subscriptions[conversationId].unsubscribe();
      delete this.subscriptions[conversationId];
      console.log(`Unsubscribed from conversation ${conversationId}`);
    }
  }

  // Update user presence status
  updateUserPresence(status) {
    if (!this.stompClient?.connected) {
      console.error('WebSocket not connected');
      return;
    }

    try {
      this.stompClient.send('/app/presence', {}, JSON.stringify({
        status,
        timestamp: new Date().toISOString(),
      }));
    } catch (error) {
      console.error('Error updating presence:', error);
    }
  }

  // Get connection status
  isConnected() {
    return this.connected && this.stompClient?.connected;
  }

  // Disconnect from WebSocket
  disconnect() {
    if (this.stompClient?.connected) {
      // Unsubscribe from all conversations
      Object.keys(this.subscriptions).forEach(conversationId => {
        this.unsubscribeFromConversation(conversationId);
      });

      this.stompClient.disconnect(() => {
        this.connected = false;
        console.log('✓ WebSocket disconnected');
      });
    }
  }

  // Reconnect if connection was lost
  reconnect(token) {
    if (!this.connected) {
      this.connect(token);
    }
  }
}

export default new WebSocketService();