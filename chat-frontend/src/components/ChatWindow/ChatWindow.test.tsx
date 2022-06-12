import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import ChatWindow from './ChatWindow';

describe('<ChatWindow />', () => {
  test('it should mount', () => {
    render(<ChatWindow chats={new Set<string>()} setChats={() => {}}/>);
    
    const chatWindow = screen.getByTestId('ChatWindow');

    expect(chatWindow).toBeInTheDocument();
  });
});