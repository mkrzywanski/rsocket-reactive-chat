import React, { lazy, Suspense } from 'react';

import {ChatListProps} from './ChatList'
const LazyChatList = lazy(() => import('./ChatList'));

const ChatList = (props: ChatListProps) => (
  <Suspense fallback={null}>
    <LazyChatList {...props} />
  </Suspense>
);

export default ChatList;
