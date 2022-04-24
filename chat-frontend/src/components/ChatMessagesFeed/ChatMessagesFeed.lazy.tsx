import React, { lazy, Suspense } from 'react';

const LazyChatMessagesFeed = lazy(() => import('./ChatMessagesFeed'));

const ChatMessagesFeed = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    <LazyChatMessagesFeed {...props} />
  </Suspense>
);

export default ChatMessagesFeed;
