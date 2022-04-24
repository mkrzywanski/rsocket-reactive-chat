import React, { lazy, Suspense } from 'react';
import {ChatInputTextBoxProps} from './ChatInputTextBox'

const LazyChatInputTextBox = lazy(() => import('./ChatInputTextBox'));

const ChatInputTextBox = (props: ChatInputTextBoxProps) => (
  <Suspense fallback={null}>
    <LazyChatInputTextBox {...props} />
  </Suspense>
);

export default ChatInputTextBox;
