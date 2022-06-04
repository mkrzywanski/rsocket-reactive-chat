import React, { FC, useState } from 'react';
import { ListGroup } from 'react-bootstrap';
import { Message } from '../../lib/chat-server-client/Message';
import ChatMessage from '../ChatMessage/ChatMessage';
import styles from './ChatMessagesFeed.module.css';

export interface ChatMessagesFeedProps {
  chatId: String
  messages : Array<Message>
}

const ChatMessagesFeed: FC<ChatMessagesFeedProps> = (props : ChatMessagesFeedProps) => {

  return (
  
  <div className={styles.ChatMessagesFeed} data-testid="ChatMessagesFeed">
    <ListGroup>
    {
      props.messages.map((item, index) => (
        <ChatMessage message={item}></ChatMessage>
      ))
    }
    </ListGroup>
  </div>
  );
}

export default ChatMessagesFeed;
