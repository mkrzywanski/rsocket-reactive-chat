import React, { FC } from 'react';
import styles from './ChatMessagesFeed.module.css';

interface ChatMessagesFeedProps {}

const ChatMessagesFeed: FC<ChatMessagesFeedProps> = () => (
  <div className={styles.ChatMessagesFeed} data-testid="ChatMessagesFeed">
    ChatMessagesFeed Component
  </div>
);

export default ChatMessagesFeed;
