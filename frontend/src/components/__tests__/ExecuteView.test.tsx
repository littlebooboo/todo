import { Todo } from '../../services/api';
import { filterTodos } from '../ExecuteView';

describe('ExecuteView filtering', () => {
  const createTodo = (id: number, title: string, priority: Todo['priority'], parentId: number | undefined = undefined): Todo => ({
    id,
    title,
    priority,
    completed: false,
    displayOrder: id,
    parentId,
    children: [],
  });

  it('should show high priority todos', () => {
    const todos = [
      createTodo(1, 'High Priority', 'HIGH'),
      createTodo(2, 'Low Priority', 'LOW'),
      createTodo(3, 'Medium Priority', 'MEDIUM'),
    ];

    const filtered = filterTodos(todos);
    expect(filtered).toHaveLength(1);
    expect(filtered[0].id).toBe(1);
  });

  it('should show parent of high priority todo', () => {
    const todos = [
      createTodo(1, 'Parent', 'LOW'),
      createTodo(2, 'High Priority Child', 'HIGH', 1),
    ];

    const filtered = filterTodos(todos);
    expect(filtered).toHaveLength(2);
    expect(filtered.map((t: Todo) => t.id).sort()).toEqual([1, 2]);
  });

  it('should show top-level item with high priority children', () => {
    const todos = [
      createTodo(1, 'Top Level', 'LOW'),
      createTodo(2, 'High Priority Child', 'HIGH', 1),
      createTodo(3, 'Another Top Level', 'LOW'),
    ];

    const filtered = filterTodos(todos);
    expect(filtered).toHaveLength(2);
    expect(filtered.map((t: Todo) => t.id).sort()).toEqual([1, 2]);
  });

  it('should show second top-level item with high priority children', () => {
    const todos = [
      createTodo(1, 'Top Level', 'LOW'),
      createTodo(2, 'Another Top Level', 'LOW'),
      createTodo(3, 'High Priority Child', 'HIGH', 2),
    ];

    const filtered = filterTodos(todos);
    expect(filtered).toHaveLength(2);
    expect(filtered.map((t: Todo) => t.id).sort()).toEqual([2, 3]);
  });

  it('should show nested high priority todos and their parents', () => {
    const todos = [
      createTodo(1, 'Top Level', 'LOW'),
      createTodo(2, 'Mid Level', 'LOW', 1),
      createTodo(3, 'High Priority', 'HIGH', 2),
    ];

    const filtered = filterTodos(todos);
    expect(filtered).toHaveLength(3);
    expect(filtered.map((t: Todo) => t.id).sort()).toEqual([1, 2, 3]);
  });

  it('should not show low priority items without high priority children', () => {
    const todos = [
      createTodo(1, 'Top Level', 'LOW'),
      createTodo(2, 'Child', 'LOW', 1),
      createTodo(3, 'Another Top Level', 'LOW'),
      createTodo(4, 'High Priority', 'HIGH'),
    ];

    const filtered = filterTodos(todos);
    expect(filtered).toHaveLength(1);
    expect(filtered[0].id).toBe(4);
  });
}); 