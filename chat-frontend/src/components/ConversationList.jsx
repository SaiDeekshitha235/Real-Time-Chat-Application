import React from 'react';

const ConversationList = ({
  conversations,
  selectedConversation,
  onSelectConversation,
  loading,
}) => {
  if (loading) {
    return (
      <div className="conversation-list">
        <p className="loading">Loading conversations...</p>
      </div>
    );
  }

  return (
    <div className="conversation-list">
      <h2>Conversations</h2>
      {conversations.length === 0 ? (
        <p className="empty">No conversations yet</p>
      ) : (
        <ul>
          {conversations.map((conv) => (
            <li
              key={conv.id}
              className={`conversation-item ${
                selectedConversation?.id === conv.id ? 'active' : ''
              }`}
              onClick={() => onSelectConversation(conv)}
            >
              <div className="conversation-info">
                <h3>{conv.name}</h3>
                <p className="last-message">
                  {conv.lastMessage || 'No messages yet'}
                </p>
              </div>
              {conv.unreadCount > 0 && (
                <span className="badge">{conv.unreadCount}</span>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ConversationList;