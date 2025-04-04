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

// Helper function to check if a todo or its children have high priority
const hasHighPriorityChild = (todo: Todo, allTodos: Todo[]): boolean => {
  if (todo.priority === 'HIGH') return true;
  
  // Check children
  const children = allTodos.filter(t => t.parentId === todo.id);
  return children.some(child => hasHighPriorityChild(child, allTodos));
};

// Helper function to get all parent IDs of a todo
const getParentIds = (todo: Todo, allTodos: Todo[]): number[] => {
  const parentIds: number[] = [];
  let currentParentId = todo.parentId;
  
  while (currentParentId !== null) {
    parentIds.push(currentParentId);
    const parent = allTodos.find(t => t.id === currentParentId);
    if (!parent) break;
    currentParentId = parent.parentId;
  }
  
  return parentIds;
};

// Helper function to filter todos based on priority rules
export const filterTodos = (todos: Todo[]): Todo[] => {
  console.log('Starting filtering with todos:', todos);
  
  // First, collect all high priority todos and their parent IDs
  const highPriorityIds = new Set<number>();
  const parentIdsToShow = new Set<number>();
  
  todos.forEach(todo => {
    if (todo.priority === 'HIGH') {
      highPriorityIds.add(todo.id);
      // Add all parent IDs of high priority todos
      const parentIds = getParentIds(todo, todos);
      parentIds.forEach(id => parentIdsToShow.add(id));
    }
  });
  
  console.log('High priority IDs:', Array.from(highPriorityIds));
  console.log('Parent IDs to show:', Array.from(parentIdsToShow));
  
  // Create a map of parent to children for easier lookup
  const parentToChildren = new Map<number, Todo[]>();
  todos.forEach(todo => {
    if (todo.parentId) {
      const children = parentToChildren.get(todo.parentId) || [];
      children.push(todo);
      parentToChildren.set(todo.parentId, children);
    }
  });
  
  console.log('Parent to children map:', Object.fromEntries(parentToChildren));
  
  // Then filter the todos while maintaining hierarchy
  const filteredTodos: Todo[] = [];
  
  // Helper function to add a todo and its high priority children
  const addTodoAndHighPriorityChildren = (todo: Todo) => {
    console.log('Adding todo and its high priority children:', todo);
    filteredTodos.push(todo);
    
    // Add all high priority children
    const children = parentToChildren.get(todo.id) || [];
    console.log('Children of todo:', todo.id, children);
    children.forEach(child => {
      if (highPriorityIds.has(child.id)) {
        console.log('Adding high priority child:', child);
        filteredTodos.push(child);
      }
    });
  };
  
  // Process todos in order
  todos.forEach(todo => {
    // Skip if we've already added this todo
    if (filteredTodos.some(t => t.id === todo.id)) {
      console.log('Skipping already added todo:', todo);
      return;
    }
    
    // Show if it's high priority
    if (highPriorityIds.has(todo.id)) {
      console.log('Processing high priority todo:', todo);
      addTodoAndHighPriorityChildren(todo);
      return;
    }
    
    // Show if it's a parent of a high priority todo
    if (parentIdsToShow.has(todo.id)) {
      console.log('Processing parent of high priority todo:', todo);
      addTodoAndHighPriorityChildren(todo);
      return;
    }
    
    // Show if it's a top-level item that has high priority children
    if (!todo.parentId && hasHighPriorityChild(todo, todos)) {
      console.log('Processing top-level item with high priority children:', todo);
      addTodoAndHighPriorityChildren(todo);
      return;
    }
  });
  
  console.log('Final filtered todos:', filteredTodos);
  return filteredTodos;
};

// Helper function to calculate the nesting level of a todo
const calculateLevel = (todo: Todo, allTodos: Todo[]): number => {
  let level = 0;
  let currentTodo = todo;
  
  while (currentTodo.parentId) {
    level += 1;
    const parent = allTodos.find(t => t.id === currentTodo.parentId);
    if (!parent) break;
    currentTodo = parent;
  }
  
  console.log('Calculated level for todo:', todo.id, ':', level);
  return level;
};

export const ExecuteView: React.FC = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [filteredTodos, setFilteredTodos] = useState<Todo[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const inputRefs = useRef<{ [key: number]: HTMLInputElement | null }>({});

  const fetchTodos = async () => {
    try {
      setLoading(true);
      const data = await todoService.getAll();
      setTodos(data);
      setFilteredTodos(filterTodos(data));
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

  // Update filtered todos whenever todos change
  useEffect(() => {
    setFilteredTodos(filterTodos(todos));
  }, [todos]);

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
                {filteredTodos.map((todo, index) => (
                  <Draggable key={todo.id} draggableId={todo.id.toString()} index={index}>
                    {(provided) => (
                      <Box
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                      >
                        <TodoLine
                          todo={todo}
                          level={calculateLevel(todo, todos)}
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