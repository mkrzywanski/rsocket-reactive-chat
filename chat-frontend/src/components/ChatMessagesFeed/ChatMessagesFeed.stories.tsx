/* eslint-disable */
import React from 'react';
import ChatMessagesFeed from './ChatMessagesFeed';

export default {
  title: "ChatMessagesFeed",
};

export const Default = () => <ChatMessagesFeed chatId={""} messages={[]}/>;

Default.story = {
  name: 'default',
};
