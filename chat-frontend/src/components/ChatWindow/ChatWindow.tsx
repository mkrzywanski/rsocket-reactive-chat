import React, { FC, useState } from 'react';
import ChatInputTextBox from '../ChatInputTextBox/ChatInputTextBox.lazy';
import ChatList from '../ChatList/ChatList';
import ChatMessagesFeed from '../ChatMessagesFeed/ChatMessagesFeed';
import styles from './ChatWindow.module.css';
import { RSocketClient } from 'rsocket-core';
import RSocketWebsocketClient from 'rsocket-websocket-client';


interface ChatWindowProps {}

const ChatWindow: FC<ChatWindowProps> = (props : ChatWindowProps) => {
  
  const [chats, setChats] = useState(new Array(0))

  const client= new RSocketClient(
    {
      setup: {
        dataMimeType: 'text/plain',
        keepAlive: 1000000, // avoid sending during test
        lifetime: 100000,
        metadataMimeType: 'text/plain',
      },
      transport: new RSocketWebsocketClient({
        url: ""
      }),
    }
  );

  return (
  <div className={styles.ChatWindow} data-testid="ChatWindow">
    <ChatList chatList={chats}/>
    <ChatMessagesFeed/>
    <ChatInputTextBox/>
  </div>
  )

};

export default ChatWindow;
