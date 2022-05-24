import React, { FC, useState } from 'react';
import { Message } from '../../lib/chat-server-client/Message';
import styles from './ChatMessagesFeed.module.css';

export interface ChatMessagesFeedProps {
  chatId: String
  messages : Array<Message>
}

const ChatMessagesFeed: FC<ChatMessagesFeedProps> = (props : ChatMessagesFeedProps) => {

  return (
  <div className={styles.ChatMessagesFeed} data-testid="ChatMessagesFeed">
    {
      props.messages.map((item, index) => (
        <div className="indent" key={index}>
          {item.content}
        </div>
      ))
    }
  </div>
  );
}

export default ChatMessagesFeed;
