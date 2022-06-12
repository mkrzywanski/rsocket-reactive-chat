/* eslint-disable */
import React from 'react';
import ChatWindow from './ChatWindow';

export default {
  title: "ChatWindow",
};

export const Default = () => <ChatWindow chats={new Set<string>()} setChats={() => {}} />;

Default.story = {
  name: 'default',
};
