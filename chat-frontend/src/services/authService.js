import apiClient from './apiClient';

const authService = {
  login: async (email, password) => {
    try {
      const response = await apiClient.post('/auth/login', { email, password });
      if (response.data.token) {
        localStorage.setItem('authToken', response.data.token);
        localStorage.setItem('userId', response.data.userId);
        localStorage.setItem('currentUser', JSON.stringify(response.data));
      }
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Login failed';
    }
  },

  signup: async (email, password, name) => {
    try {
      const response = await apiClient.post('/auth/signup', {
        email,
        password,
        name,
      });
      return response.data;
    } catch (error) {
      throw error.response?.data?.message || 'Signup failed';
    }
  },

  logout: () => {
    localStorage.clear();
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  },

  getToken: () => localStorage.getItem('authToken'),

  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },
};

export default authService;