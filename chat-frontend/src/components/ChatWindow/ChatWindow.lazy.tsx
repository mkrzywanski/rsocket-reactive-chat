import React, { lazy, Suspense } from 'react';
import { ChatWindowProps } from './ChatWindow';

const LazyChatWindow = lazy(() => import('./ChatWindow'));

const ChatWindow = (props: ChatWindowProps) => (
  <Suspense fallback={null}>
    <LazyChatWindow {...props} />
  </Suspense>
);

export default ChatWindow;
