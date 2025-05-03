'use client';

import { authService } from '../../api/auth/services';

export const login = async (credentials: { email: string; password: string }) => {
  return authService.login(credentials.email, credentials.password);
};

export const register = async (userData: { name: string; email: string; password: string }) => {
  return authService.register(userData);
};