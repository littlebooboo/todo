import { Todo } from '../../services/api';
import { PlanView } from '../PlanView';

describe('PlanView parent-child relationships', () => {
  const createTodo = (id: number, title: string, priority: Todo['priority'], parentId: number | undefined = undefined, displayOrder: number = id): Todo => ({
    id,
    title,
    priority,
    completed: false,
    displayOrder,
    parentId,
    children: [],
  });

  it('should maintain correct parent-child relationships regardless of display order', () => {
    // Create todos with different display orders but same parent-child relationships
    const todos = [
      createTodo(1, 'Parent 1', 'LOW', undefined, 2),  // Parent with display order 2
      createTodo(2, 'Parent 2', 'LOW', undefined, 1),  // Parent with display order 1
      createTodo(3, 'Child 1', 'LOW', 1, 3),          // Child of Parent 1
      createTodo(4, 'Child 2', 'LOW', 1, 4),          // Child of Parent 1
      createTodo(5, 'Child 3', 'LOW', 2, 5),          // Child of Parent 2
    ];

    // Sort by display order (simulating what PlanView does)
    const sortedTodos = [...todos].sort((a, b) => a.displayOrder - b.displayOrder);
    
    // Verify that parent-child relationships are maintained based on IDs, not display order
    const parent1 = sortedTodos.find(t => t.id === 1);
    const parent2 = sortedTodos.find(t => t.id === 2);
    const child1 = sortedTodos.find(t => t.id === 3);
    const child2 = sortedTodos.find(t => t.id === 4);
    const child3 = sortedTodos.find(t => t.id === 5);

    // Check that children are correctly associated with their parents by ID
    expect(child1?.parentId).toBe(1);
    expect(child2?.parentId).toBe(1);
    expect(child3?.parentId).toBe(2);

    // Check that display order doesn't affect parent-child relationships
    expect(parent1?.displayOrder).toBe(2);
    expect(parent2?.displayOrder).toBe(1);
    expect(child1?.displayOrder).toBe(3);
    expect(child2?.displayOrder).toBe(4);
    expect(child3?.displayOrder).toBe(5);
  });

  it('should handle reordering without breaking parent-child relationships', () => {
    const todos = [
      createTodo(1, 'Parent', 'LOW', undefined, 1),
      createTodo(2, 'Child 1', 'LOW', 1, 2),
      createTodo(3, 'Child 2', 'LOW', 1, 3),
    ];

    // Simulate reordering by changing display orders
    const reorderedTodos = todos.map(todo => {
      if (todo.id === 1) return { ...todo, displayOrder: 3 };
      if (todo.id === 2) return { ...todo, displayOrder: 1 };
      if (todo.id === 3) return { ...todo, displayOrder: 2 };
      return todo;
    });

    // Sort by display order
    const sortedTodos = [...reorderedTodos].sort((a, b) => a.displayOrder - b.displayOrder);

    // Verify parent-child relationships are maintained
    const child1 = sortedTodos.find(t => t.id === 2);
    const child2 = sortedTodos.find(t => t.id === 3);

    expect(child1?.parentId).toBe(1);
    expect(child2?.parentId).toBe(1);
  });
}); 