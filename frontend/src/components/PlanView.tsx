import React, { useState, useEffect, useRef, KeyboardEvent } from 'react';
import {
  Box,
  TextField,
  Paper,
  CircularProgress,
  Alert,
  IconButton,
  Checkbox,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import DeleteIcon from '@mui/icons-material/Delete';
import { todoService, Todo, CreateTodoDTO } from '../services/api';
import { ViewToggle } from './ViewToggle';

interface TodoLineProps {
  todo: Todo;
  level: number;
  onUpdate: (id: number, updates: Partial<CreateTodoDTO>) => Promise<void>;
  onDelete: (id: number) => Promise<void>;
  onIndent: (id: number, direction: 'in' | 'out') => Promise<void>;
  onKeyDown: (e: KeyboardEvent<HTMLInputElement>, id: number) => void;
}

const TodoLine: React.FC<TodoLineProps> = ({
  todo,
  level,
  onUpdate,
  onDelete,
  onIndent,
  onKeyDown,
}) => {
  const [title, setTitle] = useState(todo.title);
  const [priority, setPriority] = useState(todo.priority);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setTitle(todo.title);
    setPriority(todo.priority);
  }, [todo.title, todo.priority]);

  const handleTitleChange = async (newTitle: string) => {
    setTitle(newTitle);
    await onUpdate(todo.id, { title: newTitle });
  };

  const handlePriorityChange = async (newPriority: Todo['priority']) => {
    setPriority(newPriority);
    await onUpdate(todo.id, { priority: newPriority });
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    onKeyDown(e, todo.id);
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', ml: level * 2, gap: 1 }}>
      <Checkbox
        checked={todo.completed}
        onChange={(e) => onUpdate(todo.id, { completed: e.target.checked })}
      />
      <TextField
        inputRef={inputRef}
        value={title}
        onChange={(e) => handleTitleChange(e.target.value)}
        onKeyDown={handleKeyDown}
        variant="standard"
        fullWidth
        sx={{ textDecoration: todo.completed ? 'line-through' : 'none' }}
      />
      <FormControl size="small" sx={{ minWidth: 120 }}>
        <InputLabel>Priority</InputLabel>
        <Select
          value={priority}
          label="Priority"
          onChange={(e) => handlePriorityChange(e.target.value as Todo['priority'])}
        >
          <MenuItem value="LOW">Low (1)</MenuItem>
          <MenuItem value="MEDIUM">Medium (2)</MenuItem>
          <MenuItem value="HIGH">High (3)</MenuItem>
        </Select>
      </FormControl>
      <IconButton onClick={() => onDelete(todo.id)} size="small">
        <DeleteIcon />
      </IconButton>
    </Box>
  );
};

export const PlanView: React.FC = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const inputRefs = useRef<{ [key: number]: HTMLInputElement | null }>({});

  const fetchTodos = async () => {
    try {
      setLoading(true);
      const data = await todoService.getAll();
      setTodos(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch todos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTodos();
  }, []);

  const handleCreateTodo = async (parentId?: number) => {
    const newTodo: CreateTodoDTO = {
      title: '',
      priority: 'MEDIUM',
      displayOrder: todos.length,
      parentId,
    };

    try {
      const created = await todoService.create(newTodo);
      setTodos([...todos, created]);
      setSelectedId(created.id);
      // Focus the new input after a brief delay to ensure it's rendered
      setTimeout(() => {
        inputRefs.current[created.id]?.focus();
      }, 0);
    } catch (err) {
      setError('Failed to create todo');
      console.error(err);
    }
  };

  const handleUpdateTodo = async (id: number, updates: Partial<CreateTodoDTO>) => {
    try {
      const todo = todos.find(t => t.id === id);
      if (!todo) return;

      // Include all required fields in the update
      const fullUpdates: CreateTodoDTO = {
        title: todo.title,
        priority: todo.priority,
        displayOrder: todo.displayOrder,
        parentId: todo.parentId,  // Include the parentId to maintain the hierarchy
        ...updates
      };

      const updated = await todoService.update(id, fullUpdates);
      setTodos(todos.map(todo => 
        todo.id === id ? updated : todo
      ));
    } catch (err) {
      setError('Failed to update todo');
      console.error(err);
    }
  };

  const handleDeleteTodo = async (id: number) => {
    try {
      await todoService.delete(id);
      setTodos(todos.filter(todo => todo.id !== id));
    } catch (err) {
      setError('Failed to delete todo');
      console.error(err);
    }
  };

  const handleIndent = async (id: number, direction: 'in' | 'out') => {
    try {
      const todo = todos.find(t => t.id === id);
      if (!todo) return;

      const currentParentId = todo.parentId;
      const newParentId = direction === 'in' 
        ? todo.parentId || id 
        : todos.find(t => t.id === currentParentId)?.parentId || null;

      await todoService.updateParent(id, newParentId);
      await fetchTodos(); // Refresh to get updated structure
    } catch (err) {
      setError('Failed to update todo hierarchy');
      console.error(err);
    }
  };

  const handleKeyDown = async (e: KeyboardEvent<HTMLInputElement>, id: number) => {
    const todo = todos.find(t => t.id === id);
    if (!todo) return;

    switch (e.key) {
      case 'Enter':
        e.preventDefault();
        await handleCreateTodo(todo.parentId);
        break;
      case 'Tab':
        e.preventDefault();
        await handleIndent(id, e.shiftKey ? 'out' : 'in');
        break;
      case 'Backspace':
        if (todo.title === '') {
          e.preventDefault();
          await handleDeleteTodo(id);
          // Focus the previous todo's input
          const currentIndex = todos.findIndex(t => t.id === id);
          if (currentIndex > 0) {
            const prevTodo = todos[currentIndex - 1];
            inputRefs.current[prevTodo.id]?.focus();
          }
        }
        break;
    }
  };

  const onDragEnd = async (result: DropResult) => {
    if (!result.destination) return;

    const items = Array.from(todos);
    const [reorderedItem] = items.splice(result.source.index, 1);
    items.splice(result.destination.index, 0, reorderedItem);

    setTodos(items);

    try {
      await todoService.updateOrder(reorderedItem.id, result.destination.index);
    } catch (err) {
      setError('Failed to update todo order');
      console.error(err);
      await fetchTodos(); // Refresh to revert changes
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <ViewToggle />

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2 }}>
        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="todos">
            {(provided) => (
              <Box {...provided.droppableProps} ref={provided.innerRef}>
                {todos.map((todo, index) => (
                  <Draggable key={todo.id} draggableId={todo.id.toString()} index={index}>
                    {(provided) => (
                      <Box
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                      >
                        <TodoLine
                          todo={todo}
                          level={todo.parentId ? 1 : 0}
                          onUpdate={handleUpdateTodo}
                          onDelete={handleDeleteTodo}
                          onIndent={handleIndent}
                          onKeyDown={handleKeyDown}
                        />
                      </Box>
                    )}
                  </Draggable>
                ))}
                {provided.placeholder}
              </Box>
            )}
          </Droppable>
        </DragDropContext>
      </Paper>
    </Box>
  );
}; 