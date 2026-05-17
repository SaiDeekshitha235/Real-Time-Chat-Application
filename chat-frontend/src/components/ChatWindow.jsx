import React, { useState, useEffect, useRef } from 'react';
import webSocketService from '../services/webSocketService';
import messageService from '../services/messageService';
import MessageList from './MessageList';
import MessageInput from './MessageInput';

const ChatWindow = ({ conversation, user }) => {
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    loadMessages();
    
    // Subscribe to new messages
    webSocketService.subscribeToConversation(
      conversation.id,
      (newMessage) => {
        setMessages((prev) => [...prev, newMessage]);
      }
    );

    return () => {
      webSocketService.unsubscribeFromConversation(conversation.id);
    };
  }, [conversation.id]);

  const loadMessages = async () => {
    setLoading(true);
    try {
      const data = await messageService.getMessages(conversation.id);
      setMessages(data || []);
    } catch (error) {
      console.error('Failed to load messages:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSendMessage = async (content) => {
    try {
      await messageService.sendMessage(conversation.id, 'text', content);
     
    } catch (error) {
      console.error('Failed to send message:', error);
    }
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="chat-window">
      <div className="chat-header">
        <h2>
              {conversation.participantNames
               ?.filter(name => name !== user.name)
               ?.join(', ') || conversation.name}
        </h2>
      </div>

      {loading ? (
        <div className="loading">Loading messages...</div>
      ) : (
        <>
          <MessageList messages={messages} currentUser={user} />
          <div ref={messagesEndRef} />
        </>
      )}

      <MessageInput onSendMessage={handleSendMessage} />
    </div>
  );
};

export default ChatWindow;