import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import WelcomePage from './WelcomePage';

describe('<WelcomePage />', () => {
  test('it should mount', () => {
    render(<WelcomePage />);
    
    const welcomePage = screen.getByTestId('WelcomePage');

    expect(welcomePage).toBeInTheDocument();
  });
});