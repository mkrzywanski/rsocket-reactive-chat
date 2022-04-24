import React, { lazy, Suspense } from 'react';

const LazyChatWindow = lazy(() => import('./ChatWindow'));

const ChatWindow = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    <LazyChatWindow {...props} />
  </Suspense>
);

export default ChatWindow;
