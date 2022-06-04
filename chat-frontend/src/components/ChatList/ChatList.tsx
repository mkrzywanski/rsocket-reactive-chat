import React, { CSSProperties, FC } from 'react';
import styles from './ChatList.module.css';

export interface ChatInfo {

}

export interface ChatListProps {
  chatList: Set<string>
  chatOnClick : (chatId : string) => void
  footerheight? : number
}

const ChatList: FC<ChatListProps> = (props: ChatListProps) => {
  return (
    <div className={styles.ChatList} data-testid="ChatList" style={{overflowY: "auto", height: `calc(100% - ${props.footerheight}px)`}}>
      {
        Array.from(props.chatList).map((item, index) => (
          <div className="indent" key={index} onClick={e => props.chatOnClick(item)}>
            {item}
          </div>
        ))
      }
    </div>
  )

};

export default ChatList;
