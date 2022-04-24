import React, { lazy, Suspense } from 'react';

const LazyChatList = lazy(() => import('./ChatList'));

const ChatList = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    <LazyChatList {...props} />
  </Suspense>
);

export default ChatList;
