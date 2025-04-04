import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { UserManagement } from './components/UserManagement';
import { LoginForm } from './components/LoginForm';
import { ProtectedRoute } from './components/ProtectedRoute';
import { PlanView } from './components/PlanView';
import { ExecuteView } from './components/ExecuteView';
import { authService } from './services/auth';

const theme = createTheme({
  palette: {
    mode: 'light',
  },
});

function App() {
  const isAuthenticated = authService.isAuthenticated();

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Routes>
          <Route
            path="/login"
            element={
              isAuthenticated ? (
                <Navigate to="/plan" replace />
              ) : (
                <LoginForm />
              )
            }
          />
          <Route
            path="/users"
            element={
              <ProtectedRoute>
                <UserManagement />
              </ProtectedRoute>
            }
          />
          <Route
            path="/plan"
            element={
              <ProtectedRoute>
                <PlanView />
              </ProtectedRoute>
            }
          />
          <Route
            path="/execute"
            element={
              <ProtectedRoute>
                <ExecuteView />
              </ProtectedRoute>
            }
          />
          <Route
            path="/"
            element={<Navigate to={isAuthenticated ? '/plan' : '/login'} replace />}
          />
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App;
