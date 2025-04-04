import axios from 'axios';
import { authService } from './auth';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth header to all requests
api.interceptors.request.use((config) => {
  const authHeader = authService.getAuthHeader();
  if (Object.keys(authHeader).length > 0) {
    config.headers = {
      ...config.headers,
      ...authHeader,
    } as any; // Type assertion needed due to Axios type limitations
  }
  return config;
});

export interface User {
  id: number;
  username: string;
  email: string;
  roles: string[];
}

export interface CreateUserDTO {
  username: string;
  email: string;
  password: string;
  roles: string[];
}

export interface Todo {
  id: number;
  title: string;
  description?: string;
  completed: boolean;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';  // 1: Not important, not urgent | 2: Not important, urgent | 3: Important, not urgent
  dueDate?: string;
  parentId?: number;
  projectId?: number;
  displayOrder: number;
  children: Todo[];
}

export interface CreateTodoDTO {
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';  // 1: Not important, not urgent | 2: Not important, urgent | 3: Important, not urgent
  dueDate?: string;
  parentId?: number;
  projectId?: number;
  displayOrder: number;
  completed?: boolean;
}

export const userService = {
  getAll: async (): Promise<User[]> => {
    const response = await api.get('/users');
    return response.data;
  },

  getById: async (id: number): Promise<User> => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  create: async (user: CreateUserDTO): Promise<User> => {
    const response = await api.post('/users', user);
    return response.data;
  },

  update: async (id: number, user: CreateUserDTO): Promise<User> => {
    const response = await api.put(`/users/${id}`, user);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`);
  },
};

export const todoService = {
  getAll: async (): Promise<Todo[]> => {
    const response = await api.get('/todos');
    return response.data;
  },

  getById: async (id: number): Promise<Todo> => {
    const response = await api.get(`/todos/${id}`);
    return response.data;
  },

  create: async (todo: CreateTodoDTO): Promise<Todo> => {
    const response = await api.post('/todos', todo);
    return response.data;
  },

  update: async (id: number, todo: Partial<CreateTodoDTO>): Promise<Todo> => {
    const response = await api.put(`/todos/${id}`, todo);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/todos/${id}`);
  },

  updateOrder: async (id: number, order: number): Promise<Todo> => {
    const response = await api.put(`/todos/${id}/order`, { order });
    return response.data;
  },

  updateParent: async (id: number, parentId: number | null): Promise<Todo> => {
    const response = await api.put(`/todos/${id}/parent`, { parentId });
    return response.data;
  },
}; 