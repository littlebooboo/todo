import React, { useState } from 'react';
import {
  Box,
  Button,
  TextField,
  Paper,
  Typography,
  Alert,
  CircularProgress,
} from '@mui/material';
import { userService, CreateUserDTO } from '../services/api';

interface UserRegistrationFormProps {
  onSuccess: () => void;
}

export const UserRegistrationForm: React.FC<UserRegistrationFormProps> = ({ onSuccess }) => {
  const [formData, setFormData] = useState<CreateUserDTO>({
    username: '',
    email: '',
    password: '',
    roles: ['ROLE_USER'],
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await userService.create(formData);
      setFormData({
        username: '',
        email: '',
        password: '',
        roles: ['ROLE_USER'],
      });
      onSuccess();
    } catch (err) {
      setError('Failed to create user. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Paper sx={{ p: 3, maxWidth: 400, mx: 'auto', mt: 4 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Register New User
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <form onSubmit={handleSubmit}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
            fullWidth
          />
          <TextField
            label="Email"
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            required
            fullWidth
          />
          <TextField
            label="Password"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
            required
            fullWidth
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            disabled={loading}
            fullWidth
          >
            {loading ? <CircularProgress size={24} /> : 'Register'}
          </Button>
        </Box>
      </form>
    </Paper>
  );
}; 