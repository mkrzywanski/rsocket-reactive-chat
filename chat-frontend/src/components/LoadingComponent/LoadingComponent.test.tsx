import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import LoadingComponent from './LoadingComponent';

describe('<LoadingComponent />', () => {
  test('it should mount', () => {
    render(<LoadingComponent />);
    
    const loadingComponent = screen.getByTestId('LoadingComponent');

    expect(loadingComponent).toBeInTheDocument();
  });
});