interface AuthResponse {
  accessToken: string;
  tokenType: string;
}

interface LoginCredentials {
  username: string;
  password: string;
}

const TOKEN_KEY = 'auth_token';

export const authService = {
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    const data = await response.json();
    localStorage.setItem(TOKEN_KEY, data.accessToken);
    return data;
  },

  setToken: (token: string) => {
    localStorage.setItem(TOKEN_KEY, token);
  },

  logout: () => {
    localStorage.removeItem(TOKEN_KEY);
  },

  getToken: (): string | null => {
    const token = localStorage.getItem(TOKEN_KEY);
    return token;
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem(TOKEN_KEY);
  },

  getAuthHeader: (): { Authorization: string } | {} => {
    const token = authService.getToken();
    if (!token) return {};
    return { Authorization: `Bearer ${token.trim()}` };
  },
}; 