import apiClient from './apiClient';

const messageService = {
  getMessages: async (conversationId) => {
    try {
      const response = await apiClient.get(
        `/messages/conversation/${conversationId}`
      );
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Failed to fetch messages';
    }
  },

  sendMessage: async (conversationId, type, content) => {
    try {
      const response = await apiClient.post('/messages', {
        conversationId,
        type,
        content,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Failed to send message';
    }
  },

  markAsRead: async (messageId) => {
    try {
      const response = await apiClient.put(`/messages/${messageId}/read`);
      return response.data;
    } catch (error) {
      console.error('Failed to mark message as read', error);
    }
  },

  markConversationAsRead: async (conversationId) => {
    try {
      const response = await apiClient.put(
        `/messages/conversation/${conversationId}/read-all`
      );
      return response.data;
    } catch (error) {
      console.error('Failed to mark conversation as read', error);
    }
  },
};

export default messageService;