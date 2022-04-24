import React, { FC } from 'react';
import styles from './ChatList.module.css';

interface ChatInfo {

}

interface ChatListProps {
  chatList : Array<ChatInfo>
}

const ChatList = ({ chatList }: ChatListProps): JSX.Element => <div>{chatList}</div>;

// const ChatList: FC<ChatListProps> = () => (
//   <div className={styles.ChatList} data-testid="ChatList">
//     ChatList Component
//   </div>
// );

export default ChatList;
