import React, { FC } from 'react';
import styles from './ChatWindow.module.css';

interface ChatWindowProps {}

const ChatWindow: FC<ChatWindowProps> = () => (
  <div className={styles.ChatWindow} data-testid="ChatWindow">
    ChatWindow Component
  </div>
);

export default ChatWindow;
