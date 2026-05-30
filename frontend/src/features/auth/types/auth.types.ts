export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  email: string;
  userId: number;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: string; // Por defecto lo enviaremos como "USER"
}