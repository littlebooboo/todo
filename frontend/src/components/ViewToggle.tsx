import React from 'react';
import { Box, ToggleButton, ToggleButtonGroup, Typography } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';

export const ViewToggle: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const currentView = location.pathname === '/plan' ? 'plan' : 'execute';

  const handleViewChange = (
    event: React.MouseEvent<HTMLElement>,
    newView: string | null,
  ) => {
    if (newView !== null) {
      navigate(`/${newView}`);
    }
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
      <ToggleButtonGroup
        value={currentView}
        exclusive
        onChange={handleViewChange}
        aria-label="view selection"
      >
        <ToggleButton value="plan" aria-label="plan view">
          Plan
        </ToggleButton>
        <ToggleButton value="execute" aria-label="execute view">
          Execute
        </ToggleButton>
      </ToggleButtonGroup>
    </Box>
  );
}; 