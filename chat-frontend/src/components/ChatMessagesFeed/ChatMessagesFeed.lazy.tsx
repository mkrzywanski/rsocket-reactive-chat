import React, { lazy, Suspense } from 'react';
import { ChatMessagesFeedProps } from './ChatMessagesFeed';

const LazyChatMessagesFeed = lazy(() => import('./ChatMessagesFeed'));

const ChatMessagesFeed = (props: ChatMessagesFeedProps) => (
  <Suspense fallback={null}>
    <LazyChatMessagesFeed {...props} />
  </Suspense>
);

export default ChatMessagesFeed;
