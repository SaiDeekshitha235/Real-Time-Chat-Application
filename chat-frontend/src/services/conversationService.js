import apiClient from './apiClient';

const conversationService = {
  getMyConversations: async () => {
    try {
      const response = await apiClient.get('/conversations/my-conversations');
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Failed to fetch conversations';
    }
  },

  getOrCreateOneToOne: async (userId) => {
    try {
      const response = await apiClient.post('/conversations/one-to-one', {
        userId,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Failed to create conversation';
    }
  },

  createGroup: async (name, memberIds) => {
    try {
      const response = await apiClient.post('/conversations/group', {
        name,
        memberIds,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Failed to create group';
    }
  },

  addParticipant: async (conversationId, userId) => {
    try {
      const response = await apiClient.post(
        `/conversations/${conversationId}/participants/${userId}`
      );
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Failed to add participant';
    }
  },
  
};

export default conversationService;