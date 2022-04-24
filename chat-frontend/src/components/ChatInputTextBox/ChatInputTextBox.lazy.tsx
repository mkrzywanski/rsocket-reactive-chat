import React, { lazy, Suspense } from 'react';

const LazyChatInputTextBox = lazy(() => import('./ChatInputTextBox'));

const ChatInputTextBox = (props: JSX.IntrinsicAttributes & { children?: React.ReactNode; }) => (
  <Suspense fallback={null}>
    <LazyChatInputTextBox {...props} />
  </Suspense>
);

export default ChatInputTextBox;
