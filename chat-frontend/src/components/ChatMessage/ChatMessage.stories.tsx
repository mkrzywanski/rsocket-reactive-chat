/* eslint-disable */
import React from 'react';
import ChatMessage from './ChatMessage';

export default {
  title: "ChatMessage",
};

export const Default = () => <ChatMessage message={{content: "a", chatRoomId : "z", usernameFrom: "a", time: new Date()}}/>;

Default.story = {
  name: 'default',
};
