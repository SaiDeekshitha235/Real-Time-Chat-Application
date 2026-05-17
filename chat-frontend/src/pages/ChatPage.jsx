import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import webSocketService from '../services/webSocketService';
import conversationService from '../services/conversationService';
import userService from '../services/userService';
import ConversationList from '../components/ConversationList';
import ChatWindow from '../components/ChatWindow';
import Navbar from '../components/Navbar';
import '../styles/Chat.css';

const ChatPage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [conversations, setConversations] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [loading, setLoading] = useState(true);

  // User picker state
  const [showUserPicker, setShowUserPicker] = useState(false);
  const [allUsers, setAllUsers] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    loadConversations();

    const token = localStorage.getItem('authToken');
    webSocketService.connect(token);

    return () => {
      webSocketService.disconnect();
    };
  }, [user, navigate]);

  const loadConversations = async () => {
    setLoading(true);
    try {
      const data = await conversationService.getMyConversations();
      setConversations(data);
      if (data.length > 0 && !selectedConversation) {
        setSelectedConversation(data[0]);
      }
    } catch (error) {
      console.error('Failed to load conversations:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenUserPicker = async () => {
    setShowUserPicker(true);
    setLoadingUsers(true);
    try {
      const users = await userService.getAllUsers();
      setAllUsers(users);
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setLoadingUsers(false);
    }
  };

  const handleStartConversation = async (otherUserId) => {
    try {
      const conversation = await conversationService.getOrCreateOneToOne(otherUserId);
      setShowUserPicker(false);
      await loadConversations();
      setSelectedConversation(conversation);
    } catch (error) {
      console.error('Failed to start conversation:', error);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="chat-page">
      <Navbar user={user} onLogout={handleLogout} />
      <div className="chat-container">
        <div className="conversations-panel">
          <button className="btn-new-chat" onClick={handleOpenUserPicker}>
            + New Chat
          </button>

          <ConversationList
            conversations={conversations}
            selectedConversation={selectedConversation}
            onSelectConversation={setSelectedConversation}
            loading={loading}
          />
        </div>

        {selectedConversation ? (
          <ChatWindow conversation={selectedConversation} user={user} />
        ) : (
          <div className="no-conversation">
            <p>Select a conversation to start chatting</p>
          </div>
        )}
      </div>

      {showUserPicker && (
        <div className="modal-overlay" onClick={() => setShowUserPicker(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Start a new chat</h3>
              <button
                className="modal-close"
                onClick={() => setShowUserPicker(false)}
              >
                ×
              </button>
            </div>
            <div className="modal-body">
              {loadingUsers ? (
                <p>Loading users...</p>
              ) : allUsers.length === 0 ? (
                <p>No other users found.</p>
              ) : (
                <ul className="user-list">
                  {allUsers.map((u) => (
                    <li
                      key={u.id}
                      className="user-list-item"
                      onClick={() => handleStartConversation(u.id)}
                    >
                      <div className="user-avatar">
                        {u.name.charAt(0).toUpperCase()}
                      </div>
                      <div className="user-info">
                        <div className="user-name">{u.name}</div>
                        <div className="user-email">{u.email}</div>
                      </div>
                      {u.status === 'online' && (
                        <span className="status-dot online" />
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ChatPage;