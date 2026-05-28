import type { LoginRequest, AuthResponse } from '../types/auth.types';
import { apiClient } from '../../../shared/services/api';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    // Hacemos el POST al Gateway
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    
    // Guardamos los datos mockeados en el navegador
    const data = response.data;
    localStorage.setItem('token', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('username', data.username);
    
    return data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('username');
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  }
};