import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import ChatMessage from './ChatMessage';

describe('<ChatMessage />', () => {
  test('it should mount', () => {
    render(<ChatMessage message={{content: "a", chatRoomId : "z", usernameFrom: "a", time: new Date()}}/>);
    
    const chatMessage = screen.getByTestId('ChatMessage');

    expect(chatMessage).toBeInTheDocument();
  });
});