import React, { lazy, Suspense } from 'react';
import { ChatMessageProps } from './ChatMessage';

const LazyChatMessage = lazy(() => import('./ChatMessage'));

const ChatMessage = (props: ChatMessageProps) => (
  <Suspense fallback={null}>
    <LazyChatMessage {...props} />
  </Suspense>
);

export default ChatMessage;
