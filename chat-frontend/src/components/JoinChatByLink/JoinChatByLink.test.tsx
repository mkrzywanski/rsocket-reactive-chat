import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import JoinChatByLink from './JoinChatByLink';
import { MemoryRouter, Router } from 'react-router-dom';

describe('<JoinChatByLink />', () => {
  test('it should mount', () => {
    render(<MemoryRouter><JoinChatByLink addChat={() => {}}/></MemoryRouter>);
    
    const joinChatByLink = screen.getByTestId('JoinChatByLink');

    expect(joinChatByLink).toBeInTheDocument();
  });
});