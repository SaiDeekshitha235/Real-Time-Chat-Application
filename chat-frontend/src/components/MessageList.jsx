import React from 'react';

const MessageList = ({ messages, currentUser }) => {
  if (!messages || messages.length === 0) {
    return (
      <div className="messages">
        <p className="empty-messages">No messages yet. Start the conversation!</p>
      </div>
    );
  }

  return (
    <div className="messages">
      {messages.map((message) => (
        <div
          key={message.id}
          className={`message ${
            message.senderId === currentUser?.userId ? 'sent' : 'received'
          }`}
        >
          <div className="message-content">
            {message.senderName && (
              <p className="sender-name">{message.senderName}</p>
            )}
            <p className="text">{message.content}</p>
            <p className="timestamp">
              {new Date(message.createdAt).toLocaleTimeString()}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default MessageList;