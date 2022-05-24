import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import ChatList from './ChatList';

describe('<ChatList />', () => {
  test('it should mount', () => {
    render(<ChatList chatList={new Set<string>()} chatOnClick={(e) => {}}/>);
    
    const chatList = screen.getByTestId('ChatList');

    expect(chatList).toBeInTheDocument();
  });
});