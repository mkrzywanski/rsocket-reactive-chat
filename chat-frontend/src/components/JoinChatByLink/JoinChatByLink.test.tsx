import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import JoinChatByLink from './JoinChatByLink';

describe('<JoinChatByLink />', () => {
  test('it should mount', () => {
    render(<JoinChatByLink />);
    
    const joinChatByLink = screen.getByTestId('JoinChatByLink');

    expect(joinChatByLink).toBeInTheDocument();
  });
});