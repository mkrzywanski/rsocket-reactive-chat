import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import ChatMessagesFeed from './ChatMessagesFeed';

describe('<ChatMessagesFeed />', () => {
  test('it should mount', () => {
    render(<ChatMessagesFeed />);
    
    const chatMessagesFeed = screen.getByTestId('ChatMessagesFeed');

    expect(chatMessagesFeed).toBeInTheDocument();
  });
});