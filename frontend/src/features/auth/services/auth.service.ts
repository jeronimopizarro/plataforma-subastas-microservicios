// Importamos 'apiClient' entre llaves porque es una exportación nombrada
import { apiClient } from '../../../shared/services/api';
import type { LoginRequest, AuthResponse, RegisterRequest } from '../types/auth.types';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    // Usamos apiClient en lugar de api
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    
    if (response.data.accessToken) {
      localStorage.setItem('token', response.data.accessToken);
      localStorage.setItem('userId', response.data.userId.toString());
      localStorage.setItem('email', response.data.email);
    }
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<string> => {
    // Usamos apiClient en lugar de api
    const response = await apiClient.post<string>('/auth/register', data);
    return response.data; 
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('email');
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  }
};