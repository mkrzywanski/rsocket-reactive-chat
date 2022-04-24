import React, { FC } from 'react';
import styles from './ChatList.module.css';

export interface ChatInfo {

}

export interface ChatListProps {
  chatList : Array<ChatInfo>
}

const ChatList: FC<ChatListProps> = (props : ChatListProps) => (
  <div className={styles.ChatList} data-testid="ChatList">
    ChatList Component
  </div>
);

export default ChatList;
