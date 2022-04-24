import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import ChatInputTextBox from './ChatInputTextBox';

describe('<ChatInputTextBox />', () => {
  test('it should mount', () => {
    render(<ChatInputTextBox />);
    
    const chatInputTextBox = screen.getByTestId('ChatInputTextBox');

    expect(chatInputTextBox).toBeInTheDocument();
  });
});