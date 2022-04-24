import React, { FC } from 'react';
import styles from './ChatList.module.css';

export interface ChatInfo {

}

export interface ChatListProps {
  chatList : Array<ChatInfo>
}

const ChatList = ({ chatList }: ChatListProps): JSX.Element => <div></div>;

// const ChatList: FC<ChatListProps> = () => (
//   <div className={styles.ChatList} data-testid="ChatList">
//     ChatList Component
//   </div>
// );

export default ChatList;
